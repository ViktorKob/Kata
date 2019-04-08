package net.thomas.kata.sorting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.thomas.kata.exceptions.UnsupportedMethodException;

public class SortingUtil {
	enum SortingMethod {
		INSERTION_SORT,
		MERGE_SORT
	}

	public static <TYPE extends Comparable<TYPE>> List<TYPE> sort(SortingMethod method, Collection<TYPE> elements) {
		switch (method) {
			case INSERTION_SORT:
			return insertionSort(new ArrayList<>(elements));
			case MERGE_SORT:
			return mergeSort(new ArrayList<>(elements), 0, elements.size());
			default:
			throw new UnsupportedMethodException("Method " + method.name() + " has not been implemented yet");
		}
	}

	private static <TYPE extends Comparable<TYPE>> List<TYPE> insertionSort(List<TYPE> elements) {
		final ArrayList<TYPE> sortedElements = new ArrayList<>();
		elementLoop: for (final TYPE element : elements) {
			for (int elementIndex = sortedElements.size() - 1; elementIndex >= 0; elementIndex--) {
				if (element.compareTo(sortedElements.get(elementIndex)) > 0) {
					sortedElements.add(elementIndex, element);
					continue elementLoop;
				}
			}
			sortedElements.add(element);
		}
		return sortedElements;
	}

	private static <TYPE extends Comparable<TYPE>> List<TYPE> mergeSort(List<TYPE> elements, int offset, int limit) {
		final int size = limit - offset;
		if (size > 1) {
			final int middleIndex = offset + size / 2;
			mergeSort(elements, offset, middleIndex);
			mergeSort(elements, middleIndex, limit);
			elements = sortSubArrays(elements, offset, middleIndex, limit);
		}
		return elements;
	}

	private static <TYPE extends Comparable<TYPE>> List<TYPE> sortSubArrays(List<TYPE> elements, int offset, int middleIndex, int limit) {
		int leftIndex = offset;
		int rightIndex = middleIndex;
		while (leftIndex < limit - 1 && leftIndex < rightIndex && rightIndex < limit) {
			if (elements.get(leftIndex).compareTo(elements.get(rightIndex)) > 0) {
				swapLeftAndRight(elements, leftIndex, rightIndex);
				rightIndex++;
			}
			leftIndex++;
		}
		return elements;
	}

	private static <TYPE extends Comparable<TYPE>> void swapLeftAndRight(List<TYPE> elements, int leftIndex, int rightIndex) {
		final TYPE swappedElement = elements.get(leftIndex);
		elements.set(leftIndex, elements.get(rightIndex));
		elements.set(rightIndex, swappedElement);
	}
}