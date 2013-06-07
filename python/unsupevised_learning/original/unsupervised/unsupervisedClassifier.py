# This Python file uses the following encoding: utf-8
#
import os, re, sys, time

from tools import individualSentences, when, runtime, chrono, feedbackMessage, counter


class Community:
    def __init__(self, oneSet, otherSet, shared):
        self.one = oneSet
        self.other = otherSet
        self.shared = shared


class Window:
    def __init__(self, value, span):
        self.start = value
        self.end = value + span


def inCommon(window, oneSpace, otherSpace):
    oneScan = set(oneSpace[window.start:window.end])
    otherScan = set(otherSpace[window.start:window.end])
    shared = oneScan.intersection(otherScan)
    return Community(oneScan, otherScan, shared)


def evaluate(sharedWords, noisiness):
# print len(sharedWords.shared)/float(len(sharedWords.one)),\
#     len(sharedWords.shared)/float(len(sharedWords.other))
    if len(sharedWords.shared) / float(len(sharedWords.one)) < noisiness \
        and len(sharedWords.shared) / float(len(sharedWords.other)) < noisiness:
        return False
    else:
        return True


def incrementalSweep(freqDistCorpus, freqDistBackground, windowSpan, noisiness):
    noisy = True
    oneSpace = [pair[1] for pair in freqDistCorpus]
    otherSpace = [pair[1] for pair in freqDistBackground]
    window = Window(0, windowSpan)
    while noisy == True:
        sharedWords = inCommon(window, oneSpace, otherSpace)
        noisy = evaluate(sharedWords, noisiness)
        #   print window.start,window.end, noisy, sharedWords.shared
        if window.end >= len(oneSpace) \
            or window.end >= len(otherSpace):
            exit('could not create a language model.')
        else:
            window = Window(window.start + 1, windowSpan)
    noise = [pair[1] for pair in counter(oneSpace[:window.end] + \
                                         otherSpace[:window.end]) if pair[0] > 1]
    return set(noise)


def splitter(text):
    splitted = [text]
    for separator in ['.', '?', '!']:
        moreItems = []
        for item in splitted:
            moreItems += item.split(separator)
        splitted = moreItems
    return [item for item in splitted \
            if item != '']


def segmentation(lines):
    sentences = []
    correlative, correlation = dict([]), 0
    buildUp = dict([])
    record = 0
    for doc in lines:
        newSentences = splitter(doc)
        sentences += newSentences
        for sentence in newSentences:
            correlative[correlation] = sentence
            buildUp[correlation] = record
            correlation += 1
        record += 1
    return [sentences, correlative, buildUp]


def tokenize(sentence):
    sentence = sentence.lower()
    words = sentence.split(' ')
    newWords = []
    for word in words:
        if re.match('[a-z]+', word):
            newWords.append(re.sub('[^a-z]', '', word))
        elif re.match('#[0-9]+', word):
            newWords.append(word)
    return newWords


def allWords(lines):
    words = []
    c, i, u, l = 1, 20, 0, len(lines)
    for text in lines:
        u = feedbackMessage(c, i, u, l, 'loading file:')
        words += tokenize(text)
        c += 1
    return words


def advancedRead(path, relevant):
    rd = open(path, 'r')
    lines = rd.readlines()
    rd.close()
    newLines = []
    entities = dict([])
    c, i, u, l = 1, 20, 0, len(lines)
    for line in lines:
        u = feedbackMessage(c, i, u, l, 'segmenting file:')
        #	recognition = entityRecognition(line.strip(), entities, noise)
        #	newLine = recognition[0]
        #	entities = recognition[1]
        newLines.append(line)
        #	newLines.append(newLine)
        c += 1
    return [newLines, entities]


def read(file, dump):
    rd = open(file, 'r')
    lines = rd.readlines()
    lines = [line.strip() for line in lines]
    rd.close()
    if dump == 'dump':
        return allWords(lines)
    else:
        return lines


def computeNoise(background, foreground):
    print 'filtering irrelevant words...'
    background = read(background, 'dump')
    foreground = read(foreground, 'dump')
    freqDistBackground = counter(background)
    freqDistForeground = counter(foreground)
    print 'done.\ncomputing noise...'
    noise = incrementalSweep(freqDistForeground, freqDistBackground, 200, 0.05)
    print 'done.'
    return [noise, freqDistForeground]


def findConfluenceDynamic(unique):
    tippingPoint = -1
    for height in sorted(unique.keys()):
        if unique[height] < height:
            tippingPoint = height
            break
    return tippingPoint


def findConfluence(counts):
    tippingPoint = -1
    unique = dict([])
    for count in counts:
        unique[count[0]] = []
    for count in counts:
        unique[count[0]].append(count[1])
    for height in sorted(unique.keys()):
        if len(unique[height]) < height:
            tippingPoint = height
            break
    return tippingPoint


