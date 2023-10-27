// represent a node in the AVL tree
public class Node {
    Node left, right; // references to the left and right child of the current node
    int data; // node Value
    int height; // node height in the tree

    public Node() {
        left = null;
        right = null;
        data = 0;
        height = 0;
    }

    public Node(int n) {
        left = null;
        right = null;
        data = n;
        height = 0;
    }
}
