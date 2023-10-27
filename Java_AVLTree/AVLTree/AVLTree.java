// represents the AVL tree itself
public class AVLTree {
    private Node root;

    public AVLTree() {
        root = null;
    }

    public void makeEmpty() {
        root = null;
    }

    public void insert(int data) {
        root = insert(data, root);
    }

    public void delete(int key) {
        root = delete(key, root);
    }

    private int height(Node node) {
        return node == null ? -1 : node.height;
    }

    private int max(int lhs, int rhs) {
        return lhs > rhs ? lhs : rhs;
    }

    // Function to insert a node 
    private Node insert(int data, Node node) {
        if (node == null) {
            node = new Node(data);
        } else if (data <= node.data) {
            node.left = insert(data, node.left);
        } else if (data > node.data) {
            node.right = insert(data, node.right);
        } else {
            return node;
        }
        node.height = max(height(node.left), height(node.right)) + 1;
        return rebalance(node);
    }

    // Function to delete a node 
    private Node delete(int key, Node node) {
        if (node == null) {
            return node;
        }
        if (key < node.data) {
            node.left = delete(key, node.left);
        } else if (key > node.data) {
            node.right = delete(key, node.right);
        } else {
            if (node.left == null || node.right == null) {
                node = (node.left == null) ? node.right : node.left;
            } else {
                Node mostLeftChild = mostLeftChild(node.right); //get the most left child of the right sub-tree
                node.data = mostLeftChild.data;
                node.right = delete(node.data, node.right);
            }
        }
        if (node != null) {
            node.height = max(height(node.left), height(node.right)) + 1;
            node = rebalance(node);
        }
        return node;
    }

    // Method to get the most left child of a node
    private Node mostLeftChild(Node node) {
        Node current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }

    // Function to reblance the tree 
    private Node rebalance(Node node) {
        int balance = height(node.left) - height(node.right);

        if (balance > 1) {
            if (height(node.left.right) > height(node.left.left)) {
                node.left = rotateWithRightChild(node.left);
            }
            return rotateWithLeftChild(node);
        } else if (balance < -1) {
            if (height(node.right.left) > height(node.right.right)) {
                node.right = rotateWithLeftChild(node.right);
            }
            return rotateWithRightChild(node);
        }
        return node;
    }

    // rotate with left child
    private Node rotateWithLeftChild(Node k2) {
        Node k1 = k2.left;
        k2.left = k1.right;
        k1.right = k2;
        k2.height = max(height(k2.left), height(k2.right)) + 1;
        k1.height = max(height(k1.left), k2.height) + 1;
        return k1;
    }

    // rotate with right child
    private Node rotateWithRightChild(Node k1) {
        Node k2 = k1.right;
        k1.right = k2.left;
        k2.left = k1;
        k1.height = max(height(k1.left), height(k1.right)) + 1;
        k2.height = max(height(k2.right), k1.height) + 1;
        return k2;
    }


    // Function to search for an element 
    public boolean search(int val) {
        return search(root, val);
    }

    // search logic
    private boolean search(Node node, int val) {
        boolean found = false;
        while ((node != null) && !found) {
            int nodeval = node.data;
            if (val < nodeval)
                node = node.left;
            else if (val > nodeval)
                node = node.right;
            else {
                found = true;
                break;
            }
            found = search(node, val);
        }
        return found;
    }

    // Function for inorder traversal 
    public void inorder() {
        inorder(root);
    }

    // inorder traversal  logic
    private void inorder(Node node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.data + " ");
            inorder(node.right);
        }
    }

    // Function for preorder traversal
    public void preorder() {
        preorder(root);
    }

    // preorder traversal
    private void preorder(Node node) {
        if (node != null) {
            System.out.print(node.data + " ");
            preorder(node.left);
            preorder(node.right);
        }
    }

    // Function for postorder traversal
    public void postorder() {
        postorder(root);
    }

    // postorder traversal
    private void postorder(Node node) {
        if (node != null) {
            postorder(node.left);
            postorder(node.right);
            System.out.print(node.data + " ");
        }
    }

    private void printTree(Node node, String indent, boolean isTail) {
        if (node != null) {
            if (node.right != null) {
                printTree(node.right, indent + (isTail ? "│   " : "    "), false);
            }
            System.out.print(indent + (isTail ? "└── " : "┌── ") + node.data + "\n");
            if (node.left != null) {
                printTree(node.left, indent + (isTail ? "    " : "│   "), true);
            }
        }
    }

    public void printTree() {
        printTree(root, "", true);
    }

}