def filterIrrelevantWords(freqDist, tippingPoint, args):
    print 'extracting relevant words...'
    if 'strict' in args:
        possible = set([count[1] for count in freqDist if count[0] >= tippingPoint])
    else:
        possible = set([count[1] for count in freqDist if count[0]])
    relevant = set([word for word in possible if word not in noise])
    print 'done.\nloading inputs...'
    return relevant


def abstract(correlative, correlativeDocs, sentences, relevant):
    abstractions = []
    originals = dict([])
    docsByLine = dict([])
    howMany = 0
    sentential = 0
    for sentence in sentences:
        abstraction = [word for word in sentence if word in relevant]
        abstraction = set([word for word in abstraction if word != ''])
        if len(abstraction) > 2:
            abstractions.append(list(abstraction))
            originals[howMany] = correlative[sentential]
            docsByLine[howMany] = correlativeDocs[sentential]
            howMany += 1
        sentential += 1
    return [abstractions, originals, docsByLine]


def trigrammatize(abstraction, allGrams):
    for i in range(0, len(abstraction) - 2):
        for j in range(i + 1, len(abstraction) - 1):
            for z in range(j + 1, len(abstraction)):
                gram = list(set([abstraction[i], abstraction[j], abstraction[z]]))
                if len(gram) > 2:
                    try:
                        where = allGrams[gram[0]]
                        try:
                            where = where[gram[1]]
                            try:
                                where[gram[2]] += 1
                            except Exception:
                                where[gram[2]] = 1
                        except Exception:
                            where[gram[1]] = dict([])
                            where[gram[1]][gram[2]] = 1
                    except Exception:
                        allGrams[gram[0]] = dict([])
                        allGrams[gram[0]][gram[1]] = dict([])
                        allGrams[gram[0]][gram[1]][gram[2]] = 1
    return allGrams


def quantize(abstractions):
    print 'calculating random trigrams...'
    allGrams = dict([])
    c, i, u, l = 1, 20, 0, len(abstractions)
    for abstraction in abstractions:
        u = feedbackMessage(c, i, u, l, 'generating n-grams:')
        allGrams = trigrammatize(abstraction, allGrams)
        c += 1
    ranks = []
    cc, i, u, l = 1, 20, 0, len(allGrams.keys())
    x = 0
    levels = dict([])
    for gram1 in allGrams.keys():
        u = feedbackMessage(cc, i, u, l, 'calculating n-gram frequencies:')
        for gram2 in allGrams[gram1].keys():
            for gram3 in allGrams[gram1][gram2].keys():
                score = allGrams[gram1][gram2][gram3]
                if score in levels.keys():
                    levels[score] += 1
                else:
                    levels[score] = 1
                gram = gram1 + '_' + gram2 + '_' + gram3
                ranks.append((score, gram))
                del allGrams[gram1][gram2][gram3]
                x += 1
        cc += 1
        #	print 'done. calculating tipping point...'
    #	tippingPoint = findConfluenceDynamic(levels)
    #	print 'done.\nfiltering data.\nextracting best feature sets...'
    ranks = [rank for rank in ranks if rank[0] >= tippingPoint]
    ranks.sort()
    ranks.reverse()
    print 'done.\nclustering...'
    return ranks


class Cluster:
    def __init__(self, trigram):
        self.trigram = trigram
        self.space = []
        self.features = set([])
        self.indexes = []
        self.vectors = []
        self.documents = []


def startClustering(trigrams):
    clusters = dict([])
    for trigram in trigrams:
        clusters[trigram] = Cluster(trigram)
    return clusters


def clusterize(freqDist, originalAbstractions, originals, \
               docsByLine, lines, returnIndividualSenteces):
    trigrams = [pair[1] for pair in freqDist]
    clusters = startClustering(trigrams)
    abstractions = [set(abstraction) for abstraction in originalAbstractions]
    territory = [originals[z] for z in range(0, len(abstractions))]
    indexes = range(0, len(abstractions))
    print len(abstractions), 'initial vectors,', len(trigrams), \
        'initial feature sets'
    c, i, u, l = 1, 20, 0, len(trigrams)
    #   abstractions = list of sentences without noise
    while abstractions and trigrams:
        c += 1
        u = feedbackMessage(c, i, u, l, 'clustering: ' + \
                                        str(len(abstractions)) + ' remaining sentence vectors, ' + \
                                        str(len(trigrams)) + ' remaining feature sets -')
        trigram = trigrams[0]
        features = set(trigram.split('_'))
        unclassifiedAbstractions = []
        unclassifiedOriginals = []
        unclassifiedIndexes = []
        for j in range(0, len(abstractions)):
            abstraction = abstractions[j]
            #	print features,'\t',abstraction,'\t',features.intersection(abstraction)
            if features.intersection(abstraction) == features:
                where = clusters[trigram]
                if returnIndividualSenteces == True:
                    where.documents.append(territory[j])
                    where.indexes.append(indexes[j])
                else:
                    original = lines[docsByLine[indexes[j]]]
                    original = original[original.index(', ') + 2:original.index(', ') + 102]
                    where.documents.append(original)
                    where.indexes.append(docsByLine[indexes[j]])
                where.vectors.append(abstraction)
                where.space += list(abstraction)
                #		else:
                #			unclassifiedAbstractions.append(abstraction)
                #			unclassifiedOriginals.append(territory[j])
                #			unclassifiedIndexes.append(indexes[j])
                #	abstractions = unclassifiedAbstractions
                #	territory = unclassifiedOriginals
                #	indexes = unclassifiedIndexes
        trigrams = trigrams[1:]
    if returnIndividualSenteces == False:
        for key in clusters.keys():
            clusters[key].documents = sorted(list(set(clusters[key].documents)))
            clusters[key].indexes = sorted(list(set(clusters[key].indexes)))
    return clusters


