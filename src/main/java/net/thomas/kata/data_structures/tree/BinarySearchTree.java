package net.thomas.kata.data_structures.tree;

import static java.util.Collections.emptyList;

import java.util.LinkedList;
import java.util.List;

/***
 * Basic unbalanced, stable search tree that works with any Comparable implementation.
 */
public class BinarySearchTree<TYPE extends Comparable<TYPE>> {
	private BinaryTreeNode<TYPE> root;

	public BinarySearchTree() {
		root = null;
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
		return getContents(root);
	}

	private List<TYPE> getContents(BinaryTreeNode<TYPE> currentNode) {
		if (currentNode == null) {
			return emptyList();
		} else {
			final List<TYPE> contents = new LinkedList<>();
			contents.addAll(getContents(currentNode.getLeft()));
			contents.add(currentNode.getContents());
			contents.addAll(getContents(currentNode.getRight()));
			return contents;
		}
	}

	public boolean isEmpty() {
		return root == null;
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