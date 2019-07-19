package net.thomas.kata.geometry.objects;

import static java.util.Collections.unmodifiableCollection;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PortalGraphNode {

	private final Point2D center;
	private final Set<PortalGraphEdge> edges;

	public PortalGraphNode(PolygonTriangle triangle) {
		center = triangle.calculateCenter();
		edges = new HashSet<>();
	}

	public void addEdge(PortalGraphEdge edge) {
		edges.add(edge);
	}

	public Collection<PortalGraphEdge> getEdges() {
		return unmodifiableCollection(edges);
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
