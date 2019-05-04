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
	public void shouldBeIdenticalWhenCopied() {
		final PolygonVertex vertex = new PolygonVertex(1, 1);
		final PolygonVertex copy = new PolygonVertex(vertex);
		assertEquals(vertex, copy);
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
		final PolygonVertex vertex1 = new PolygonVertex(-10, 10);
		final PolygonVertex vertex2 = new PolygonVertex(10, 10);
		final PolygonVertex vertex3 = new PolygonVertex(0, 1);
		final PolygonVertex vertex4 = new PolygonVertex(-9, 0);
		final PolygonVertex vertex5 = new PolygonVertex(9, 0);
		final PolygonVertex vertex6 = new PolygonVertex(0, -1);
		final PolygonVertex vertex7 = new PolygonVertex(-10, -10);
		final PolygonVertex vertex8 = new PolygonVertex(10, -10);
		vertex4.insertAfter(vertex7);
		vertex7.insertAfter(vertex6);
		vertex6.insertAfter(vertex8);
		vertex8.insertAfter(vertex5);
		vertex5.insertAfter(vertex2);
		vertex2.insertAfter(vertex3);
		vertex3.insertAfter(vertex1);
		final Iterator<PolygonVertex> sweepline = vertex7.buildSweepline().iterator();
		assertEquals(vertex1, sweepline.next());
		assertEquals(vertex2, sweepline.next());
		assertEquals(vertex3, sweepline.next());
		assertEquals(vertex4, sweepline.next());
		assertEquals(vertex5, sweepline.next());
		assertEquals(vertex6, sweepline.next());
		assertEquals(vertex7, sweepline.next());
		assertEquals(vertex8, sweepline.next());
		assertFalse(sweepline.hasNext());
	}
}