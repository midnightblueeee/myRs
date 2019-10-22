package sf;

public class Tree<k extends Comparable<? super k>, v> {
	Node<k, v> root;

	static class Node<k, v> {
		Node<k, v> left;
		Node<k, v> right;
		v value;
		k key;

		Node(k k, v v) {
			this.key = k;
			this.value = v;
		}
	}

	void put(Node<k, v> node, k key, v val) {
		int cp = node.key.compareTo(key);
		Node<k, v> newNode = new Node<k, v>(key, val);
		if (cp == 0)
			node.value = val;
		else if (cp < 0) {
			if (node.right == null)
				node.right = newNode;
			else
				put(node.right, key, val);
		} else {
			if (node.left == null)
				node.left = newNode;
			else put(node.left, key, val);
		}
	}

	void put(k key, v val) {
		if (root == null) {
			root = new Node<k, v>(key, val);
			return;
		}
		put(root, key, val);
	}

	void printTree(Node<k, v> node) {
		if (node == null)
			return;	
		printTree(node.left);
		System.out.println(node.value);
		printTree(node.right);

	}

	void printTree() {
		printTree(root);
	}
	public static void main(String[] args) {
		Tree<Integer,String> tree=new Tree<Integer,String>();
		tree.put(1, "1");
		tree.put(690, "690");
		tree.put(4, "4");
		tree.put(77, "77");
		tree.put(77, "77779");
		tree.put(2993, "2993");
		tree.put(111, "111");
		tree.printTree();
	}
}
