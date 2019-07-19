package net.thomas.kata.geometry.pathfinding.objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PortalGraphNode implements Iterable<PortalGraphNode> {

	private final Portal portal;
	private final Set<PortalGraphNode> neighbours;

	public PortalGraphNode(Portal portal) {
		this.portal = portal;
		neighbours = new HashSet<>();
	}

	public void addNeighbour(PortalGraphNode node) {
		neighbours.add(node);
	}

	public Portal getPortal() {
		return portal;
	}

	@Override
	public Iterator<PortalGraphNode> iterator() {
		return neighbours.iterator();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + portal.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PortalGraphNode)) {
			return false;
		}
		final PortalGraphNode other = (PortalGraphNode) obj;
		if (!portal.equals(other.portal)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return portal.toString();
	}
}
