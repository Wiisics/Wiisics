/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import org.apache.commons.math3.stat.descriptive.rank.Median;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * @author funstein
 */
public class HarmonicPhysicsProcessor extends PhysicsProcessor {
    private static final double STILLNESS_THRESHOLD = 0.1;

    private double[] crests;
    private int crestIndex;

    private double[] troughs;
    private int troughIndex;

    private long[] jumps;
    private int jumpIndex;

    boolean upwards;
    boolean directionSet;

    private double w;
    private double A;
    private double amplitude;
    private long lastCalculationTime;

    private long lastTime;
    private double[] lastAcceleration;

    private double[] acceleration;
    private double[] velocity;
    private double[] displacement;
    private long thisTime;

    public HarmonicPhysicsProcessor() {
        lastTime = -1;
        thisTime = -1;

        crests = new double[10];
        crests[crests.length] = -999999;
        crestIndex = 0;

        troughs = new double[10];
        troughs[troughs.length] = 999999;
        troughIndex = 0;

        jumps = new long[10];
        jumps[jumps.length] = -1;

        w = -1;
        A = -1;
        amplitude = -1;

        lastCalculationTime = -1;

        lastAcceleration = new double[0];
        acceleration = new double[3];
        velocity = new double[3];
        displacement = new double[3];
    }

    public void update(double xInput, double yInput, double zInput, double[] calibratedData) {
        acceleration[0] = xInput;
        acceleration[1] = yInput;
        acceleration[2] = zInput;

        displacement[0] = 0;
        displacement[1] = 0;
        displacement[2] = 0;

        velocity[0] = 0;
        velocity[1] = 0;
        velocity[2] = 0;

        acceleration = fixAcceleration(acceleration, calibratedData);
        acceleration[0] = 0;
        acceleration[1] = 0;
        thisTime = System.currentTimeMillis();
        if (lastTime > 0) {
            if (!directionSet) {
                upwards = (acceleration[2] > lastAcceleration[2]);
                directionSet = true;
            }

            if (thisTime - lastCalculationTime > 1) {
                // Find if the current Z point is a switch point, a trough point, or a crest point
                // First, check for a switch point. If the sign of the previous one is different from the sign of this one, it's a switch
                if (lastAcceleration[2] * acceleration[2] < 0) {
                    // Find where exactly the switch happened:
                    // Find the deltas
                    long timeDifference = (thisTime - lastTime);
                    double accDifference = (acceleration[2] - lastAcceleration[2]);

                    // Find the fraction of X it happened in
                    double accFraction = (0 - lastAcceleration[2]) / accDifference;

                    // And find the time it happened in!
                    long timeOfSwitch = (long) (accFraction * timeDifference);

                    // Add this point to the switch points! (Shift everything back by one first)
                    for (int i = 0; i < jumps.length - 1; i++) {
                        jumps[i] = jumps[i + 1];
                    }
                    jumps[jumps.length - 1] = timeOfSwitch;
                }

                // Now, check if the last point was a crest. For that, we need to have been going upwards, and this point has to be smaller than that one.
                if (upwards && acceleration[2] < lastAcceleration[2]) {
                    // Add crest's height to the list of crests
                    crests[crestIndex] = lastAcceleration[2];
                    crestIndex = (crestIndex + 1) % crests.length;
                    upwards = false;
                }

                // And finally, if it was a trough. For that, we need to have been going downwards, and the point has to be greater than the previous one.
                if (!upwards && acceleration[2] > lastAcceleration[2]) {
                    troughs[troughIndex] = lastAcceleration[2];
                    troughIndex = (troughIndex + 1) % troughs.length;
                    upwards = true;
                }

                // Now try to determine if it is possible to comment on amplitude and period
                // First, amplitude.
                if (crests[crests.length] > 0 && troughs[troughs.length] < 0) {
                    Median median = new Median();
                    double medianCrest = median.evaluate(crests);
                    double medianTrough = median.evaluate(troughs);
                    amplitude = (medianCrest - medianTrough) / 2.0;
                }

                // Then, period
                if (jumps[jumps.length] < 0) {
                    // Calculate differences
                    double[] halfPeriods = new double[9];
                    for (int i = 0; i < 9; i++) {
                        halfPeriods[i] = jumps[i + 1] - jumps[i];
                    }
                    Median median = new Median();
                    double medianHalfPeriod = median.evaluate(halfPeriods);
                    w = Math.PI / medianHalfPeriod;
                }

                // Now, if we have both:
                if (w > 0 && amplitude > 0) {
                    A = amplitude / (-1 * w * w);
                }

                // And just set the final calculation time:
                lastCalculationTime = thisTime;
            }

            // Alrighty. Now finally, if we have both A and w, find x and v
            if (A > 0 && w > 0) {
                displacement[2] = acceleration[2] / (-1 * w * w); // a = -w^2 * x
                velocity[2] = w * Math.sqrt(A*A - displacement[2]*displacement[2]); // V = w √(A^2 - x^2)
            }
        } else {
            lastTime = thisTime;
            lastAcceleration = acceleration;
        }
    }

    private static boolean isStill (double[] fixedAcc) {
        /*boolean stillness = true;
        for (double aFixedAcc : fixedAcc) {
            if (Math.abs(aFixedAcc) > STILLNESS_THRESHOLD) {
                stillness = false;
                break;
            }
        }

        return stillness;*/

        return false;
    }
}
