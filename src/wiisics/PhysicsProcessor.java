/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.io.*;

/**
 *
 * @author funstein
 */
public class PhysicsProcessor {
    private long beginTime = 0;
    private long thisTime = 0;
    private long lastTime = 0;
    
    private double[] acceleration = new double[3];
    private double[] lastAcceleration = new double[3];
    
    private double[] velocity = new double[3];
    private double[] lastVelocity = new double[3];
    
    private double[] displacement = new double[3];
    private double[] lastDisplacement = new double[3];
    
    private PrintWriter writer;
    
    public PhysicsProcessor() {
        beginTime = System.currentTimeMillis();
        thisTime = System.currentTimeMillis(); 
        
        try {
            PrintWriter writer = new PrintWriter("log.csv", "UTF-8");
        } catch (Exception e) {
            
        }
    }

    public void update(double xInput, double yInput, double zInput) {
        long thisTime = System.currentTimeMillis();

        lastAcceleration[0] = acceleration[0];
        lastAcceleration[1] = acceleration[1];
        lastAcceleration[2] = acceleration[2];

        acceleration[0] = xInput; //(int) (xInput / 5 * 300) + 300;
        acceleration[1] = yInput; //(int) (yInput / 5 * 300) + 300;
        acceleration[2] = zInput; //(int) (zInput / 5 * 300) + 300;

        if (lastTime != 0) {
            // Insert velocity, displacement calculation here
        }

        lastTime = thisTime;
        
        writer.printf("%d;%3f;%3f;%3f/n", thisTime, acceleration[0], acceleration[1], acceleration[2]);
    }
    
    public double[] getAcceleration() {
        return acceleration;
    }
    
    public double[] getLastAcceleration() {
        return lastAcceleration;
    }
    
    public double[] getVelocity() {
        return velocity;
    }
    
    public double[] getLastVelocity() {
        return lastVelocity;
    }
    
    public double[] getDisplacement() {
        return displacement;
    }
    
    public double[] getLastDisplacement() {
        return lastDisplacement;
    }
     
    public long getTime() {
        return thisTime;
    }
    
    public long getLastTime() {
        return lastTime;
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
