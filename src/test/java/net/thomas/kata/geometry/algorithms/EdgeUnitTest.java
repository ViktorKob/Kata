package net.thomas.kata.geometry.algorithms;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import net.thomas.kata.geometry.objects.PolygonVertex;

public class EdgeUnitTest {
	@Test
	public void shouldBeLeftOfEdge() {
		final Edge edge = new Edge(new PolygonVertex(-1, -1), new PolygonVertex(1, 1));
		assertTrue(edge.isLeftOf(new PolygonVertex(0, 1)));
	}

	@Test
	public void shouldBeLeftOfReverseEdge() {
		final Edge edge = new Edge(new PolygonVertex(1, 1), new PolygonVertex(-1, -1));
		assertTrue(edge.isLeftOf(new PolygonVertex(-1, 0)));
	}

	@Test
	public void shouldNotBeLeftOfEdge() {
		final Edge edge = new Edge(new PolygonVertex(-1, -1), new PolygonVertex(1, 1));
		assertFalse(edge.isLeftOf(new PolygonVertex(1, 0)));
	}

	@Test
	public void shouldNotBeLeftOfReverseEdge() {
		final Edge edge = new Edge(new PolygonVertex(1, 1), new PolygonVertex(-1, -1));
		assertFalse(edge.isLeftOf(new PolygonVertex(1, 0)));
	}

	@Test
	public void shouldBeLeftOfDegenerateEdge() {
		final Edge edge = new Edge(new PolygonVertex(1, -1), new PolygonVertex(1, 1));
		assertTrue(edge.isLeftOf(new PolygonVertex(0, -1)));
	}

	@Test
	public void shouldNotBeLeftOfDegenerateEdge() {
		final Edge edge = new Edge(new PolygonVertex(1, -1), new PolygonVertex(1, 1));
		assertFalse(edge.isLeftOf(new PolygonVertex(2, 0)));
	}
}