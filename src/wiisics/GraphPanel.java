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
    private WiisicsHandler handler;
    private String name;
    private int number;
    
    public GraphPanel(String name, int number, WiisicsHandler handler) {
        super();
        this.name = name;
        this.number = number;
        this.handler = handler;
    }
    
    @Override
    public void paintComponent(Graphics graphics) {
        handler.getDisplay().setBoolean(number, false);
        super.paintComponent(graphics);
        double width = this.getWidth();
        double height = this.getHeight();
        paintGraphPanel(graphics, width, height);
        handler.getDisplay().setBoolean(number, true);
    }

    public void paintGraphPanel(Graphics graphics, double width, double height) {
        // Calculate the width of one millisecond if the entire graph will encompass 10 seconds
        double secondWidth = width / GRAPH_TIMEFRAME;

        // Make a clone of the linkedlist for use here
        GraphList<GraphDot> list = handler.getDisplay().getGraphList();

        //Now scan the entire graph to find the lowest and highest Y values
        double[][] scaleSet = list.getLimits();
        double minValue = scaleSet[number/4][0];
        double maxValue = scaleSet[number/4][1];

        //Now find the multiplier for the Y values as well as the topmost Y value
        int padding = (int) (height / 20);
        double drawableArea = (height - (2 * padding));
        double multiplier = drawableArea / (maxValue - minValue);
        double topValue = maxValue + (padding / multiplier);

        // Draw Y axis
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, 0, 0, (int) height);

        //Let's draw the X axis
        int yCoord = (int) (padding + (topValue * multiplier));
        graphics.setColor(Color.BLACK);
        graphics.drawLine(0, yCoord, (int) width, yCoord);

        // Draw Y axis labels
        int topPixelIncrement = ((int) (yCoord / 3.0));
        double topRealIncrement = topPixelIncrement / multiplier;

        int bottomPixelIncrement = ((int) ((height - yCoord) / 3.0));
        double bottomRealIncrement = bottomPixelIncrement / multiplier;

        // Draw the top
        FontMetrics metrics = graphics.getFontMetrics();
        int descent = metrics.getDescent();
        graphics.drawString(String.format("% .2f", -1 * 2 * topRealIncrement), 0, yCoord + (-1 * 2 * topPixelIncrement) + descent);
        graphics.drawString(String.format("% .2f", -1 * 1 * topRealIncrement), 0, yCoord + (-1 * 1 * topPixelIncrement) + descent);
        graphics.drawString(String.format("% .2f", -1 * 0 * topRealIncrement), 0, yCoord + descent);
        graphics.drawString(String.format("% .2f", -1 * -1 * bottomRealIncrement), 0, yCoord + (-1 * -1 * bottomPixelIncrement) + descent);
        graphics.drawString(String.format("% .2f", -1 * -2 * bottomRealIncrement), 0, yCoord + (-1 * -2 * bottomPixelIncrement) + descent);

        //Now let's start drawing the graphs
        graphics.setColor(Color.RED);
        GraphDot future = null;
        GraphDot past = null;
        double lastX = width;
        ListIterator<GraphDot> it2 = list.listIterator(list.size());
        while (it2.hasPrevious()) {
            past = it2.previous();
            if (past.isPause()) {
                double startX = lastX;
                if (startX < 0)
                    break;
                double startY = 0;

                double endX = startX;
                double endY = height;

                graphics.setColor(Color.BLUE);
                graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
                graphics.setColor(Color.RED);
                //System.out.printf("Width %d and Height %d: Start Coords (%d, %d); End Coords (%d, %d)\n", width, height, startX, startY, endX, endY);
                lastX = endX;
                future = null;
            } else {
                if (future != null) {
                    double startX = lastX;
                    if (startX < 0)
                        break;
                    double startY = (int) (padding + ((topValue - future.getValue()[number / 4][number % 4]) * multiplier));

                    double endX = lastX - ((int) (((future.getTime() - past.getTime()) * secondWidth) / 1000));
                    double endY = (int) (padding + ((topValue - past.getValue()[number / 4][number % 4]) * multiplier));

                    graphics.drawLine((int) startX, (int) startY, (int) endX, (int) endY);
                    //System.out.printf("Width %d and Height %d: Start Coords (%d, %d); End Coords (%d, %d)\n", width, height, startX, startY, endX, endY);
                    lastX = endX;
                }
                future = past;
                past = null;
            }
        }
        it2 = null;

        graphics.setColor(Color.BLUE);
        graphics.drawString(name, (int) (width - (metrics.stringWidth(name) + 5)), metrics.getHeight() + 5);
    }
}
