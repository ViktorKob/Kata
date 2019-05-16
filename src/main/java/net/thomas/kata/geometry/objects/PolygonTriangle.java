package net.thomas.kata.geometry.objects;

import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_1;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_2;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_3;

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
		VERTEX_3
	}

	private final PolygonVertex[] vertices;
	private final PolygonTriangle[] neighbours;

	public PolygonTriangle(PolygonVertex vertex1, PolygonVertex vertex2, PolygonVertex vertex3) {
		vertices = new PolygonVertex[3];
		neighbours = new PolygonTriangle[3];
		setVertex(VERTEX_1, vertex1);
		setVertex(VERTEX_2, vertex2);
		setVertex(VERTEX_3, vertex3);
	}

	public void setVertex(TriangleVertex vertexId, PolygonVertex instance) {
		vertices[vertexId.ordinal()] = instance;
	}

	public void setNeighbour(TriangleVertex vertexId, PolygonTriangle instance) {
		neighbours[vertexId.ordinal()] = instance;
	}

	public PolygonVertex getVertex(TriangleVertex vertexId) {
		return vertices[vertexId.ordinal()];
	}

	public PolygonTriangle getNeighbour(TriangleVertex vertexId) {
		return neighbours[vertexId.ordinal()];
	}
}