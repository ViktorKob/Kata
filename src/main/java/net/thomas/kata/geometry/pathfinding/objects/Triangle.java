package net.thomas.kata.geometry.pathfinding.objects;

import static java.util.Collections.emptySet;

import java.awt.geom.Point2D;
import java.util.Collection;

public class Triangle {
	private final Point2D p1;
	private final Point2D p2;
	private final Point2D p3;
	private Collection<PortalGraphNode> portalNodes;

	public Triangle(Point2D p1, Point2D p2, Point2D p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		portalNodes = emptySet();
	}

	public void setPortalNodes(Collection<PortalGraphNode> portalNodes) {
		this.portalNodes = portalNodes;
	}
}
