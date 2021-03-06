package net.thomas.kata.geometry.objects;

import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide.TRIANGLE_SIDES;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide.matchingVertex;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.TRIANGLE_VERTICES;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_1;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_2;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_3;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.getIdForNextVertex;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/***
 * Counterclockwise representation of a triangle with neighbour relations. Neighbour1 is connected
 * by edge(VERTEX_1 -> VERTEX_2), neighbour2 by edge(VERTEX_2 -> VERTEX_3), and so forth. Neighbours
 * can be null.
 *
 */
public class PolygonTriangle {
	public static enum TriangleVertex {
		VERTEX_1,
		VERTEX_2,
		VERTEX_3;
		public static final TriangleVertex[] TRIANGLE_VERTICES = TriangleVertex.values();

		public static TriangleVertex matchingSide(TriangleSide sideId) {
			return TRIANGLE_VERTICES[sideId.ordinal()];
		}

		public static TriangleVertex getIdForNextVertex(final TriangleVertex vertexId) {
			return vertexId == VERTEX_3 ? VERTEX_1 : TRIANGLE_VERTICES[vertexId.ordinal() + 1];
		}

		public TriangleVertex next() {
			return getIdForNextVertex(this);
		}
	}

	public static enum TriangleSide {
		SIDE_1, // VERTEX_1 -> VERTEX_2
		SIDE_2, // VERTEX_2 -> VERTEX_3
		SIDE_3; // VERTEX_3 -> VERTEX_1
		public static final TriangleSide[] TRIANGLE_SIDES = TriangleSide.values();

		public static TriangleSide matchingVertex(TriangleVertex vertexId) {
			return TRIANGLE_SIDES[vertexId.ordinal()];
		}
	}

	private final PolygonVertex[] vertices;
	private final PolygonTriangle[] neighbours;
	private final Map<PolygonVertex, TriangleVertex> vertexIds;

	public PolygonTriangle(PolygonVertex vertex1, PolygonVertex vertex2, PolygonVertex vertex3) {
		vertices = new PolygonVertex[3];
		neighbours = new PolygonTriangle[3];
		vertexIds = new HashMap<>();
		setVertex(VERTEX_1, vertex1);
		setVertex(VERTEX_2, vertex2);
		setVertex(VERTEX_3, vertex3);
	}

	public void setVertex(TriangleVertex vertexId, PolygonVertex instance) {
		vertices[vertexId.ordinal()] = instance;
		vertexIds.put(instance, vertexId);
	}

	public void setNeighbour(TriangleSide sideId, PolygonTriangle instance) {
		neighbours[sideId.ordinal()] = instance;
	}

	public PolygonVertex getVertex(TriangleVertex vertexId) {
		return vertices[vertexId.ordinal()];
	}

	public TriangleVertex getVertexId(PolygonVertex vertex) {
		return vertexIds.get(vertex);
	}

	public PolygonTriangle getNeighbour(TriangleSide sideId) {
		return neighbours[sideId.ordinal()];
	}

	public Point2D calculateCenter() {
		double x = 0.0d;
		double y = 0.0d;
		for (final Point2D vertex : vertices) {
			x += vertex.getX();
			y += vertex.getY();
		}
		return new Point2D.Double(x / vertices.length, y / vertices.length);
	}

	public boolean isNeighbourTo(PolygonTriangle candidate) {
		int verticesInCommon = 0;
		for (final PolygonVertex vertex : vertices) {
			if (candidate.contains(vertex)) {
				verticesInCommon++;
			}
		}
		return verticesInCommon == 2;
	}

	public boolean isConnectedTo(PolygonTriangle candidate) {
		for (final TriangleSide side : TRIANGLE_SIDES) {
			if (candidate.equals(neighbours[side.ordinal()])) {
				return true;
			}
		}
		return false;
	}

	private boolean contains(PolygonVertex candidate) {
		for (final PolygonVertex vertex : vertices) {
			if (vertex.equals(candidate)) {
				return true;
			}
		}
		return false;
	}

	/***
	 * We connect to the vertex before the edge that connects the triangles. So for each triangle, we
	 * need to find the vertex that is not present in the other triangle and then take the vertex just
	 * after as the "connecting" vertex.
	 */
	public void connect(PolygonTriangle neighbour) {
		TriangleVertex leftVertex = null;
		TriangleVertex rightVertex = null;

		for (final TriangleVertex vertexId : TRIANGLE_VERTICES) {
			if (!neighbour.contains(getVertex(vertexId))) {
				leftVertex = getIdForNextVertex(vertexId);
			}
			if (!contains(neighbour.getVertex(vertexId))) {
				rightVertex = getIdForNextVertex(vertexId);
			}
		}
		setNeighbour(matchingVertex(leftVertex), neighbour);
		neighbour.setNeighbour(matchingVertex(rightVertex), this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(vertices);
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
		if (!(obj instanceof PolygonTriangle)) {
			return false;
		}
		final PolygonTriangle other = (PolygonTriangle) obj;
		if (!Arrays.equals(vertices, other.vertices)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append(getVertex(VERTEX_1) + (!getVertex(VERTEX_1).getTwins().isEmpty() ? "(T)" : "") + ", ");
		builder.append(getVertex(VERTEX_2) + (!getVertex(VERTEX_2).getTwins().isEmpty() ? "(T)" : "") + ", ");
		builder.append(getVertex(VERTEX_3) + (!getVertex(VERTEX_3).getTwins().isEmpty() ? "(T)" : ""));
		builder.append(" with neighbours VERTEX_1: " + (getNeighbour(matchingVertex(VERTEX_1)) != null));
		builder.append(", VERTEX_2: " + (getNeighbour(matchingVertex(VERTEX_2)) != null));
		builder.append(", VERTEX_3: " + (getNeighbour(matchingVertex(VERTEX_3)) != null));
		return builder.toString();
	}
}