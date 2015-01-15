/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

/**
 *
 * @author funstein
 */
public class GraphDot {
    private long time;

    private boolean pause;
    private double[][] value;
    
    public GraphDot (long time, double[][] value) {
        this.time = time;
        this.value = value;
        this.pause = false;
        Debugger.println("Graph dot created.");
    }

    public GraphDot (long time) {
        this.time = time;
        this.pause = true;
    }
    
    public long getTime() {
        return this.time;
    }

    public double[][] getValue() { return this.value; }

    public boolean isPause() {
        return pause;
    }
}
