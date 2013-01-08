import WordNormalizer.LemmatizerModel;
import WordNormalizer.RussianPorterStemmer;
import WordNormalizer.TrainDataParser;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static String line;

    public static void main(String[] args) {

        // Opening train data file
        if (args.length != 1) {
            System.out.print("Train data file is not specified.");
            System.exit(0);
        }
        // Create an empty instance of model
        LemmatizerModel lemmatizer = new LemmatizerModel();

         // Optional stemmer can be used for cases when model couldn't find any lemma
        RussianPorterStemmer stemmer = new RussianPorterStemmer();

        // Creating parser and perform training action
        new TrainDataParser().LoadData(args[0], lemmatizer);

        // Process user input to show work of this prototype
        while (true) {

            // User input invitation
            System.out.print("input word form >>>");
            // reading russian word from stdin
            String word = getLine();

            // Get lemma
            ArrayList<String> lemmas = lemmatizer.getLemmas(word);

            if (lemmas != null) {
                System.out.print(lemmas);
            }
            else {
                System.out.print("Unknown word.");
                // Optional thing, perform simple stemming
                System.out.print("Try to stemm it: " + stemmer.do_stemming(word));
            }
            System.out.print("\n");
        }
    }

    // Java-related crap for line-by-line text file reading.
    public static String getLine() {
        java.io.BufferedReader stdin = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
        String line = null;
        try {
            line = stdin.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }
}
