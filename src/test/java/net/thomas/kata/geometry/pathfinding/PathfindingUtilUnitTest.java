package net.thomas.kata.geometry.pathfinding;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static net.thomas.kata.geometry.pathfinding.PathfindingUtil.OptimizationTechnique.NONE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.geometry.pathfinding.objects.Path;
import net.thomas.kata.geometry.pathfinding.objects.Portal;
import net.thomas.kata.geometry.pathfinding.objects.PortalGraphNode;
import net.thomas.kata.geometry.pathfinding.objects.Triangle;

public class PathfindingUtilUnitTest {

	private static final Triangle SOME_TRIANGLE = new Triangle(POINT(0.0, 0.0), POINT(5.0, 0.0), POINT(0.0, 5.0));
	private static final Triangle SOME_TRIANGLE_ONE_STEP_AWAY = new Triangle(POINT(5.0, 0.0), POINT(5.0, 5.0), POINT(0.0, 5.0));
	private static final Triangle SOME_TRIANGLE_TWO_STEPS_AWAY = new Triangle(POINT(5.0, 0.0), POINT(10.0, 5.0), POINT(5.0, 5.0));
	private static final Triangle SOME_OTHER_TRIANGLE_TWO_STEPS_AWAY = new Triangle(POINT(0.0, 5.0), POINT(5.0, 5.0), POINT(5.0, 10.0));
	private static final Triangle SOME_TRIANGLE_IN_A_DIFFERENT_POLYGON = new Triangle(POINT(-5.0, -5.0), POINT(-10.0, -10.0), POINT(-5.0, -10.0));

	private static final Portal PORTAL_1 = new Portal(SOME_TRIANGLE.getP2(), SOME_TRIANGLE.getP3());
	private static final Portal PORTAL_2 = new Portal(SOME_TRIANGLE_ONE_STEP_AWAY.getP1(), SOME_TRIANGLE_ONE_STEP_AWAY.getP2());
	private static final Portal PORTAL_3 = new Portal(SOME_TRIANGLE_ONE_STEP_AWAY.getP2(), SOME_TRIANGLE_ONE_STEP_AWAY.getP3());

	private static final PortalGraphNode FIRST_NODE = new PortalGraphNode(PORTAL_1);
	private static final PortalGraphNode SECOND_NODE = new PortalGraphNode(PORTAL_2);
	private static final PortalGraphNode THIRD_NODE = new PortalGraphNode(PORTAL_3);

	private static final Point2D SOME_POINT_IN_FIRST_TRIANGLE = POINT(1.0, 1.0);
	private static final Point2D SOME_OTHER_POINT_IN_FIRST_TRIANGLE = POINT(2.0, 1.0);
	private static final Point2D SOME_POINT_IN_SECOND_TRIANGLE = POINT(4.0, 4.0);
	private static final Point2D SOME_POINT_IN_THIRD_TRIANGLE = POINT(6.0, 4.0);
	private static final Point2D SOME_POINT_IN_ANOTHER_POLYGON_TRIANGLE = POINT(-6.0, -7.0);
	private static final Point2D SOME_POINT_OUTSIDE_ALL_TRIANGLES = POINT(20.0, 20.0);

	static {
		FIRST_NODE.addNeighbour(SECOND_NODE);
		FIRST_NODE.addNeighbour(THIRD_NODE);
		SECOND_NODE.addNeighbour(FIRST_NODE);
		SECOND_NODE.addNeighbour(THIRD_NODE);
		THIRD_NODE.addNeighbour(FIRST_NODE);
		THIRD_NODE.addNeighbour(SECOND_NODE);
	}

	private static Point2D POINT(double x, double y) {
		return new Point2D.Double(x, y);
	}

	private Map<Triangle, Collection<PortalGraphNode>> triangles2PortalsMock;
	private PathfindingUtil util;

