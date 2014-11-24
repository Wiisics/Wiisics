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
    private double value;
    
    public GraphDot (long time, double value) {
        this.time = time;
        this.value = value;
        Debugger.println("Graph dot created.");
    }
    
    public long getTime() {
        return this.time;
    }
    
    public double getValue() {
        return this.value;
    }
    
}
