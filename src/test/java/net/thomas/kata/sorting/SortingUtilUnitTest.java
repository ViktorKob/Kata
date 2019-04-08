package net.thomas.kata.sorting;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import net.thomas.kata.sorting.SortingUtil.SortingMethod;

@RunWith(Parameterized.class)
public class SortingUtilUnitTest {
	private static final long SEED = 1234l;

	@Parameters
	public static Collection<SortingMethod> methods() {
		return asList(SortingMethod.values());
	}

	private final SortingMethod method;

	public SortingUtilUnitTest(SortingMethod method) {
		this.method = method;
	}

	@Test
	public void shouldSurviveEmptyList() {
		final Collection<Integer> elements = emptyList();
		final List<Integer> sortedElements = SortingUtil.sort(method, elements);
		assertEquals(emptyList(), sortedElements);
	}

	@Test
	public void shouldSurviveSingletonList() {
		final Collection<Integer> elements = singleton(1);
		final List<Integer> sortedElements = SortingUtil.sort(method, elements);
		assertEquals(singletonList(1), sortedElements);
	}

	@Test
	public void shouldSortSimplePreSortedList() {
		final Collection<Integer> elements = asList(1, 2, 3, 4);
		final List<Integer> sortedElements = SortingUtil.sort(method, elements);
		assertEquals(asList(1, 2, 3, 4), sortedElements);
	}

	@Test
	public void shouldSortSimpleList() {
		final Collection<Integer> elements = asList(2, 1, 4, 3);
		final List<Integer> sortedElements = SortingUtil.sort(method, elements);
		assertEquals(asList(1, 2, 3, 4), sortedElements);
	}

	@Test
	public void shouldSortComplexList() {
		final Collection<Integer> elements = createUnsortedElements(50000);
		final List<Integer> sortedElements = SortingUtil.sort(method, elements);
		assertTrue(elementsAreSorted(sortedElements));
	}

	private Collection<Integer> createUnsortedElements(int count) {
		final Random random = new Random(SEED);
		final Collection<Integer> elements = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			elements.add(random.nextInt());
		}
		return elements;
	}

	private boolean elementsAreSorted(List<Integer> sortedElements) {
		for (int i = 0; i < sortedElements.size() - 1; i++) {
			if (sortedElements.get(i) < sortedElements.get(i++)) {
				return false;
			}
		}
		return true;
	}
}