import numpy as np

with open("YAGO4-types-freq20-single-mapping.txt", encoding="UTF-8") as f:
    entity_type = {}
    type_dict = {}
    type_idx = 0
    aa = f.readline().strip()
    while aa:
        bb = aa.split('\t')
        entity_type[bb[0]] = bb[1]
        if bb[1] not in type_dict:
            type_dict[bb[1]] = type_idx
            type_idx += 1
        aa = f.readline().strip()
        
with open("entities.dict", encoding="UTF-8") as f:
    entities_dict = {}
    aa = f.readline().strip()
    while aa:
        bb = aa.split('\t')
        entities_dict[bb[1]] = int(bb[0])
        aa = f.readline().strip()

for item in entity_type:
    if item in entities_dict:
        types_dd = entity_type[item]
        entity_type[item] = (type_dict[types_dd], entities_dict[item])
    else:
        print(f'could not locate {item} in entity dict')
        
with open("type_dict.txt", 'w', encoding="UTF-8") as f:
    for item in type_dict:
        idx = type_dict[item]
        f.write(str(idx) + '\t' + item)
        f.write('\n')

with open("entity_type_embedding.txt", 'w', encoding="UTF-8") as f:
    for item in entity_type:
        type_idx, embed_idx = entity_type[item]
        f.write(item + '\t' + str(type_idx) + '\t' + str(embed_idx))
        f.write('\n')

embedding = np.load("entity_embedding_200.npy")

embedding_clean = []
label = []
for item in entity_type:
    type_idx, embed_idx = entity_type[item]
    embedding_clean.append(embedding[embed_idx])
    label.append(np.array([type_idx]))
    
embedding_clean_npy = np.stack(embedding_clean, 0)

label_npy = np.concatenate(label)

np.savez('embedding_label.npz', embedding=embedding_clean_npy, label=label_npy)