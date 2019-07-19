package net.thomas.kata.geometry.objects;

import static org.junit.Assert.assertEquals;

import java.awt.geom.Point2D;

import org.junit.Test;

public class PortalUnitTest {
	private static final Point2D SOME_POINT = new Point2D.Double(1, 2);
	private static final Point2D SOME_OTHER_POINT = new Point2D.Double(3, 4);

	@Test
	public void shouldBeIdentical() {
		final Portal left = new Portal(SOME_POINT, SOME_OTHER_POINT);
		final Portal right = new Portal(SOME_POINT, SOME_OTHER_POINT);
		assertEquals(left, right);
	}

	@Test
	public void shouldBeIdenticalWhenReversed() {
		final Portal left = new Portal(SOME_POINT, SOME_OTHER_POINT);
		final Portal right = new Portal(SOME_OTHER_POINT, SOME_POINT);
		assertEquals(left, right);
	}

	@Test
	public void shouldHaveSameHashcodeWhenReversed() {
		final Portal left = new Portal(SOME_POINT, SOME_OTHER_POINT);
		final Portal right = new Portal(SOME_OTHER_POINT, SOME_POINT);
		assertEquals(left.hashCode(), right.hashCode());
	}
}
