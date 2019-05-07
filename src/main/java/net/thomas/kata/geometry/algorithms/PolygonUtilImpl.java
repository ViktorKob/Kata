package net.thomas.kata.geometry.algorithms;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static net.thomas.kata.geometry.algorithms.PolygonUtilImpl.EPSILON;
import static net.thomas.kata.geometry.algorithms.VertexType.END;
import static net.thomas.kata.geometry.algorithms.VertexType.MERGE;
import static net.thomas.kata.geometry.algorithms.VertexType.REGULAR;
import static net.thomas.kata.geometry.algorithms.VertexType.SPLIT;
import static net.thomas.kata.geometry.algorithms.VertexType.START;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.thomas.kata.geometry.PolygonUtil;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonUtilImpl implements PolygonUtil {
	public static final double EPSILON = 0.0000001d;

	@Override
	public Collection<PolygonVertex> getMonotoneParts(PolygonVertex polygon) {
		return new MonotonePolygonExtractor(polygon).calculateMonotonePolygons();
	}

	/***
	 * Based on Berg, Krevald, Overmars & Schwarzkopf - Computational Geometry (2nd ed.) <BR>
	 * Chapter 3 - Polygon Triangulation
	 */
	static class MonotonePolygonExtractor {
		private final Map<PolygonVertex, Edge> edges;
		private final Map<PolygonVertex, VertexType> vertexTypes;
		private final List<PolygonVertex> sweepline;
		private final StatusSearchTree status;
		private final Collection<PolygonVertex> monotonePolygons;

		public MonotonePolygonExtractor(PolygonVertex polygon) {
			edges = buildEdgeMap(polygon);
			vertexTypes = determineVertexTypes(polygon);
			sweepline = polygon.buildSweepline();
			status = new StatusSearchTree();
			monotonePolygons = new LinkedList<>();
		}

		private Map<PolygonVertex, Edge> buildEdgeMap(PolygonVertex polygon) {
			final Map<PolygonVertex, Edge> edges = new HashMap<>();
			for (final PolygonVertex vertex : polygon) {
				edges.put(vertex, new Edge(vertex, vertex.getAfter()));
			}
			return edges;
		}

		private Map<PolygonVertex, VertexType> determineVertexTypes(PolygonVertex polygon) {
			final Map<PolygonVertex, VertexType> vertexTypes = new HashMap<>();
			for (final PolygonVertex vertex : polygon) {
				vertexTypes.put(vertex, getVertexType(vertex));
			}
			return vertexTypes;
		}

		private VertexType getVertexType(PolygonVertex vertex) {
			final double angle = calculateInteriorAngleFor(vertex);
			if (vertex.y >= vertex.getBefore().y && vertex.y > vertex.getAfter().y) {
				if (angle < PI) {
					return START;
				} else {
					return SPLIT;
				}
			}
			if (vertex.y <= vertex.getBefore().y && vertex.y < vertex.getAfter().y) {
				if (angle < PI) {
					return END;
				} else {
					return MERGE;
				}
			}
			return REGULAR;
		}

		private double calculateInteriorAngleFor(PolygonVertex vertex) {
			double angle = atan2(vertex.y - vertex.getAfter().y, vertex.x - vertex.getAfter().x)
					- atan2(vertex.getBefore().y - vertex.y, vertex.getBefore().x - vertex.x);
			if (angle < 0) {
				angle += PI * 2;
			}
			if (abs(angle - PI) < EPSILON) {
				return PI;
			}
			return angle;

		}

		public Collection<PolygonVertex> calculateMonotonePolygons() {
			monotonePolygons.clear();
			for (final PolygonVertex vertex : sweepline) {
				final VertexType vertexType = vertexTypes.get(vertex);
				switch (vertexType) {
					case START:
						handleStartVertex(vertex);
						break;
					case MERGE:
						handleMergeVertex(vertex);
						break;
					case REGULAR:
						handleRegularVertex(vertex);
						break;
					case SPLIT:
						handleSplitVertex(vertex);
						break;
					case END:
						handleEndVertex(vertex);
						break;
				}
			}
			return monotonePolygons;
		}

		private void handleStartVertex(PolygonVertex vertex) {
			final Edge edge = edges.get(vertex);
			edge.setHelper(vertex);
			status.insert(edge);
		}

		private void handleMergeVertex(PolygonVertex vertex) {
			final Edge edgeBeforeVertex = edges.get(vertex.getBefore());
			if (vertexTypes.get(edgeBeforeVertex.getHelper()) == MERGE) {
				monotonePolygons.add(cutOutMonotonePiece(vertex, edgeBeforeVertex.getHelper()));
			}
			status.deleteEdge(edgeBeforeVertex);
			final Edge edgeLeftOfVertex = status.locateNearestEdgeToTheLeft(vertex);
			if (vertexTypes.get(edgeLeftOfVertex.getHelper()) == MERGE) {
				monotonePolygons.add(cutOutMonotonePiece(vertex, edgeLeftOfVertex.getHelper()));
			}
			edgeLeftOfVertex.setHelper(vertex);
		}

		private void handleRegularVertex(PolygonVertex vertex) {
			if (interiorIsToTheRight(vertex)) {
				final Edge edgeBeforeVertex = edges.get(vertex.getBefore());
				if (vertexTypes.get(edgeBeforeVertex.getHelper()) == MERGE) {
					monotonePolygons.add(cutOutMonotonePiece(edgeBeforeVertex.getHelper(), vertex));
				}
				status.deleteEdge(edgeBeforeVertex);
				final Edge edgeAfterVertex = edges.get(vertex);
				edgeAfterVertex.setHelper(vertex);
				status.insert(edgeAfterVertex);
			} else {
				final Edge edgeToTheLeft = status.locateNearestEdgeToTheLeft(vertex);
				if (vertexTypes.get(edgeToTheLeft.getHelper()) == MERGE) {
					monotonePolygons.add(cutOutMonotonePiece(vertex, edgeToTheLeft.getHelper()));
				}
				edgeToTheLeft.setHelper(vertex);
			}
		}

		private void handleSplitVertex(PolygonVertex vertex) {
			final Edge edgeToTheLeft = status.locateNearestEdgeToTheLeft(vertex);
			cutOutMonotonePiece(edgeToTheLeft.getHelper(), vertex);
			edgeToTheLeft.setHelper(vertex);
			final Edge edgeToAdd = edges.get(vertex);
			edgeToAdd.setHelper(vertex);
			status.insert(edgeToAdd);
		}

		private void handleEndVertex(PolygonVertex vertex) {
			final Edge edge = edges.get(vertex.getBefore());
			if (vertexTypes.get(edge.getHelper()) == MERGE) {
				monotonePolygons.add(cutOutMonotonePiece(vertex, edge.getHelper()));
			}
			status.deleteEdge(edge);
			monotonePolygons.add(vertex);
		}

		private boolean interiorIsToTheRight(PolygonVertex vertex) {
			return vertex.getAfter().y < vertex.getBefore().y;
		}

		private PolygonVertex cutOutMonotonePiece(PolygonVertex before, PolygonVertex after) {
			return before.cutIntoTwoPolygons(after);
		}
	}
}

