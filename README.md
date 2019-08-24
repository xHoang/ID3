# ID3

The aim of this assignment is to implement the ID3 algorithm in Java to perform decision tree learning
and classification for objects with discrete (String-valued) attributes. You will be given two input files,
one containing labelled training examples, and the other containing examples which have no label,
which your programme has to classify. In each file the examples will be described by a list of attributes,
one example per line, with the attribute values separated by commas (comma-separated value or CSV
format). You may assume that none of the attribute names or values contains a comma or other
punctuation. The first line of each input file contains the names of the attributes. The last attribute
on each line in the training file is the class that the example belongs to. Apart from the class, which
only appears in the training data, the other attributes will be in the same order in both files.

You are provided with a skeleton programme to get you started. The skeleton programme takes care
of the text processing: reading and parsing the CSV files, and finding the set of values that each
attribute can have. You may assume that no attribute has a value in the testing data that does not
also occur in the training data. The skeleton programme also contains most of the data structures
that you need to write your methods. You need to complete the methods classify() and train().
Do NOT change their specification or any of the predefined data structures, or else you might fail
automatic marking. You may add new methods, variables and classes as they are needed. Do not
change the main() method.

You are also provided with two sets of test files (each containing the two input files and the correct
output file). Test with (replacing the relevant file names):

*java ID3 TrainFile.csv TestFile.csv >MyOutput.csv
diff MyOutput.csv OutputFile.csv*

The diff command should give no output if your code is correct. Note that the given test files are
very simple tests (the data is taken from the questions in Tutorial 5, so you can check the code step
by step). You will need to design your own tests to make sure your code functions correctly under
all legal input conditions (e.g. 1 class; 3 or more classes; and cases where the training set can not be
perfectly classified). Note: the supplied tests are only a small part of the automatic testing.
