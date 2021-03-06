/*
 * Corpus.java
 *
 * Written by: Adam Goforth
 * Started on: Dec 4, 2005
 */
package jxtract;

import java.io.*;
import java.util.*;


/**
 * The Corpus class represents one or more text files that make up a corpus.
 *
 * @author Adam Goforth
 */
public class Corpus {

    String filename;
    File f;
    FileInputStream fis;
    BufferedReader bReader;

    /**
     * Constructor for the Corpus.
     * Currently only supports a file, but should support a directory
     * with multiple files.
     *
     * @param filename_ The text file that contains the corpus.
     */
    public Corpus(String filename_) {
        filename = filename_;
    } // End constructor

    /**
     * Open the file for reading.
     *
     * @return <code>true</code> if the open succeeded, <code>false</code>
     * if the open failed.
     */
    public boolean openFile() {
        f = null;
        fis = null;
        BufferedInputStream bis;
        bReader = null;

        boolean retVal = true;

        try {
            f = new File(filename);
            fis = new FileInputStream(f);
            // Supposed to be faster if a BufferedInputStream is used.
            bis = new BufferedInputStream(fis);
            bReader = new BufferedReader(new InputStreamReader(bis));
        } catch (IOException e) {
            // Catch IO errors from FileInputStream
            System.out.println("Error opening file: " + e.getMessage());
            retVal = false;
        }

        return retVal;
    }

    /**
     * Close the file if it was open.
     *
     * @return <code>true</code> if the file was already closed or the
     * operation closed it. <code>false</code> if the file is still
     * open after execution.
     */
    public boolean closeFile() {
        boolean retVal = true;

        //	If the file is open, close it.
        if (bReader != null) {
            try {
                bReader.close();
            } catch (IOException ioe) {
                retVal = false;
            }
        }
        return retVal;
    }

    /**
     * This returns a Vector of Strings, where each String is a sentence in the
     * Corpus that contains the specified word.
     *
     * @param word_ The word that is searched for in the corpus.
     * @return The Vector of Strings with the sentences.
     */
    public Vector<String> getSentencesWith(String word_) {
        Vector<String> foundSentences = new Vector<>();
        // Open the file on the disk
        openFile();
        if (bReader != null) {
            String record;
            try {
                while ((record = bReader.readLine()) != null) {
                    if (record.contains(" " + word_ + " ") || record.startsWith(word_ + " ") || record.endsWith(" " + word_)) {
                        foundSentences.add(record);
                    }
                }
            } catch (IOException e) {
                // Catch IO errors from FileInputStream
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        //DEBUG System.out.println("Found " + foundSentences.size() + " sentences.");
        // Close the file.  Must be done before the return.
        closeFile();
        return foundSentences;
    }

    /**
     * This returns a Vector of Strings, where each String is a sentence in the
     * Corpus that contains the specified words, with <code>w2</code> being
     * <code>distance</code> words away from <code>w1</code>.
     *
     * @param w1       The first word
     * @param w2       The second word
     * @param distance The distance between them. -5 to -1 and 1 to 5 are valid values.
     * @return A Vector of Strings with one matched sentence per string.
     */
    public Vector<String> getSentencesWith(String w1, String w2, int distance) {
        Vector<String> foundSentences = new Vector<>();
        // Open the file on the disk
        openFile();
        if (bReader != null) {
            String record;
            try {
                while ((record = bReader.readLine()) != null) {
                    //	Remove punctuation
                    record = record.replaceAll(" (\\.|!|\\?|,|;|:|\\-|\\(|\\)|\"|%|#)", "");

                    //  Split the sentence up into "words" (characters separated by at least one space)
                    String[] words = record.split(" ");

                    // Find w1 and see if it's the proper distance from w2
                    // There may be more than one instace of w1, so make sure we
                    // check all of them
                    boolean addS = false;
                    for (int i = 0; i < words.length; i++) {
                        // Found an instance of w1
                        if (words[i].equals(w1)) {
                            // Distance is not outside of the sentence
                            if (i + distance >= 0 && i + distance < words.length) {
                                if (words[i + distance].equals(w2)) {
                                    addS = true;
                                }
                            }
                        }
                    } // End for

                    // If we found a match, add the sentence to the Vector
                    if (addS) {
                        foundSentences.add(record);
                    }
                }
            } catch (IOException e) {
                // Catch IO errors from FileInputStream
                System.out.println("Error reading file: " + e.getMessage());
            }
        }
        //DEBUG System.out.println("Found " + foundSentences.size() + " sentences.");
        // Close the file.  Must be done before the return.
        closeFile();
        return foundSentences;
    } // End getSentencesWith(String w1, String w2, int distance)

    public Vector<String> getFrequentWords(int minFrequency) {
        Vector<String> freqWords = new Vector<>();
        // Define a Map and create a HashMap
        Map<String, Integer> map = new HashMap<>();
        final Integer ONE = 1;

        openFile();
        String record;

        try {
            while ((record = bReader.readLine()) != null) {
                // System.out.println(recCount + ": " + record);

                // Split words
                StringTokenizer tokenizer = new StringTokenizer(record, " ");

                while (tokenizer.hasMoreTokens()) {
                    // Get word
                    String key = tokenizer.nextToken();

                    if (!key.matches("(\\.|!|\\?|,|;|:|\\-|\\(|\\)|\"|%|#|'s)")) {

                        if (!key.matches("(was|am|has|the|a|and|be|but|by|can|such|could|do|for|have|him|her|i|is|we|he|she|it|may|might|mine|must|need|no|not|nor|none|our|where|whether|while|which|you|your|to|of|on|with|in|so|or|my|its|if|his|hers|as|an|at|this|they|there|then|that|are|would|who|whom|them|each|from|ourselves|when|these)")) {
                            // Get frequency
                            // If not found, add word to map with count one
                            // Otherwise, add to map with old count + 1

                            Integer frequency = map.get(key);
                            if (frequency == null) {
                                frequency = ONE;
                            } else {
                                frequency = frequency + 1;
                            }
                            map.put(key, frequency);
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Catch IO errors from FileInputStream
            System.out.println("Error reading file: " + e.getMessage());
        }

        // Pull out the frequent words and add them to freqWords
        Set<String> keys = map.keySet();
        Iterator it = keys.iterator();
        int theFreq;
        while (it.hasNext()) {
            // Get Bigram
            String key = (String) it.next();
            theFreq = map.get(key);

            if (theFreq >= minFrequency) {
                //DEBUG freqWords.add(key + " " + theFreq);
                freqWords.add(key);
            }
        }


        closeFile();
        return freqWords;
    } // End getFrequentWords
}
