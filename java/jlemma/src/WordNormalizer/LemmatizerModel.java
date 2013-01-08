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

    private static final Pattern RUSSIAN_WORD = Pattern.compile("[А-Яа-я-]+"); //[\p{IsCyrillic}]  Russian words

    private final Map tree = new TreeMap<Character, Object>(); // The root node for prefix tree

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

        // traversing of the leaf we're looking for. adding virtual nodes on the fly.
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
                // Step down in the depth
                node = next_node;
            }
            else {
                 return null;
            }
        }

        // Saving of actual lemmas.
        ArrayList<String> l = (ArrayList<String>)node.get('#');
        if (l != null) {
            word_lemmas.addAll((ArrayList<String>)node.get('#'));
        }
        else {
            return null;
        }

        return word_lemmas;
    }
}
