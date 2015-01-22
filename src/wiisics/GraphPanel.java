/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

import javax.swing.*;
import java.awt.*;
import java.util.ListIterator;

/**
 * @author funstein
 */
public class GraphPanel extends JPanel {
    public static final int GRAPH_TIMEFRAME = 5;
    private final WiisicsHandler handler;
    private final String name;
    private final int number;

    public GraphPanel(String name, int number, WiisicsHandler handler) {
        super();
        this.name = name;
        this.number = number;
        this.handler = handler;
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        double width = this.getWidth();
        double height = this.getHeight();
        paintGraphPanel(graphics, width, height);
    }

    public void paintGraphPanel(Graphics graphics, double width, double height) {
        // Calculate the width of one second if the entire graph will encompass 10 seconds
        double secondWidth = height / GRAPH_TIMEFRAME;

        // Make a clone of the linkedlist for use here
        GraphList<GraphDot> list = handler.getDisplay().getGraphList();

        //Now scan the entire graph to find the lowest and highest Y values
        double[][] scaleSet = list.getLimits();
        double minValue = scaleSet[number / 4][0];
        double maxValue = scaleSet[number / 4][1];

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
        float size = graphics.getFont().getSize2D() * (float) (height / 300.0);
        Font font = graphics.getFont().deriveFont(size);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        int descent = metrics.getDescent();
        graphics.drawString(String.format("% .2f", 2 * topRealIncrement), 0, yCoord + (-1 * 2 * topPixelIncrement) + descent); //NON-NLS
        graphics.drawString(String.format("% .2f", 1 * topRealIncrement), 0, yCoord + (-1 * 1 * topPixelIncrement) + descent); //NON-NLS
        graphics.drawString(String.format("% .2f", -0 * topRealIncrement), 0, yCoord + descent); //NON-NLS
        graphics.drawString(String.format("% .2f", -1 * bottomRealIncrement), 0, yCoord + (-1 * -1 * bottomPixelIncrement) + descent); //NON-NLS
        graphics.drawString(String.format("% .2f", -2 * bottomRealIncrement), 0, yCoord + (-1 * -2 * bottomPixelIncrement) + descent); //NON-NLS

        if (list.size() > 0) {
            // Let's try and get the time information ready
            long maxTime = list.get(list.size() - 1).getTime(); // Get the latest data time
            long timeFit = (long) ((width / secondWidth) * 1000); // How many milliseconds fit?
            long minTime = maxTime - timeFit; // The smallest data time
            long zeroTime = list.get(0).getTime();
            boolean zeroFits = minTime <= zeroTime; // Does zero fit in the frame?

            int minSeconds, maxSeconds;
            if (zeroFits) {
                // If zero is in the frame, start from zero and keep going
                minSeconds = 0;
                maxSeconds = ((int) ((maxTime - zeroTime) / 1000.0));
            } else {
                // Otherwise, start from the right side and keep going backwards
                maxSeconds = (int) ((maxTime - zeroTime) / 1000.0);
                minSeconds = (int) (((minTime - zeroTime) / 1000.0) + 0.5D);
            }

            // Draw the timestamps!
            for (int currentSecond = maxSeconds; currentSecond >= minSeconds; currentSecond--) {
                long milliseconds = (currentSecond * 1000) + zeroTime;
                int middleX = (int) (width - ((((maxTime - milliseconds) * (long) secondWidth) / 1000.0)));
                String print = currentSecond + "s";
                int startX = middleX - (metrics.stringWidth(print) / 2);
                if (zeroFits && startX < 0)
                    startX = 0;

                graphics.drawLine(middleX, yCoord + 3, middleX, yCoord - 3); // Draw the tick
                graphics.drawString(print, startX, (int) (yCoord - ((descent * 2) + 5))); // And then the text
            }

            //Now let's start drawing the graphs
            graphics.setColor(Color.RED);
            GraphDot future = null;
            GraphDot past;
            ListIterator<GraphDot> it2 = list.listIterator(list.size());
            while (it2.hasPrevious()) {
                past = it2.previous();
                int thisX = (int) (width - ((((maxTime - past.getTime()) * (long) secondWidth) / 1000.0)));
                if (past.isPause()) {
                    graphics.setColor(Color.BLUE);
                    graphics.drawLine(thisX, 0, thisX, (int) height);
                    graphics.setColor(Color.RED);
                    future = null;
                } else {
                    if (future != null) {
                        if (thisX < 0)
                            break;
                        int thisY = (int) (padding + ((topValue - past.getValue()[number / 4][number % 4]) * multiplier));

                        int otherX = (int) (width - ((((maxTime - future.getTime()) * (long) secondWidth) / 1000.0)));
                        int otherY = (int) (padding + ((topValue - future.getValue()[number / 4][number % 4]) * multiplier));

                        graphics.drawLine(thisX, thisY, otherX, otherY);
                    }
                    future = past;
                    past = null;
                }
            }
        }

        graphics.setColor(Color.BLUE);
        graphics.drawString(name, (int) (width - (metrics.stringWidth(name) + 5)), metrics.getHeight() + 5);
    }
}
