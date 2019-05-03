package net.thomas.kata.geometry.algorithms;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.geometry.objects.PolygonVertex;

public class StatusSearchTreeUnitTest {

	private StatusSearchTree searchTree;

	@Before
	public void setUp() {
		searchTree = new StatusSearchTree();
	}

	@Test
	public void shouldNotHaveAnyEdgesWhenEmpty() {
		final Edge result = searchTree.searchLeft(new PolygonVertex(0, 0));
		assertNull(result);
	}

	@Test
	public void shouldNotReturnEdgeRightOfVertex() {
		searchTree.insert(new Edge(new PolygonVertex(0, 0), new PolygonVertex(1, 1)));
		final Edge result = searchTree.searchLeft(new PolygonVertex(0, 1));
		assertNull(result);
	}

	@Test
	public void shouldReturnEdgeLeftOfVertex() {
		final Edge expectedEdge = new Edge(new PolygonVertex(0, 0), new PolygonVertex(1, 1));
		searchTree.insert(expectedEdge);
		final Edge result = searchTree.searchLeft(new PolygonVertex(1, 0));
		assertSame(expectedEdge, result);
	}

	@Test
	public void shouldReturnLeftmostEdge() {
		final Edge expectedEdge = new Edge(new PolygonVertex(0, 0), new PolygonVertex(1, 1));
		searchTree.insert(new Edge(new PolygonVertex(2, 0), new PolygonVertex(3, 1)));
		searchTree.insert(expectedEdge);
		final Edge result = searchTree.searchLeft(new PolygonVertex(1, 0));
		assertSame(expectedEdge, result);
	}

	@Test
	public void shouldReturnRightmostEdge() {
		final Edge expectedEdge = new Edge(new PolygonVertex(2, 0), new PolygonVertex(3, 1));
		searchTree.insert(new Edge(new PolygonVertex(0, 0), new PolygonVertex(1, 1)));
		searchTree.insert(expectedEdge);
		final Edge result = searchTree.searchLeft(new PolygonVertex(3, 0));
		assertSame(expectedEdge, result);
	}
}
