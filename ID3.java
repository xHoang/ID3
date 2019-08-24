// ECS629/759 Assignment 2 - ID3 Skeleton Code
// Author: Simon Dixon
// ID3 Implementation completed by Hoang Le - 161140218
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

class ID3 {

	/** Each node of the tree contains either the attribute number (for non-leaf
	 *  nodes) or class number (for leaf nodes) in <b>value</b>, and an array of
	 *  tree nodes in <b>children</b> containing each of the children of the
	 *  node (for non-leaf nodes).
	 *  The attribute number corresponds to the column number in the training
	 *  and test files. The children are ordered in the same order as the
	 *  Strings in strings[][]. E.g., if value == 3, then the array of
	 *  children correspond to the branches for attribute 3 (named data[0][3]):
	 *      children[0] is the branch for attribute 3 == strings[3][0]
	 *      children[1] is the branch for attribute 3 == strings[3][1]
	 *      children[2] is the branch for attribute 3 == strings[3][2]
	 *      etc.
	 *  The class number (leaf nodes) also corresponds to the order of classes
	 *  in strings[][]. For example, a leaf with value == 3 corresponds
	 *  to the class label strings[attributes-1][3].
	 **/
	class TreeNode {

		TreeNode[] children;
		int value;

		public TreeNode(TreeNode[] ch, int val) {
			value = val;
			children = ch;
		} // constructor

		public String toString() {
			return toString("");
		} // toString()
		
		String toString(String indent) {
			if (children != null) {
				String s = "";
				for (int i = 0; i < children.length; i++)
					s += indent + data[0][value] + "=" +
							strings[value][i] + "\n" +
							children[i].toString(indent + '\t');
				return s;
			} else
				return indent + "Class: " + strings[attributes-1][value] + "\n";
		} // toString(String)

	} // inner class TreeNode
	private String used;
	private int attributes; 	// Number of attributes (including the class)
	private int examples;		// Number of training examples
	private TreeNode decisionTree;	// Tree learnt in training, used for classifying
	private String[][] data;	// Training data indexed by example, attribute
	private String[][] strings; // Unique strings for each attribute
	private int[] stringCount;  // Number of unique strings for each attribute

	public ID3() {
		used = "used";
		attributes = 0;
		examples = 0;
		decisionTree = null;
		data = null;
		strings = null;
		stringCount = null;
	} // constructor
	
	public void printTree() {
		if (decisionTree == null)
			error("Attempted to print null Tree");
		else
			System.out.println(decisionTree);
	} // printTree()

	/** Print error message and exit. **/
	static void error(String msg) {
		System.err.println("Error: " + msg);
		System.exit(1);
	} // error()

	static final double LOG2 = Math.log(2.0);
	
	static double xlogx(double x) {
		return x == 0? 0: x * Math.log(x) / LOG2;
	} // xlogx()

	/** Execute the decision tree on the given examples in testData, and print
	 *  the resulting class names, one to a line, for each example in testData.
	 **/
	public void classify(String[][] testData) {
		if (decisionTree == null)
			error("Please run training phase before classification");
		for(int i = 1; i<testData.length; i++) {
			String ans = transverse(decisionTree, testData[i]); // Send in the tree and a row to go along with it
			System.out.println(ans); // Output classification to console which can be then be mapped to a file using "> xyz.file"
		}
		
	} // classify()

	public String transverse(TreeNode currentNode, String[] row){
		// Base case should return leaf node which means esentially it would only be the node where it's children are null
		if(currentNode.children == null){
			// Returns [attributes-1] because we want the class and [currentNode value] to get the value stored at that attribute
			//  since it is a leaf node
			return strings[attributes-1][currentNode.value];
		}
		else { // Transverse through tree compare for each unique string for the attribute using Strings[currentNode.value]
		// In doing so, keep this in a loop and check to see if the test data value is equal to the strings[][] value
		// Once done return the position where the string was found and return the node with children on it.
		int posInStrings = -1;
			for(int i=0; i < strings[currentNode.value].length; i++){
				if(row[currentNode.value].equals(strings[currentNode.value][i])){
					posInStrings = i;
				}
				
			}
			// Transverse the decision tree by calling the method again via recursion but passing the current node children
			// until the node it is currently on has no children in which just return the classification which is returned back
			// as a string
			
			return transverse(currentNode.children[posInStrings], row);
		
		}
		
	} // transverse()

