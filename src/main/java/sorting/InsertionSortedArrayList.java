package main.java.sorting;

import java.util.ArrayList;
import java.util.Collection;

public class InsertionSortedArrayList<CONTENT_TYPE> extends ArrayList<CONTENT_TYPE> {
	private static final long serialVersionUID = 1L;

	private final ArrayList<CONTENT_TYPE> contents;

	public InsertionSortedArrayList() {
		contents = new ArrayList<>();
	}

	@Override
	public boolean add(CONTENT_TYPE e) {
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends CONTENT_TYPE> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends CONTENT_TYPE> c) {
		throw new UnsupportedMethodException("addAll at index is not supported for sorted array list");
	}

	@Override
	public CONTENT_TYPE set(int index, CONTENT_TYPE element) {
		throw new UnsupportedMethodException("set value at index is not supported for sorted array list");
	}

	@Override
	public void add(int index, CONTENT_TYPE element) {
		throw new UnsupportedMethodException("add at index is not supported for sorted array list");
	}
}