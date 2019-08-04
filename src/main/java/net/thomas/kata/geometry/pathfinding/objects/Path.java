package net.thomas.kata.geometry.pathfinding.objects;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Path {
	public final Point2D origin;
	public final Point2D destination;
	public final List<PortalStep> route;

	public Path(Point2D origin, Point2D destination) {
		this.origin = origin;
		this.destination = destination;
		route = new LinkedList<>();
	}

	public void addPortal(Portal portal) {
		route.add(new PortalStep(portal));
	}

	public static class PortalStep {
		public final Portal portal;
		public final Point2D waypoint;
		public final Point2D optimizedWaypoint;

		public PortalStep(Portal portal) {
			this.portal = portal;
			waypoint = portal.getCenter();
			optimizedWaypoint = portal.getCenter();
		}

		public boolean hasBeenOptimized() {
			return !waypoint.equals(optimizedWaypoint);
		}
	}
}