	public void train(String[][] trainingData) {
		indexStrings(trainingData);
		String[] usedAttributes = data[0].clone(); // Get all the headers or rather attribute names
		decisionTree = new TreeNode(null, 0);
		buildTree(decisionTree, trainingData, usedAttributes);
	} // train()
	/**
	* Returns a boolean if all the attributes have been used and have been replaced with 
	* "used" string. There is a running counter and if used is equal to the number of columns/attributes
	* then that means that all the attributes have been used
	**/
	boolean checkUsedAttributes(String[] attrCol){
		int attrCounter = 0;
		//boolean usedAttribute;
		for(int i=0; i<attrCol.length - 1; i++){
			if(attrCol[i].equals(used)){
				attrCounter++;
			}
			
		}
		/* we don't include class so -1 */
		if(attrCounter == attrCol.length - 1){
			return true;
		}else{
			return false;
		}
		
	} //checkUsedAttributes
	
	
	/** Grabs a subset or rather makes a subset of the currentDataSet that it is given.
	*  
	**/
	public String[][] getSubset(String[][] currentDataSet, int attr, int attrVal){
		int attrCounter = countAttributes(currentDataSet, attr, attrVal);
		// Again we don't want a class "attribute" column
		String[][] subSet = new String[attrCounter+1][currentDataSet[0].length-1];
		int rowCount = 1;
		int rows = currentDataSet.length;
		subSet[0] = currentDataSet[0];
		for (int i = 1; i < rows; i++) {
			if (currentDataSet[i][attr].equals(strings[attr][attrVal])) {
				subSet[rowCount] = currentDataSet[i];
				rowCount++;
			}
		}
		return subSet;
		
	} //getSubset()
	
	/**
	* The heart of the program. Here we build the decision tree.
	* It takes in an array of data and the current TreeNode.
	* Each call on buildTree will esentially split the dataset on
	* the best attribute. The current node that is set to be split will have it's value
	* the same as the best attribute's value. It's children are then also added with their respective indexes.
	* If it's a leaf node (entropy = 0 or no more attributes) the method would return and we will have our tree!
	**/
	
	public void buildTree(TreeNode node, String[][] currentDataSet, String[] usedAttributes){
		//Calculate the root entropy
		double rootEntropy = calcEntropy(currentDataSet);			
		double rows = examples-1;
		double comparator = 0; 
		int bestAttribute = 0;
		double[] infoGain = new double[attributes];
		double[] subSetEntropy;
		double[] instanceCount;
	
		// most common attribute in the subset
		if (rootEntropy <= 0.0 ||  checkUsedAttributes(usedAttributes)) {
			int leafClass = 0;
			int instances = 0;
			for (int z = 0; z < stringCount[attributes-1]; z++) {
				if (instances < countAttributes(currentDataSet, currentDataSet[0].length-1, z)) {
					instances = countAttributes(currentDataSet, currentDataSet[0].length-1, z);
					leafClass = z;
				}
			}
			node.value = leafClass;
			return;
		} else {	
		//check every attribute for the highest information gain to split on
			for (int i = 0; i < currentDataSet[0].length-1; i++) {
				if (usedAttributes[i].equals(used)) {
					//ignore these attributes;
					infoGain[i] = 0;
				} else {
					//initalise variables needed to calculate information gain
					subSetEntropy = new double[stringCount[i]];
					instanceCount = new double[stringCount[i]];
					for (int j = 0; j < stringCount[i]; j++) {
					//Every attribute gets a subset and then we calculate their respective entropy for their children nodes and
					// count every instance in their attribute in order to calculate entropy and later information gain!
						String[][] subSet = getSubset(currentDataSet, i, j);
						subSetEntropy[j] = calcEntropy(subSet);
						instanceCount[j] = countAttributes(subSet, i, j);
					}
					//now we have all the info we can calculate information gain
					infoGain[i] = rootEntropy;
					double tmp = 0;
					for (int a = 0; a < subSetEntropy.length; a++) {	
						//You get NaN on empty subset so we need to check for it and deal with it appropriately
						tmp = (instanceCount[a]/rows*subSetEntropy[a]);
						if (!Double.isNaN(tmp)) {
							infoGain[i] -= tmp;						
						}
					}
					infoGain[i] = Math.abs(infoGain[i]); // Make sure value is positive
					//highest gain so far will be the attribute to split on
					if (infoGain[i] >= comparator && !usedAttributes[i].equals(used)) {
						comparator = infoGain[i];
						bestAttribute = i;
					}	
				}
			}
			//Since it went through this else statement, it is a non-leaf node and therefore
			// we adjust accordingling by chaing the node's value
			node.value = bestAttribute;
			node.children = new TreeNode[stringCount[bestAttribute]];

			for (int n = 0; n < stringCount[bestAttribute]; n++) {
								String[] temp = usedAttributes.clone();
				String[][] newSubSet = getSubset(currentDataSet, bestAttribute, n);
				node.children[n] = new TreeNode(null, 0);
				if (newSubSet.length != 1) {temp[bestAttribute] = used;
					buildTree(node.children[n], newSubSet, temp);
				
				} else {
					// split data has no rows so force that node to be checked by setting all their attribute values to "used"
					for (int m = 0; m < temp.length-1; m++) {
						temp[m] = used;
					}
					buildTree(node.children[n], currentDataSet, temp);
				}	
			}
		}
		
		
		
		
	} // buildTree()
	

	
	public int countAttributes(String[][] currentDataSet, int attr, int attrVal) {
		int count = 0; 
		if (currentDataSet.length == 1) {
			return count;
		}
		// Don't want class headers
		for (int i = 1; i < currentDataSet.length; i++) {
			if (currentDataSet[i][attr].equals(strings[attr][attrVal])) {
				count++;
			}
		}		
		return count;
	} // countAttributes()
	
