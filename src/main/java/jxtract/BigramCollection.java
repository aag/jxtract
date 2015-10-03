/**
 * BigramCollection.java
 * A class for the JXtract collocation software.
 * <p/>
 * Written by: Adam Goforth
 * Started on: Dec 4, 2005
 */
package jxtract;

import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;


/**
 * BigramCollection is the datastructure that contains information about a word
 * and all of the bigrams that are within a distance of 5 words from it in any
 * sentence in the corpus.
 *
 * @author Adam Goforth
 */
public class BigramCollection {
    TreeMap<String, Bigram> bigrams;
    double wFreq;

    /**
     * Constructor
     */
    public BigramCollection() {
        bigrams = new TreeMap<>();
        wFreq = 0;
    }

    /**
     * Takes a sentence and adds all bigrams in the "phrase" (+/- 5 words) to
     * the collection. Note: The p value of the wi word is determined based on
     * the examples in Table 2 of the Smadja paper, not the examples in the Step
     * 1.2 description. These examples seem to be contradictory, and the
     * convention of "p+1 means wi is one word to the right of w" seems more
     * intuitive.
     *
     * @param s The sentence to be added.
     * @throws Exception If given sentence does not contain the word this
     *                   BigramCollection tracks.
     */
    public void addSentence(String w, String s, boolean includeClosedClass) throws Exception {
        int wIndex = 0;

        // Remove punctuation
        s = s.replaceAll(" (\\.|!|\\?|,|;|:|\\-|\\(|\\)|\"|%|#)", "");

        //DEBUG System.out.println("Cleaned sentence: " + s);

        // Split the sentence up into "words" (characters separated by at least one space)
        String[] words = s.split(" ");

        boolean found = false;
        // Find w
        for (int i = 0; i < words.length; i++) {
            //DEBUG System.out.println("Comparing w: " + w + " with words[i]: \'" + words[i] + "\'");
            if (w.equals(words[i])) {
                wIndex = i;
                found = true;
            }
        }

        // Throw an exception if the sentence doesn't contain the whole word w
        if (!found) {
            throw new Exception(
                    "Could not add sentences: sentence does not contain word \""
                            + w + "\"\nSentence: " + s);
        } else {


            //DEBUG System.out.println("Index of w is: " + wIndex);

            // Go through all the Bigram matchups (+/- 5 words) and insert them
            int startIndex = wIndex - 5;
            if (startIndex < 0) {
                startIndex = 0;
            }

            // Loop through the 10 surrounding words, but don't fall off the end
            // of the array
            for (int i = startIndex; (i < words.length) && (i <= wIndex + 5); i++) {
                // Don't insert w
                // Poor man's closed-class word exclusion
                // TODO Replace with tag recognition
                if (!w.equals(words[i]) && (includeClosedClass || !words[i].matches("(the|a|and|be|but|by|can|such|could|do|for|have|him|her|i|is|we|he|she|it|may|might|mine|must|need|no|not|nor|none|our|where|whether|while|which|you|your|to|of|on|with|in|so|or|my|its|if|his|hers|as|an|at|this|they|there|then|that|are|would|who|whom|them|each|from|ourselves|when|these)"))) {
                    // Bigram doesn't exist yet, so create it and insert into
                    // the collection
                    if (!containsBigram(words[i])) {
                        bigrams.put(words[i], new Bigram(w, words[i]));
                        //DEBUG System.out.println("Creating bigram " + words[i]);
                    }
                    bigrams.get(words[i]).addInstance(i - wIndex);
                }
            }
        }
    }

    /**
     * Returns fbar, the average frequency of all bigrams for this word.
     *
     * @return fbar, the average frequency
     */
    public double getFbar() {
        return (wFreq / bigrams.size());
    }

