package net.thomas.kata.geometry.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.junit.Test;

public class PolygonVertexUnitTest {
	private static final double EPSILON = 0.000001d;

	@Test
	public void shouldHaveValues() {
		final PolygonVertex vertex = new PolygonVertex(1, 1);
		assertEquals(1.0d, vertex.x, EPSILON);
		assertEquals(1.0d, vertex.y, EPSILON);
	}

	@Test
	public void shouldHaveSelfAsBefore() {
		final PolygonVertex vertex = new PolygonVertex(1, 1);
		assertSame(vertex, vertex.getBefore());
	}

	@Test
	public void shouldHaveSelfAsAfter() {
		final PolygonVertex vertex = new PolygonVertex(1, 1);
		assertSame(vertex, vertex.getAfter());
	}

	@Test
	public void shouldHaveOtherVertexAsBefore() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		vertex1.insertAfter(vertex2);
		assertSame(vertex2, vertex1.getBefore());
	}

	@Test
	public void shouldHaveOtherVertexAsAfter() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		vertex1.insertAfter(vertex2);
		assertSame(vertex2, vertex1.getAfter());
	}

	@Test
	public void shouldHaveSelfAsAsBeforeBefore() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		vertex1.insertAfter(vertex2);
		assertSame(vertex1, vertex1.getBefore().getBefore());
	}

	@Test
	public void shouldHaveSelfAsAsAfterAfter() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		vertex1.insertAfter(vertex2);
		assertSame(vertex1, vertex1.getAfter().getAfter());
	}

	@Test
	public void shouldIterateThroughBothVerticesOnce() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		vertex1.insertAfter(vertex2);
		final Iterator<PolygonVertex> iterator = vertex1.iterator();
		assertSame(vertex1, iterator.next());
		assertSame(vertex2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void shouldGenerateCorrectSweepline() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 3);
		final PolygonVertex vertex2 = new PolygonVertex(-1, 2);
		final PolygonVertex vertex3 = new PolygonVertex(1, 2);
		final PolygonVertex vertex4 = new PolygonVertex(1, 1);
		vertex1.insertAfter(vertex2);
		vertex2.insertAfter(vertex3);
		vertex3.insertAfter(vertex4);
		final Iterator<PolygonVertex> sweepline = vertex1.buildSweepline().iterator();
		assertSame(vertex1, sweepline.next());
		assertSame(vertex2, sweepline.next());
		assertSame(vertex3, sweepline.next());
		assertSame(vertex4, sweepline.next());
		assertFalse(sweepline.hasNext());
	}
}