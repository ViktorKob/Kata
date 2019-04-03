package net.thomas.kata.sorting;

import static java.util.Collections.emptySet;

import org.junit.Test;

import net.thomas.kata.exceptions.UnsupportedMethodException;

public class InsertionSortedArrayListUnitTest {

	@Test(expected = UnsupportedMethodException.class)
	public void shouldRejectAddAllAtIndex() {
		final InsertionSortedArrayList<Object> list = new InsertionSortedArrayList<>();
		list.addAll(0, emptySet());
	}
}