    /**
     * Returns sigma, the standard deviation of the frequency of all bigrams for
     * this word.
     *
     * @return sigma, the standard deviation
     */
    public double getSigma() {
        double fbar = getFbar();
        Bigram tempBG;

        double term1 = (1 / (double) (bigrams.size() - 1));
        double term2 = 0;
        double x;

        // Sum term2
        Set<String> keys = bigrams.keySet();
        // Iterating over all the Bigrams
        for (String key : keys) {
            // Get Bigram
            tempBG = bigrams.get(key);

            x = tempBG.getFreq() - fbar;
            term2 += x * x;
        }

	    /* Java 5.0 Syntax - Refactored to above
        for ( Object key : bigrams.keySet() ){
			tempBG = ((Bigram)bigrams.get(key));
			
			x = tempBG.getFreq() - fbar;
			term2 += x*x;
		} */

        // This should be the standard deviation. Implementation of a formula
        // from Wikipedia
        return Math.sqrt(term1 * term2);
    }

    /**
     * Gets a string representing the contents of the BigramCollection similar
     * to the format presented in Table 2 of the Smadja paper.
     *
     * @return The String containing the table-format contents of the bigrams.
     */
    public String getTable2() {
        String output;
        Bigram tempBG;

        output = "Freq\tp-5\tp-4\tp-3\tp-2\tp-1\tp1\tp2\tp3\tp4\tp5\tw, wi\n";

        //	 Go through all the Bigrams and add their values to the output.
        Set<String> keys = bigrams.keySet();
        // Iterating over all the Bigrams
        for (String key : keys) {
            // Get Bigram
            tempBG = bigrams.get(key);

            output = output +
                    tempBG.getFreq() + "\t" +
                    tempBG.getp(-5) + "\t" +
                    tempBG.getp(-4) + "\t" +
                    tempBG.getp(-3) + "\t" +
                    tempBG.getp(-2) + "\t" +
                    tempBG.getp(-1) + "\t" +
                    tempBG.getp(1) + "\t" +
                    tempBG.getp(2) + "\t" +
                    tempBG.getp(3) + "\t" +
                    tempBG.getp(4) + "\t" +
                    tempBG.getp(5) + "\t" +
                    tempBG.getw() + ", " + tempBG.getwi() + "\n";
        }
		
		/* Java 5.0 Syntax - Refactored to above
		for ( Object key : bigrams.keySet()){
			tempBG = ((Bigram)bigrams.get(key));
			output = output +
					 tempBG.getFreq() + "\t" + 
					 tempBG.getp(-5) + "\t" + 
					 tempBG.getp(-4) + "\t" + 
					 tempBG.getp(-3) + "\t" + 
					 tempBG.getp(-2) + "\t" + 
					 tempBG.getp(-1) + "\t" + 
					 tempBG.getp(1) + "\t" + 
					 tempBG.getp(2) + "\t" + 
					 tempBG.getp(3) + "\t" + 
					 tempBG.getp(4) + "\t" + 
					 tempBG.getp(5) + "\t" +
					 w + ", " + tempBG.getwi() + "\n";
		} */

        return output;
    } // End getTable

    /**
     * Present the results of Step 1.3 in the Smadja algorithm. This is similar
     * to Table 4 in the paper.
     *
     * @return A string containing the table.
     */
    public String getTable4() {
        String output;
        Bigram tempBG;

        output = "distance\tstrength\tspread\t\twi\twj\n";

        // Go through all the Bigrams and add their values to the output.
        Set<String> keys = bigrams.keySet();
        // Iterating over all the Bigrams
        for (String key : keys) {
            // Get Bigram
            tempBG = bigrams.get(key);

            if (tempBG.getStrength() > 1 && tempBG.getSpread() > 3) {
                // Add all the interesting distances to the output
                String dString = "";
                Vector<Integer> distances = tempBG.getDistances(1);
                for (Integer distance : distances) {
                    dString = dString + distance + " ";
                }

                output = output +
                        dString + "\t\t" +
                        (int) tempBG.getStrength() + "\t\t" +
                        tempBG.getSpread() + "\t\t" +
                        tempBG.getw() + ", " + tempBG.getwi() + "\n";
            }
        }
		
		
	    /* Java 5.0 Syntax - Refactored to above
		for ( Object key : bigrams.keySet()){
			tempBG = ((Bigram)bigrams.get(key));
			if (tempBG.getStrength() > 1 && tempBG.getVariance() > 3){
				output = output +
				"1" + "\t\t" + 
				(int)tempBG.getStrength() + "\t\t" +
				 tempBG.getVariance() + "\t\t" +
				 w + ", " + tempBG.getwi() + "\n";
			}
		} */

        return output;
    }

