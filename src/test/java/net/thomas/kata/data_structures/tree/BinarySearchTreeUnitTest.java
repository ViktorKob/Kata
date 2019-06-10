package net.thomas.kata.data_structures.tree;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class BinarySearchTreeUnitTest {
	private BinarySearchTree<Integer> tree;

	@Before
	public void setUpEmptySearchTree() {
		tree = new BinarySearchTree<>();
	}

	@Test
	public void shouldBeEmpty() {
		assertTrue(tree.isEmpty());
	}

	@Test
	public void shouldContainValue() {
		tree.insert(1);
		assertEquals(1, (int) tree.getContents().get(0));
	}

	@Test
	public void shouldContainValuesInOrderWhenAlreadyOrdered() {
		tree.insert(1).insert(2).insert(3);
		final List<Integer> contents = tree.getContents();
		assertEquals(1, (int) contents.get(0));
		assertEquals(2, (int) contents.get(1));
		assertEquals(3, (int) contents.get(2));
	}

	@Test
	public void shouldContainValuesInOrderWhenNotOrdered() {
		tree.insert(3).insert(1).insert(2);
		final List<Integer> contents = tree.getContents();
		assertEquals(1, (int) contents.get(0));
		assertEquals(2, (int) contents.get(1));
		assertEquals(3, (int) contents.get(2));
	}
}