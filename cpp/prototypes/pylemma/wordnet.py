# -*- coding: utf-8 -*-

import json
from pprint import pprint

def to_json(python_object):
    if isinstance(python_object, bytes):
        return {'__class__': 'bytes',
            '__value__': list(python_object)}
    raise TypeError(repr(python_object) + ' is not JSON serializable')

class Dawg(object):

    def __init__(self):
        self.tree = {'': {}}

    def add_form(self, word_form, index):
        node = self.tree
        for ch in word_form:
            if not ch in node:
                node[ch] = {}
            node = node[ch]
        node['lemma'] = index


class word_net:
    def __init__(self):
        self.lemmas = []
        self.dawg = Dawg()

    def add(self, word_form, lemma):

        index = -1
        try:
            index = self.lemmas.index(lemma)
        except Exception as e:
            pass #print(e)

        if index < 0:
            self.lemmas.append(lemma)
            index = len(self.lemmas) - 1

        self.dawg.add_form(word_form, index)
        # self.dawg.add_form(word_form, lemma)

    def print(self):
        pprint(self.dawg.tree)

#        data = json.dumps(self.dawg)
#        f = open('model.dat','w')
#        f.write(data)
#        f.close()

