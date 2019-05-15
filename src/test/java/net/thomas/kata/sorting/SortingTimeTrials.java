package net.thomas.kata.sorting;

import static java.lang.System.nanoTime;
import static net.thomas.kata.sorting.SortingUtil.SortingMethod.INSERTION_SORT;
import static net.thomas.kata.sorting.SortingUtil.SortingMethod.MERGE_SORT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class SortingTimeTrials {
	public static void main(String[] args) throws Exception {
		final Collection<Integer> elements = createUnsortedElements(100000);
		runOnUnsortedSamples(elements);
		runOnSortedSamples(elements);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void runOnUnsortedSamples(final Collection<Integer> elements) throws Exception {
		final List<Integer> firstCopyOfElements = new ArrayList(elements);
		final Collection<Integer> secondCopyOfElements = new ArrayList(elements);
		final Collection<Integer> thirdCopyOfElements = new ArrayList(elements);
		executeTest("Time spend on build in method:", () -> {
			Collections.sort(firstCopyOfElements);
			return firstCopyOfElements;
		});
		executeTest("Time spend on custom merge sort method:", () -> {
			return SortingUtil.sort(MERGE_SORT, secondCopyOfElements);
		});
		executeTest("Time spend on custom insertion sort method:", () -> {
			return SortingUtil.sort(INSERTION_SORT, thirdCopyOfElements);
		});
	}

	private static void runOnSortedSamples(final Collection<Integer> elements) throws Exception {
		final List<Integer> sortedElements = new ArrayList<>(elements);
		Collections.sort(sortedElements);
		executeTest("Time spend on build in method (presorted):", () -> {
			Collections.sort(sortedElements);
			return sortedElements;
		});
		executeTest("Time spend on custom merge sort method (presorted):", () -> {
			return SortingUtil.sort(MERGE_SORT, sortedElements);
		});
		executeTest("Time spend on custom insertion sort method (presorted):", () -> {
			return SortingUtil.sort(INSERTION_SORT, sortedElements);
		});
	}

	private static List<Integer> executeTest(String description, Callable<List<Integer>> test) throws Exception {
		final long stamp = nanoTime();
		final List<Integer> elements = test.call();
		System.out.println(description + " " + calculateTimeSpend(stamp));
		return elements;
	}

	private static String calculateTimeSpend(long stamp) {
		return (nanoTime() - stamp) / 10000 / 100.0d + " ms";
	}

	private static Collection<Integer> createUnsortedElements(int count) {
		final Random random = new Random(1234l);
		final Collection<Integer> elements = new LinkedList<>();
		for (int i = 0; i < count; i++) {
			elements.add(random.nextInt());
		}
		return elements;
	}
}