import random

import numpy as np
import matplotlib.pyplot as plt

from collections import Counter



DIR = '../dat/YAGO4/out/'


TRAIN_SPLIT = 0.7


# find entities

entity_set = set()
all_entities = []
with open(DIR + 'YAGO4-facts-freq20-both-ends.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        e1, r, e2 = line.strip().split('\t')
        if e1 not in entity_set:
            all_entities.append(e1)
            entity_set.add(e1)
        if e2 not in entity_set:
            all_entities.append(e2)
            entity_set.add(e2)


random.seed(0)
random.shuffle(all_entities)
n_train = int(len(all_entities) * TRAIN_SPLIT)

train_entities = all_entities[:n_train]
test_entities = all_entities[n_train:]


# count types
train_entity_set = set(train_entities)
test_entity_set = set(test_entities)

train_counter = Counter()
test_counter = Counter()

final_entities = set()

with open(DIR + 'YAGO4-types-freq20.txt', 'r') as f:
    lines = f.readlines()
    for line in lines:
        line = line.strip()
        e, t = line.split('\t')
        if e in train_entity_set:
            train_counter.update([t])
            final_entities.add(e)
        elif e in test_entity_set:
            test_counter.update([t])
            final_entities.add(e)



THRESHOLD_TRAIN = 5
THRESHOLD_TEST = 5

train_types = [t for t, c in train_counter.most_common() if c >= THRESHOLD_TRAIN]
test_types = [t for t, c in test_counter.most_common() if c >= THRESHOLD_TEST]

types = set(train_types).intersection(set(test_types))

print(len(types), 'types are preserved')


final_train_entities = set()
final_test_entities = set()
final_train_rel = 0
final_test_rel = 0
with open(DIR + 'YAGO4-types-freq20.txt', 'r') as f_in:
    f_train = open(DIR + 'YAGO4ET20-train.txt1', 'w+')
    f_test = open(DIR + 'YAGO4ET20-test.txt1', 'w+')
    lines = f_in.readlines()
    for line in lines:
        e, t = line.strip().split('\t')
        if t in types:
            if e in train_entity_set:
                f_train.write(line)
                final_train_entities.add(e)
                final_train_rel += 1

            elif e in test_entity_set:
                f_test.write(line)
                final_test_entities.add(e)
                final_test_rel += 1

f_train.close()
f_test.close()

print(final_train_rel, final_test_rel, len(final_train_entities) + len(final_test_entities))