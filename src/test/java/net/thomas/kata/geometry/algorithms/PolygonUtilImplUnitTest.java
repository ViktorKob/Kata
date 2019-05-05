package net.thomas.kata.geometry.algorithms;

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
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(polygon).iterator();
		assertEquals(polygon, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}

	@Test
	public void shouldDivideMergeToMerge() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(4, 0);
		final PolygonVertex vertex3 = new PolygonVertex(4, 2);
		final PolygonVertex vertex4 = new PolygonVertex(3, 1);
		final PolygonVertex vertex5 = new PolygonVertex(2, 2);
		final PolygonVertex vertex6 = new PolygonVertex(1, 1);
		final PolygonVertex vertex7 = new PolygonVertex(0, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6, vertex7).build();
		final Iterator<PolygonVertex> monotoneParts = util.getMonotoneParts(polygon).iterator();
		assertEquals(vertex4, monotoneParts.next());
		assertEquals(vertex4, monotoneParts.next());
		assertEquals(vertex2, monotoneParts.next());
		assertFalse(monotoneParts.hasNext());
	}
}