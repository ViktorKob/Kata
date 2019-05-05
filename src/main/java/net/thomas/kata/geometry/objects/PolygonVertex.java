package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/***
 * Counterclockwise representation of a polygon
 */
public class PolygonVertex extends Point2D.Double implements Iterable<PolygonVertex> {
	private static final long serialVersionUID = 1L;

	private PolygonVertex before;
	private PolygonVertex after;
	private PolygonVertex twin;

	public PolygonVertex(double x, double y) {
		super(x, y);
		before = this;
		after = this;
	}

	public PolygonVertex(PolygonVertex vertex) {
		this(vertex.x, vertex.y);
		before = this;
		after = this;
		twin = vertex;
		vertex.twin = this;
	}

	public void insertAfter(PolygonVertex... vertices) {
		PolygonVertex current = this;
		for (final PolygonVertex vertex : vertices) {
			current.after.before = vertex;
			vertex.after = current.after;
			vertex.before = current;
			current.after = vertex;
			current = vertex;
		}
	}

	public void replaceBefore(PolygonVertex vertex) {
		before = vertex;
		vertex.after = this;
	}

	public void replaceAfter(PolygonVertex vertex) {
		after = vertex;
		vertex.before = this;
	}

	public PolygonVertex getBefore() {
		return before;
	}

	public PolygonVertex getAfter() {
		return after;
	}

	public PolygonVertex getTwin() {
		return twin;
	}

	public List<PolygonVertex> buildSweepline() {
		final List<PolygonVertex> sweepline = copyVertices();
		sortVertices(sweepline);
		return sweepline;
	}

	private List<PolygonVertex> copyVertices() {
		final List<PolygonVertex> sweepline = new ArrayList<>();
		final PolygonVertex clone = createClone();
		for (final PolygonVertex vertex : clone) {
			sweepline.add(vertex);
		}
		return sweepline;
	}

	private PolygonVertex createClone() {
		PolygonVertex currentCopy = null;
		PolygonVertex root = null;
		for (final PolygonVertex vertex : this) {
			if (currentCopy == null) {
				currentCopy = root = new PolygonVertex(vertex);
			} else {
				currentCopy.insertAfter(new PolygonVertex(vertex));
				currentCopy = currentCopy.getAfter();
			}
		}
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

	public PolygonVertex cutIntoTwoPolygons(PolygonVertex targetVertex) {
		final PolygonVertex nextInNewPolygon = getAfter();
		final PolygonVertex secondLastInNewPolygon = targetVertex.getBefore();
		final PolygonVertex thisClone = new PolygonVertex(this);
		final PolygonVertex targetClone = new PolygonVertex(targetVertex);
		after = targetVertex;
		targetVertex.before = this;
		thisClone.after = nextInNewPolygon;
		nextInNewPolygon.before = thisClone;
		targetClone.before = secondLastInNewPolygon;
		secondLastInNewPolygon.after = targetClone;
		thisClone.before = targetClone;
		targetClone.after = thisClone;
		return thisClone;
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