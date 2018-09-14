"""
This program reads in a voting-data.tsv file, and uses it to induce a decision tree classifier.
The program will print out a pruned version of the tree, then a series of accuracies calculated out.

NOTE: This decision tree does not appear to function correctly. The pruned tree is not particularly accurate,
mostly because the unpruned tree is VERY inaccurate (I can't tell why, on smaller scale it works fine).
Additionally, the pruned result is frighteningly short, and while I've had variants that appeared to be
a more effective structure, they outputted the wrong party.
There's some commented out code meant to display the unpruned tree and it's accuracy for debug purposes.

"""
__author__ = "David S Smith"
__version__ = "3.26.2018"



import math
import sys

class Datum:
    """
    This represents a single datapoint in a dataset of representatives
    """
    def __init__(self, dat_id, dat_party, dat_votes):
        """
        dat_id is a string id associated with a representative
        dat_party is a string identifying a representative's political affiliation as either R (Republican)
                    or D (Democrat)
        dat_votes is a string (or generally a list of characters) representing votes on a list of policies
                    + indicates a yea, - a nay, and . an absence or abstain
        """
        self.dat_id = dat_id #this is the datum's id
        self.dat_party = dat_party #this should be the actual classification of the datum
        self.dat_votes = dat_votes #this should be a list of votes as characters in a string

    def __str__(self):
        """
        Debug readout for a Datum
        """
        to_string = "ID: " + str(self.dat_id) + " --- CLASSIFICATION: " + str(self.dat_party) + " --- VOTED: " + str(self.dat_votes)
        return to_string


original_issues = []
def parse_file():
    """
    This parses the file, creates the unpruned tree, prunes it, tests the algorithm, and prints out information
    on the tree including several accuracies and a pruned tree.
    """
    if len(sys.argv) < 2:
        print("Need a file")
        sys.exit(1)

    data_input = open(sys.argv[1])

    data = []
    for line in data_input: #for each of these lines
        if(len(line) == 0): pass #skip empty lines
        split_within_line = line.split("\t") #split by tabs
        new_datum = Datum(split_within_line[0], split_within_line[1], split_within_line[2]) #feed splits into a Datum object
        data.append(new_datum) #add Datum to list of data

    #make a list of characters representing the issues
    for i in range(len(data[0].dat_votes)-1): #from 0 to the end of the list of issues from the first datum
        original_issues.append(chr(i+97))


    i = 0
    tuning_set = []
    training_set = []
    num_reps = len(data)
    for i in range(0, num_reps-1):
        if (i % 4 == 0):
            tuning_set.append(data[i])
        else:
            training_set.append(data[i])

    pair = _count_parties(training_set)

    unpruned = induce_node_tree(training_set, original_issues,"D",-1)
    # print("\n####  UNPRUNED TREE  ####\n")
    # print(unpruned)

    unprune_acc = calc_accuracy(unpruned, tuning_set)

    pruned = prune_tree(unpruned, tuning_set)
    print("\n####  PRUNED TREE  ####\n")
    print(pruned)

    acc = calc_accuracy(pruned, training_set)

    # print("Accuracy of unpruned tree with tuning_set: " + str(unprune_acc))
    print("Accuracy of pruned tree with tuning_set: " + str(acc))
    leave_one_out_cross_validation(data)




def leave_one_out_cross_validation(data_set):
    """
    Removes each datum, generates the tree with the remainder, tests the removed datum, and prints out the
    total accuracy of the trees
    """
    with_removal = []
    total = 0
    correct = 0
    for dat in data_set:
        removed = [x for x in data_set]

        removed.remove(dat)
        t = test_with_data(removed)
        actual = dat.dat_party
        classified = classify(dat, t)
        if actual == classified: correct+=1
        total+=1

    print("Accuracy after L.O.O.C.V.: " + str(correct/total))



def test_with_data(data):
    """
    Uses the data as the training and tuning inputs for a decision tree
    Outputs the pruned tree.
    """
    i = 0
    tuning_set = []
    training_set = []
    num_reps = len(data)
    for i in range(0, num_reps-1):
        if (i % 4 == 0):
            tuning_set.append(data[i])
        else:
            training_set.append(data[i])

    unpruned = induce_node_tree(training_set, original_issues, "D", -1)
    pruned = prune_tree(unpruned, tuning_set)

    return pruned