    /**
     * Returns a Vector with the bigrams left after Stage 1 of the algorithm
     * and their characteristics.
     *
     * @return A Vector containing all of the S1Bigrams
     */
    public Vector<S1Bigram> getStageOneBigrams(double k0, double k1, double U0) {
        // Go through all the Bigrams and if they pass stage one processing, add
        // them to a new Vector
        Vector<S1Bigram> passedStage = new Vector<>();
        Bigram tempBG;
        Set<String> keys = bigrams.keySet();

        // Iterating over all the Bigrams
        for (String key : keys) {
            // Get Bigram
            tempBG = bigrams.get(key);

            if (tempBG.getStrength() >= k0 && tempBG.getSpread() >= U0) {
                Vector<Integer> distances = tempBG.getDistances(k1);
                passedStage.add(new S1Bigram(tempBG.getw(), tempBG.getwi(), tempBG.getStrength(), tempBG.getSpread(), distances));
            }
        }

        return passedStage;
    }

    public void stage2(double T) {
        // Go through all the bigrams and calculate the total occurances of each
        // position
        Bigram tempBG = null;
        int pos = 0;
        int[] freqs = new int[10];
        Vector<String> ngram = new Vector<>();

        Set<String> keys = bigrams.keySet();
        // Iterating over all the Bigrams
        for (String key : keys) {
            // Get Bigram
            tempBG = bigrams.get(key);

            for (int i = 0; i < 10; i++) {
                if (i < 5) {
                    pos = i - 5;
                } else if (i >= 5) {
                    pos = i - 4;
                }
                freqs[i] += tempBG.getp(pos);
            }
        }

        // Print out all frequencies
        //DEBUG System.out.print("Frequencies: ");
        //for (int i = 0; i < 10; i++){
        //	System.out.print(freqs[i] + " ");
        //}
        //System.out.println("");

        // Go through each position
        for (int i = 0; i < 10; i++) {
            boolean addedWord = false;
            if (i < 5) {
                pos = i - 5;
            } else if (i >= 5) {
                pos = i - 4;
            }
            if (i == 5) {
                ngram.add(tempBG.getw());
            }
            // For each position, go through each wi
            // Iterating over all the Bigrams
            for (String key : keys) {
                // Get Bigram
                tempBG = bigrams.get(key);

                if ((freqs[i] > 0) && (((double) tempBG.getp(pos) / (double) freqs[i]) > T)) {
                    // Add
                    //double frequency = (double) tempBG.getp(pos) / (double) freqs[i];
                    //DEBUG System.out.println("Adding " + tempBG.getwi() + ", frequency: " + frequency);
                    ngram.add(tempBG.getwi());
                    addedWord = true;
                }
            }
            if (!addedWord) {
                ngram.add("_");
            }

        }
        for (String aNgram : ngram) {
            System.out.print(aNgram + " ");
        }
        System.out.println("");
    }

    /**
     * Checks if the collection contains a bigram of w and the given argument.
     *
     * @param wi_ String of the potential collocate of w.
     * @return <code>true</code> if the bigram exists, <code>false</code> otherwise.
     */
    private boolean containsBigram(String wi_) {
        return bigrams.containsKey(wi_);
    }

    /**
     * Removes all bigrams from the collection.
     */
    private void clearBigrams() {
        bigrams.clear();
    }


    /**
     * Bigram holds a single Bigram and its frequency information.
     *
     * @author Adam Goforth
     */
    private class Bigram implements Comparable {
        String w;
        String wi;
        String PP;
        int freq;
        int[] p;

        /**
         * Constructor
         *
         * @param wi_ The second word in the Bigram. The first is defined by the
         *            BigramCollection data members.
         */
        public Bigram(String w_, String wi_) {
            w = w_;
            wi = wi_;
            freq = 0;
            PP = "";
            p = new int[10];
        }

