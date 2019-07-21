package net.thomas.kata.geometry.pathfinding.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;

import java.util.Iterator;

import org.junit.Test;

public class PortalGraphNodeUnitTest {

	private static final Portal SOME_PORTAL = mock(Portal.class);
	private static final PortalGraphNode SOME_NODE = mock(PortalGraphNode.class);

	@Test
	public void shouldEnableIterationOfNeighbours() {
		final PortalGraphNode node = new PortalGraphNode(SOME_PORTAL);
		node.addNeighbour(SOME_NODE);
		final Iterator<PortalGraphNode> iterator = node.iterator();
		assertEquals(SOME_NODE, iterator.next());
	}

	@Test
	public void shouldContainOnlyUniqueNeighbours() {
		final PortalGraphNode node = new PortalGraphNode(SOME_PORTAL);
		node.addNeighbour(SOME_NODE);
		node.addNeighbour(SOME_NODE);
		final Iterator<PortalGraphNode> iterator = node.iterator();
		iterator.next();
		assertFalse(iterator.hasNext());
	}
}
