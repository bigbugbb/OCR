from __future__ import print_function
import sys
import csv
import copy
import random
    
def load_original_dataset(path):
    data = []
    
    with open(path, 'r', 4096) as csvfile:
        dialect = csv.Sniffer().sniff(csvfile.read(), delimiters='\t ')
        csvfile.seek(0)
        reader = csv.reader(csvfile, dialect)
  
        for row in reader:
            v = [attr.strip() for attr in row[6:-1]]
            v.append(row[1].strip())
            data.append(v)

    return data

def create_our_dataset(data, extra_feature=False):
    
    length = len(data)
    
    # randomly choose 10% data as the test data
    with open('test.csv', 'wb') as csvfile_test:
        writer = csv.writer(csvfile_test, delimiter='\t')
        for i in range(0, length / 10):
            idx = random.randint(1, len(data))
            writer.writerow(data[idx])
            del data[idx]

    temp = copy.deepcopy(data)
    length = len(data)
    with open('training.csv', 'wb') as csvfile:
        writer = csv.writer(csvfile, delimiter='\t')
        writer.writerows(data)

    if extra_feature:
        add_extra_features('test.csv', 'test_extra.csv')
        add_extra_features('training.csv', 'training_extra.csv')

    convert_to_arff('test.csv', 'test.arff')
    convert_to_arff('training.csv', 'training.arff')

    if extra_feature:
        convert_to_arff('test_extra.csv', 'test_extra.arff', True)
        convert_to_arff('training_extra.csv', 'training_extra.arff', True)

def create_sub_dataset(path, postfix):
    data = []
    
    with open(path, 'r', 4096) as csvfile:
        dialect = csv.Sniffer().sniff(csvfile.read(), delimiters='\t ')
        csvfile.seek(0)
        reader = csv.reader(csvfile, dialect)
        
        for row in reader:
            data.append([attr.strip() for attr in row])

    prefix = path[:path.find('.')] + '_'
    
    for c in postfix:
        with open(prefix + c + '.csv', 'wb') as csvfile:
            writer = csv.writer(csvfile, delimiter='\t')
            for i, v in enumerate(data):
                if v[-1] == c:
                    writer.writerow(v)

def load_dataset(path):
    data   = []
    labels = []
    
    with open(path, 'r', 4096) as csvfile:
        dialect = csv.Sniffer().sniff(csvfile.read(), delimiters='\t ')
        csvfile.seek(0)
        reader = csv.reader(csvfile, dialect)
        
        for row in reader:
            data.append([attr.strip() for attr in row[:-1]])
            labels.append(row[-1])
    
    return data, labels

def add_extra_features(input, output):
    data, labels = load_dataset(input)
    
    new_data = []

    for row in data:
        extra = []
        for i in range(16):
            sum = 0
            for j in range(8):
                if row[i * 8 + j] == '1':
                    sum += 1
            extra.append(sum)
        for j in range(8):
            sum = 0
            for i in range(16):
                if row[i * 8 + j] == '1':
                    sum += 1
            extra.append(sum)
        row.extend(extra)
        new_data.append(row)

    with open(output, 'wb') as csvfile:
        writer = csv.writer(csvfile, delimiter='\t')
        for i, row in enumerate(new_data):
            row.extend(labels[i])
        writer.writerows(new_data)

def convert_to_arff(input, output, extra_feature=False):
    
    with open(output, 'wb') as arff_file:
        arff_file.write('@RELATION test\n')
        length = 128
        if extra_feature: length += 24
        for i in range(0, length):
            arff_file.write('@ATTRIBUTE ' + str(i) + ' NUMERIC\n')
        arff_file.write('@ATTRIBUTE label {a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z}\n')
        arff_file.write('@DATA\n')
        
        with open(input, 'r', 4096) as csvfile:
            dialect = csv.Sniffer().sniff(csvfile.read(), delimiters='\t ')
            csvfile.seek(0)
            reader = csv.reader(csvfile, dialect)
                        
            for row in reader:
                data = ''
                for i, v in enumerate(row):
                    if i != len(row) - 1:
                        data += v + ','
                    else:
                        data += v
                data += '\n'
                arff_file.write(data)
        
        arff_file.close()

if __name__ == "__main__":
    create_our_dataset(load_original_dataset(sys.argv[1]), True)




