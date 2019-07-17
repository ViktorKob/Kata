package net.thomas.kata.geometry.objects;

import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_1;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_2;
import static net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex.VERTEX_3;

import java.awt.geom.Point2D;
import java.util.Arrays;

import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide;
import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleVertex;

public class PolygonGraphNode {

	public Point2D[] vertices;
	public PolygonGraphNode[] neighbours;

	public PolygonGraphNode(PolygonTriangle triangle) {
		vertices = new Point2D[TriangleVertex.values().length];
		vertices[VERTEX_1.ordinal()] = new Point2D.Double(triangle.getVertex(VERTEX_1).x, triangle.getVertex(VERTEX_1).y);
		vertices[VERTEX_2.ordinal()] = new Point2D.Double(triangle.getVertex(VERTEX_2).x, triangle.getVertex(VERTEX_2).y);
		vertices[VERTEX_3.ordinal()] = new Point2D.Double(triangle.getVertex(VERTEX_3).x, triangle.getVertex(VERTEX_3).y);
		neighbours = new PolygonGraphNode[TriangleSide.values().length];
	}

	public void setNeighbour(TriangleSide side, PolygonGraphNode node) {
		neighbours[side.ordinal()] = node;
	}

	public PolygonGraphNode getNeighbour(TriangleSide side) {
		return neighbours[side.ordinal()];
	}

	public Point2D getCenter() {
		double x = 0.0d;
		double y = 0.0d;
		for (final Point2D vertex : vertices) {
			x += vertex.getX();
			y += vertex.getY();
		}
		return new Point2D.Double(x / vertices.length, y / vertices.length);
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
		if (!(obj instanceof PolygonGraphNode)) {
			return false;
		}
		final PolygonGraphNode other = (PolygonGraphNode) obj;
		if (!Arrays.equals(vertices, other.vertices)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(vertices);
	}
}
