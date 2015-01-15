package wiisics;

import java.util.LinkedList;

/**
 * Created by funstein on 12/01/15.
 */
public class GraphList<T> extends LinkedList<T> {
    private double[][] limits;

    public GraphList() {
        super();

        limits = new double[3][];
        limits[0] = new double[2];
        limits[1] = new double[2];
        limits[2] = new double[2];
    }

    public void setLimits(double sMin, double sMax, double vMin, double vMax, double aMin, double aMax) {
        limits[0][0] = sMin;
        limits[0][1] = sMax;

        limits[1][0] = vMin;
        limits[1][1] = vMax;

        limits[2][0] = aMin;
        limits[2][1] = aMax;
    }

    public void setLimits(double[][] values) {
        limits = values;
    }

    public double[][] getLimits() {
        return limits;
    }
}