def orderClusters(clusters):
    ranks = []
    for key in clusters.keys():
        score = len(clusters[key].indexes)
        if score > 0:
            ranks.append((score, key))
    ranks.sort()
    ranks.reverse()
    return ranks


def purge(ranks, clusters):
    for rank in ranks:
        key = rank[1]
        oneCluster = clusters[key]
        freqDist = counter(oneCluster.space)
        tippingPoint = findConfluence(freqDist)
        features = set([count[1] for count in freqDist if count[0] > tippingPoint])
        oneCluster.features = features
        #
    newRanks = []
    print len(ranks), 'incoming clusters and...'
    while ranks:
        key = ranks[0][1]
        validated = False
        remaining = []
        oneCluster = clusters[key]
        #
        for j in range(1, len(ranks)):
            otherKey = ranks[j][1]
            otherCluster = clusters[otherKey]
            shared = oneCluster.features.intersection(otherCluster.features)
            if shared != set([]):
                if (len(shared) * 100) / len(oneCluster.features) < 50 and \
                                        (len(shared) * 100) / len(otherCluster.features) < 50 and \
                                otherCluster.features != set([]):
                    remaining.append(ranks[j])
                    validated = True
            elif otherCluster.features != set([]):
                remaining.append(ranks[j])
                validated = True
            #
        if oneCluster.features != set([]) and \
                        validated == True:
            newRanks.append((ranks[0]))
        ranks = remaining
    print '...', len(newRanks), 'outgoing clusters.'
    return newRanks


def save(folder, ranks, top, originals):
    outFolder = re.sub("^.*/", "", folder)
    outFolder = re.sub("\..*$", "", outFolder)
    if os.path.exists(outFolder):
        os.system('rm -r ' + outFolder + '; mkdir ' + outFolder)
    else:
        os.system('mkdir ' + outFolder)
    index = []
    for rank in ranks:
        lines = []
        header = str(rank[1]) + '\t' + str(len(clusters[rank[1]].documents))
        lines.append(header)
        index.append(re.sub("_", ",", header))
        for j in range(0, len(clusters[rank[1]].documents)):
            doc = clusters[rank[1]].documents[j]
            entry = '\t' + str(clusters[rank[1]].indexes[j]) + '\t' + doc
            lines.append(entry)
        lines.append('\n')
        wrt = open(outFolder + '/' + \
                   str(len(clusters[rank[1]].documents)) + \
                   '-' + rank[1] + '.out.txt', 'w')
        wrt.write('\n'.join(lines))
        wrt.close()

    rpt = open(outFolder + '/index.txt', 'w')
    rpt.write('\n'.join(index))
    rpt.close()

    print len(ranks), 'non-zero positive clusters for', top, 'documents'
    print sum([r[0] for r in ranks]) / len(ranks), 'average cluster size'


background = sys.argv[1]
foreground = sys.argv[2]
start1 = when()
computation = computeNoise(background, foreground)
chrono(start1)
print '----- completed stage 0 ----- (=)'
noise = computation[0]
freqDist = computation[1]
tippingPoint = findConfluence(freqDist)
relevant = filterIrrelevantWords(freqDist, tippingPoint, sys.argv)
chrono(start1)
print '----- completed stage 1 -----'

start2 = when()
reading = advancedRead(foreground, relevant)
lines = reading[0]
segments = segmentation(lines)
tokenized = [tokenize(sentence) for sentence in segments[0]]
chrono(start2)
print '----- completed stage 2 -----'

start3 = when()
refined = abstract(segments[1], segments[2], tokenized, relevant)
abstractions = refined[0]
originals = refined[1]
docsByLine = refined[2]
chrono(start3)
print '----- completed stage 3 -----'

start4 = when()
topDist = quantize(abstractions)
chrono(start4)
print '----- completed stage 4 -----'

start5 = when()
clusters = clusterize(topDist, abstractions, originals, \
                      docsByLine, lines, individualSentences(sys.argv))
chrono(start5)
print '----- completed stage 5 -----'

start6 = when()
ranks = orderClusters(clusters)
chrono(start6)
print '----- completed stage 6 -----'

start7 = when()
ranks = purge(ranks, clusters)
chrono(start7)
print '----- completed stage 7 -----'
chrono(start1)
print '----- completed main stages ----- (=)'

save(sys.argv[2], ranks, len(lines), originals)
print '----- saved ----- (=)'
chrono(start1)