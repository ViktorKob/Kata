package net.thomas.kata.geometry.algorithms;

import static java.lang.Math.abs;
import static net.thomas.kata.geometry.algorithms.VertexType.END;
import static net.thomas.kata.geometry.algorithms.VertexType.MERGE;
import static net.thomas.kata.geometry.algorithms.VertexType.REGULAR;
import static net.thomas.kata.geometry.algorithms.VertexType.SPLIT;
import static net.thomas.kata.geometry.algorithms.VertexType.START;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import net.thomas.kata.geometry.PolygonUtil;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonUtilImpl implements PolygonUtil {

	@Override
	public Collection<PolygonVertex> getMonotoneParts(PolygonVertex polygon) {
		return new MonotonePolygonExtractor(polygon).getMonotonePolygons();
	}

	/***
	 * Based on Berg, Krevald, Overmars & Schwarzkopf - Computational Geometry (2nd ed.) <BR>
	 * Chapter 3 - Polygon Triangulation
	 */
	static class MonotonePolygonExtractor {
		private final Map<PolygonVertex, VertexType> vertexTypes;
		private final List<PolygonVertex> sweepline;
		private final StatusSearchTree status;
		private final Collection<PolygonVertex> monotonePolygons;

		public MonotonePolygonExtractor(PolygonVertex polygon) {
			vertexTypes = determineVertexTypes(polygon);
			sweepline = polygon.buildSweepline();
			status = new StatusSearchTree();
			monotonePolygons = new HashSet<>();
		}

		private Map<PolygonVertex, VertexType> determineVertexTypes(PolygonVertex polygon) {
			final Map<PolygonVertex, VertexType> vertexTypes = new HashMap<>();
			for (final PolygonVertex vertex : polygon) {
				vertexTypes.put(vertex, getVertexType(vertex));
			}
			return vertexTypes;
		}

		private VertexType getVertexType(PolygonVertex vertex) {
			if (vertex.y > vertex.getBefore().y && vertex.y > vertex.getAfter().y) {
				if (vertex.getBefore().x < vertex.getAfter().getX()) {
					return START;
				} else {
					return SPLIT;
				}
			}
			if (vertex.y < vertex.getBefore().y && vertex.y < vertex.getAfter().y) {
				if (vertex.getBefore().x < vertex.getAfter().getX()) {
					return MERGE;
				} else {
					return END;
				}
			}
			return REGULAR;
		}

		public Collection<PolygonVertex> getMonotonePolygons() {
			monotonePolygons.clear();
			for (final PolygonVertex vertex : sweepline) {
				final VertexType vertexType = vertexTypes.get(vertex);
				System.out.println("Vertex [" + vertex.x + ", " + vertex.y + "] is a " + vertexType + " vertex");
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
			final Edge edge = new Edge(vertex.getBefore(), vertex);
			edge.setHelper(vertex);
			status.insert(edge);
		}

		private void handleMergeVertex(PolygonVertex vertex) {

		}

		private void handleRegularVertex(PolygonVertex vertex) {

		}

		private void handleSplitVertex(PolygonVertex vertex) {
			final Edge edge = status.locateNearestEdgeToTheLeft(vertex);
			monotonePolygons.add(buildMonotoneSequence(edge));

			status.insert(new Edge(vertex.getBefore(), vertex));

			// Update helper for lines
			// line.helper = &newThis;
			// v.segment->helper = &v;

		}

		private void handleEndVertex(PolygonVertex vertex) {
			final Edge edge = status.extractEdgeWithEndVertex(vertex);
			monotonePolygons.add(buildMonotoneSequence(edge));
		}

		private PolygonVertex buildMonotoneSequence(Edge edge) {
			System.out.println("***************");
			System.out.println(edge.getStartVertex());
			System.out.println(edge.getHelper());
			System.out.println(edge.getEndVertex());
			System.out.println("(/\\) " + edge.getTopVertex());
			System.out.println("(\\/) " + edge.getBottomVertex());
			System.out.println("***************");
			return edge.getEndVertex();
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

	public Edge extractEdgeWithEndVertex(PolygonVertex vertex) {
		if (root.contents.getStartVertex() == vertex) {
			final Edge edge = root.contents;
			root = removeElement(root);
			return edge;
		}

		Node current = root;
		boolean searching = true;
		Edge edge = null;
		while (searching) {
			if (current.contents.isLeftOf(vertex)) {
				if (current.right.contents.getStartVertex() == vertex) {
					edge = current.right.contents;
					current.right = removeElement(current.right);
					searching = false;
				} else {
					current = current.right;
				}
			} else {
				if (current.left.contents.getStartVertex() == vertex) {
					edge = current.left.contents;
					current.left = removeElement(current.left);
					searching = false;
				} else {
					current = current.left;
				}
			}
		}
		return edge;
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
	public double EPSILON = 0.0000001d;
	private final PolygonVertex start;
	private final PolygonVertex end;
	private PolygonVertex helper;

	public Edge(PolygonVertex start, PolygonVertex end) {
		this.start = start;
		this.end = end;
	}

	public PolygonVertex getStartVertex() {
		return start;
	}

	public PolygonVertex getEndVertex() {
		return end;
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
		return start + " -> " + end;
	}
}