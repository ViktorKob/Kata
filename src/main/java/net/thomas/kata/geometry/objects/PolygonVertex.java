package net.thomas.kata.geometry.objects;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.PriorityQueue;

public class PolygonVertex extends Point2D.Double implements Iterable<PolygonVertex> {
	private static final long serialVersionUID = 1L;

	private PolygonVertex before;
	private PolygonVertex after;

	public PolygonVertex(double x, double y) {
		super(x, y);
		before = this;
		after = this;
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

	public PriorityQueue<PolygonVertex> buildSweepline() {
		final PriorityQueue<PolygonVertex> queue = createSweeplineQueue();
		PolygonVertex current = this;
		do {
			queue.add(current);
			current = current.getAfter();
		} while (current != this);
		return queue;
	}

	private PriorityQueue<PolygonVertex> createSweeplineQueue() {
		final PriorityQueue<PolygonVertex> queue = new PriorityQueue<>((left, right) -> {
			System.out.println();
			System.out.println(left.x + ", " + left.y);
			System.out.println(right.x + ", " + right.y);
			if (left.y == right.y) {
				final int i = left.x < right.x ? -1 : 1;
				System.out.println("x: " + i);
				return i;
			} else {
				final int i = left.y > right.y ? -1 : 1;
				System.out.println("y: " + i);
				return i;
			}
		});
		return queue;
	}

	@Override
	public String toString() {
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