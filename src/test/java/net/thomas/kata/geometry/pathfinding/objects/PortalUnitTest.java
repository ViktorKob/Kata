package net.thomas.kata.geometry.pathfinding.objects;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Line2D;
import java.awt.geom.Line2D.Double;
import java.awt.geom.Point2D;

import org.junit.Before;
import org.junit.Test;

public class PortalUnitTest {
	private static final double EPSILON = 0.01;
	private static final Point2D SOME_POINT = new Point2D.Double(0, 0);
	private static final Point2D SOME_OTHER_POINT = new Point2D.Double(2, 2);
	private static final Point2D MIDDLE_POINT = new Point2D.Double(1, 1);
	private static final Point2D SOME_LINE_SEGMENT_START_POINT = new Point2D.Double(-1, 3);
	private static final Point2D SOME_LINE_SEGMENT_END_POINT_THROUGH_MIDDLE = new Point2D.Double(3, -1);
	private static final Point2D SOME_LINE_SEGMENT_END_POINT_BELOW_SOME_POINT = new Point2D.Double(-1, -1);
	private static final Point2D SOME_LINE_SEGMENT_END_POINT_ABOVE_SOME_OTHER_POINT = new Point2D.Double(3, 3);
	private Portal defaultPortal;

	@Before
	public void setUp() {
		defaultPortal = new Portal(SOME_POINT, SOME_OTHER_POINT);
	}

	@Test
	public void shouldBeIdentical() {
		final Portal other = new Portal(SOME_POINT, SOME_OTHER_POINT);
		assertEquals(defaultPortal, other);
	}

	@Test
	public void shouldBeIdenticalWhenReversed() {
		final Portal other = new Portal(SOME_OTHER_POINT, SOME_POINT);
		assertEquals(defaultPortal, other);
	}

	@Test
	public void shouldHaveSameHashcodeWhenReversed() {
		final Portal other = new Portal(SOME_OTHER_POINT, SOME_POINT);
		assertEquals(defaultPortal.hashCode(), other.hashCode());
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsMiddlePoint() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_START_POINT, SOME_LINE_SEGMENT_END_POINT_THROUGH_MIDDLE);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(MIDDLE_POINT.getX(), point.getX(), EPSILON);
		assertEquals(MIDDLE_POINT.getY(), point.getY(), EPSILON);
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsSomePoint() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_START_POINT, SOME_LINE_SEGMENT_END_POINT_BELOW_SOME_POINT);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(SOME_POINT.getX(), point.getX(), EPSILON);
		assertEquals(SOME_POINT.getY(), point.getY(), EPSILON);
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsSomeOtherPoint() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_START_POINT, SOME_LINE_SEGMENT_END_POINT_ABOVE_SOME_OTHER_POINT);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(SOME_OTHER_POINT.getX(), point.getX(), EPSILON);
		assertEquals(SOME_OTHER_POINT.getY(), point.getY(), EPSILON);
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsMiddlePointWithReversedLine() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_END_POINT_THROUGH_MIDDLE, SOME_LINE_SEGMENT_START_POINT);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(MIDDLE_POINT.getX(), point.getX(), EPSILON);
		assertEquals(MIDDLE_POINT.getY(), point.getY(), EPSILON);
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsSomePointWithReversedLine() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_END_POINT_BELOW_SOME_POINT, SOME_LINE_SEGMENT_START_POINT);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(SOME_POINT.getX(), point.getX(), EPSILON);
		assertEquals(SOME_POINT.getY(), point.getY(), EPSILON);
	}

	@Test
	public void shouldDetermineBestIntersectionPointAsSomeOtherPointWithReversedLine() {
		final Line2D line = asLine(SOME_LINE_SEGMENT_END_POINT_ABOVE_SOME_OTHER_POINT, SOME_LINE_SEGMENT_START_POINT);
		final Point2D point = defaultPortal.getBestIntersectionPoint(line);
		assertEquals(SOME_OTHER_POINT.getX(), point.getX(), EPSILON);
		assertEquals(SOME_OTHER_POINT.getY(), point.getY(), EPSILON);
	}

	private Double asLine(Point2D start, Point2D end) {
		return new Line2D.Double(start, end);
	}
}
