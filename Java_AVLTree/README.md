# Data Structures and Algorithms: Implementation and Analysis of Self-Balancing Trees


<a name="_e6dsnimhvwe6"></a>**Table Of Contents**

[**1.0 Implementation**](#_5sr7krlb4r2)

[1.1 Self-Balancing Trees (AVL Tree)](#_sx1vn3qvbgx)

[Interface](#_9fp6vwoq33u1)

[Logic of Data Structure](#_y0iamc2fy5re)

[I. Classes](#_3xpbmq5bkn6k)

[II. AVL Tree Functions](#_8wzo2amizjtq)

[Input Accuracy of Example Data](#_ydtfwyxofrl)

[I. Scenario 1 (No duplicates)](#_70vf5l86pg6)

[II. Scenario 2 (With duplicates)](#_vchhcykokfmc)

[**2.0 Challenges**](#_csp8mxyalvwf)

[2.1  Self-Balancing Trees (AVL)	15](#_7atqcs522a33)



## <a name="_5sr7krlb4r2"></a>**1.0 Implementation**
This section displays the implementation of the Self-Balancing Tree (AVL) and Hashing (Open Addressing and Separate Chaining) programs created. 
## <a name="_sx1vn3qvbgx"></a>**2.1 Self-Balancing Trees (AVL Tree)**
### <a name="_9fp6vwoq33u1"></a>**Interface**


|Interface|Explanation|
| - | - |
|![](/Screenshot/001.png)|When the program is being executed, a menu is displayed for the user to input their desired operation.|
|![](/Screenshot/002.png)|<p>If the user chooses to insert a node, they can enter **'1'** to choose the insert operation then input the integer element they wish to add.</p><p></p><p>This process continues repeatedly until the user decides not to insert any more nodes.</p>|
|![](/Screenshot/003.png)|To display the AVL tree, the user needs to input  **'4'**. |
|![](/Screenshot/004.png)![](/Screenshot/005.png)|If the user wants to search for a specific node that has been previously input, the program will print **'true'** if the node is present in the AVL tree. However, it will print **'false'** if it is not found.|
|![](Screenshot/006.png)|Upon removing a node from the AVL tree, the tree will be automatically updated. When the user inputs **'4'**, they can observe the changes in the AVL tree.|
|![](/Screenshot/007.png)|<p>If the user selects **'5'**, they can choose to clear the entire AVL tree. Once the tree is cleared, if they subsequently choose **'4'** to display the tree, nothing will be printed as the tree is now empty.</p><p></p><p></p><p></p><p></p><p></p>|
### <a name="_y0iamc2fy5re"></a>**Logic of Data Structure**

#### <a name="_3xpbmq5bkn6k"></a>**I. Classes**


|Classes|Explanation|
| - | - |
|<p>Node Class</p><p>![](/Screenshot/008.png)</p>|<p>The Node class represents a node in the AVL tree. It includes the:</p><p>- left and right children of the node</p><p>- data of the current node</p><p>- height of the current node</p>|
|<p>AVL Tree Class</p><p>![](/Screenshot/009.png)</p>|The AVL Tree class represents the tree itself. It includes the logic to build the AVL tree and performs operations on the tree.|
|<p>Main Class</p><p>![](/Screenshot/010.png)</p>|<p>The Main class represents the main method of the AVL Tree Implementation program. It acts as the interface of the program and allows users to interact with the AVL tree. Users can choose to:</p><p></p><p>- Insert a value</p><p>- Delete a value</p><p>- Search for a value</p><p>- Display the tree</p><p>- Clear the tree</p><p>- Exit the program</p>|

#### <a name="_8wzo2amizjtq"></a>**II. AVL Tree Functions**


|Functions / Methods|Explanation|
| - | - |
|<p>- AVL Tree initialization and deletion (Root Node)</p><p>![](/Screenshot/011.png)</p>|<p>A new AVL tree is formed by creating an empty node called “root”. The operations of the tree begins at the root or initial node.</p><p></p><p>The “makeEmpty” method clears the entire tree by resetting the value of the root node to null.</p>|
|<p>- Node Insertion![](/Screenshot/012.png)</p><p>- Visual example, Insert 2</p><p>![](/Screenshot/013.png)</p>|<p>The insert method is used to insert data into the AVL tree through nodes when called..</p><p></p><p>Starting from the root node, each node is checked. If the node checked is empty (=null), the data would be inserted into the node of the tree. </p><p></p><p>If the node checked is not empty, the input data to be inserted would be compared to the node's data. If the input data is less than or equal to the node's data, we go to the left child of the node and if it is greater than the node's data, we go to the right child instead. This checking logic is called recursively at each node from the top to bottom of the tree, until there is an empty node to insert the data.</p><p></p><p>After inserting the node, the height of the current node is updated. Based on the balance factor of the updated height, the tree would then be rebalanced starting from the current node if necessary.</p>|
|<p>- Node Deletion</p><p></p><p>![](/Screenshot/014.png)</p>|<p>The delete method is used to delete data from the AVL tree when called.</p><p></p><p>Similar to the insertion logic, each node is checked starting from the root node. If the data to be deleted (key) is less than or equal to the node's data, we go to the left child of the node and if it is greater than the node's data, we go to the right child instead. This checking logic is called recursively at each node from the top to bottom of the tree, until the node's data is equal to the key. If all nodes are checked and no equal data is found, it means the key is not found in the tree, so it returns null.</p><p></p><p>Once a node with data equal to the key is found, it will be the node to be deleted. If the node only has one child, the node is replaced with its child. But if the node has two children, we replace the node with the smallest value in the right subtree (the **most left child** of the right subtree).</p><p></p><p>After deleting the node, the height of the current node is updated and the tree would then be rebalanced if necessary.</p>|
|<p>- Rebalance Function </p><p>![](/Screenshot/015.png)</p>|<p>The rebalance function is used to maintain the property of the self-balancing AVL Tree, by rotating the nodes so that their balance factor  is within [-1,+1].</p><p></p><p>When the tree is unbalanced, it will either perform a left or right rotation depending on the balance factor. After each rotation, the height and the balance factor of each node will be updated. The tree will continue to perform rotations until it is completely balanced.</p>|
|<p>- Search Function</p><p>![](/Screenshot/016.png)</p><p></p>|<p>Since the AVL tree is a binary search tree, it will search for the element in the tree by comparing it to each node from top to bottom. If the element to be search is lesser or equal to the node, it will continue the search in the left subtree and if the element is greater, it will continue to the right subtree.The function uses a loop to search each, as long as the node is not null and found is false.</p><p></p><p>If a node with the same value is found, it will return a true value, and if found remains false, the element is not in the current tree.</p>|
|<p>- Tree Traversal</p><p>![](/Screenshot/017.png)</p>|<p>This function displays the preorder, inorder, and postorder traversal of the AVL tree by printing the nodes in different sequences. The traversal sequence are as follows:</p><p>- Preorder Traversal</p><p>&emsp;- root → left → right  </p><p>- Inorder Traversal</p><p>&emsp;- left → root  → right</p><p>- Postorder Traversal</p><p>&emsp;- left → right → root </p>|
|<p>- Print Tree</p><p>![](/Screenshot/018.png)</p>|<p>This is the logic used for printing the AVL tree, to provide the user with a visual representation of their data in a tree.</p><p>![](/Screenshot/019.png)</p>|
|- Import Scanner|The scanner class is imported from the java.util package to read the user's input in the interface.|
|<p>- Operation Selection </p><p>![](/Screenshot/020.png)</p>|<p>An infinite while loop is used to display the operation interface to the user, until the user wishes to exit the program. With the help of switch cases, the user is able to choose to either insert and delete a node into the AVL tree, search the tree for an element, display the tree (both visual diagram and tree traversals), and clear the entire tree.</p><p></p><p>To prevent errors, this code segment also uses while loops to help ensure that the user only inputs data with a valid data type, which is integers.</p>|
### <a name="_ydtfwyxofrl"></a>**Input Accuracy of Example Data**

#### <a name="_70vf5l86pg6"></a>**I. Scenario 1 (No duplicates)**

Input data: 50, 20, 45, 30, 65, 55, 15, 80, 70, 10 



|**Operations**|**Desired Output**|**Program Output**|
| - | - | - |
|Insert = [50, 20, 45, 30, 65, 55, 15, 80, 70, 10 ]|![](/Screenshot/021.png)|![](/Screenshot/022.png)|
|Insert = 5|<p>Before balancing:-<br>![](/Screenshot/023.png)<br>After balancing:-</p><p>![](/Screenshot/024.png)</p>|![](/Screenshot/025.png)|
|Search = 7|7 is not in the tree<br>![](/Screenshot/026.png)|![](/Screenshot/027.png)|
|Search = 50|![](/Screenshot/028.png)|![](/Screenshot/029.png)|
|Delete = 70|![](/Screenshot/030.png)|<p>![](/Screenshot/031.png)</p><p></p><p>The output differs from the desired output because the program replaces the node to be deleted with the inorder successor, and then deletes the inorder successor. The desired output’s programs replaces the node with the inorder predecessor instead.</p>|
####
#### <a name="_c2rwziq2vc8w"></a><a name="_vchhcykokfmc"></a>**II. Scenario 2 (With duplicates)**

Input data: 21, 19, 21, 5, 2, 12, 25, 30, 16


|**Operations**|**Desired Output**|**Program Output**|
| - | - | - |
|Insert = [21, 19, 21, 5, 2, 12, 25, 25, 30 ]|![](/Screenshot/032.png)|![](/Screenshot/033.png)|
|Delete = 25|![](/Screenshot/034.png)|![](/Screenshot/035.png)|
|Insert = 12|![](/Screenshot/036.png)|<p>![](/Screenshot/037.png)</p><p></p><p>The output differs from the desired output because the program inserts duplicate values into the left subtree instead of the right subtree.</p>|
|Search = 21|![](/Screenshot/038.png)|![](/Screenshot/039.png)|

## <a name="_csp8mxyalvwf"></a>**2.0 Challenges** 
This section displays the challenges of the Self-Balancing Tree (AVL) and Hashing (Open Addressing and Separate Chaining) programs created. 
## <a name="_7atqcs522a33"></a>**2.1  Self-Balancing Trees (AVL)**

|No|Challenge|Description|Solution|
| - | - | - | - |
|1|Balance Maintenance|For every node, the height of the left and right subtrees must differ by at most one. Maintaining this balance during insertions and deletions are a challenge|<p>Our implementation handles this by implementing rotations. After each insertion or deletion, we compute the balance factor of each node. If the balance factor is greater than 1 or less than -1, the tree is deemed unbalanced. To correct this, we perform one of four types of rotations: LL, LR, RR, or RL, as implemented in the **rotateWithLeftChild** and **rotateWithRightChild** methods. The **rebalance** method further ensures the appropriate rotation is accomplished.</p><p></p><p>![](/Screenshot/040.png)</p><p>Diagram 1 : Snippet of code for Balance Maintenance </p><p></p>|
|2|Deletion Management|Deletion in an AVL tree can be complex, especially in the event that the node that will be deleted has two children. This operation involves removing the node and reconfiguring the tree to maintain balance.|<p>Our implementation resolves this issue by implementing a recursive method. Upon finding the node to be deleted, the number of children is evaluated. If the node has two children, the in-order successor (the smallest node in the right subtree) is located. Then the node to be deleted is replaced with the in-order successor. Lastly, the in-order successor is deleted. This operation is guided by the **delete** and **mostLeftChild** methods.</p><p></p><p>![](/Screenshot/041.png)</p><p>Diagram 2 : Snippet of code for Deletion Management</p><p></p>|
|3|Efficient Search Operations|Searching for an element in an AVL tree can be inefficient if the tree is not balanced. If the AVL tree is not balanced, it may require a traversal of every node (O(n)).|<p>The self-balancing property of the AVL tree ensures successful search operations. By maintaining balance after each insertion or deletion, the AVL tree guarantees a logarithmic time complexity for search operations. This operation is executed by the **search** method.</p><p></p><p>Snippet of code : </p><p></p><p>![](/Screenshot/042.png)</p><p>Diagram 3 : Snippet of code for Efficient Search Operations</p><p></p>|
|4|Tree Visualization|Visualising the structure of the AVL tree and its changes over time can be challenging. However, it is crucial for comprehension and debugging.|<p>We have addressed this challenge by implementing  **printTree** function, which provides a visual representation of the tree. By traversing the tree and printing nodes in a structured manner, the user can gain a visual understanding of the AVL tree’s changes over time.</p><p>Snippet of code : </p><p>![](/Screenshot/043.png)</p><p>Diagram 4 : Snippet of code Tree Visualization</p>|





