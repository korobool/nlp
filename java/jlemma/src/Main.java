import WordNormalizer.LemmatizerModel;
import WordNormalizer.RussianPorterStemmer;
import WordNormalizer.TrainDataParser;

import java.io.IOException;
import java.util.ArrayList;

public class Main {

    private static String line;

    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.print("Train data file is not specified.");
            System.exit(0);
        }
        LemmatizerModel lemmatizer = new LemmatizerModel();

        // According to task we have to provide a way how to pack the size of model
        lemmatizer.setCompressionAspect(1.0);
        lemmatizer.setCollectMode(1);

        // Optional stemmer can be used for cases when model couldn't find any lemma
        RussianPorterStemmer stemmer = new RussianPorterStemmer();

        new TrainDataParser().LoadData(args[0], lemmatizer);

        while (true) {
            System.out.print("input word form >>>");
            String word = getLine();

            ArrayList<String> lemmas = lemmatizer.getLemmas(word);

            if (lemmas != null) {
                System.out.print(lemmas);
            }
            else {
                System.out.print("Unknown word.");
                // Optional thing, uncomment it to allow simple stemming
                System.out.print("Try to stemm it: " + stemmer.stem(word));
            }
            System.out.print("\n");
        }
    }

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