def classify(data_point, tree):
    """
    Classifies a datum with a given decision tree, outputting the party id
    the tree classifies the datum as.
    """
    current = tree
    while(current.is_leaf == False): #while we're not at a leaf
        q = tree.issue
        v = data_point.dat_votes[ord(q) - 97]
        if(current is None): pass
        current = current.get_classification(v)
    #we should now be at a Leaf
    if(current is None): print("FATAL")
    c =current.get_classification("")
    # print("classified: " + str(data_point) + " as " + str(c))
    return c







def _count_parties(data_set): #DEMOCRATS, THEN REPUBLICANS
    """
    Outputs a tuple representing the number of democrats and republicans in a dataset
    """
    reps = 0
    dems = 0
    for data_point in data_set:
        if data_point.dat_party == "R": reps+=1
        if data_point.dat_party == "D": dems+=1

    return (dems, reps)



def calc_entropy(data_set): #calculates total entropy of the dataset
    """Calculates the entropy of a dataset"""
    republicans = 0
    democrats = 0
    total = 0
    for data_point in data_set:
        party = data_point.dat_party
        if party == "R":
            republicans+=1
        elif party == "D":
            democrats+=1
        total+=1

    if total == 0: return 0
    prob_dem = democrats/total
    prob_rep = republicans/total
    if prob_dem == 0: return -(prob_rep * math.log(prob_rep, 2))
    if prob_rep == 0: return -(prob_dem * math.log(prob_dem, 2))

    entropy = (-prob_dem * math.log(prob_dem, 2)) -(prob_rep * math.log(prob_rep, 2))
    return entropy


def split_by(data_set, question, entropy_before):
    """
    Takes in a dataset, an issue (as a character), and the entropy of the dataset previously
    Returns a tuple containing the gain and the subsets resulting in the following format:
    (gain, (yea_subset, nay_subset, abstain_subset))
    """
    yea_set = []
    nay_set = []
    abstain_set = []

    for data_point in data_set:
        vote = data_point.dat_votes[ord(question)-97] #the way this representative voted is the data_point's votes indexed by question
        party = data_point.dat_party

        if vote == "+":
            yea_set.append(data_point)
        if vote == "-":
            nay_set.append(data_point)
        if vote == ".":
            abstain_set.append(data_point)

    yea_entropy = abs(calc_entropy(yea_set) * (len(yea_set))/len(data_set))
    nay_entropy = abs(calc_entropy(nay_set) * (len(nay_set))/len(data_set))
    abstain_entropy = abs(calc_entropy(abstain_set) * (len(abstain_set))/len(data_set))
    calc_gain = entropy_before - (yea_entropy + nay_entropy + abstain_entropy)
    return (calc_gain,(yea_set, nay_set, abstain_set))



def _same_votes(data_set):
    """Outputs
    False if the data points don't have homogenous votes,
    True if they do.
    """
    current = data_set[0].dat_votes
    for data_point in data_set:
        if data_point.dat_votes != current: return False #if we ever get to a data point that's different from the first, this case does not apply

    return True #if we proceed through all the data without stopping, return True



def _majority(data_set):
    """
    Outputs the majority party in a dataset as a string ("R" or "D") or None if
    there is no majority.
    """
    pair = _count_parties(data_set)
    democrats = pair[0]
    republicans = pair[1]
    if democrats > republicans: return "D"
    if democrats < republicans: return "R"
    else: return None






def prune_tree(tree, tuning_set):

    def _get_internals(tree):
        """
        Takes in a tree and adds the internal nodes to a list
        """
        y = tree.yea
        n = tree.nay
        a = tree.abstain
        if (y.is_leaf == False):
            internal_nodes.append(y)
            _get_internals(y)
        if (n.is_leaf == False):
            internal_nodes.append(n)
            _get_internals(n)
        if (a.is_leaf == False):
            internal_nodes.append(a)
            _get_internals(a)
        return


    internal_nodes = []
    _get_internals(tree)
    best_accuracy = calc_accuracy(tree, tuning_set)
    best_node = None
    while True:
        internal_nodes = []
        _get_internals(tree)
        best_node = None
        for branch in internal_nodes:
            branch.make_leaf(True)
            accuracy = calc_accuracy(tree, tuning_set)
            if accuracy >= best_accuracy: ## if this pruning improves accuracy or matches it
                best_accuracy = accuracy
                best_node = branch
            branch.make_leaf(False)

        if best_node is None: break
        else: best_node.perma_leaf() #permanently closes the Node into a leaf, removing pointers to children

    internal_nodes = []
    return tree




