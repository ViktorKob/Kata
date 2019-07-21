package net.thomas.kata.geometry.pathfinding;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Collections.emptySet;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import net.thomas.kata.geometry.pathfinding.PathfindingUtil.TrianglePosition;
import net.thomas.kata.geometry.pathfinding.objects.Path;
import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;
import net.thomas.kata.geometry.pathfinding.objects.Triangle;

public class PathfindingUtil {
	private final Map<Triangle, Collection<PortalGraphNode>> triangles2Portals;
	private final DirtyIntervals intervalsX;
	private final DirtyIntervals intervalsY;

	public PathfindingUtil(Map<Triangle, Collection<PortalGraphNode>> triangles2Portals) {
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

	public Path buildPath(Point2D location, Point2D destination) {
		return new PathfinderAlgorithm(location, destination).findPath();
	}

	class PathfinderAlgorithm {
		private final Point2D origin;
		private final Point2D destination;
		private final Triangle startTriangle;
		private final Triangle endTriangle;

		public PathfinderAlgorithm(Point2D origin, Point2D destination) {
			this.origin = origin;
			this.destination = destination;
			startTriangle = lookupTriangleAt(origin);
			endTriangle = lookupTriangleAt(destination);
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

		public Path findPath() {
			if (startTriangle != null && endTriangle != null) {
				if (startTriangle.equals(endTriangle)) {
					return new Path(origin, destination);
				} else {
					return traverseGraphToDetermineBestPath();
				}
			} else {
				return null;
			}
		}

		private Path traverseGraphToDetermineBestPath() {
			final Set<PortalGraphNode> visitedNodes = new HashSet<>();
			final PriorityQueue<Step> candidateSteps = prepareInitialCandidates(triangles2Portals.get(startTriangle), visitedNodes);
			final Collection<PortalGraphNode> endNodes = triangles2Portals.get(endTriangle);
			final Step finalStep = determinePath(candidateSteps, visitedNodes, endNodes);
			if (finalStep != null) {
				final Stack<Step> stepsInOrder = reverseOrderOfSteps(finalStep);
				return buildPathShape(stepsInOrder);
			} else {
				return null;
			}
		}

		private PriorityQueue<Step> prepareInitialCandidates(final Collection<PortalGraphNode> startNodes, Set<PortalGraphNode> visitedNodes) {
			final PriorityQueue<Step> candidateSteps = new PriorityQueue<>();
			for (final PortalGraphNode node : startNodes) {
				visitedNodes.add(node);
				candidateSteps.add(buildStep(null, node));
			}
			return candidateSteps;
		}

		private Step determinePath(final PriorityQueue<Step> candidateSteps, Set<PortalGraphNode> visitedNodes, final Collection<PortalGraphNode> endNodes) {
			Step finalStep = null;
			while (!candidateSteps.isEmpty()) {
				final Step currentStep = candidateSteps.poll();
				System.out.println(currentStep.squaredDistanceTravelled + " -> " + currentStep.estimatedSquaredDistanceRemaining);
				if (endNodes.contains(currentStep.node)) {
					finalStep = currentStep;
					break;
				} else {
					for (final PortalGraphNode neighbour : currentStep.node) {
						if (!visitedNodes.contains(neighbour)) {
							visitedNodes.add(neighbour);
							candidateSteps.add(buildStep(currentStep, neighbour));
						}
					}
				}
			}
			return finalStep;
		}

		private Stack<Step> reverseOrderOfSteps(final Step finalStep) {
			Step current = finalStep;
			final Stack<Step> stepsInOrder = new Stack<>();
			while (current != null) {
				stepsInOrder.push(current);
				current = current.previousStep;
			}
			return stepsInOrder;
		}

		private Path buildPathShape(final Stack<Step> stepsInOrder) {
			final Path path = new Path(origin, destination);
			while (!stepsInOrder.isEmpty()) {
				final Step step = stepsInOrder.pop();
				path.addPortal(step.node.getPortal());
			}
			return path;
		}

		private Step buildStep(Step previousStep, PortalGraphNode node) {
			double distanceTravelled = 0.0;
			if (previousStep != null) {
				distanceTravelled += previousStep.squaredDistanceTravelled;
				// final Point2D closestPoint = node.getPortal().calculatePointClosestTo(destination);
				distanceTravelled += node.getPortal().getCenter().distanceSq(previousStep.node.getCenterOfPortal());
			} else {
				distanceTravelled += node.getPortal().getCenter().distanceSq(origin);
			}
			final double remainingDistance = node.getCenterOfPortal().distanceSq(destination);
			return new Step(distanceTravelled, remainingDistance, node, previousStep);
		}

		class Step implements Comparable<Step> {
			public final double squaredDistanceTravelled;
			public final double estimatedSquaredDistanceRemaining;
			public final PortalGraphNode node;
			public final Step previousStep;

			public Step(double squaredDistanceTravelled, double estimatedSquaredDistanceRemaining, PortalGraphNode node, Step previousStep) {
				this.squaredDistanceTravelled = squaredDistanceTravelled;
				this.estimatedSquaredDistanceRemaining = estimatedSquaredDistanceRemaining;
				this.node = node;
				this.previousStep = previousStep;
			}

			public double getPriceOfRoute() {
				return squaredDistanceTravelled + estimatedSquaredDistanceRemaining;
			}

			@Override
			public int compareTo(Step other) {
				return Double.compare(getPriceOfRoute(), other.getPriceOfRoute());
			}

			@Override
			public String toString() {
				return getPriceOfRoute() + " to go through " + node;
			}
		}
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

		public PathfindingUtil build() {
			return new PathfindingUtil(triangles2Portals);
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