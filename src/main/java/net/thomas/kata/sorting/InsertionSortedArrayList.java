package net.thomas.kata.sorting;

import java.util.ArrayList;
import java.util.Collection;

public class InsertionSortedArrayList<CONTENT_TYPE extends Comparable<CONTENT_TYPE>> extends ArrayList<CONTENT_TYPE> {
	private static final long serialVersionUID = 1L;

	@Override
	public boolean add(CONTENT_TYPE element) {
		for (int i = 0; i < size(); i++) {
			if (get(i).compareTo(element) > 0) {
				super.add(i, element);
				return true;
			}
		}
		super.add(element);
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends CONTENT_TYPE> elements) {
		for (final CONTENT_TYPE element : elements) {
			add(element);
		}
		return true;
	}

	@Override
	public void add(int index, CONTENT_TYPE element) {
		throw new UnsupportedOperationException("add at index is not supported for sorted array list");
	}

	@Override
	public boolean addAll(int index, Collection<? extends CONTENT_TYPE> elements) {
		throw new UnsupportedOperationException("addAll at index is not supported for sorted array list");
	}

	@Override
	public CONTENT_TYPE set(int index, CONTENT_TYPE element) {
		throw new UnsupportedOperationException("set element at index is not supported for sorted array list");
	}
}