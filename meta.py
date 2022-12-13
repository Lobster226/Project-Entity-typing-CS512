import numpy as np
import matplotlib.pyplot as plt

from collections import Counter


DIR = ''


# find entity types

train_entity_types = {}
with open(DIR + 'YAGO4ET20-train.txt', ) as f:
    lines = f.readlines()
    for line in lines:
        e, t = line.strip().split('\t')
        if e not in train_entity_types:
            train_entity_types[e] = []
        train_entity_types[e].append(t)

test_entity_types = {}
with open(DIR + 'YAGO4ET20-test.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        e, t = line.strip().split('\t')
        if e not in test_entity_types:
            test_entity_types[e] = []
        test_entity_types[e].append(t)


# read in taxonomy

child_types = {}
with open(DIR + 'YAGO4-class.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        line = line.strip()
        sub, parent = line.split('\t')
        if parent not in child_types:
            child_types[parent] = []
        child_types[parent] = sub

def get_subtypes(selected_type) -> set:
    ret = set()
    if selected_type in child_types:
        for child in child_types[selected_type]:
            ret.update(get_subtypes(child))
    return ret

# Generate meta tasks

def generate_meta_task(selected_type):
    # compatible types
    compatible_types = get_subtypes(selected_type)
    compatible_types.add(selected_type)

    positive_examples = []
    negative_examples = []
    for e, t_list in train_entity_types.items():
        if len(compatible_types.intersection(set(t_list))) == 0:
            negative_examples.append(e)
        else:
            positive_examples.append(e)
    return positive_examples, negative_examples

def get_test_positive_examples(
        selected_type, 
        test_compatible_types=False):
    # compatible types
    
    if test_compatible_types:
        compatible_types = get_subtypes(selected_type)
    else:
        compatible_types = set()
    compatible_types.add(selected_type)
    positive_examples = []
    for e, t_list in test_entity_types.items():
        if len(compatible_types.intersection(set(t_list))) > 0:
            positive_examples.append(e)
    return positive_examples

if __name__ == '__main__':
    test_type = 'http://yago-knowledge.org/resource/Human'
    import time
    start = time.time()
    p_ex, n_ex = generate_meta_task(test_type)
    print(time.time() - start)
    print(p_ex[:10])
    print(n_ex[:10])