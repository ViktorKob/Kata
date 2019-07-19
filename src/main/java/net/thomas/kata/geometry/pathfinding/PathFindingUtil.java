package net.thomas.kata.geometry.pathfinding;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Map;

import net.thomas.kata.geometry.objects.PolygonTriangle;
import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;

public class PathFindingUtil {
	private final Map<PolygonTriangle, Collection<PortalGraphNode>> triangles2Portals;

	public PathFindingUtil(Map<PolygonTriangle, Collection<PortalGraphNode>> triangles2Portals) {
		this.triangles2Portals = triangles2Portals;
	}

	public GeneralPath buildPath(Point2D location, Point2D destination) {
		return null;
	}
}
