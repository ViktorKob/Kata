package net.thomas.kata.geometry.algorithms;

import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.util.stream.Collectors.toSet;
import static net.thomas.kata.geometry.algorithms.PolygonUtilImpl.EPSILON;
import static net.thomas.kata.geometry.algorithms.VertexRelation.ABOVE;
import static net.thomas.kata.geometry.algorithms.VertexRelation.BELOW;
import static net.thomas.kata.geometry.algorithms.VertexSide.LEFT;
import static net.thomas.kata.geometry.algorithms.VertexType.END;
import static net.thomas.kata.geometry.algorithms.VertexType.MERGE;
import static net.thomas.kata.geometry.algorithms.VertexType.REGULAR;
import static net.thomas.kata.geometry.algorithms.VertexType.SPLIT;
import static net.thomas.kata.geometry.algorithms.VertexType.START;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide.TRIANGLE_SIDES;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.TRIANGLE_VERTICES;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.matchingSide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import net.thomas.kata.geometry.PolygonUtil;
import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide;
import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex;
import net.thomas.kata.geometry.objects.PolygonVertex;
import net.thomas.kata.geometry.objects.Portal;
import net.thomas.kata.geometry.objects.PortalGraphNode;

public class PolygonUtilImpl implements PolygonUtil {
	public static final double EPSILON = 0.0000001d;

	@Override
	public Collection<PolygonVertex> getMonotoneParts(Collection<PolygonVertex> polygons) {
		return new MonotonePolygonExtractor(polygons).calculateMonotonePolygons();
	}

	@Override
	public Collection<PolygonTriangle> triangulateMonotonePolygons(Collection<PolygonVertex> monotonePolygons) {
		return new MonotonePolygonTriangulator(monotonePolygons).buildTriangleGraphs();
	}

	@Override
	public Collection<PortalGraphNode> buildPortalGraphs(Collection<PolygonTriangle> triangleGraphs) {
		return new TriangleGraphConverter(triangleGraphs).buildPortalGraphs();
	}

	/***
	 * Based on Berg, Krevald, Overmars & Schwarzkopf - Computational Geometry (2nd ed.) <BR>
	 * Chapter 3 - Polygon Triangulation
	 *
	 * Polygons must be counterclockwise and disjoint for the algorithm to run correctly. Holes can be
	 * added as clockwise polygons fully contained within other polygons.
	 */
	private static class MonotonePolygonExtractor {
		private final Map<PolygonVertex, Edge> edges;
		private final Map<PolygonVertex, VertexType> vertexTypes;
		private final List<PolygonVertex> sweepline;
		private final StatusSearchTree status;
		private final Collection<PolygonVertex> monotonePolygons;

		public MonotonePolygonExtractor(Collection<PolygonVertex> polygons) {
			edges = buildEdgeMap(polygons);
			vertexTypes = determineVertexTypes(polygons);
			sweepline = buildSweepline(polygons);
			status = new StatusSearchTree();
			monotonePolygons = new LinkedList<>();
		}

		private Map<PolygonVertex, Edge> buildEdgeMap(Collection<PolygonVertex> polygons) {
			final Map<PolygonVertex, Edge> edges = new HashMap<>();
			for (final PolygonVertex polygon : polygons) {
				for (final PolygonVertex vertex : polygon) {
					edges.put(vertex, new Edge(vertex, vertex.getAfter()));
				}
			}
			return edges;
		}

		private Map<PolygonVertex, VertexType> determineVertexTypes(Collection<PolygonVertex> polygons) {
			final Map<PolygonVertex, VertexType> vertexTypes = new HashMap<>();
			for (final PolygonVertex polygon : polygons) {
				for (final PolygonVertex vertex : polygon) {
					vertexTypes.put(vertex, getVertexType(vertex));
				}
			}
			return vertexTypes;
		}

		private List<PolygonVertex> buildSweepline(Collection<PolygonVertex> polygons) {
			final SweeplineBuilder builder = new SweeplineBuilder();
			for (final PolygonVertex vertex : polygons) {
				builder.addPolygon(vertex);
			}
			return builder.build();
		}

