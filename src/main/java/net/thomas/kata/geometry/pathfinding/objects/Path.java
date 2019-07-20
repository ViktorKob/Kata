package net.thomas.kata.geometry.pathfinding.objects;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Path {
	public final Point2D origin;
	public final Point2D destination;
	public final List<Portal> route;

	public Path(Point2D origin, Point2D destination) {
		this.origin = origin;
		this.destination = destination;
		route = new LinkedList<>();
	}

	public void addPortal(Portal portal) {
		route.add(portal);
	}
}
