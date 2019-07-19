package net.thomas.kata.geometry.objects;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class PortalGraphNodeV2 implements Iterable<PortalGraphNodeV2> {

	private final Portal portal;
	private final Set<PortalGraphNodeV2> neighbours;

	public PortalGraphNodeV2(Portal portal) {
		this.portal = portal;
		neighbours = new HashSet<>();
	}

	public void addNeighbour(PortalGraphNodeV2 node) {
		neighbours.add(node);
	}

	public Portal getPortal() {
		return portal;
	}

	@Override
	public Iterator<PortalGraphNodeV2> iterator() {
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
		if (!(obj instanceof PortalGraphNodeV2)) {
			return false;
		}
		final PortalGraphNodeV2 other = (PortalGraphNodeV2) obj;
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
