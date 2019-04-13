package net.thomas.kata.sorting;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class InsertionSortedArrayListUnitTest {
	private static final Set<Integer> SOME_COLLECTION = emptySet();
	private static final Integer SOME_OBJECT = 0;
	private static final Integer SOME_SMALLER_OBJECT = -1;
	private static final Integer SOME_LARGER_OBJECT = 1;
	private InsertionSortedArrayList<Integer> listUnderTest;

	@Before
	public void setUpForTest() {
		listUnderTest = new InsertionSortedArrayList<>();
	}

	@Test
	public void shouldContainElementAfterInsertion() {
		listUnderTest.add(SOME_OBJECT);
		assertTrue(listUnderTest.contains(SOME_OBJECT));
	}

	@Test
	public void shouldContainElementsAfterInsertion() {
		listUnderTest.addAll(asList(SOME_OBJECT, SOME_LARGER_OBJECT));
		assertTrue(listUnderTest.contains(SOME_OBJECT));
		assertTrue(listUnderTest.contains(SOME_LARGER_OBJECT));
	}

	@Test
	public void shouldContainElementsInOrderAfterInsertion() {
		listUnderTest.add(SOME_OBJECT);
		listUnderTest.add(SOME_SMALLER_OBJECT);
		final Iterator<Integer> elements = listUnderTest.iterator();
		assertEquals(SOME_SMALLER_OBJECT, elements.next());
		assertEquals(SOME_OBJECT, elements.next());
	}

	@Test
	public void shouldContainElementsInOrderAfterInsertionFromCollection() {
		listUnderTest.addAll(asList(SOME_OBJECT, SOME_SMALLER_OBJECT, SOME_LARGER_OBJECT));
		final Iterator<Integer> elements = listUnderTest.iterator();
		assertEquals(SOME_SMALLER_OBJECT, elements.next());
		assertEquals(SOME_OBJECT, elements.next());
		assertEquals(SOME_LARGER_OBJECT, elements.next());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldRejectAddAtIndex() {
		listUnderTest.add(0, SOME_OBJECT);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldRejectAddAllAtIndex() {
		listUnderTest.addAll(0, SOME_COLLECTION);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldRejectSet() {
		listUnderTest.set(0, SOME_OBJECT);
	}
}
