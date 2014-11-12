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
    
    private double pitch = 0;
    private double roll = 0;
    
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
    }

    public void update(double xInput, double yInput, double zInput, double pitch, double roll) {
        lastTime = thisTime;
        thisTime = System.currentTimeMillis();

        this.pitch = pitch;
        this.roll = roll;
        
        lastAcceleration[0] = acceleration[0];
        lastAcceleration[1] = acceleration[1];
        lastAcceleration[2] = acceleration[2];

        acceleration[0] = round(xInput, 2);
        acceleration[1] = round(yInput, 2);
        acceleration[2] = round(zInput - 1, 2);
        
        lastVelocity[0] = velocity[0];
        lastVelocity[1] = velocity[1];
        lastVelocity[2] = velocity[2];
        
        //if(!(velocity[0])== 0 && velocity[1])== 0 && velocity[2])== 0))
        long deltaT = thisTime - lastTime;
        velocity[0] = ((acceleration[0] + lastAcceleration[0]) / 2) * deltaT + lastVelocity[0];
        velocity[1] = ((acceleration[1] + lastAcceleration[1]) / 2) * deltaT + lastVelocity[1];
        velocity[2] = ((acceleration[2] + lastAcceleration[2]) / 2) * deltaT + lastVelocity[2];
        

        if (lastTime != 0) {
            // Insert velocity, displacement calculation here
        }
        
        double totalAcc = Math.sqrt(Math.pow(acceleration[0], 2) + Math.pow(acceleration[1], 2) + Math.pow(acceleration[2], 2));
        
        //System.out.printf("%d;%.3f;%.3f;%.3f;%.3f\n", thisTime, acceleration[0], acceleration[1], acceleration[2], totalAcc);
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
    
    public long getBeginTime() {
        return beginTime;
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
