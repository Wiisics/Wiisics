package wiisics;

/*
 * Wiisics
 * Calibrator.java
 * Uskudar American Academy, 2015
 *
 * Cem Gokmen <cem@cemgokmen.com>
 * Hakan Alpan <hakan.alpan@hotmail.com>
 */

class Calibrator {
    private static final double cancellationThreshold = 0.05;
    private static final int calibrationSeconds = 5;

    private final long startTime;
    private boolean aborted;

    private double lastAcceleration[];
    private double lastAMag;
    private double lastRoll;
    private double lastPitch;

    private double rollTotal;
    private int rollCount;
    private double pitchTotal;
    private int pitchCount;
    private double[] accelerationTotals;
    private int accelerationCount;

    private final WiisicsHandler handler;

    public Calibrator(WiisicsHandler handler, double pitch, double roll, double[] acceleration) {
        lastRoll = roll;
        lastPitch = pitch;
        lastAcceleration = acceleration;
        lastAMag = calculateAccelerationMagnitude(acceleration);

        aborted = !isDataValid(pitch, roll, acceleration);
        if (aborted) {
            returnData();
        }

        rollTotal = roll;
        rollCount = 1;

        pitchTotal = pitch;
        pitchCount = 1;

        accelerationTotals = acceleration;
        accelerationCount = 1;

        startTime = System.currentTimeMillis();

        this.handler = handler;
    }

    public void addData(double pitch, double roll, double[] acceleration) {
        if (!aborted) {
            if (isDataValid(pitch, roll, acceleration)) {
                rollTotal += roll;
                rollCount++;

                pitchTotal += pitch;
                pitchCount++;

                accelerationTotals[0] += acceleration[0];
                accelerationTotals[1] += acceleration[1];
                accelerationTotals[2] += acceleration[2];
                accelerationCount++;


                lastRoll = roll;
                lastPitch = pitch;
                lastAcceleration = acceleration;
                lastAMag = calculateAccelerationMagnitude(acceleration);
            } else {
                aborted = true;
            }
        }

        returnData();
    }

    private void returnData() {
        if (aborted) {
            handler.getWiisics().calibrationResults(new double[0]);
        } else {
            if (System.currentTimeMillis() - startTime >= calibrationSeconds * 1000) {
                double[] results = new double[5];
                results[0] = pitchTotal / pitchCount;
                results[1] = rollTotal / rollCount;
                results[2] = accelerationTotals[0] / accelerationCount;
                results[3] = accelerationTotals[1] / accelerationCount;
                results[4] = accelerationTotals[2] / accelerationCount;

                handler.getWiisics().calibrationResults(results);
            }
        }
    }

    private boolean isDataValid(double pitch, double roll, double[] acceleration) {
        // Check roll deviation
        if (Math.abs(roll - lastRoll) > cancellationThreshold)
            return false;

        // Check pitch deviation
        if (Math.abs(pitch - lastPitch) > cancellationThreshold)
            return false;

        // Calculate acceleration magnitude
        double aMag = calculateAccelerationMagnitude(acceleration);

        // Check acceleration magnitude unreality (too far from 1g?)
        /*if (Math.abs(aMag - 1) > cancellationThreshold)
            return false;*/

        // Check acceleration magnitude deviation
        if (Math.abs(aMag - lastAMag) > cancellationThreshold)
            return false;

        // Check acceleration component deviation
        for (int i = 0; i < acceleration.length; i++) {
            if (Math.abs(acceleration[i] - lastAcceleration[i]) > cancellationThreshold)
                return false;
        }

        // Wow, you went through a whole checklist. Congratulations, data!
        return true;
    }

    private static double calculateAccelerationMagnitude(double[] acceleration) {
        double squareSum = 0;
        for (int i = 0; i < acceleration.length; i++) {
            squareSum += Math.pow(acceleration[i], 2);
        }
        double aMagn = Math.sqrt(squareSum);

        return aMagn;
    }
}