		private VertexType getVertexType(PolygonVertex vertex) {
			final VertexRelation before = determineRelation(vertex, vertex.getBefore());
			final VertexRelation after = determineRelation(vertex, vertex.getAfter());
			final double interiorAngle = calculateInteriorAngleFor(vertex);

			if (oneIsBelowAndOneIsAbove(before, after)) {
				return REGULAR;
			} else if (before == BELOW) {
				return interiorAngle < PI ? END : MERGE;
			} else {
				return interiorAngle < PI ? START : SPLIT;
			}
		}

		private VertexRelation determineRelation(PolygonVertex vertex, PolygonVertex neighbour) {
			if (vertex.y > neighbour.y) {
				return ABOVE;
			} else if (vertex.y < neighbour.y) {
				return BELOW;
			} else {
				return vertex.x < neighbour.x ? ABOVE : BELOW;
			}
		}

		private double calculateInteriorAngleFor(PolygonVertex vertex) {
			double angle = atan2(vertex.y - vertex.getAfter().y, vertex.x - vertex.getAfter().x)
					- atan2(vertex.getBefore().y - vertex.y, vertex.getBefore().x - vertex.x);
			if (angle < 0) {
				angle += PI * 2;
			}
			if (abs(angle) < EPSILON) {
				return 0.0;
			}
			if (abs(angle - PI) < EPSILON) {
				return PI;
			}
			return angle;
		}

		private boolean oneIsBelowAndOneIsAbove(final VertexRelation before, final VertexRelation after) {
			return before != after;
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
				monotonePolygons.add(cutOutMonotonePiece(edgeBeforeVertex.getHelper(), vertex));
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
			return vertex.getAfter().y < vertex.getBefore().y || vertex.getAfter().y == vertex.getBefore().y && vertex.getAfter().x > vertex.getBefore().x;
		}

		private PolygonVertex cutOutMonotonePiece(PolygonVertex before, PolygonVertex after) {
			return before.cutIntoTwoPolygons(after);
		}
	}

	/***
	 * Based on Berg, Krevald, Overmars & Schwarzkopf - Computational Geometry (2nd ed.) <BR>
	 * Chapter 3 - Polygon Triangulation
	 */
	private static class MonotonePolygonTriangulator {
		private final Collection<PolygonVertex> monotonePolygons;

		public MonotonePolygonTriangulator(Collection<PolygonVertex> monotonePolygons) {
			this.monotonePolygons = monotonePolygons;
		}

		public Collection<PolygonTriangle> buildTriangleGraphs() {
			final Collection<PolygonTriangle> triangleGraphs = new LinkedList<>();
			for (final PolygonVertex monotonePolygon : monotonePolygons) {
				triangleGraphs.add(buildTriangleGraph(monotonePolygon));
			}
			return triangleGraphs;
		}

		private PolygonTriangle buildTriangleGraph(PolygonVertex monotonePolygon) {
			final TriangleGraphBuilder builder = new TriangleGraphBuilder();
			final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().addPolygon(monotonePolygon).build().iterator();
			final Stack<PolygonVertex> unprocessedVertices = new Stack<>();
			final PolygonVertex firstVertex = sweepline.next();
			final VertexSideMapForMonotonePolygon sideMap = new VertexSideMapForMonotonePolygon(firstVertex);
			unprocessedVertices.push(firstVertex);
			PolygonVertex next = sweepline.next();
			unprocessedVertices.push(next);
			VertexSide currentSide = sideMap.getSide(next);
			while (sweepline.hasNext()) {
				next = sweepline.next();
				if (next != sideMap.getBottom()) {
					final VertexSide nextSide = sideMap.getSide(next);
					if (nextSide != currentSide) {
						final Stack<PolygonVertex> constructionStack = reverseStack(unprocessedVertices);
						PolygonVertex nextOtherSideVertex = constructionStack.pop();
						PolygonVertex nextNextOtherSideVertex;
						do {
							nextNextOtherSideVertex = constructionStack.pop();
							builder.add(buildTriangle(nextSide, next, nextOtherSideVertex, nextNextOtherSideVertex));
							nextOtherSideVertex = nextNextOtherSideVertex;
						} while (!constructionStack.isEmpty());
						unprocessedVertices.push(nextOtherSideVertex);
					} else {
						PolygonVertex lastVertex = unprocessedVertices.pop();
						while (!unprocessedVertices.isEmpty() && edgeIsInsidePolygon(currentSide, next, unprocessedVertices.peek(), lastVertex)) {
							builder.add(buildTriangle(nextSide, next, lastVertex, unprocessedVertices.peek()));
							lastVertex = unprocessedVertices.pop();
						}
						unprocessedVertices.push(lastVertex);
					}
					unprocessedVertices.push(next);
					currentSide = nextSide;
				}
			}
			final Stack<PolygonVertex> constructionStack = reverseStack(unprocessedVertices);
			PolygonVertex secondLast = constructionStack.pop();
			while (!constructionStack.isEmpty()) {
				final PolygonVertex last = constructionStack.pop();
				final PolygonTriangle triangle = buildTriangle(sideMap.getSide(last), next, last, secondLast);
				builder.add(triangle);
				secondLast = last;
			}
			return builder.build();
		}

