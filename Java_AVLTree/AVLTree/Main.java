import java.util.Scanner;

// contains the main method as the interface of the program
public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Creating object of AVLTree 
        AVLTree tree = new AVLTree();

        System.out.println("AVLTree Implementation\n");

        // Perform tree operations
        while (true) {
            System.out.print("\n-----------------------\n");
            System.out.println("AVLTree Operations\n");
            System.out.println("1. Insert ");
            System.out.println("2. Delete");
            System.out.println("3. Search");
            System.out.println("4. Display AVL Tree");
            System.out.println("5. Clear tree");
            System.out.println("6. Exit");
            System.out.print("-----------------------\n");

            System.out.println("Enter your choice:  ");
            scan.nextLine();
            while (!scan.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                scan.nextLine();
            }
            int choice = scan.nextInt();
            switch (choice) {

                case 1:
                    System.out.println("Enter integer element to insert");
                    scan.nextLine();
                    while (!scan.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        scan.nextLine();
                    }
                    tree.insert(scan.nextInt());
                    break;
                case 2:
                    System.out.println("Enter integer element to delete");
                    scan.nextLine();
                    while (!scan.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        scan.nextLine();
                    }
                    tree.delete(scan.nextInt());
                    break;
                case 3:
                    System.out.println("Enter integer element to search");
                    scan.nextLine();
                    while (!scan.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        scan.nextLine();
                    }
                    System.out.println("\nSearch result : " + tree.search(scan.nextInt()));
                    break;
                case 4:
                    System.out.print("\nPre order : ");
                    tree.preorder();
                    System.out.print("\nIn order : ");
                    tree.inorder();
                    System.out.print("\nPost order : ");
                    tree.postorder();
                    System.out.print("\n\nAVL Tree Visualisation: \n\n");
                    tree.printTree();
                    break;
                case 5:
                    System.out.println("\nTree Cleared");
                    tree.makeEmpty();
                    break;
                case 6:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;

                default:
                    System.out.println("Invalid input. Please try again \n ");
                    break;
            }
        }
    }
}
