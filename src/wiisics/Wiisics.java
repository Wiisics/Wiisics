package wiisics;

import com.intel.bluetooth.BlueCoveConfigProperties;
import java.util.ArrayList;
import java.awt.*;
import javax.swing.*;
import wiiremotej.*;
import wiiremotej.event.*;
import javax.sound.sampled.*;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implements WiiRemoteListener and acts as a general test class. Note that you
 * can ignore the main method pretty much, as it mostly has to do with the
 * graphs and GUIs. At the very end though, there's an example of how to connect
 * to a remote and how to prebuffer audio files.
 *
 * @author Michael Diamond
 * @version 1/05/07
 */
public class Wiisics extends WiiRemoteAdapter {

    private static boolean accelerometerSource = true; //true = wii remote, false = nunchuk
    private static boolean lastSource = true;
    private static boolean mouseTestingOn;
    private static int status = 0;
    private static int accelerometerStatus = 0;
    private static int analogStickStatus = 0;
    private static JFrame mouseTestFrame;
    private static JPanel mouseTestPanel;
    private WiiRemote remote;
    private static JFrame graphFrame;
    private static JPanel graph;
    private static int[][] pixels;
    
    // These are all normalization variables by Cem
    private static int t = 0;
    private static int x = 0;
    private static int y = 0;
    private static int z = 0;
    private static int lastX = 0;
    private static int lastY = 0;
    private static int lastZ = 0;
    public static double lastHeight = 0;
    public static double height = 0;
    public static double lastVelocity = 0;
    public static double velocity = 0;
    public static long lastTime = 0;
    public static long beginTime = 0;
    public static long lastCalibTime = 0;
    public static double zeroAcc = 0;
    public static long lastAccChangeTime = 0;
    public static double lastAcc = 0;

