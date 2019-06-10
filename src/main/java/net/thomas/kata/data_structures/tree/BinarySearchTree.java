package net.thomas.kata.data_structures.tree;

import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.IN_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.OUT_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.POST_ORDER;
import static net.thomas.kata.data_structures.tree.BinarySearchTree.TraversalMethod.PRE_ORDER;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/***
 * Basic unbalanced, stable search tree that works with any Comparable implementation.
 */
public class BinarySearchTree<TYPE extends Comparable<TYPE>> {
	private BinaryTreeNode<TYPE> root;
	private final Map<TraversalMethod, ValueExtractor> traversalMethods;

	public BinarySearchTree() {
		root = null;
		traversalMethods = new EnumMap<>(TraversalMethod.class);
		traversalMethods.put(PRE_ORDER, new PreOrderExtractor());
		traversalMethods.put(IN_ORDER, new InOrderExtractor());
		traversalMethods.put(POST_ORDER, new PostOrderExtractor());
		traversalMethods.put(OUT_ORDER, new OutOrderExtractor());
	}

	public BinarySearchTree<TYPE> insert(TYPE value) {
		final BinaryTreeNode<TYPE> newNode = new BinaryTreeNode<>(value);
		if (root == null) {
			root = newNode;
		} else {
			insertIntoSubtree(newNode, root);
		}
		return this;
	}

	private void insertIntoSubtree(BinaryTreeNode<TYPE> newNode, BinaryTreeNode<TYPE> currentNode) {
		if (newNode.compareTo(currentNode) < 0) {
			if (currentNode.getLeft() == null) {
				currentNode.setLeft(newNode);
			} else {
				insertIntoSubtree(newNode, currentNode.getLeft());
			}
		} else {
			if (currentNode.getRight() == null) {
				currentNode.setRight(newNode);
			} else {
				insertIntoSubtree(newNode, currentNode.getRight());
			}
		}
	}

	public List<TYPE> getContents() {
		return getContents(IN_ORDER);
	}

	public List<TYPE> getContents(TraversalMethod method) {
		if (traversalMethods.containsKey(method)) {
			return traversalMethods.get(method).extractValues(root);
		} else {
			throw new RuntimeException("Traversal Method not yet implemented: " + method);
		}
	}

	public boolean isEmpty() {
		return root == null;
	}

	public static enum TraversalMethod {
		PRE_ORDER,
		IN_ORDER,
		OUT_ORDER,
		POST_ORDER,
		BREADTH_FIRST
	}

	abstract class ValueExtractor {

		public final List<TYPE> extractValues(BinaryTreeNode<TYPE> node) {
			final List<TYPE> contents = new LinkedList<>();
			if (node != null) {
				_extractValues(node, contents);
			}
			return contents;
		}

		protected abstract void _extractValues(BinaryTreeNode<TYPE> node, List<TYPE> contents);
	}

	class PreOrderExtractor extends ValueExtractor {
		@Override
		public void _extractValues(BinaryTreeNode<TYPE> node, List<TYPE> contents) {
			contents.add(node.getContents());
			contents.addAll(extractValues(node.getLeft()));
			contents.addAll(extractValues(node.getRight()));
		}
	}

	class InOrderExtractor extends ValueExtractor {
		@Override
		public void _extractValues(BinaryTreeNode<TYPE> node, List<TYPE> contents) {
			contents.addAll(extractValues(node.getLeft()));
			contents.add(node.getContents());
			contents.addAll(extractValues(node.getRight()));
		}
	}

	class OutOrderExtractor extends ValueExtractor {
		@Override
		public void _extractValues(BinaryTreeNode<TYPE> node, List<TYPE> contents) {
			contents.addAll(extractValues(node.getRight()));
			contents.add(node.getContents());
			contents.addAll(extractValues(node.getLeft()));
		}
	}

	class PostOrderExtractor extends ValueExtractor {
		@Override
		public void _extractValues(BinaryTreeNode<TYPE> node, List<TYPE> contents) {
			contents.addAll(extractValues(node.getLeft()));
			contents.addAll(extractValues(node.getRight()));
			contents.add(node.getContents());
		}
	}
}

class BinaryTreeNode<TYPE extends Comparable<TYPE>> implements Comparable<BinaryTreeNode<TYPE>> {
	private final TYPE contents;
	private BinaryTreeNode<TYPE> left;
	private BinaryTreeNode<TYPE> right;

	public BinaryTreeNode(TYPE contents) {
		this.contents = contents;
		left = right = null;
	}

	public TYPE getContents() {
		return contents;
	}

	public BinaryTreeNode<TYPE> getLeft() {
		return left;
	}

	public void setLeft(BinaryTreeNode<TYPE> left) {
		this.left = left;
	}

	public BinaryTreeNode<TYPE> getRight() {
		return right;
	}

	public void setRight(BinaryTreeNode<TYPE> right) {
		this.right = right;
	}

	@Override
	public int compareTo(BinaryTreeNode<TYPE> other) {
		return contents.compareTo(other.contents);
	}
}