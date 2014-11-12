/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 *
 * @author funstein
 */
public class PhysicsProcessor {

    private int t = 0;
    private int aX = 0;
    private int aY = 0;
    private int aZ = 0;
    private int lastX = 0;
    private int lastY = 0;
    private int lastZ = 0;
    public double lastHeight = 0;
    public double height = 0;
    public double lastVelocity = 0;
    public double velocity = 0;
    public long lastTime = 0;
    public long beginTime = 0;
    public long lastCalibTime = 0;
    public double zeroAcc = 0;
    public long lastAccChangeTime = 0;
    public double lastAcc = 0;

    public void update(double xInput, double yInput, double zInput) {
        long thisTime = System.currentTimeMillis();
        double acc = round(zInput, 3);
        if (beginTime > 0 && thisTime - beginTime < 5000) {
            long deltaT = thisTime - lastCalibTime;
            zeroAcc = zeroAcc + (acc * deltaT);

            lastCalibTime = thisTime;

            if (thisTime - beginTime > 5000) {
                zeroAcc = zeroAcc / (thisTime - beginTime);
                beginTime = -1;
            }
        }

        lastX = aX;
        lastY = aY;
        lastZ = aZ;

        aX = (int) (xInput / 5 * 300) + 300;
        aY = (int) (yInput / 5 * 300) + 300;
        aZ = (int) (zInput / 5 * 300) + 300;

        t++;

        if (lastTime != 0) {
            // Insert velocity, displacement calculation here
        }

        lastTime = thisTime;
    }
    
    public double[] getAcceleration() {
        double[] result = {
    }

    public static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
