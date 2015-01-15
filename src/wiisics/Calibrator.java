package wiisics;

/**
 * Created by funstein on 08/01/15.
 */
public class Calibrator {
    private static final double cancellationThreshold = 0.05;
    private static final int calibrationSeconds = 5;

    private long startTime;
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

    private WiisicsHandler handler;

    public Calibrator (WiisicsHandler handler, double pitch, double roll, double[] acceleration) {
        lastRoll = roll;
        lastPitch = pitch;
        lastAcceleration = acceleration;
        lastAMag = calculateAccelerationMagnitude(acceleration);

        aborted = !isDataValid(pitch, roll, acceleration);
        if (aborted) {
            System.out.println("Directly aborted");
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
            } else {
                aborted = true;
                System.out.println("Abort!");
            }
        }

        returnData();
    }

    private void returnData() {
        if (aborted) {
            System.out.println("End calibration fail");
            handler.getWiisics().calibrationResults(new double[0]);
        } else {
            if (System.currentTimeMillis() - startTime >= calibrationSeconds * 1000) {
                System.out.println("End calibration pass");
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

    public boolean isDataValid(double pitch, double roll, double[] acceleration) {
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

    public static double calculateAccelerationMagnitude (double[] acceleration) {
        double squareSum = 0;
        for (int i = 0; i < acceleration.length; i++) {
            squareSum += Math.pow(acceleration[i], 2);
        }
        double aMag = Math.sqrt(squareSum);

        return aMag;
    }
}