def calc_accuracy(tree, data_set):
    correct = 0
    total = 0
    for data_point in data_set:
        total+=1
        actual = data_point.dat_party
        classified = classify(data_point, tree)
        if actual == classified: correct+=1

    return (correct/total)


def induce_node_tree(data_set, issues, prev_majority, level):
    level+=1
    parties = _count_parties(data_set)
    democrats = parties[0] #the number of democrats in this data_set
    republicans = parties[1] #the number of republicans in this data_set

    ##########################################
    ####LEAF OR NONE CASES####################
    if (democrats == 0) and (republicans == 0): #if there are no representatives
        return Node(is_leaf = True, classification = prev_majority, depth = level) #return the majority above

    #FIXME: for some reason these spit out the opposite of what they should
    if democrats == 0: #if there are republicans but no democrats
        return Node(is_leaf = True, classification = "R", depth = level)
    if republicans == 0: #if there are democrats but no republicans
        return Node(is_leaf = True, issue = None, classification = "D", depth = level)

    if _same_votes(data_set) or (len(issues) == 0): #if the votes of all datums are the same or we are out of issues
        this_maj = _majority(data_set)
        if this_maj is None:
            return Node(is_leaf = True, classification = prev_majority, depth = level)
        else:
            return Node(is_leaf = True, classification = this_maj, depth = level)

    ##########################################
    ####  BRANCH CASES  ######################


    ####  CHOOSING A BRANCH ISSUE  ###########
    options = {}
    entropy_before = calc_entropy(data_set)
    for issue in issues: #attempt to split by each issue
        split = split_by(data_set, issue, entropy_before) #NOTE: Split returns tuple containing (gain, subsets)
        gain_of = split[0]
        subsets_of = split[1]
        if gain_of in options: pass #this is to guarantee that if we encounter two identical gains we don't overwrite
        options[gain_of] = (issue, subsets_of) #place in hashmap to be referenced later

    gain = max(options, key=float) #choose greatest gain
    selection = options[gain] #grab splits and question related to greatest gain
    question = selection[0]
    subsets = selection[1]

    ####  PREPARING RECURSIVE CALL  ##########

    yeas = subsets[0]
    nays = subsets[1]
    abstains = subsets[2]
    new_issues = [x for x in issues]
    new_issues.remove(question) #remove issue from list to pass down

    maj = _majority(data_set)
    if maj is None:
        maj = prev_majority

    #### RECURIVE CALL #######################

    yea_path = induce_node_tree(yeas, new_issues, maj,level)
    nay_path = induce_node_tree(nays, new_issues, maj,level)
    abstain_path = induce_node_tree(abstains, new_issues, maj,level)

    branch = Node(is_leaf=False, issue=question, classification = None, yea=yea_path, nay=nay_path, abstain=abstain_path, depth=level, majority = maj)
    # internal_nodes.append(branch)#FIXME: This isn't going to work
    return branch



class Node:

    def __init__(self, is_leaf=True, issue=None, classification=None, yea=None, nay=None, abstain=None, depth=0, majority=None):
        self.is_leaf = is_leaf
        self.issue = issue
        self.classification = classification
        self.yea = yea
        self.nay = nay
        self.abstain = abstain
        self.depth = depth
        self.majority = majority

    def get_classification(self, vote):
        if self.is_leaf:
            if self.classification is None:
                return self.majority
            else: self.classification
        if vote == "+": return self.yea
        if vote == "-": return self.nay
        if vote == ".": return self.abstain
        else:
            return self.classification

    def perma_leaf(self):
        self.is_leaf = True
        self.issue = None
        self.yea = None
        self.nay = None
        self.abstain = None

    def make_leaf(self, state):
        self.is_leaf = state

    def __str__(self):
        if self.is_leaf:
            if self.classification is None:
                return self.majority
            else:
                return self.classification
        string = "Issue " + str(self.issue) + ":\n" + ("  "*self.depth) +"+ " + str(self.yea) + "\n" + ("  "*self.depth) + "- " + str(self.nay) + "\n" + ("  "*self.depth) + ". " + str(self.abstain)
        return string

parse_file()
