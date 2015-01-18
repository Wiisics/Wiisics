/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import Jama.Matrix;

import javax.vecmath.Vector3d;
import java.text.DecimalFormat;

/**
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

    private static final DecimalFormat newFormat = new DecimalFormat("#0.##");

    public PhysicsProcessor() {
        beginTime = -1;
        thisTime = -1;
    }

    public void update(double xInput, double yInput, double zInput, double[] calibratedData, boolean isStill) {
        if (beginTime != -1) {
            lastTime = thisTime;
            thisTime = System.currentTimeMillis();

            pitch = calibratedData[0];
            roll = calibratedData[1];

            lastAcceleration[0] = acceleration[0];
            lastAcceleration[1] = acceleration[1];
            lastAcceleration[2] = acceleration[2];

            lastVelocity[0] = velocity[0];
            lastVelocity[1] = velocity[1];
            lastVelocity[2] = velocity[2];

            lastDisplacement[0] = displacement[0];
            lastDisplacement[1] = displacement[1];
            lastDisplacement[2] = displacement[2];

            if (!isStill) {
                acceleration = new double[]{xInput, yInput, zInput};
                acceleration = fixAcceleration(acceleration, calibratedData);
                acceleration[0] = round(acceleration[0]);
                acceleration[1] = round(acceleration[1]);
                acceleration[2] = round(acceleration[2]);

                long deltaT = thisTime - lastTime;
                velocity[0] = acceleration[0] * (deltaT / 1000.0) + lastVelocity[0];
                velocity[1] = acceleration[1] * (deltaT / 1000.0) + lastVelocity[1];
                velocity[2] = acceleration[2] * (deltaT / 1000.0) + lastVelocity[2];

                displacement[0] = velocity[0] * (deltaT / 1000.0) + lastDisplacement[0];
                displacement[1] = velocity[1] * (deltaT / 1000.0) + lastDisplacement[1];
                displacement[2] = velocity[2] * (deltaT / 1000.0) + lastDisplacement[2];
            } else {
                long deltaT = thisTime - lastTime;

                velocity[0] = 0;
                velocity[1] = 0;
                velocity[2] = 0;

                displacement[0] = lastDisplacement[0];
                displacement[1] = lastDisplacement[1];
                displacement[2] = lastDisplacement[2];

                acceleration[0] = (velocity[0] - lastVelocity[0]) / deltaT;
                acceleration[1] = (velocity[1] - lastVelocity[1]) / deltaT;
                acceleration[2] = (velocity[2] - lastVelocity[2]) / deltaT;
            }
        } else {
            beginTime = System.currentTimeMillis();
            thisTime = System.currentTimeMillis();

            acceleration = new double[]{xInput, yInput, zInput};
            acceleration = fixAcceleration(acceleration, calibratedData);
            acceleration[0] = round(acceleration[0]);
            acceleration[1] = round(acceleration[1]);
            acceleration[2] = round(acceleration[2]);
        }
    }

    double[] fixAcceleration(double[] accValues, double[] calibrationValues) {
        // The Z axis of our rotated frame is opposite gravity
        Vector3d zAxis = new Vector3d(-calibrationValues[2], -calibrationValues[3], -calibrationValues[4]);
        zAxis.normalize();

        // The Y axis of our rotated frame is an arbitrary vector perpendicular to gravity
        // Note that this convention will have problems as zAxis.x approaches +/-1 since the magnitude of
        // [0, zAxis.z, -zAxis.y] will approach 0

        double z = zAxis.getZ();
        double y = -1 * zAxis.getY();
        Vector3d yAxis = new Vector3d(0, z, y);
        yAxis.normalize();

        // The X axis is just the cross product of Y and Z
        Vector3d xAxis = new Vector3d();
        xAxis.cross(yAxis, zAxis);

        double[][] values = {{xAxis.getX(), yAxis.getX(), zAxis.getX()}, {xAxis.getY(), yAxis.getY(), zAxis.getY()}, {xAxis.getZ(), yAxis.getZ(), zAxis.getZ()}};
        Matrix matrix = new Matrix(values);

        Matrix inverted = matrix.inverse();

        // Subtract the zero acceleration
        accValues[0] -= calibrationValues[2];
        accValues[1] -= calibrationValues[3];
        accValues[2] -= calibrationValues[4];

        double[][] accelerationVector = {accValues};
        Matrix accelerationVMatrix = new Matrix(accelerationVector);

        Matrix endVector = accelerationVMatrix.times(inverted);
        return endVector.getArrayCopy()[0];
    }

    public double[] getAcceleration() {
        return acceleration;
    }

    public double[] getVelocity() {
        return velocity;
    }

    public double[] getDisplacement() {
        return displacement;
    }

    public long getTime() {
        return thisTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    private static double round(double value) {
        return value;
        //return Double.valueOf(newFormat.format(value));
    }
}
