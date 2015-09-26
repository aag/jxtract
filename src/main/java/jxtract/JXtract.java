/**
 * JXtract.java
 * JXtract is a Java implementation of the Xtract
 * tool described by Frank Smadja in his 1993
 * ACL paper "Retrieving Collocations from Text: Xtract"
 * <p/>
 * Written by: Adam Goforth
 * Started on: Dec 4, 2005
 */


package jxtract;

import java.util.Vector;


/**
 * @author Adam Goforth
 */

public class JXtract {

    private Corpus corpus;
    private boolean getFrequencies;
    private boolean showHelp;
    private int minFrequency;
    private String sourcefilename;
    private String word;

    /**
     * Constructor
     */
    public JXtract() {
        getFrequencies = false;
        minFrequency = 1000;
        showHelp = false;
        sourcefilename = "";
        word = "";
    }

    public static void main(String[] args) {
        JXtract xtractor = new JXtract();
        xtractor.parseProgArgs(args);
    }

    /**
     * showHelp
     * Prints the command line help for JXtract.
     */
    private static void showHelp() {
        System.out.println("JXtract: a collocation extractor");
        System.out.println("Usage: JXtract -source filename [-printfrequencies [-minfrequency frequency]] [-word word]");
        System.out.println("Example: JXtract -source ep-00-en.txt -word European");
        System.out.println("");
        System.out.println("Arguments:");
        System.out.println("-source\t\t\tThe corpus file.  Must be English language.");
        System.out.println("-word\t\t\tThe word that will be used to search for collocations");
        System.out.println("-printfrequencies\t(Optional) An alternate mode to finding collocations.  JXtract can also be used to");
        System.out.println("\t\t\tfind the most frequent words in a file");
        System.out.println("-minfrequency\t\t(Optional)If JXtract is used to get word frequencies, this determines the lower");
        System.out.println("\t\t\tfrequency threshold, below which less frequently appearing words will not be reported");
    }

    /**
     * getCollocations()
     * This runs the Xtract algorithm on a word and corpus and
     * returns the labelled collocations for that word.
     */
    private void getCollocations(String w) {
        System.out.println("Finding collocations containing the word " + w);
        w = w.toLowerCase();
        Vector foundSentences = corpus.getSentencesWith(w);

        BigramCollection bigrams = new BigramCollection();
        try {
            for (int i = 0; i < foundSentences.size(); i++) {
                //System.out.println("Found sentence: " + foundSentences.get(i));
                bigrams.addSentence(w, (String) foundSentences.get(i), false);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
        //System.out.println("\n" + bigrams.getTable4());
        Vector postStage1 = bigrams.getStageOneBigrams(1, 1, 10);

        //DEBUG System.out.println("w\twi\tstrength\t\tspread\tdistance");
        for (int i = 0; i < postStage1.size(); i++) {
            S1Bigram tempBG = (S1Bigram) postStage1.get(i);

            for (int j = 0; j < tempBG.getDistances().size(); j++) {
                //DEBUG System.out.println(tempBG.getw() + "\t" + tempBG.getwi() + "\t" + tempBG.getStrength() + "\t" + tempBG.getSpread() + "\t" + tempBG.getDistances().get(j));
                Vector stage2sentences = corpus.getSentencesWith(
                        tempBG.getw(),
                        tempBG.getwi(),
                        ((Integer) tempBG.getDistances().get(j)).intValue()
                );
                BigramCollection s2bigrams = new BigramCollection();

                // Add all sentences with this bigram to a collection
                try {
                    for (int k = 0; k < stage2sentences.size(); k++) {
                        //System.out.println("Found sentence: " + stage2sentences.get(k));
                        s2bigrams.addSentence(w, (String) stage2sentences.get(k), true);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                }
                //System.out.println("\n" + s2bigrams.getTable2());
                //System.out.println("^-- " + tempBG.getw() + " " + tempBG.getwi());
                s2bigrams.stage2(0.75);
                //System.out.println(" ");
            }

        }

    } // End getCollocations()

    /**
     * printFrequentWords
     * Print frequent words in the corpus
     */
    private void printFrequentWords(int freq) {
        Vector words = new Vector();
        words = corpus.getFrequentWords(freq);
        for (int i = 0; i < words.size(); i++) {
            System.out.println(words.get(i));
        }
    }

    /**
     * Set class' variables based on command line arguments.
     *
     * @param args Command line arguments.
     */
    public void parseProgArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-printfrequencies")) {
                getFrequencies = true;
            } else if (args[i].equals("-minfrequency")) {
                minFrequency = Integer.parseInt(args[i + 1]);
            } else if (args[i].equals("-source")) {
                sourcefilename = args[i + 1];
            } else if (args[i].equals("-word")) {
                word = args[i + 1];
            } else if (args[i].equals("-h") || args[i].equals("-help") || args[i].equals("--help")) {
                showHelp = true;
            }
        }

        if (showHelp) {
            showHelp();
            System.exit(0);
        }

        // Make sure source is given
        if (!sourcefilename.equals("")) {
            corpus = new Corpus(sourcefilename);

            // Either get word frequencies or find collocations
            if (getFrequencies) {
                printFrequentWords(minFrequency);
            } else if (!word.equals("")) {
                getCollocations(word);
            }

        } else {
            showHelp();
            System.exit(0);
        }
    }
}
