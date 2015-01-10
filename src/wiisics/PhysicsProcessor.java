/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import wiiremotej.AccelerationConstants;

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
    
    private long duration = 0;
    
    private double sum0;
    private double sum1;
    private double sum2;
    private double stdAcc0;
    private double stdAcc1;
    private double stdAcc2;
    private int i;
    private boolean j;

    
    public PhysicsProcessor() {
        beginTime = System.currentTimeMillis();
        thisTime = System.currentTimeMillis(); 
    }

    public void update(double xInput, double yInput, double zInput, double pitch, double roll) {
        lastTime = thisTime;
        thisTime = System.currentTimeMillis();
        duration = thisTime-beginTime;

        lastAcceleration[0] = acceleration[0];
        lastAcceleration[1] = acceleration[1];
        lastAcceleration[2] = acceleration[2];

        acceleration[0] = round(xInput, 2);
        acceleration[1] = round(yInput, 2);
        acceleration[2] = round(zInput, 2);

        /*acceleration[0] = round(-1 * (xInput - constants.xOne())/(constants.xOne()-constants.xZero()), 2);
        acceleration[1] = round(-1 * (yInput - constants.yOne())/(constants.yOne()-constants.yZero()), 2);
        acceleration[2] = round(-1 * (zInput - constants.zOne())/(constants.zOne()-constants.zZero()), 2);*/
        if(duration > 500 && duration < 1500){
        	sum0 += round(xInput, 2);
           	sum1 += round(yInput, 2);
           	sum2 += round(zInput, 2);
           	i++;
        }
        
        if(duration > 1500 && j){
        	j=false;
        	stdAcc0 = sum0/i;
        	stdAcc1 = sum1/i;
        	stdAcc2 = sum2/i;
        	double accCheck = Math.sqrt(stdAcc0*stdAcc0 + stdAcc1*stdAcc1 + stdAcc2*stdAcc2);
        	if (accCheck < 1.010 && accCheck > 0.970) {
        		System.out.println("Calibration successful.");
        		
        	} else {
        		System.out.println("Calibration failed.");
        		//Buras� do�ru mu bilmiyorum
        		System.exit(0);
        	}
        }
       
        
        if(duration >= 2000){
        	 lastAcceleration[0] = acceleration[0];
             lastAcceleration[1] = acceleration[1];
             lastAcceleration[2] = acceleration[2];

             acceleration[0] = round(xInput, 2) - stdAcc0;
             acceleration[1] = round(yInput, 2) - stdAcc1;
             acceleration[2] = round(zInput, 2) - stdAcc2;
             
             lastVelocity[0] = velocity[0];
             lastVelocity[1] = velocity[1];
             lastVelocity[2] = velocity[2];
             
             lastDisplacement[0] = displacement[0];
             lastDisplacement[1] = displacement[1];
             lastDisplacement[2] = displacement[2];
        }
       

        // Bu bölümü grafikleri delirtmesin diye commentledim -Cem
        long deltaT = thisTime - lastTime;
        velocity[0] = ((acceleration[0] + lastAcceleration[0]) / 2) * ((double)deltaT/1000) + lastVelocity[0];
        velocity[1] = ((acceleration[1] + lastAcceleration[1]) / 2) * ((double)deltaT/1000) + lastVelocity[1];
        velocity[2] = ((acceleration[2] + lastAcceleration[2]) / 2) * ((double)deltaT/1000) + lastVelocity[2];
        

        displacement[0] = (Math.pow(velocity[0],2)-Math.pow(lastVelocity[0],2))/(acceleration[0] + lastAcceleration[0]);
        displacement[1] = (Math.pow(velocity[1],2)-Math.pow(lastVelocity[1],2))/(acceleration[1] + lastAcceleration[1]);
        displacement[2] = (Math.pow(velocity[2],2)-Math.pow(lastVelocity[2],2))/(acceleration[2] + lastAcceleration[2]);
        
        double totalV = Math.sqrt(Math.pow(velocity[0], 2) + Math.pow(velocity[1], 2) + Math.pow(velocity[2], 2));
        double totalDisp = Math.sqrt(Math.pow(displacement[0], 2) + Math.pow(displacement[1], 2) + Math.pow(displacement[2], 2));

        if (duration > 2000) {
            // Insert velocity, displacement calculation here
        	// Bu bölümü grafikleri delirtmesin diye commentledim -Cem
        	/*long deltaT = thisTime - lastTime;
            velocity[0] = ((acceleration[0] + lastAcceleration[0]) / 2) * ((double)deltaT/1000) + lastVelocity[0];
            velocity[1] = ((acceleration[1] + lastAcceleration[1]) / 2) * ((double)deltaT/1000) + lastVelocity[1];
            velocity[2] = ((acceleration[2] + lastAcceleration[2]) / 2) * ((double)deltaT/1000) + lastVelocity[2];*/
            
            /*
            displacement[0] = (Math.pow(velocity[0],2)-Math.pow(lastvelocity[0],2))/(acceleration[0] + lastAcceleration[0]);
            displacement[1] = (Math.pow(velocity[1],2)-Math.pow(lastvelocity[1],2))/(acceleration[1] + lastAcceleration[1]);
            displacement[2] = (Math.pow(velocity[2],2)-Math.pow(lastvelocity[2],2))/(acceleration[2] + lastAcceleration[2]);
            
            double totalV = Math.sqrt(Math.pow(velocity[0], 2) + Math.pow(velocity[1], 2) + Math.pow(velocity[2], 2));
            double totalDisp = Math.sqrt(Math.pow(displacement[0], 2) + Math.pow(displacement[1], 2) + Math.pow(displacement[2], 2));
            */
        }

        
        double totalAcc = Math.sqrt(Math.pow(acceleration[0], 2) + Math.pow(acceleration[1], 2) + Math.pow(acceleration[2], 2));
        
        System.out.printf("%d     %.3f;%.3f     %.3f;%.3f;%.3f\n", thisTime, pitch, roll, acceleration[0], acceleration[1], acceleration[2]);
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
