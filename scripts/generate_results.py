#!/usr/bin/env python
# vim: set sw=4 sts=4 et foldmethod=indent :

import sys

configurations = []

class Config(object):
    def __init__(self, name, dataset):
        self.name = name
        self.dataset = dataset

def print_configuration(configuration):
    return ''.join(['<td>' + str(field) + '</td>' for field in [configuration.name, configuration.dataset, configuration.recall, configuration.precision]])

def print_configurations(configurations):
    configurations_list = list(configurations.values())
    configurations_list.sort(key=lambda x: x.name)
    return '\n'.join(['<tr>' + print_configuration(config) + '</tr>' for config in configurations_list])

def print_table(configurations):
    return '''<table border="1"><thead>
            <th>classname</th><th>dataset</th><th>recall</th><th>precision</th>
            %s
            </thead></table>''' % print_configurations(configurations)

if __name__ == '__main__':
    input_string = sys.stdin.read()
    lines = filter(None, input_string.split('\n'))
    configurations = {}
    for line in lines:
        parts = line.split()
        metric = parts[6]
        dataset = parts[8]
        classname = parts[3]

        config = configurations.get(classname + '_' + dataset)
        if config is None:
            config = Config(classname, dataset)
        configurations[classname + '_' + dataset] = config
        value = parts[11]
        if metric == 'precision':
            config.precision = value
        elif metric == 'recall':
            config.recall = value
        else:
            raise 'Invalid metric'

    output = print_table(configurations)
    print(output)
