import numpy as np 


######################## 1. feladat, entrópiaszámítás #########################
def get_entropy(n_cat1: int, n_cat2: int) -> float:
    if n_cat1 == 0:
        e1 = 0
    else:
        a1 = n_cat1 / (n_cat1 + n_cat2)
        e1 = -a1 * np.log2(a1)
    
    if n_cat2 == 0:
        e2 = 0
    else:
        a2 = n_cat2 / (n_cat1 + n_cat2)
        e2 = -a2 * np.log2(a2)
    
    
    return e1 + e2

###################### 2. feladat, optimális szeparáció #######################
def get_information_gain(features_by_column: np.ndarray, labels: np.ndarray, sep_val: int) -> float:
    entropy = get_entropy(labels.sum(), len(labels) - labels.sum())
    
    labels_less = labels[features_by_column <= sep_val]
    e = len(labels_less)
    entropy_less = get_entropy(labels_less.sum(), len(labels_less) - labels_less.sum())

    labels_greater = labels[features_by_column > sep_val]
    f = len(labels_greater)
    entropy_greater = get_entropy(labels_greater.sum(), len(labels_greater) - labels_greater.sum())
    
    return entropy - e / (e + f) * entropy_less - f / (e + f) * entropy_greater

def get_best_separation_by_column(features_by_column: np.ndarray, labels: np.ndarray) -> (float, int):
    information_gain, separation_value = 0, 0
    sep_vals = np.unique(features_by_column)
    for sep_val in sep_vals:
        ig = get_information_gain(features_by_column, labels, sep_val)
        if ig > information_gain:
            information_gain = ig
            separation_value = sep_val
    
    return information_gain, separation_value

def get_best_separation(features: list, labels: list) -> (int, int):
    if type(features) == list:
        features = np.array(features)
    if type(labels) == list:
        labels = np.array(labels)

    best_separation_feature, best_separation_value = 0, 0
    best_information_gain = 0
    
    for i in range(features.shape[1]):
        features_by_column = features[:, i]
        information_gain, sep_val = get_best_separation_by_column(features_by_column, labels)
        if information_gain > best_information_gain:
            best_information_gain = information_gain
            best_separation_value = sep_val
            best_separation_feature = i
    
    return best_separation_feature, best_separation_value

################### 3. feladat, döntési fa implementációja ####################

class DecisionTree:
    feature: int
    treshold: int
    lessNode = None
    greaterNode = None
    def __init__(self, feature: int, treshold: int):
        self.feature = feature
        self.treshold = treshold


def build_tree(features: np.ndarray, labels: np.ndarray) -> DecisionTree:
    feature, sep_val = get_best_separation(features, labels)
    tree = DecisionTree(feature, sep_val)

    features_by_column = features[:, feature]

    features_less = features[features_by_column <= sep_val, :]
    labels_less = labels[features_by_column <= sep_val]
    if np.all(labels_less == 1):
        tree.lessNode = 1
    elif np.all(labels_less == 0):
        tree.lessNode = 0
    else:
        tree.lessNode = build_tree(np.delete(features_less, feature, axis=1), labels_less)

    features_greater = features[features_by_column > sep_val, :]
    labels_greater = labels[features_by_column > sep_val]
    if np.all(labels_less == 1):
        tree.greaterNode = 1
    elif np.all(labels_greater == 0):
        tree.greaterNode = 0
    else:
        tree.greaterNode = build_tree(np.delete(features_greater, feature, axis=1), labels_greater)
    
    return tree

def predict(tree: DecisionTree, sample: np.ndarray) -> int:
    f = tree.feature
    if sample[f] <= tree.treshold:
        node = tree.lessNode
    else:
        node = tree.greaterNode

    if type(node) == int:
        return node
    
    return predict(node, np.delete(sample, f))

def main():
    data = np.loadtxt("train.csv", delimiter=',', dtype=np.int32)
    features = data[:, :-1]
    labels = data[:, -1]

    tree = build_tree(features, labels)

    with open("results.csv", "w") as fout:
        test_data = np.loadtxt("test.csv", delimiter=',', dtype=np.int32)
        for sample in test_data:
            pred = predict(tree, sample)
            print(pred, file=fout)
    
    return 0

if __name__ == "__main__":
    main()
