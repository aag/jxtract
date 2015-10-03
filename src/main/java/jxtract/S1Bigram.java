/**
 * S1Bigram.java
 * <p/>
 * <p/>
 * Written by: Adam Goforth
 * Started on: Dec 6, 2005
 */
package jxtract;

import java.util.Vector;

/**
 * A small class to hold a bigram and the results of Stage 1.
 *
 * @author Adam Goforth
 */
public class S1Bigram {
    private String w;
    private String wi;
    private double strength;
    private double spread;
    private Vector<Integer> distances;

    public S1Bigram(String w_, String wi_, double strength_, double spread_, Vector<Integer> distances_) {
        w = w_;
        wi = wi_;
        strength = strength_;
        spread = spread_;
        distances = distances_;
    }

    public String getw() {
        return w;
    }

    public String getwi() {
        return wi;
    }

    public double getStrength() {
        return strength;
    }

    public double getSpread() {
        return spread;
    }

    public Vector<Integer> getDistances() {
        return distances;
    }
}
