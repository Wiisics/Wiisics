/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JPanel;

/**
 *
 * @author funstein
 */
public class GraphPanel extends JPanel {
    public static final int GRAPH_TIMEFRAME = 5;
    private LinkedList<GraphDot> graphItems;
    String name;
    
    public GraphPanel(String name) {
        super();
        this.name = name;
        this.graphItems = new LinkedList<GraphDot>();
    }
    
    public void addGraphItem (long time, double value) {
        Debugger.println("New graph item command received.");
        GraphDot dot = new GraphDot(time, value);
        graphItems.add(dot);
        removePastItems();
        this.repaint();
    }
    
    public void removePastItems () {
        ListIterator<GraphDot> it = graphItems.listIterator(graphItems.size());
        
        long currentDeltaTime = 0;
        long lastTimeStamp = -1;
        
        while (it.hasPrevious()) {
            GraphDot item = it.previous();
            long thisTime = item.getTime();
            if (lastTimeStamp != -1) {
                if (currentDeltaTime > (GRAPH_TIMEFRAME * 1500)) { // Multiplying with 1500 instead of 1000 here as a safety margin
                    it.remove();
                } else {
                    currentDeltaTime += lastTimeStamp - thisTime;
                }
            }
            
            lastTimeStamp = thisTime;
        }
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        double width = this.getWidth();
        double height = this.getHeight();
        
        // Calculate the width of one millisecond if the entire graph will encompass 10 seconds
        double secondWidth = width / GRAPH_TIMEFRAME;
        
        // Make a clone of the linkedlist for use here
        LinkedList<GraphDot> list = (LinkedList<GraphDot>) graphItems.clone();
        
        //Now scan the entire graph to find the lowest and highest Y values
        ListIterator<GraphDot> it = list.listIterator();
        double maxValue = 0.1;
        double minValue = -0.1;
        while (it.hasNext()) {
            GraphDot nextItem = it.next();
            double value = nextItem.getValue();
            if (value > maxValue)
                maxValue = value;
            if (value < minValue)
                minValue = value;
        }
        
        it = null;
        
        //Now find the multiplier for the Y values as well as the topmost Y value
        int padding = (int) (height / 20);
        double multiplier = (height - (2 * padding)) / (maxValue - minValue);
        double topValue = maxValue + (padding / multiplier);
        
        //Let's draw the X axis
        int yCoord = (int) (padding + (topValue * multiplier));
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, yCoord, (int) width, yCoord);
        
        //Now let's start drawing the graphs
        graphics.setColor(Color.RED);
        GraphDot future = null;
        GraphDot past = null;
        double lastX = width;
        ListIterator<GraphDot> it2 = list.listIterator(list.size());
        while (it2.hasPrevious()) {
            past = it2.previous();
            if (future != null) {
                double startX = lastX;
                double startY = (int) (padding + ((topValue - future.getValue()) * multiplier));
                
                double endX = lastX - ((int) (((future.getTime() - past.getTime()) * secondWidth) / 1000));
                double endY = (int) (padding + ((topValue - past.getValue()) * multiplier));
                
                graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
                //System.out.printf("Width %d and Height %d: Start Coords (%d, %d); End Coords (%d, %d)\n", width, height, startX, startY, endX, endY);
                lastX = endX;
            }
            future = past;
            past = null;
        }
        it2 = null;
        
        graphics.setColor(Color.BLACK);
        FontMetrics fm = graphics.getFontMetrics();
        
        long dt = 0;
        try {
            GraphDot first = list.get(0);
            GraphDot last = list.get(list.size() - 1);
            if (first != null && last != null)
                dt = (last.getTime() - first.getTime()) / 1000;
        } catch (Exception e) {}
        
        graphics.drawString(name + ": " + list.size() + " items and dt of " + dt + "s", 5, fm.getHeight() + 5);

        /*if (time >= 800 || accelerometerSource != lastSource) {
         lastSource = accelerometerSource;
         graphics.clearRect(0, 0, 800, 600);
         graphics.fillRect(0, 0, 800, 600);
         graphics.setColor(Color.WHITE);
         graphics.drawLine(0, 300, 800, 300);
         }*/

        /*int oldX = (int) (((lastTime - beginTime) / 1000.0) * (width / 100));
        int newX = (int) (((time - beginTime) / 1000.0) * (width / 100));

        double[] acceleration = physics.getVelocity();
        double[] lastAcceleration = physics.getLastVelocity();

        int totalAcc = (height / 2) - ((int) (Math.sqrt(Math.pow(acceleration[0], 2) + Math.pow(acceleration[1], 2) + Math.pow(acceleration[2], 2)) * 75));
        int lastTotalAcc = (height / 2) - ((int) (Math.sqrt(Math.pow(lastAcceleration[0], 2) + Math.pow(lastAcceleration[1], 2) + Math.pow(lastAcceleration[2], 2)) * 75));
        */
        //graphics.setColor(Color.RED);
        //graphics.drawLine(oldX, lastTotalAcc, newX, totalAcc);

        //System.out.printf("%d, %d\n", newX, totalAcc);
    }
}
