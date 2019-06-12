package net.thomas.kata.data_structures.tree;

import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.BREADTH_FIRST;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.IN_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.OUT_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.POST_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.PRE_ORDER;
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
		final List<Integer> contents = tree.getContents(IN_ORDER);
		assertEquals(1, (int) contents.get(0));
		assertEquals(2, (int) contents.get(1));
		assertEquals(3, (int) contents.get(2));
	}

	@Test
	public void shouldContainValuesPreOrder() {
		tree.insert(2).insert(1).insert(3);
		final List<Integer> contents = tree.getContents(PRE_ORDER);
		assertEquals(2, (int) contents.get(0));
		assertEquals(1, (int) contents.get(1));
		assertEquals(3, (int) contents.get(2));
	}

	@Test
	public void shouldContainValuesOutOrder() {
		tree.insert(2).insert(1).insert(3);
		final List<Integer> contents = tree.getContents(OUT_ORDER);
		assertEquals(3, (int) contents.get(0));
		assertEquals(2, (int) contents.get(1));
		assertEquals(1, (int) contents.get(2));
	}

	@Test
	public void shouldContainValuesPostOrder() {
		tree.insert(2).insert(1).insert(3);
		final List<Integer> contents = tree.getContents(POST_ORDER);
		assertEquals(1, (int) contents.get(0));
		assertEquals(3, (int) contents.get(1));
		assertEquals(2, (int) contents.get(2));
	}

	@Test
	public void shouldContainValuesBreathFirst() {
		tree.insert(2).insert(1).insert(3);
		final List<Integer> contents = tree.getContents(BREADTH_FIRST);
		assertEquals(2, (int) contents.get(0));
		assertEquals(1, (int) contents.get(1));
		assertEquals(3, (int) contents.get(2));
	}
}