		private static class TriangleGraphBuilder {
			private final Set<PolygonTriangle> looseTriangles;

			public TriangleGraphBuilder() {
				looseTriangles = new HashSet<>();
			}

			public TriangleGraphBuilder add(PolygonTriangle triangle) {
				final Set<PolygonTriangle> absorbedTriangles = new HashSet<>();
				for (final PolygonTriangle looseTriangle : looseTriangles) {
					if (looseTriangle.isNeighbourTo(triangle)) {
						looseTriangle.connect(triangle);
						absorbedTriangles.add(looseTriangle);
					}
				}
				looseTriangles.removeAll(absorbedTriangles);
				looseTriangles.add(triangle);
				return this;
			}

			public PolygonTriangle build() {
				if (looseTriangles.size() > 1) {
					throw new RuntimeException("Number of loose triangles was " + looseTriangles.size() + ". It should have been 1. " + looseTriangles);
				}
				return looseTriangles.iterator().next();
			}
		}

		private Stack<PolygonVertex> reverseStack(final Stack<PolygonVertex> unprocessedVertices) {
			final Stack<PolygonVertex> constructionStack = new Stack<>();
			while (!unprocessedVertices.isEmpty()) {
				constructionStack.push(unprocessedVertices.pop());
			}
			return constructionStack;
		}

		private PolygonTriangle buildTriangle(VertexSide side, PolygonVertex next, final PolygonVertex last, final PolygonVertex secondLast) {
			PolygonTriangle triangle;
			if (side == LEFT) {
				triangle = new PolygonTriangle(secondLast, last, next);
			} else {
				triangle = new PolygonTriangle(secondLast, next, last);
			}
			return triangle;
		}

		private boolean edgeIsInsidePolygon(VertexSide currentSide, PolygonVertex current, PolygonVertex previous, PolygonVertex secondPrevious) {
			final PolygonVertex a = current;
			final PolygonVertex b = previous;
			final PolygonVertex c = secondPrevious;
			final double determinant = (c.x - b.x) * (b.y - a.y) - (b.x - a.x) * (c.y - b.y);
			return currentSide == LEFT ? determinant < 0 : determinant > 0;
		}
	}

	private static class TriangleGraphConverter {
		private final Collection<PolygonTriangle> intermediateTriangleGraphs;

		public TriangleGraphConverter(Collection<PolygonTriangle> intermediateTriangleGraphs) {
			this.intermediateTriangleGraphs = intermediateTriangleGraphs;
		}

		public Collection<PortalGraphNode> buildPortalGraphs() {
			connectAllAdjacentTriangles(intermediateTriangleGraphs);
			final HashSet<PolygonTriangle> pendingNodes = extractAllUniqueTriangles(intermediateTriangleGraphs);
			return convertToPortalGraphs(pendingNodes);
		}

		private void connectAllAdjacentTriangles(Collection<PolygonTriangle> intermediateTriangleGraphs) {
			final Map<PolygonVertex, Set<PolygonTriangle>> verticesWithTwins = new HashMap<>();
			final Set<PolygonTriangle> visitedTriangles = new HashSet<>();
			for (final PolygonTriangle triangle : intermediateTriangleGraphs) {
				visitTriangle(triangle, visitedTriangles, verticesWithTwins);
			}
			for (final Set<PolygonTriangle> triangles : verticesWithTwins.values()) {
				for (final PolygonTriangle triangle : triangles) {
					connectAdjacentTriangles(verticesWithTwins, triangle);
				}
			}
		}

