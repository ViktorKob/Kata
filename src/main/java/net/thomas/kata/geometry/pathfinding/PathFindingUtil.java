package net.thomas.kata.geometry.pathfinding;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.emptySet;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;
import net.thomas.kata.geometry.pathfinding.objects.Triangle;

public class PathFindingUtil {
	private final Map<Triangle, Collection<PortalGraphNode>> triangles2Portals;
	private final DirtyIntervals intervalsX;
	private final DirtyIntervals intervalsY;

	public PathFindingUtil(Map<Triangle, Collection<PortalGraphNode>> triangles2Portals) {
		this.triangles2Portals = triangles2Portals;
		intervalsX = buildSegmentTree(triangles2Portals.keySet(), Point2D::getX);
		intervalsY = buildSegmentTree(triangles2Portals.keySet(), Point2D::getY);
	}

	private DirtyIntervals buildSegmentTree(Set<Triangle> keySet, Function<Point2D, Double> fetchFunction) {
		final List<TrianglePosition> positions = new LinkedList<>();
		for (final Triangle triangle : keySet) {
			positions.add(new TrianglePosition(triangle, fetchFunction, true));
			positions.add(new TrianglePosition(triangle, fetchFunction, false));
		}
		positions.sort(null);
		return new DirtyIntervals(positions);
	}

	public GeneralPath buildPath(Point2D location, Point2D destination) {
		final Triangle startTriangle = lookupTriangleAt(location);
		final Triangle endTriangle = lookupTriangleAt(destination);
		System.out.println(startTriangle);
		System.out.println(endTriangle);
		return null;
	}

	private Triangle lookupTriangleAt(Point2D point) {
		final Set<Triangle> trianglesAtX = intervalsX.activeTrianglesAt(point.getX());
		final Set<Triangle> trianglesAtY = intervalsY.activeTrianglesAt(point.getY());
		final HashSet<Triangle> candidates = new HashSet<>(trianglesAtX);
		candidates.retainAll(trianglesAtY);
		for (final Triangle triangle : candidates) {
			if (triangle.contains(point)) {
				return triangle;
			}
		}
		return null;
	}

	/***
	 * Only here to be able to render it. Should not be exposed in final version.
	 */
	@Deprecated
	public Map<Triangle, Collection<PortalGraphNode>> getTriangle2PortalNodeMap() {
		return triangles2Portals;
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
}

class TrianglePosition implements Comparable<TrianglePosition> {
	public final Triangle triangle;
	public final double position;
	public final boolean isStartPosition;

	public TrianglePosition(Triangle triangle, Function<Point2D, Double> fetchFunction, boolean isStartPosition) {
		this.triangle = triangle;
		this.isStartPosition = isStartPosition;
		final double value1 = fetchFunction.apply(triangle.getP1());
		final double value2 = fetchFunction.apply(triangle.getP2());
		final double value3 = fetchFunction.apply(triangle.getP3());
		if (isStartPosition) {
			position = min(value1, min(value2, value3));
		} else {
			position = max(value1, max(value2, value3));
		}
	}

	@Override
	public int compareTo(TrianglePosition other) {
		return Double.compare(position, other.position);
	}

	@Override
	public String toString() {
		return position + ": " + triangle;
	}

}

/***
 * Quick and dirty interval "registry" to enable location to triangle lookups. Should be replaced by
 * segment- or interval tree.
 */
class DirtyIntervals {
	private final List<ActiveTriangles> intervals;

	public DirtyIntervals(List<TrianglePosition> decisionPoints) {
		final Set<Triangle> activeTriangles = new HashSet<>();
		intervals = new LinkedList<>();
		for (final TrianglePosition decisionPoint : decisionPoints) {
			if (decisionPoint.isStartPosition) {
				activeTriangles.add(decisionPoint.triangle);
				intervals.add(new ActiveTriangles(decisionPoint.position, new HashSet<>(activeTriangles)));
			} else {
				activeTriangles.remove(decisionPoint.triangle);
				intervals.add(new ActiveTriangles(decisionPoint.position, new HashSet<>(activeTriangles)));
			}
		}
	}

	public Set<Triangle> activeTrianglesAt(double position) {
		final Iterator<ActiveTriangles> iterator = intervals.iterator();
		ActiveTriangles correctInterval = null;
		ActiveTriangles nextInterval = null;
		do {
			correctInterval = nextInterval;
			nextInterval = iterator.next();
		} while (position >= nextInterval.startPosition);
		if (correctInterval != null) {
			return correctInterval.activeTriangles;
		} else {
			return emptySet();
		}
	}
}

class ActiveTriangles {
	public final double startPosition;
	public final Set<Triangle> activeTriangles;

	public ActiveTriangles(double startPosition, Set<Triangle> activeTriangles) {
		this.startPosition = startPosition;
		this.activeTriangles = activeTriangles;
	}
}