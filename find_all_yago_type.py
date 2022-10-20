import csv
from encodings.utf_8_sig import encode
import pandas as pd

"""
Run the following SPARQL Query to get all types.

#prefix onto:<http://www.ontotext.com/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>
select DISTINCT ?s from onto:disable-sameAs {
	?n rdf:type ?s.
}
"""
# Please choose the correct file path before running the code.
input_file = 'C://Users//28639//Desktop//2022 Fall//CS 512//Project//YAGO_data//query_result_all_types.csv'
df = pd.read_csv(input_file)
filter_data = df[df['s'].str.contains('http://yago-knowledge.org/resource/')]
print(filter_data)
filter_data.to_csv('C://Users//28639//Desktop//2022 Fall//CS 512//Project//YAGO_data//query_result_all_yago_types.csv')