		private void connectAdjacentTriangles(final Map<PolygonVertex, Set<PolygonTriangle>> verticesWithTwins, final PolygonTriangle triangle) {
			final Set<PolygonTriangle> neighbourCandidates = new HashSet<>();
			for (final TriangleVertex vertexId : TRIANGLE_VERTICES) {
				final Set<PolygonTriangle> twinTriangles = verticesWithTwins.get(triangle.getVertex(vertexId));
				if (twinTriangles != null) {
					neighbourCandidates.addAll(twinTriangles.stream().filter((potentialCandidate) -> {
						return isTrueCandidate(triangle, potentialCandidate);
					}).collect(toSet()));
				}
			}
			if (!neighbourCandidates.isEmpty()) {
				for (final PolygonTriangle candidate : neighbourCandidates) {
					candidate.connect(triangle);
				}
			}
		}

		private boolean isTrueCandidate(final PolygonTriangle triangle, PolygonTriangle candidate) {
			return candidate != triangle && triangle.isNeighbourTo(candidate) && !triangle.isConnectedTo(candidate);
		}

		private void visitTriangle(final PolygonTriangle triangle, final Set<PolygonTriangle> visitedTriangles,
				final Map<PolygonVertex, Set<PolygonTriangle>> verticesWithTwins) {
			if (triangle != null) {
				visitedTriangles.add(triangle);
				checkForTwinVertices(triangle, verticesWithTwins);
				visitSides(triangle, visitedTriangles, verticesWithTwins);
			}
		}

		private void checkForTwinVertices(final PolygonTriangle triangle, final Map<PolygonVertex, Set<PolygonTriangle>> verticesWithTwins) {
			for (final TriangleVertex vertexId : TRIANGLE_VERTICES) {
				final PolygonVertex vertex = triangle.getVertex(vertexId);
				if (!vertex.getTwins().isEmpty()) {
					if (!verticesWithTwins.containsKey(vertex)) {
						verticesWithTwins.put(vertex, new HashSet<>());
					}
					verticesWithTwins.get(vertex).add(triangle);
				}
			}
		}

		private void visitSides(final PolygonTriangle triangle, final Set<PolygonTriangle> visitedTriangles,
				final Map<PolygonVertex, Set<PolygonTriangle>> verticesWithTwins) {
			for (final TriangleSide side : TRIANGLE_SIDES) {
				final PolygonTriangle neighbour = triangle.getNeighbour(side);
				if (!visitedTriangles.contains(neighbour)) {
					visitTriangle(neighbour, visitedTriangles, verticesWithTwins);
				}
			}
		}

		private HashSet<PolygonTriangle> extractAllUniqueTriangles(Collection<PolygonTriangle> intermediateTriangleGraphs) {
			final HashSet<PolygonTriangle> pendingNodes = new HashSet<>();
			for (final PolygonTriangle triangle : intermediateTriangleGraphs) {
				pendingNodes.add(triangle);
				addNeighbours(pendingNodes, triangle);
			}
			return pendingNodes;
		}

		private void addNeighbours(HashSet<PolygonTriangle> nodes, PolygonTriangle currentTriangle) {
			for (final TriangleSide side : TRIANGLE_SIDES) {
				addNeighbourIfNotPresent(nodes, currentTriangle, side);
			}
		}

		private void addNeighbourIfNotPresent(HashSet<PolygonTriangle> nodes, PolygonTriangle currentTriangle, TriangleSide side) {
			final PolygonTriangle neighbour = currentTriangle.getNeighbour(side);
			if (neighbour != null && !nodes.contains(neighbour)) {
				nodes.add(neighbour);
				addNeighbours(nodes, neighbour);
			}
		}

		private Collection<PortalGraphNode> convertToPortalGraphs(final HashSet<PolygonTriangle> pendingNodes) {
			final Map<Portal, PortalGraphNode> portalGraphs = new HashMap<>();
			final Set<PolygonTriangle> visitedTriangles = new HashSet<>();
			for (final PolygonTriangle triangle : pendingNodes) {
				convertNodeWithNeighbours(triangle, portalGraphs, visitedTriangles);
			}
			final Collection<PortalGraphNode> distinctGraphs = extractDistinctGraphsV2(portalGraphs);
			return distinctGraphs;
		}

