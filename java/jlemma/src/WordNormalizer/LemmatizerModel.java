package WordNormalizer;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;


// This class represents the model of lemmatizer. It is based on prefix (tier) tree with lemmas as leaves.
// Stored in memory. Prototype model can be optimized for memory usage by replacing "node as object" tree
// structure to compressed heap based or custom binary model. In case of low memory application we can move
// model to hard drive, cause quantity of access on getting lemma is no larger then word length and won't
// become a bottle neck.
public class LemmatizerModel {

    private static final int TAIL_CUT_THRESHOLD = 4; // Maximum count of lemmas to return
    private double compressionAspect = 1; // Reduces the size of model and lemmatization accuracy

    private static final Pattern RUSSIAN_WORD = Pattern.compile("[А-Яа-я-]+"); //[\p{IsCyrillic}]  Russian words

    private final Map tree = new TreeMap<Character, Object>(); // The root node for prefix tree
    private int lemmasCount = 0; // Maximum count of lemmas to return. Return only one lemma if exists.

    // It is great to store lemmas strings in special container, but Java supports strings pool.
    // Resulting logic will be based on strings pool automatically. So, this is redundant for java.
    // private ArrayList<String> lemmas = new ArrayList<String>();

    // Train model entry. Stores the word and its lemma in a compressed model.
    public void addWordForm(String wordForm, String lemma) {
        // Validation
        if (!(is_valid(wordForm) && is_valid(lemma)))
            return;

        // Selecting root for DFS prefix tree traversing while adding
        Map<Character, Object> node = tree;

        // The actual length of path. By default is equal to length of word
        int tailPosition = wordForm.length();

        // Applying path reducing to make model size smaller (unfortunately quality can be lost)
        if (this.compressionAspect < 1.0 && tailPosition > this.TAIL_CUT_THRESHOLD) {
            tailPosition = (int)(tailPosition * this.compressionAspect); // implicit decimal truncation to int
        }

        // traversing of the leave we're looking for. adding virtual nodes on the fly.
        for (int index=0; index < tailPosition; index++) {

            // Getting corresponding node
            Map<Character, Object> next_node = (Map)node.get(wordForm.charAt(index));
            // Check if node is in the tree
            if (next_node != null) {
                // Setting found node as current
                node = next_node;
            }
            else {
                // if node wasn't found we have to create it and select as current node
                TreeMap<Character, Object> map = new TreeMap<Character, Object>();
                node.put(wordForm.charAt(index) ,map);

                // Setting it as a current node
                node = map;
            }
        }

        // int lemmaIndex = getLemmaIndex(lemma);

        // Check is there any lemma for this path and add it if it doesn't exist
        ArrayList<String> word_lemmas = (ArrayList<String> )node.get('#');
        // If no lemma found in this node we should crate it.
        if (word_lemmas == null) {
            word_lemmas = new ArrayList<String>();
            node.put('#', word_lemmas);
        }

        // Check is lemma in list
        int index = word_lemmas.indexOf(lemma);
        if (index < 0)
            word_lemmas.add(lemma);

        // Verbose training output.
        System.out.print("Learning WordForm: " + wordForm + "\n");
    }

    // Optional way to access available lemmas (in case of storing lemmas in special data structure)
//    private int getLemmaIndex(String lemma) {
//
//        int lemmaIndex = lemmas.indexOf(lemma);
//
//        if (lemmaIndex < 0)  {
//            lemmas.add(lemma);
//            lemmaIndex = getLemmaIndex(lemma); // to make it fast we can optimize search from O(n) to O(log(N))
//        }
//        return lemmaIndex;
//    }

    // Validation for non-empty russian word
    private boolean is_valid(String line) {
        if (line == null || line.length() < 1)
            return false;
        return RUSSIAN_WORD.matcher(line).matches();
    }

    // Retrieve lemma by prefix tree search
    public ArrayList<String> getLemmas(String word) {
        // Search root
        Map<Character, Object> node = tree;

        // Lemmas container
        ArrayList<String> word_lemmas = new ArrayList<String>();

        // DFS searching
        for (int index=0; index < word.length(); index++) {

            Map<Character, Object> next_node = (Map)node.get(word.charAt(index));
            if (next_node != null) {
                if (this.lemmasCount > 0) {
                    ArrayList<String> l = (ArrayList<String>)node.get('#');
                    if (l != null) {
                        // Store all available lemmas in the path
                        word_lemmas.addAll((ArrayList<String>)node.get('#'));
                    }
                }
                // Step down in the depth
                node = next_node;
            }
            else {
                if (this.compressionAspect == 1.0)
                    return null;
                else {
                    // if compression is allowed we can use some kind of heuristic to detect actual form. Here is
                    // naive implementation (heuristic can be based on RussianPorterStemmer class )
                    return getTail(word_lemmas, word.length());
                }
            }
        }

        // Saving of actual lemmas.
        ArrayList<String> l = (ArrayList<String>)node.get('#');
        if (l != null) {
            word_lemmas.addAll((ArrayList<String>)node.get('#'));
        };

        if (this.lemmasCount != 0 ) {
            return getTail(word_lemmas, word.length());
        }

        return word_lemmas;
    }


    // In the tree - path we have a lot of lemmas. Here we can apply heuristic to detect closest option(s).
    private ArrayList<String> getTail(ArrayList<String> word_lemmas, int maxWordLength) {
        ArrayList<String> tail = new ArrayList<String>();
        int index = word_lemmas.size() - 1;

        // Here we can apply heuristic analysis to get the most applicable lemma
        // At hte moment it is naive and imperfect.
        for (int i = index; tail.size() <= this.lemmasCount || i < 1; i--) {

            String word = word_lemmas.get(i);
            if (word.length() <= maxWordLength)
                tail.add(word);
        }
        return tail;
    }

    // Setter for possible compression aspect
    public void setCompressionAspect(double compressionAspect) {
        if (compressionAspect > 1.0 || compressionAspect < .3 )
            return;

        this.compressionAspect = compressionAspect;

        if (compressionAspect == 1.0) {
            // We have to specify collecting mode
            this.setCollectMode(0);
        }
        else {
            // We have to specify collecting mode
            this.setCollectMode(3);
        }
    }

    // Sets collecting mode. 0 - do not collect path-lemmas
    public void setCollectMode(int lemmasCount) {
        this.lemmasCount = lemmasCount;
    }
}
