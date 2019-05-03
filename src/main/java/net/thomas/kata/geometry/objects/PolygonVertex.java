package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * Clockwise representation of a polygon
 */
public class PolygonVertex extends Point2D.Double implements Iterable<PolygonVertex> {
	private static final long serialVersionUID = 1L;

	private PolygonVertex before;
	private PolygonVertex after;

	public PolygonVertex(double x, double y) {
		super(x, y);
		before = this;
		after = this;
	}

	public PolygonVertex(PolygonVertex vertex) {
		this(vertex.x, vertex.y);
	}

	public void insertAfter(PolygonVertex vertex) {
		after.before = vertex;
		vertex.after = after;
		vertex.before = this;
		after = vertex;
	}

	public PolygonVertex getBefore() {
		return before;
	}

	public PolygonVertex getAfter() {
		return after;
	}

	public List<PolygonVertex> buildSweepline() {
		final List<PolygonVertex> sweepline = copyVertices();
		sortVertices(sweepline);
		return sweepline;
	}

	private List<PolygonVertex> copyVertices() {
		final List<PolygonVertex> sweepline = new ArrayList<>();
		final PolygonVertex root = createClone();
		PolygonVertex current = root;
		do {
			sweepline.add(current);
			current = current.getAfter();
		} while (current != root);
		return sweepline;
	}

	private PolygonVertex createClone() {
		PolygonVertex currentOriginal = this, currentCopy = null;
		PolygonVertex root = null;
		do {
			if (currentCopy == null) {
				currentCopy = root = new PolygonVertex(currentOriginal);
			} else {
				currentCopy.insertAfter(new PolygonVertex(currentOriginal));
				currentCopy = currentCopy.getAfter();
			}
			currentOriginal = currentOriginal.getAfter();
		} while (currentOriginal != this);
		return root;
	}

	private void sortVertices(final List<PolygonVertex> sweepline) {
		sweepline.sort((left, right) -> {
			final int difference = java.lang.Double.compare(left.y, right.y);
			if (difference == 0) {
				return java.lang.Double.compare(left.x, right.x);
			} else {
				return -difference;
			}
		});
	}

	public String allToString() {
		final StringBuilder polygon = new StringBuilder("Polygon: ");
		PolygonVertex current = this;
		do {
			polygon.append("[" + current.x + ", " + current.y + "], ");
			current = current.after;
		} while (current != this);
		polygon.deleteCharAt(polygon.length() - 1);
		polygon.deleteCharAt(polygon.length() - 1);
		return polygon.toString();
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!super.equals(obj)) {
			return false;
		}
		return true;
	}

	@Override
	public Iterator<PolygonVertex> iterator() {
		return new PolygonVertexIterator(this);
	}

	class PolygonVertexIterator implements Iterator<PolygonVertex> {
		private final PolygonVertex root;
		private PolygonVertex current;
		private boolean started;

		public PolygonVertexIterator(PolygonVertex root) {
			this.root = current = root;
			started = false;
		}

		@Override
		public boolean hasNext() {
			return !started || current != root;
		}

		@Override
		public PolygonVertex next() {
			started = true;
			final PolygonVertex vertex = current;
			current = current.getAfter();
			return vertex;
		}
	}
}