		private void convertNodeWithNeighbours(PolygonTriangle triangle, Map<Portal, PortalGraphNode> portalGraphs, Set<PolygonTriangle> visitedTriangles) {
			if (!visitedTriangles.contains(triangle)) {
				visitedTriangles.add(triangle);
				final List<PortalGraphNode> relevantPortalNodes = new ArrayList<>();
				for (final TriangleSide side : TRIANGLE_SIDES) {
					if (triangle.getNeighbour(side) != null) {
						final PortalGraphNode node = buildGraphForSide(triangle, portalGraphs, visitedTriangles, side);
						relevantPortalNodes.add(node);
					}
				}
				connectPortals(relevantPortalNodes);
			}
		}

		private PortalGraphNode buildGraphForSide(PolygonTriangle triangle, Map<Portal, PortalGraphNode> portalGraphs,
				Set<PolygonTriangle> visitedTriangles, final TriangleSide side) {
			final TriangleVertex rightVertex = matchingSide(side);
			final Portal portal = new Portal(triangle.getVertex(rightVertex.next()), triangle.getVertex(rightVertex));
			if (!portalGraphs.containsKey(portal)) {
				portalGraphs.put(portal, new PortalGraphNode(portal));
			}
			final PortalGraphNode node = portalGraphs.get(portal);
			convertNodeWithNeighbours(triangle.getNeighbour(side), portalGraphs, visitedTriangles);
			return node;
		}

		private void connectPortals(final List<PortalGraphNode> relevantNodes) {
			if (relevantNodes.size() > 1) {
				for (int i = 0; i < relevantNodes.size() - 1; i++) {
					for (int j = i; j < relevantNodes.size(); j++) {
						relevantNodes.get(i).addNeighbour(relevantNodes.get(j));
						relevantNodes.get(j).addNeighbour(relevantNodes.get(i));
					}
				}
			}
		}

		private Collection<PortalGraphNode> extractDistinctGraphsV2(Map<Portal, PortalGraphNode> portalGraphs) {
			final Collection<PortalGraphNode> distinctGraphs = new HashSet<>();
			final Set<PortalGraphNode> visitedNodes = new HashSet<>();
			for (final PortalGraphNode node : portalGraphs.values()) {
				if (!visitedNodes.contains(node)) {
					distinctGraphs.add(node);
				}
				visitNode(node, visitedNodes);
			}
			return distinctGraphs;
		}

		private void visitNode(final PortalGraphNode node, final Set<PortalGraphNode> visitedNodes) {
			if (node != null && !visitedNodes.contains(node)) {
				visitedNodes.add(node);
				for (final PortalGraphNode neighbour : node) {
					visitNode(neighbour, visitedNodes);
				}
			}
		}
	}
}

enum VertexRelation {
	ABOVE,
	BELOW
}

enum VertexSide {
	TOP,
	LEFT,
	RIGHT,
	BOTTOM
}

enum VertexType {
	START,
	END,
	REGULAR,
	SPLIT,
	MERGE
}

class SweeplineBuilder {
	private final List<PolygonVertex> polygons;

	public SweeplineBuilder() {
		polygons = new LinkedList<>();
	}

	public SweeplineBuilder addPolygon(PolygonVertex polygon) {
		polygons.add(polygon);
		return this;
	}

	public List<PolygonVertex> build() {
		final List<PolygonVertex> sweepline = cloneIntoSweeplineList(polygons);
		sortVertices(sweepline);
		return sweepline;
	}

	private List<PolygonVertex> cloneIntoSweeplineList(List<PolygonVertex> polygons2) {
		final List<PolygonVertex> sweepline = new ArrayList<>();
		for (final PolygonVertex polygon : polygons) {
			for (final PolygonVertex vertex : polygon.createClone()) {
				sweepline.add(vertex);
			}
		}
		return sweepline;
	}

	private void sortVertices(final List<PolygonVertex> sweepline) {
		sweepline.sort((left, right) -> {
			final int difference = java.lang.Double.compare(left.y, right.y);
			if (difference == 0) {
				return java.lang.Double.compare(left.x, right.x);
			} else {
				return -difference;
			}
		});
	}
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
	public String toString() {
		return start + " -> " + end + (helper != null ? " (Helper: " + helper + ")" : "");
	}
}