        /**
         * Getter for w1
         *
         * @return w1
         */
        public String getwi() {
            return wi;
        }

        /**
         * Getter for w1
         *
         * @return w
         */
        public String getw() {
            return w;
        }


        /**
         * Getter for PP (the part of speech of w1)
         *
         * @return PP
         */
        public String getPP() {
            return PP;
        }

        /**
         * Getter for the frequency of the Bigram (number of times it appears)
         * in the corpus.
         *
         * @return frequency of the Bigram
         */
        public int getFreq() {
            return freq;
        }

        /**
         * Returns the strength of this bigram, as defined in Step 1.3 of the
         * Smadja paper.
         *
         * @return the strength of the bigram
         */
        public double getStrength() {
            return ((freq - getFbar()) / getSigma());
        }

        public double getSpread() {
            double u = 0;
            double ps;

            for (int i = 0; i < 10; i++) {
                ps = p[i] - (freq / 10);
                u += (ps * ps);
            }
            u = u / 10;
            return u;
        }

        /**
         * getDistances() returns a vector of all the distances from w that are
         * considered "interesting" as defined by equation C3 in Smadja, Step
         * 1.3
         *
         * @param k1 The threshhold above which which distances are considered
         *           interesting. Smadja recommends 1.
         * @return A Vector of relative positions to w, in the range of -5 to 5,
         * excluding 0
         */
        public Vector<Integer> getDistances(double k1) {
            Vector<Integer> distances = new Vector<>();
            double minPeak;

            // Equation from Smadja, Step 1.3
            minPeak = (freq / 10) + (k1 * Math.sqrt(getSpread()));

            // Loop through the distances < 0 and > 0 and add the interesting
            // relative positions
            for (int i = 0; i < 5; i++) {
                if (p[i] > minPeak) {
                    distances.add(i - 5);
                }
            }

            for (int i = 5; i < 10; i++) {
                if (p[i] > minPeak) {
                    distances.add(i - 4);
                }
            }

            return distances;
        }

        /**
         * Returns the frequency of this Bigram, with <code>w1</code> being
         * <code>offset</code> words away from <code>w</code>.
         *
         * @param offset A value from -5 to -1 or 1 to 5
         * @return The frequency of the Bigram at the given distance if the
         * offset is a valid number, otherwise -1.
         */
        public int getp(int offset) {

            // Only values of -5 to -1 and 1 to 5 are allowed.
            if (offset < 0 && offset >= -5) {
                return p[offset + 5];
            } else if (offset > 0 && offset <= 5) {
                return p[offset + 4];
            }

            // Invalid offset
            return -1;
        }

        /**
         * Insert an instance of a bigram into the Bigram
         *
         * @param offset The offset of wi compared to w
         * @throws Exception if the offset is out of bounds, which is < -5, 0, >5
         */
        public void addInstance(int offset) throws Exception {
            if ((offset < -5) || (offset > 5) || (offset == 0)) {
                throw new Exception("Cannot add instance: offset out of bounds");
            } else {
                if (offset < 0) {
                    offset += 5;
                } else if (offset > 0) {
                    offset += 4;
                }
                p[offset]++;
                freq++;
                wFreq++;
            }
        }

        /**
         * compareTo is used to sort bigrams according to their wi values. The
         * sorting is done by the String.compareTo() method, so ordering will be
         * based on the behavior of that method.
         *
         * @param obj_ The thing to compare this Bigram to.
         * @return a negative integer, zero, or a positive integer as this
         * object is less than, equal to, or greater than the specified
         * Bigram. If the specified object is not a Bigram, throws a
         * ClassCastException
         */
        public int compareTo(Object obj_) throws ClassCastException {
            if (obj_ instanceof Bigram) {
                // Base ordering on ordering of wi Strings
                return wi.compareTo(((Bigram) obj_).getwi());
            } // else
            throw new ClassCastException("Comparison attempted between a Bigram and a non-Bigram object");
        }


    } // End class Bigram

} // End class BigramCollection