	/**
	* Pass the dataset we want to calculate the entropy from. I am making a big
	* assumption where the last column (should be representing class) is the class
	* I will utilise the stringCount variable and manipulate it in order to get the right number of
	* columns and rows
	**/
	public double calcEntropy(String[][] currentDataSet) {
		double rows = currentDataSet.length-1;
		double[] noClassInstances = new double[stringCount[attributes-1]];
	//loops through each class's instances and returns a value to say how many instances are in there
		for (int i = 0; i < stringCount[attributes-1]; i++) {
			noClassInstances[i] = countAttributes(currentDataSet, attributes-1, i);
		}
		// E(S) = -xlogx(P+) - xlogx(P-)
		double entropy = -xlogx(noClassInstances[0]/rows);
		for (int a = 1; a < noClassInstances.length; a++) {
			entropy -= (xlogx(noClassInstances[a]/rows));
		}	
		return Math.abs(entropy); // due to being a double need to force values to be positive or negative. Sometimes can get -0.0 because of float
	} // calcEntropy()

	/** Given a 2-dimensional array containing the training data, numbers each
	 *  unique value that each attribute has, and stores these Strings in
	 *  instance variables; for example, for attribute 2, its first value
	 *  would be stored in strings[2][0], its second value in strings[2][1],
	 *  and so on; and the number of different values in stringCount[2].
	 **/
	void indexStrings(String[][] inputData) {
		data = inputData;
		examples = data.length;
		attributes = data[0].length;
		stringCount = new int[attributes];
		strings = new String[attributes][examples];// might not need all columns
		int index = 0;
		for (int attr = 0; attr < attributes; attr++) {
			stringCount[attr] = 0;
			for (int ex = 1; ex < examples; ex++) {
				for (index = 0; index < stringCount[attr]; index++)
					if (data[ex][attr].equals(strings[attr][index]))
						break;	// we've seen this String before
				if (index == stringCount[attr])		// if new String found
					strings[attr][stringCount[attr]++] = data[ex][attr];
			} // for each example
		} // for each attribute
	} // indexStrings()

	/** For debugging: prints the list of attribute values for each attribute
	 *  and their index values.
	 **/
	void printStrings() {
		for (int attr = 0; attr < attributes; attr++)
			for (int index = 0; index < stringCount[attr]; index++)
				System.out.println(data[0][attr] + " value " + index +
									" = " + strings[attr][index]);
	} // printStrings()
		
	/** Reads a text file containing a fixed number of comma-separated values
	 *  on each line, and returns a two dimensional array of these values,
	 *  indexed by line number and position in line.
	 **/
	static String[][] parseCSV(String fileName)
								throws FileNotFoundException, IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String s = br.readLine();
		int fields = 1;
		int index = 0;
		while ((index = s.indexOf(',', index) + 1) > 0)
			fields++;
		int lines = 1;
		while (br.readLine() != null)
			lines++;
		br.close();
		String[][] data = new String[lines][fields];
		Scanner sc = new Scanner(new File(fileName));
		sc.useDelimiter("[,\n]");
		for (int n = 0; n < lines; n++)
			for (int f = 0; f < fields; f++)
				if (sc.hasNext())
					data[n][f] = sc.next();
				else
					error("Scan error in " + fileName + " at " + n + ":" + f);
		sc.close();
		return data;
	} // parseCSV()

	public static void main(String[] args) throws FileNotFoundException,
												  IOException {
		if (args.length != 2)
			error("Expected 2 arguments: file names of training and test data");
		String[][] trainingData = parseCSV(args[0]);
		String[][] testData = parseCSV(args[1]);
		ID3 classifier = new ID3();
		classifier.train(trainingData);
		classifier.printTree();
		classifier.classify(testData);
	} // main()

} // class ID3
