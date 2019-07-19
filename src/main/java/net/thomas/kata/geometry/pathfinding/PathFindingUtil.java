package net.thomas.kata.geometry.pathfinding;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;
import net.thomas.kata.geometry.pathfinding.objects.Triangle;

public class PathFindingUtil {
	private final Map<Triangle, Collection<PortalGraphNode>> triangles2Portals;

	public PathFindingUtil(Map<Triangle, Collection<PortalGraphNode>> triangles2Portals) {
		this.triangles2Portals = triangles2Portals;
	}

	public GeneralPath buildPath(Point2D location, Point2D destination) {
		return null;
	}

	public static class Builder {
		private final Map<Triangle, Collection<PortalGraphNode>> triangles2Portals;

		public Builder() {
			triangles2Portals = new HashMap<>();
		}

		public Builder addTriangleWithNodes(Triangle triangle, Collection<PortalGraphNode> nodes) {
			triangles2Portals.put(triangle, new HashSet<>(nodes));
			return this;
		}

		public PathFindingUtil build() {
			return new PathFindingUtil(triangles2Portals);
		}
	}

	/***
	 * Only here to be able to render it. Should not be exposed in final version.
	 */
	@Deprecated
	public Map<Triangle, Collection<PortalGraphNode>> getTriangle2PortalNodeMap() {
		return triangles2Portals;
	}
}