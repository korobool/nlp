from wordnet import word_net


tier = word_net()

def process_line(line):
    items = line.strip('\n').split(',')
    if len(items) != 2:
        return
    tier.add(items[0], items[1])



for line in open('lemmas-rus-training.txt'):
    process_line(line)

tier.print()