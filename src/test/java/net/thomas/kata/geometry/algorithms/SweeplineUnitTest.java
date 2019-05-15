package net.thomas.kata.geometry.algorithms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Test;

import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class SweeplineUnitTest {
	@Test
	public void shouldKeepOrderForRegularCase() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 2);
		final PolygonVertex vertex2 = new PolygonVertex(0, 1);
		final PolygonVertex polygon = new PolygonBuilder().add(vertex1, vertex2).build();
		final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().add(polygon).build().iterator();
		assertEquals(vertex1, sweepline.next());
		assertEquals(vertex2, sweepline.next());
		assertFalse(sweepline.hasNext());
	}

	@Test
	public void shouldReorderForRegularCase() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 2);
		final PolygonVertex vertex2 = new PolygonVertex(0, 1);
		final PolygonVertex polygon = new PolygonBuilder().add(vertex2, vertex1).build();
		final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().add(polygon).build().iterator();
		assertEquals(vertex1, sweepline.next());
		assertEquals(vertex2, sweepline.next());
		assertFalse(sweepline.hasNext());
	}

	@Test
	public void shouldKeepOrderForCoplanarCase() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(1, 1);
		final PolygonVertex polygon = new PolygonBuilder().add(vertex1, vertex2).build();
		final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().add(polygon).build().iterator();
		assertEquals(vertex1, sweepline.next());
		assertEquals(vertex2, sweepline.next());
		assertFalse(sweepline.hasNext());
	}

	@Test
	public void shouldReorderForCoplanarCase() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(1, 1);
		final PolygonVertex polygon = new PolygonBuilder().add(vertex2, vertex1).build();
		final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().add(polygon).build().iterator();
		assertEquals(vertex1, sweepline.next());
		assertEquals(vertex2, sweepline.next());
		assertFalse(sweepline.hasNext());
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
		final PolygonVertex polygon = new PolygonBuilder().add(vertex4, vertex7, vertex6, vertex8, vertex5, vertex2, vertex3, vertex1).build();
		final Iterator<PolygonVertex> sweepline = new SweeplineBuilder().add(polygon).build().iterator();
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
