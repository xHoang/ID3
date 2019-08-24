# ID3 Implementation

Implemented the ID3 algorithm in Java to perform decision tree learning
and classification for objects with discrete (String-valued) attributes.

![realestate](https://i.imgur.com/mbgt1HY.png)

### Training data
![train](https://i.imgur.com/vD5YBXz.png)

### Training test data
![train](https://i.imgur.com/lOhpb3U.png)

###Classification
![asdas](https://i.imgur.com/0szffut.png)

Here you can see that 
### ID3 Stratergy

The ID3 algorithm tries to adhere to the pseudo code that is shown online and discussed on
the slides. Hence, the implementation focuses on building a decision tree which initially is
made using training data which is already classified. Once these rules / decisions have been
set the tree is then probed and tested to another unclassified dataset to see how that data is
classified. To implement this, on every iteration, it iterates through every unused attribute of
the training set and then calculates the entropy of that attribute. (Later calculate information
gain). It selects the attribute with the smallest entropy and then the initial training set is split
into subsets of data. It then recursively goes through the subset considering attributes it’s
never seen before only stopping if the entropy is 0 or there are no more attributes to be
considered. This is when we create a leaf node, which is pure (only one classification), and
so cannot be split upon again. 

In my implementation of ID3, I tried to follow the guidelines of the pseudo code which
specifically focuses on calculating entropy and information gain to determine the split in the
dataset. It is also important to keep track on which attributes were also considered during the
initial call. I track this initial call by using a string array called “usedAttributes” and I pass this
to my main recursive call “buildTree()” which is called initially by “Train()”. usedAttributes
contains all the names of all the attributes of the dataset. I achieve this by utilising .Clone().
This array is then passed along buildTree() so it can know when to consider certain
attributes when determining when to split or not. In my implementation, I make sure the
information gain value of used attribute is zero as we always choose the highest value which
means these attributes won’t be considered again. 

In a good summary, the Train() algorithm gets called from main. Here, I create my
usedAttributes String array by cloning all the attributes names via data[0].clone(). The
decision tree is then initialised and then sent to the buildTree() recursive call. During
buildTree, the root nodes entropy is calculated. It skips the base case (to say it’s a leaf node
– 0 entropy / All attributed have been considered). Hence, it goes to my else clause where it
checks the highest information gain value subset. I made two for loops, separate from each
other. The first gets all the respective entropy for the root nodes children and count every
instance and put them in their respective arrays subSetEntropy and instanceCount. We then
calculate information again and then make the current node a decision node and update it’s
values by making node.value = the best attribute and it’s children to be initialised. We then
call buildTree again with the new subset and used attributes. 

Some design decisions that were made was I wanted this to be quite simple. I believe using
other data structures like a HashSet or HashMap may have been better in the long term due
to their great lookup time but due to time constraints I was unable to do so.

Other design decisions I met were dealing with empty sets. My code initially tries to preemptively decide that the second row is where data is stored where the first are the headers.
However, there are empty sets where their “data” is only the headers and I cannot iterate to
an empty set because it doesn’t have a second row – an ArrayOutOfBounds. In this case, I
just have to catch this exception and send the full dataset back up but with all the attributes
“used” so none of the things will be considered and then will be decided as a leaf node as
shown in the base case.

### Bugs

// TODO: Program uses a pure leaf when data is available but no more attributes left. Need to consider if they don't have the same class
