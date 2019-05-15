package net.thomas.kata.geometry.algorithms;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import net.thomas.kata.geometry.PolygonUtil;
import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class PolygonUtilImplUnitTest {
	private PolygonUtil util;
	private PolygonBuilder builder;

	@Before
	public void setUp() {
		util = new PolygonUtilImpl();
		builder = new PolygonBuilder();
	}

	@Test
	public void shouldNotChangeSimplePolygon() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 1);
		final PolygonVertex vertex3 = new PolygonVertex(0, 1);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(polygon, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldSurviveColinearCases() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(2, 0);
		final PolygonVertex vertex4 = new PolygonVertex(2, 1);
		final PolygonVertex vertex5 = new PolygonVertex(1, 1);
		final PolygonVertex vertex6 = new PolygonVertex(0, 1);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(vertex3, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldCleanupAngleCalculationCoplanarCases() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 1);
		final PolygonVertex vertex3 = new PolygonVertex(2, 2);
		final PolygonVertex vertex4 = new PolygonVertex(1, 3);
		final PolygonVertex vertex5 = new PolygonVertex(0, 4);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4, vertex5).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(polygon, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldHandleMergeCasesCorrectly() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(6, 0);
		final PolygonVertex vertex3 = new PolygonVertex(6, 4);
		final PolygonVertex vertex4 = new PolygonVertex(5, 3);
		final PolygonVertex vertex5 = new PolygonVertex(4, 4);
		final PolygonVertex vertex6 = new PolygonVertex(3, 2);
		final PolygonVertex vertex7 = new PolygonVertex(2, 3);
		final PolygonVertex vertex8 = new PolygonVertex(1, 2);
		final PolygonVertex vertex9 = new PolygonVertex(0, 4);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(vertex6, monotoneParts.next());
		assertEquals(vertex6, monotoneParts.next());
		assertEquals(vertex6, monotoneParts.next());
		assertEquals(vertex2, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldHandleRegularCasesCorrectly() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(4, 0);
		final PolygonVertex vertex3 = new PolygonVertex(4, 4);
		final PolygonVertex vertex4 = new PolygonVertex(4, 6);
		final PolygonVertex vertex5 = new PolygonVertex(3, 5);
		final PolygonVertex vertex6 = new PolygonVertex(2, 6);
		final PolygonVertex vertex7 = new PolygonVertex(1, 3);
		final PolygonVertex vertex8 = new PolygonVertex(0, 4);
		final PolygonVertex vertex9 = new PolygonVertex(0, 1);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7, vertex8, vertex9).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(vertex3, monotoneParts.next());
		assertEquals(vertex7, monotoneParts.next());
		assertEquals(vertex2, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldHandleSplitCaseCorrectly() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 1);
		final PolygonVertex vertex3 = new PolygonVertex(2, 0);
		final PolygonVertex vertex6 = new PolygonVertex(2, 2);
		final PolygonVertex vertex7 = new PolygonVertex(0, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex6, vertex7).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(vertex1, monotoneParts.next());
		assertEquals(vertex3, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldHandleEndCasesCorrectly() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 2);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(2, 2);
		final PolygonVertex vertex4 = new PolygonVertex(1, 1);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(asList(polygon)).iterator();
		assertEquals(vertex2, monotoneParts.next());
		assertEquals(vertex2, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}
}