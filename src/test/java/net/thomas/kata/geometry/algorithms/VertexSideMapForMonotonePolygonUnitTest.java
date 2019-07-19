package net.thomas.kata.geometry.algorithms;

import static net.thomas.kata.geometry.algorithms.VertexSide.BOTTOM;
import static net.thomas.kata.geometry.algorithms.VertexSide.LEFT;
import static net.thomas.kata.geometry.algorithms.VertexSide.RIGHT;
import static net.thomas.kata.geometry.algorithms.VertexSide.TOP;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.thomas.kata.geometry.objects.PolygonBuilder;
import net.thomas.kata.geometry.objects.PolygonVertex;

public class VertexSideMapForMonotonePolygonUnitTest {
	@Test
	public void shouldPreserveTopOfLine() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(0, 0);
		new PolygonBuilder().add(vertex1, vertex2).build();
		final VertexSideMapForMonotonePolygon sides = new VertexSideMapForMonotonePolygon(vertex1);
		assertEquals(TOP, sides.getSide(vertex1));
	}

	@Test
	public void shouldDetermineBottomOfLine() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(0, 0);
		new PolygonBuilder().add(vertex1, vertex2).build();
		final VertexSideMapForMonotonePolygon sides = new VertexSideMapForMonotonePolygon(vertex1);
		assertEquals(vertex2, sides.getBottom());
		assertEquals(BOTTOM, sides.getSide(vertex2));
	}

	@Test
	public void shouldBeOnTheLeftSide() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(-1, .5);
		final PolygonVertex vertex3 = new PolygonVertex(0, 0);
		new PolygonBuilder().add(vertex1, vertex2, vertex3).build();
		final VertexSideMapForMonotonePolygon sides = new VertexSideMapForMonotonePolygon(vertex1);
		assertEquals(LEFT, sides.getSide(vertex2));
	}

	@Test
	public void shouldBeOnTheRightSide() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(0, 0);
		final PolygonVertex vertex3 = new PolygonVertex(1, .5);
		new PolygonBuilder().add(vertex1, vertex2, vertex3).build();
		final VertexSideMapForMonotonePolygon sides = new VertexSideMapForMonotonePolygon(vertex1);
		assertEquals(RIGHT, sides.getSide(vertex3));
	}

	@Test
	public void shouldHaveTwoVerticesOnEachSide() {
		final PolygonVertex vertex1 = new PolygonVertex(0, 1);
		final PolygonVertex vertex2 = new PolygonVertex(-1, .75);
		final PolygonVertex vertex3 = new PolygonVertex(-1, .25);
		final PolygonVertex vertex4 = new PolygonVertex(0, 0);
		final PolygonVertex vertex5 = new PolygonVertex(1, .25);
		final PolygonVertex vertex6 = new PolygonVertex(1, .75);
		new PolygonBuilder().add(vertex1, vertex2, vertex3, vertex4, vertex5, vertex6).build();
		final VertexSideMapForMonotonePolygon sides = new VertexSideMapForMonotonePolygon(vertex1);
		assertEquals(TOP, sides.getSide(vertex1));
		assertEquals(LEFT, sides.getSide(vertex2));
		assertEquals(LEFT, sides.getSide(vertex3));
		assertEquals(BOTTOM, sides.getSide(vertex4));
		assertEquals(RIGHT, sides.getSide(vertex5));
		assertEquals(RIGHT, sides.getSide(vertex6));
	}
}