enum VertexType {
	START,
	END,
	REGULAR,
	SPLIT,
	MERGE
}

enum Direction {
	CLOCKWISE,
	COUNTERCLOCKWISE
}

/***
 * TODO: This should be a balanced search tree (e.g. red / black tree), if O(n*log(n)) is to be
 * guaranteed
 ***/
class StatusSearchTree {
	class Node {
		public Node left;
		public Node right;
		public Edge contents;

		public Node(Edge contents) {
			this.contents = contents;
			left = right = null;
		}
	}

	private Node root;

	StatusSearchTree() {
		root = null;
	}

	public void insert(Edge edge) {
		if (root == null) {
			root = new Node(edge);
		} else {
			Node current = root;
			boolean searching = true;
			while (searching) {
				if (current.contents.isLeftOf(edge.getTopVertex())) {
					if (current.right != null) {
						current = current.right;
					} else {
						current.right = new Node(edge);
						searching = false;
					}
				} else {
					if (current.left != null) {
						current = current.left;
					} else {
						current.left = new Node(edge);
						searching = false;
					}
				}
			}
		}
	}

	public void deleteEdge(Edge edge) {
		if (root.contents == edge) {
			root = removeElement(root);
			return;
		}

		Node current = root;
		boolean searching = true;
		while (searching) {
			if (current.contents.isLeftOf(edge.getBottomVertex())) {
				if (current.right.contents == edge) {
					current.right = removeElement(current.right);
					searching = false;
				} else {
					current = current.right;
				}
			} else {
				if (current.left.contents == edge) {
					current.left = removeElement(current.left);
					searching = false;
				} else {
					current = current.left;
				}
			}
		}
	}

