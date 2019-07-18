package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;

import net.thomas.kata.geometry.objects.PolygonTriangle.TriangleSide;

public class PortalGraphNode {

	private final Point2D center;
	private final PortalGraphNode[] neighbours;

	public PortalGraphNode(PolygonTriangle triangle) {
		center = triangle.calculateCenter();
		neighbours = new PortalGraphNode[TriangleSide.values().length];
	}

	public void setNeighbour(TriangleSide side, PortalGraphNode node) {
		neighbours[side.ordinal()] = node;
	}

	public PortalGraphNode getNeighbour(TriangleSide side) {
		return neighbours[side.ordinal()];
	}

	public Point2D getCenter() {
		return center;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + center.hashCode();
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
		if (!(obj instanceof PortalGraphNode)) {
			return false;
		}
		final PortalGraphNode other = (PortalGraphNode) obj;
		if (!center.equals(other.center)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return center.toString();
	}
}
