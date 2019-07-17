package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/***
 * Counterclockwise representation of a polygon. In clockwise order, it should be treated as a hole
 * in another polygon instead.
 */
public class PolygonVertex extends Point2D.Double implements Iterable<PolygonVertex> {
	private static final long serialVersionUID = 1L;

	private PolygonVertex before;
	private PolygonVertex after;
	private final List<PolygonVertex> twins;

	public PolygonVertex(double x, double y) {
		super(x, y);
		before = this;
		after = this;
		twins = new LinkedList<>();
	}

	public PolygonVertex(PolygonVertex vertex) {
		this(vertex.x, vertex.y);
		twins.addAll(vertex.twins);
	}

	public void setTwin(PolygonVertex twin) {
		twins.add(twin);
		twin.twins.add(this);
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

	public List<PolygonVertex> getTwins() {
		return twins;
	}

	public PolygonVertex createClone() {
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

	public PolygonVertex cutIntoTwoPolygons(PolygonVertex targetVertex) {
		final PolygonVertex nextInNewPolygon = getAfter();
		final PolygonVertex secondLastInNewPolygon = targetVertex.getBefore();
		final PolygonVertex thisClone = new PolygonVertex(this);
		final PolygonVertex targetClone = new PolygonVertex(targetVertex);
		thisClone.setTwin(this);
		targetClone.setTwin(targetVertex);
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