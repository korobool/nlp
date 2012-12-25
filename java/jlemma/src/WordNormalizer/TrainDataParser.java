package WordNormalizer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Auxiliary. Class helps to parse data-entries in training data file.
// It reads entries from file and sends them to train method in the model
public class TrainDataParser {

    // Words model (word prefix tree is expected)
    private LemmatizerModel words;

    // Opens the file and parses it.
    public void LoadData(String dataFile, LemmatizerModel words) {
        // Open input file with train data
        this.words = words;
        try {
            BufferedReader br = new BufferedReader(new FileReader(dataFile));
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
            br.close();
        }
        catch (IOException e)
        {
              System.err.print("Couldn't open file.");
        }
    }

    // processes single line. splits it and perform basic validation.
    private void processLine(String line) {
        // ё  symbol is no longer used in natural language processing in order to reduce space size and
        // follow language development trends.
        String[] items = line.toLowerCase().replace('ё','е').trim().split(",");

        // Validation 2
        if (items.length != 2) {
            return;
        }

        // It is expected that first word is the word form and second - its lemma.
        String wordForm = items[0];
        String lemma = items[1];

        // Train model
        words.addWordForm(wordForm, lemma);
    }
}
