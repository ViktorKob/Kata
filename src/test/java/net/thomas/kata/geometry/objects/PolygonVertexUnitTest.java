package net.thomas.kata.geometry.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class PolygonVertexUnitTest {
	private static final double EPSILON = 0.000001d;
	private PolygonBuilder builder;

	@Before
	public void setUp() {
		builder = new PolygonBuilder();
	}

	@Test
	public void shouldHaveValues() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		assertEquals(1.0d, vertex.x, EPSILON);
		assertEquals(1.0d, vertex.y, EPSILON);
	}

	@Test
	public void shouldHaveSelfAsBefore() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		assertSame(vertex, vertex.getBefore());
	}

	@Test
	public void shouldHaveSelfAsAfter() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		assertSame(vertex, vertex.getAfter());
	}

	@Test
	public void shouldBeIdenticalWhenCopied() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		final PolygonVertex copy = new PolygonVertex(vertex);
		assertEquals(vertex, copy);
	}

	@Test
	public void shouldHaveSelfAsBeforeWhenCopied() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		final PolygonVertex copy = new PolygonVertex(vertex);
		assertSame(copy, copy.getBefore());
	}

	@Test
	public void shouldHaveSelfAsAfterWhenCopied() {
		final PolygonVertex vertex = builder.add(1, 1).build();
		final PolygonVertex copy = new PolygonVertex(vertex);
		assertSame(copy, copy.getAfter());
	}

	@Test
	public void shouldHaveOtherVertexAsBefore() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2).build();
		assertSame(vertex2, polygon.getBefore());
	}

	@Test
	public void shouldHaveOtherVertexAsAfter() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2).build();
		assertSame(vertex2, polygon.getAfter());
	}

	@Test
	public void shouldHaveSelfAsAsBeforeBefore() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2).build();
		assertSame(vertex1, polygon.getBefore().getBefore());
	}

	@Test
	public void shouldHaveSelfAsAsAfterAfter() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2).build();
		assertSame(vertex1, polygon.getAfter().getAfter());
	}

	@Test
	public void shouldIterateThroughBothVerticesOnce() {
		final PolygonVertex vertex1 = new PolygonVertex(1, 1);
		final PolygonVertex vertex2 = new PolygonVertex(2, 2);
		final PolygonVertex polygon = builder.add(vertex1, vertex2).build();
		final Iterator<PolygonVertex> iterator = polygon.iterator();
		assertSame(vertex1, iterator.next());
		assertSame(vertex2, iterator.next());
		assertFalse(iterator.hasNext());
	}

	@Test
	public void shouldCutNewPolygonOutOfOriginal() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(1, 1);
		final PolygonVertex vertex4 = new PolygonVertex(0, 1);
		final PolygonVertex polygonVertex1 = builder.add(vertex1, vertex2, vertex3, vertex4).build();
		polygonVertex1.cutIntoTwoPolygons(vertex3);
		final Iterator<PolygonVertex> newPolygon = vertex1.iterator();
		assertEquals(vertex1, newPolygon.next());
		assertEquals(vertex3, newPolygon.next());
		assertEquals(vertex4, newPolygon.next());
		assertFalse(newPolygon.hasNext());
	}

	@Test
	public void shouldBuildNewPolygonIntoFromOriginal() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(1, 1);
		final PolygonVertex vertex4 = new PolygonVertex(0, 1);
		final PolygonVertex polygonVertex1 = builder.add(vertex1, vertex2, vertex3, vertex4).build();
		final Iterator<PolygonVertex> newPolygon = polygonVertex1.cutIntoTwoPolygons(vertex3).iterator();
		assertEquals(vertex1, newPolygon.next());
		assertEquals(vertex2, newPolygon.next());
		assertEquals(vertex3, newPolygon.next());
		assertFalse(newPolygon.hasNext());
	}

	@Test
	public void shouldRememberOriginalSourceVertex() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(1, 1);
		final PolygonVertex vertex4 = new PolygonVertex(0, 1);
		final PolygonVertex polygonVertex1 = builder.add(vertex1, vertex2, vertex3, vertex4).build();
		final PolygonVertex clonedVertex = polygonVertex1.cutIntoTwoPolygons(vertex3);
		assertSame(vertex1, clonedVertex.getTwins().get(0));
	}

	@Test
	public void shouldRememberOriginalTargetVertex() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 0);
		final PolygonVertex vertex2 = new PolygonVertex(1, 0);
		final PolygonVertex vertex3 = new PolygonVertex(1, 1);
		final PolygonVertex vertex4 = new PolygonVertex(0, 1);
		final PolygonVertex polygonVertex1 = builder.add(vertex1, vertex2, vertex3, vertex4).build();
		final PolygonVertex clonedVertex = polygonVertex1.cutIntoTwoPolygons(vertex3);
		assertSame(vertex3, clonedVertex.getBefore().getTwins().get(0));
	}
}