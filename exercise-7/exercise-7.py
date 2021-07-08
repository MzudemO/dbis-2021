def load_data_set():
    """
    Load a sample data set
    Returns:
        A data set: A list of transactions. Each transaction contains several items.
    """
    data_set = [
        ["1", "2", "5"],
        ["2", "4"],
        ["2", "3"],
        ["1", "2", "4"],
        ["1", "3"],
        ["2", "3"],
        ["1", "3"],
        ["1", "2", "3", "5"],
        ["1", "2", "3"],
    ]
    return data_set


def data_set_from_txt():
    """
    Load a data set from a text file
    File is expected to have one transaction per line, with identifiers separated by whitespace
    Returns:
        A data set: A list of transactions. Each transaction contains several items.
    """
    data_set = []
    with open("transactions.txt") as f:
        for line in f:
            data_set.append(line.split(" ")[:-1])

    max_t = 0
    for t in data_set:
        if len(t) > max_t:
            max_t = len(t)
    print(f"Longest transaction, largest possible frequent item set: {max_t}")
    return data_set


def create_C1(data_set):
    """
    Create frequent candidate 1-itemset C1 by scaning data set.
    Args:
        data_set: A list of transactions. Each transaction contains several items.
    Returns:
        C1: A set which contains all frequent candidate 1-itemsets
    """
    C1 = set()
    for t in data_set:
        for item in t:
            item_set = frozenset([item])
            C1.add(item_set)
    return C1


def is_apriori(Ck_item, Lksub1):
    """
    Judge whether a frequent candidate k-itemset satisfy Apriori property.
    Args:
        Ck_item: a frequent candidate k-itemset in Ck which contains all frequent
                 candidate k-itemsets.
        Lksub1: Lk-1, a set which contains all frequent candidate (k-1)-itemsets.
    Returns:
        True: satisfying Apriori property.
        False: Not satisfying Apriori property.
    """
    for item in Ck_item:
        sub_Ck = Ck_item - frozenset([item])
        if sub_Ck not in Lksub1:
            return False
    return True


def create_Ck(Lksub1, k):
    """
    Create Ck, a set which contains all all frequent candidate k-itemsets
    by Lk-1's own connection operation.
    Args:
        Lksub1: Lk-1, a set which contains all frequent candidate (k-1)-itemsets.
        k: the item number of a frequent itemset.
    Return:
        Ck: a set which contains all all frequent candidate k-itemsets.
    """
    Ck = set()
    len_Lksub1 = len(Lksub1)
    list_Lksub1 = list(Lksub1)
    for i in range(len_Lksub1):
        for j in range(1, len_Lksub1):
            l1 = list(list_Lksub1[i])
            l2 = list(list_Lksub1[j])
            l1.sort()
            l2.sort()
            if l1[0 : k - 2] == l2[0 : k - 2]:
                Ck_item = list_Lksub1[i] | list_Lksub1[j]
                # pruning
                if is_apriori(Ck_item, Lksub1):
                    Ck.add(Ck_item)
    return Ck


def generate_Lk_by_Ck(data_set, Ck, min_support, support_data):
    """
    Generate Lk by executing a delete policy from Ck.
    Args:
        data_set: A list of transactions. Each transaction contains several items.
        Ck: A set which contains all all frequent candidate k-itemsets.
        min_support: The minimum support.
        support_data: A dictionary. The key is frequent itemset and the value is support.
    Returns:
        Lk: A set which contains all all frequent k-itemsets.
    """
    Lk = set()
    item_count = {}
    for t in data_set:
        for item in Ck:
            if item.issubset(t):
                if item not in item_count:
                    item_count[item] = 1
                else:
                    item_count[item] += 1
    t_num = float(len(data_set))
    for item in item_count:
        if (item_count[item] / t_num) >= min_support:
            Lk.add(item)
            support_data[item] = item_count[item] / t_num
    return Lk


def generate_L(data_set, k, min_support):
    """
    Generate all frequent itemsets.
    Args:
        data_set: A list of transactions. Each transaction contains several items.
        k: Maximum number of items for all frequent itemsets.
        min_support: The minimum support.
    Returns:
        L: The list of Lk.
        support_data: A dictionary. The key is frequent itemset and the value is support.
    """
    support_data = {}
    C1 = create_C1(data_set)
    L1 = generate_Lk_by_Ck(data_set, C1, min_support, support_data)
    Lksub1 = L1.copy()
    L = []
    L.append(Lksub1)
    for i in range(2, k + 1):
        print(i)
        Ci = create_Ck(Lksub1, i)
        Li = generate_Lk_by_Ck(data_set, Ci, min_support, support_data)
        print(f"itemsets with {i} items: {len(Li)}")
        Lksub1 = Li.copy()
        L.append(Lksub1)
    return L, support_data


if __name__ == "__main__":
    """
    Test
    """
    data_set = data_set_from_txt()
    L, support_data = generate_L(data_set, k=26, min_support=0.01)
    for Lk in L[:-1]:
        print("=" * 50)
        print("frequent " + str(len(list(Lk)[0])) + "-itemsets\t\tsupport")
        print("=" * 50)
        for freq_set in Lk:
            print(freq_set, support_data[freq_set])


"""
    OUTPUT
"""

"""
2
itemsets with 2 items: 11
3
itemsets with 3 items: 1
4
itemsets with 4 items: 0
5
itemsets with 5 items: 0
6
itemsets with 6 items: 0
7
itemsets with 7 items: 0
8
itemsets with 8 items: 0
9
itemsets with 9 items: 0
10
itemsets with 10 items: 0
11
itemsets with 11 items: 0
12
itemsets with 12 items: 0
13
itemsets with 13 items: 0
14
itemsets with 14 items: 0
15
itemsets with 15 items: 0
16
itemsets with 16 items: 0
17
itemsets with 17 items: 0
18
itemsets with 18 items: 0
19
itemsets with 19 items: 0
20
itemsets with 20 items: 0
21
itemsets with 21 items: 0
22
itemsets with 22 items: 0
23
itemsets with 23 items: 0
24
itemsets with 24 items: 0
25
itemsets with 25 items: 0
26
itemsets with 26 items: 0
==================================================
frequent 1-itemsets		support
==================================================
... trunc.
==================================================
frequent 2-itemsets		support
==================================================
frozenset({'829', '368'}) 0.0127
frozenset({'704', '39'}) 0.0139
frozenset({'217', '283'}) 0.0104
frozenset({'390', '722'}) 0.0103
frozenset({'825', '39'}) 0.0151
frozenset({'825', '704'}) 0.0135
frozenset({'682', '368'}) 0.0128
frozenset({'390', '227'}) 0.0101
frozenset({'217', '346'}) 0.0149
frozenset({'789', '829'}) 0.0119
frozenset({'692', '368'}) 0.0114
==================================================
frequent 3-itemsets		support
==================================================
frozenset({'825', '704', '39'}) 0.0128
==================================================
"""