    public static void main(String args[]) {
        //basic console logging options...
        WiiRemoteJ.setConsoleLoggingAll();
        //WiiRemoteJ.setConsoleLoggingOff();

        try {
            mouseTestFrame = new JFrame();
            mouseTestFrame.setTitle("Mouse test");
            final int LS = 50; //line spacing
            mouseTestFrame.setSize(4 * LS, 7 * LS);
            mouseTestFrame.setResizable(false);

            mouseTestPanel = new JPanel() {
                public void paintComponent(Graphics graphics) {
                    graphics.clearRect(0, 0, 4 * LS, 7 * LS);
                    graphics.setColor(Color.YELLOW);
                    if (status == 0) {
                        graphics.fillRect(status * LS, (accelerometerStatus + 1) * LS, LS, LS);
                    } else if (status == 3) {
                        graphics.fillRect(status * LS, (analogStickStatus + 1) * LS, LS, LS);
                    } else {
                        graphics.fillRect(status * LS, LS, LS, LS);
                    }

                    graphics.setColor(Color.BLACK);
                    graphics.drawString("WM", (int) (LS * 0.5), (int) (LS * 1.5));
                    graphics.drawString("WT", (int) (LS * 0.5), (int) (LS * 2.5));
                    graphics.drawString("NM", (int) (LS * 0.5), (int) (LS * 3.5));
                    graphics.drawString("NT", (int) (LS * 0.5), (int) (LS * 4.5));
                    graphics.drawString("**", (int) (LS * 1.5), (int) (LS * 1.5));
                    graphics.drawString("**", (int) (LS * 2.5), (int) (LS * 1.5));
                    graphics.drawString("NA", (int) (LS * 3.5), (int) (LS * 1.5));
                    graphics.drawString("NR", (int) (LS * 3.5), (int) (LS * 2.5));
                    graphics.drawString("LA", (int) (LS * 3.5), (int) (LS * 3.5));
                    graphics.drawString("LR", (int) (LS * 3.5), (int) (LS * 4.5));
                    graphics.drawString("RA", (int) (LS * 3.5), (int) (LS * 5.5));
                    graphics.drawString("RR", (int) (LS * 3.5), (int) (LS * 6.5));

                    paintChildren(graphics);
                }
            };

            mouseTestPanel.setLayout(new FlowLayout());
            mouseTestPanel.add(new JLabel("A          I       IA         AS"));
            mouseTestFrame.add(mouseTestPanel);

            graphFrame = new JFrame();
            graphFrame.setTitle("Accelerometer graph: Wii Remote");
            graphFrame.setSize(800, 600);
            graphFrame.setResizable(false);

            t = 801;
            pixels = new int[800][600];
            graph = new JPanel() {
                public void paintComponent(Graphics graphics) {
                    if (t >= 800 || accelerometerSource != lastSource) {
                        t = 0;
                        lastSource = accelerometerSource;
                        graphics.clearRect(0, 0, 800, 600);
                        graphics.fillRect(0, 0, 800, 600);
                        graphics.setColor(Color.WHITE);
                        graphics.drawLine(0, 300, 800, 300);
                    }

                    graphics.setColor(Color.RED);
                    graphics.drawLine(t, lastX, t, x);
                    graphics.setColor(Color.GREEN);
                    graphics.drawLine(t, lastY, t, y);
                    graphics.setColor(Color.BLUE);
                    graphics.drawLine(t, lastZ, t, z);

                    graphics.setColor(Color.YELLOW);
                    graphics.drawLine(t, (int) lastVelocity, t, (int) velocity);

                    graphics.setColor(Color.WHITE);
                    graphics.drawLine(t, (int) lastHeight, t, (int) height);
                }
            };
            graphFrame.add(graph);
            graphFrame.setVisible(true);

            //Find and connect to a Wii Remote
            System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true"); //Fix for weird bug in BlueCove
            WiiRemote remote = null;

            while (remote == null) {
                try {
                    remote = WiiRemoteJ.connectToRemote("0022AAD458BD"); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                } catch (Exception e) {
                    remote = null;
                    //e.printStackTrace();
                    System.out.println("Failed to connect remote. Trying again.");
                }
            }

            beginTime = System.currentTimeMillis();

            remote.addWiiRemoteListener(new Wiisics(remote));
            remote.setAccelerometerEnabled(true);
            remote.setSpeakerEnabled(true);
            remote.setIRSensorEnabled(true, WRIREvent.BASIC);
            remote.setLEDIlluminated(0, true);

            final WiiRemote remoteF = remote;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    remoteF.disconnect();
                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Wiisics(WiiRemote remote) {
        this.remote = remote;
    }

    public void disconnected() {
        System.out.println("Remote disconnected... Please Wii again.");
        System.exit(0);
    }

    public void statusReported(WRStatusEvent evt) {
        System.out.println("Battery level: " + (double) evt.getBatteryLevel() / 2 + "%");
        System.out.println("Continuous: " + evt.isContinuousEnabled());
        System.out.println("Remote continuous: " + remote.isContinuousEnabled());
    }


    public void accelerationInputReceived(WRAccelerationEvent evt) {
        //System.out.println("R: " + evt.getRoll());
        //System.out.println("P: " + evt.getPitch());
        if (accelerometerSource) {
            long thisTime = System.currentTimeMillis();
            double acc = round(evt.getZAcceleration(), 3);
            if (beginTime > 0 && thisTime - beginTime < 5000) {
                long deltaT = thisTime - lastCalibTime;
                zeroAcc = zeroAcc + (acc * deltaT);

                lastCalibTime = thisTime;

                if (thisTime - beginTime > 5000) {
                    zeroAcc = zeroAcc / (thisTime - beginTime);
                    beginTime = -1;
                }
            }

            lastX = x;
            lastY = y;
            lastZ = z;

            x = (int) (evt.getXAcceleration() / 5 * 300) + 300;
            y = (int) (evt.getYAcceleration() / 5 * 300) + 300;
            z = (int) (evt.getZAcceleration() / 5 * 300) + 300;

            t++;

            if (lastTime == 0) {
                lastTime = thisTime;
            } else {
                double deltaT = (thisTime - lastTime) / 1000.0;
                acc = acc - zeroAcc;

                if (acc - lastAcc > 0.001) {
                    lastAccChangeTime = thisTime;
                    lastAcc = acc;
                }

                if (thisTime - lastAccChangeTime > 2000) {
                    zeroAcc = lastAcc;
                    velocity = 0;
                }

                double deltaV = acc * deltaT;
                double deltaX = (velocity * deltaT) + (0.5 * acc * Math.pow(deltaT, 2));

                lastVelocity = velocity;
                velocity = velocity + deltaV;

                lastHeight = height;
                height = height + deltaX;

                lastTime = thisTime;

                System.out.printf("Velocity: %f - height: %f - acceleration: %f - deltaT: %f - deltaV: %f - deltaX: %f%n", velocity, height, acc, deltaT, deltaV, deltaX);
            }

            graph.repaint();
        }

        /*System.out.println("---Acceleration Data---");
         System.out.println("X: " + evt.getXAcceleration());
         System.out.println("Y: " + evt.getYAcceleration());
         System.out.println("Z: " + evt.getZAcceleration());
         */
    }

    public void extensionInputReceived(WRExtensionEvent evt) {
        System.out.println("Extension input: " + evt);
    }

    public void extensionConnected(WiiRemoteExtension extension) {
        System.out.println("Extension connected!");
        try {
            remote.setExtensionEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void extensionPartiallyInserted() {
        System.out.println("Extension partially inserted. Push it in more next time!");
    }

    public void extensionUnknown() {
        System.out.println("Extension unknown. Did you try to plug in a toaster or something?");
    }

    public void extensionDisconnected(WiiRemoteExtension extension) {
        System.out.println("Extension disconnected. Why'd you unplug it, eh?");
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