	@Before
	public void setUp() {
		triangles2PortalsMock = mock(TriangleMap.class);
		when(triangles2PortalsMock.keySet()).thenReturn(
				new HashSet<>(asList(SOME_TRIANGLE, SOME_TRIANGLE_ONE_STEP_AWAY, SOME_TRIANGLE_TWO_STEPS_AWAY, SOME_TRIANGLE_IN_A_DIFFERENT_POLYGON)));
		when(triangles2PortalsMock.get(SOME_TRIANGLE)).thenReturn(singleton(FIRST_NODE));
		when(triangles2PortalsMock.get(SOME_TRIANGLE_ONE_STEP_AWAY)).thenReturn(asList(FIRST_NODE, SECOND_NODE, THIRD_NODE));
		when(triangles2PortalsMock.get(SOME_TRIANGLE_TWO_STEPS_AWAY)).thenReturn(singleton(SECOND_NODE));
		when(triangles2PortalsMock.get(SOME_OTHER_TRIANGLE_TWO_STEPS_AWAY)).thenReturn(singleton(THIRD_NODE));
		when(triangles2PortalsMock.get(SOME_TRIANGLE_IN_A_DIFFERENT_POLYGON)).thenReturn(emptySet());
		util = new PathfindingUtil(triangles2PortalsMock);
	}

	@Test
	public void shouldBuildPathWithinTriangle() {
		final Path path = util.buildPath(SOME_POINT_IN_FIRST_TRIANGLE, SOME_OTHER_POINT_IN_FIRST_TRIANGLE, NONE);
		assertEquals(SOME_POINT_IN_FIRST_TRIANGLE, path.origin);
		assertEquals(SOME_OTHER_POINT_IN_FIRST_TRIANGLE, path.destination);
		assertTrue(path.route.isEmpty());
	}

	@Test
	public void shouldBuildSingleStepPathThroughFirstPortal() {
		final Path path = util.buildPath(SOME_POINT_IN_FIRST_TRIANGLE, SOME_POINT_IN_SECOND_TRIANGLE, NONE);
		assertEquals(SOME_POINT_IN_FIRST_TRIANGLE, path.origin);
		assertEquals(SOME_POINT_IN_SECOND_TRIANGLE, path.destination);
		assertEquals(1, path.route.size());
		assertEquals(PORTAL_1, path.route.get(0));
	}

	@Test
	public void shouldBuildTwoStepPathThroughFirstAndSecondPortal() {
		final Path path = util.buildPath(SOME_POINT_IN_FIRST_TRIANGLE, SOME_POINT_IN_THIRD_TRIANGLE, NONE);
		assertEquals(SOME_POINT_IN_FIRST_TRIANGLE, path.origin);
		assertEquals(SOME_POINT_IN_THIRD_TRIANGLE, path.destination);
		assertEquals(2, path.route.size());
		assertEquals(PORTAL_1, path.route.get(0));
		assertEquals(PORTAL_2, path.route.get(1));
	}

	@Test
	public void shouldBeStableWhenCommingFromPointOutsidePolygon() {
		final Path path = util.buildPath(SOME_POINT_OUTSIDE_ALL_TRIANGLES, SOME_POINT_IN_FIRST_TRIANGLE, NONE);
		assertNull(path);
	}

	@Test
	public void shouldBeStableWhenAimingForPointOutsidePolygon() {
		final Path path = util.buildPath(SOME_POINT_IN_FIRST_TRIANGLE, SOME_POINT_OUTSIDE_ALL_TRIANGLES, NONE);
		assertNull(path);
	}

	@Test
	public void shouldBeStableWhenCommingFromPointInAnotherPolygon() {
		final Path path = util.buildPath(SOME_POINT_IN_ANOTHER_POLYGON_TRIANGLE, SOME_POINT_IN_FIRST_TRIANGLE, NONE);
		assertNull(path);
	}

	@Test
	public void shouldBeStableWhenAimingForPointInAnotherPolygon() {
		final Path path = util.buildPath(SOME_POINT_IN_FIRST_TRIANGLE, SOME_POINT_IN_ANOTHER_POLYGON_TRIANGLE, NONE);
		assertNull(path);
	}

	abstract class TriangleMap implements Map<Triangle, Collection<PortalGraphNode>> {
	}
}
