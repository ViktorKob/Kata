package net.thomas.kata.geometry.pathfinding.objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Point2D;

import org.junit.Test;

public class TriangleUnitTest {

	private static final Point2D SOME_POINT_1 = new Point2D.Double(0, 0);
	private static final Point2D SOME_POINT_2 = new Point2D.Double(10, 0);
	private static final Point2D SOME_POINT_3 = new Point2D.Double(0, 10);
	private static final Point2D SOME_POINT_INSIDE = new Point2D.Double(1, 1);
	private static final Point2D SOME_POINT_OUTSIDE = new Point2D.Double(10, 10);

	@Test
	public void shouldContainPoint() {
		final Triangle triangle = new Triangle(SOME_POINT_1, SOME_POINT_2, SOME_POINT_3);
		assertTrue(triangle.contains(SOME_POINT_INSIDE));
	}

	@Test
	public void shouldNotContainPoint() {
		final Triangle triangle = new Triangle(SOME_POINT_1, SOME_POINT_2, SOME_POINT_3);
		assertFalse(triangle.contains(SOME_POINT_OUTSIDE));
	}
}
