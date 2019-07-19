package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;

public class PortalGraphEdge {

	private final PortalGraphNode source;
	private final PortalGraphNode destination;
	private final Portal portal;

	public PortalGraphEdge(PortalGraphNode source, PortalGraphNode destination, Point2D portalBoundaryLeft, Point2D portalBoundaryRight) {
		this.source = source;
		this.destination = destination;
		portal = new Portal(portalBoundaryLeft, portalBoundaryRight);
	}

	public PortalGraphNode getSource() {
		return source;
	}

	public PortalGraphNode getDestination() {
		return destination;
	}

	public Portal getPortal() {
		return portal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (destination == null ? 0 : destination.hashCode());
		result = prime * result + (source == null ? 0 : source.hashCode());
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
		if (!(obj instanceof PortalGraphEdge)) {
			return false;
		}
		final PortalGraphEdge other = (PortalGraphEdge) obj;
		if (destination == null) {
			if (other.destination != null) {
				return false;
			}
		} else if (!destination.equals(other.destination)) {
			return false;
		}
		if (source == null) {
			if (other.source != null) {
				return false;
			}
		} else if (!source.equals(other.source)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return source.toString() + " <-> " + destination;
	}
}
