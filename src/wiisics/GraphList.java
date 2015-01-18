package wiisics;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by funstein on 12/01/15.
 */
public class GraphList<T> extends CopyOnWriteArrayList<T> {
    private double[][] limits;

    public GraphList() {
        super();

        limits = new double[3][];
        limits[0] = new double[2];
        limits[1] = new double[2];
        limits[2] = new double[2];
    }

    public void setLimits(double[][] values) {
        limits = values;
    }

    public double[][] getLimits() {
        return limits;
    }
}