	public Edge locateNearestEdgeToTheLeft(final PolygonVertex vertex) {
		Node current = root;
		Node closest = null;
		while (true) {
			if (current == null) {
				return closest != null ? closest.contents : null;
			} else if (current.contents.isLeftOf(vertex)) {
				current = current.right;
			} else {
				closest = current;
				current = current.left;
			}
		}
	}

	private Node removeElement(Node element) {
		if (element.left != null) {
			Node current = element.left;
			if (current.right == null) {
				current.right = element.right;
				return current;
			}
			while (current.right.right != null) {
				current = current.right;
			}
			final Node newRoot = current.right;
			current.right = current.right.left;
			newRoot.left = element.left;
			newRoot.right = element.right;
			return newRoot;
		} else if (element.right != null) {
			Node current = element.right;
			if (current.left == null) {
				return current;
			}
			while (current.left.left != null) {
				current = current.left;
			}
			final Node newRoot = current.left;
			current.left = current.left.right;
			newRoot.left = element.left;
			newRoot.right = element.right;
			return newRoot;
		}
		return null;
	}
}

class Edge {
	private final PolygonVertex start;
	private final PolygonVertex end;
	private PolygonVertex helper;
	private Edge twin;

	public Edge(PolygonVertex start, PolygonVertex end) {
		this.start = start;
		this.end = end;
		twin = null;
	}

	public PolygonVertex getStartVertex() {
		return start;
	}

	public PolygonVertex getEndVertex() {
		return end;
	}

	public void setTwin(Edge twin) {
		this.twin = twin;
	}

	public Edge getTwin() {
		return twin;
	}

	public PolygonVertex getTopVertex() {
		if (start.y > end.y) {
			return start;
		} else if (start.y < end.y) {
			return end;
		} else {
			return start.x < end.x ? start : end;
		}
	}

	public PolygonVertex getBottomVertex() {
		if (start.y < end.y) {
			return start;
		} else if (start.y > end.y) {
			return end;
		} else {
			return start.x > end.x ? start : end;
		}
	}

	public PolygonVertex getHelper() {
		return helper;
	}

	public void setHelper(PolygonVertex helper) {
		this.helper = helper;
	}

	public boolean isLeftOf(PolygonVertex vertex) {
		final double projectionX = determineProjectionOfVertexY(vertex.y);
		if (projectionX >= vertex.x - EPSILON) {
			return true;
		}
		return false;
	}

	private double determineProjectionOfVertexY(double vertexY) {
		final double deltaX = end.x - start.x;
		if (abs(deltaX) < EPSILON) {// vertical line
			return start.x;
		} else {
			return calculateProjection(vertexY, deltaX);
		}
	}

	private double calculateProjection(double vertexY, final double deltaX) {
		final double deltaY = end.y - start.y;
		final double a = deltaY / deltaX;
		final double b = end.y - a * end.x;
		final double projection = (vertexY - b) / a;
		return projection;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (end == null ? 0 : end.hashCode());
		result = prime * result + (start == null ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Edge)) {
			return false;
		}
		final Edge other = (Edge) obj;
		if (end == null) {
			if (other.end != null) {
				return false;
			}
		} else if (!end.equals(other.end)) {
			return false;
		}
		if (start == null) {
			if (other.start != null) {
				return false;
			}
		} else if (!start.equals(other.start)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return start + " -> " + end + (helper != null ? " (Helper: " + helper + ")" : "");
	}
}