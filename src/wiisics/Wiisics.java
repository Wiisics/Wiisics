package wiisics;

import com.intel.bluetooth.BlueCoveConfigProperties;
import java.awt.*;
import javax.swing.*;
import wiiremotej.*;
import wiiremotej.event.*;

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
    
    private static PhysicsProcessor physics;


    public static void main(String args[]) {
        //basic console logging options...
        WiiRemoteJ.setConsoleLoggingAll();
        //WiiRemoteJ.setConsoleLoggingOff();

        try {
            graphFrame = new JFrame();
            graphFrame.setTitle("Accelerometer graph: Wii Remote");
            graphFrame.setSize(800, 600);
            graphFrame.setResizable(false);

            physics = new PhysicsProcessor();
            pixels = new int[800][600];
            graph = new JPanel() {
                public void paintComponent(Graphics graphics) {
                    int time = physics.getTime();
                    
                    if (time >= 800 || accelerometerSource != lastSource) {
                        physics.setTime(0);
                        lastSource = accelerometerSource;
                        graphics.clearRect(0, 0, 800, 600);
                        graphics.fillRect(0, 0, 800, 600);
                        graphics.setColor(Color.WHITE);
                        graphics.drawLine(0, 300, 800, 300);
                    }
                    
                    double[] acceleration = physics.getAcceleration();
                    double[] lastAcceleration = physics.getLastAcceleration();
                    graphics.setColor(Color.RED);
                    graphics.drawLine(time, (int) lastAcceleration[0], time, (int) acceleration[0]);
                    graphics.setColor(Color.GREEN);
                    graphics.drawLine(time, (int) lastAcceleration[1], time, (int) acceleration[1]);
                    graphics.setColor(Color.BLUE);
                    graphics.drawLine(time, (int) lastAcceleration[2], time, (int) acceleration[2]);

                    /*graphics.setColor(Color.YELLOW);
                    graphics.drawLine(time, (int) physics.getLastVelocity(), t, (int) physics.getVelocity());

                    graphics.setColor(Color.WHITE);
                    graphics.drawLine(time, (int) physics.getLastX(), t, (int) height);*/
                    
                    //System.out.printf("Velocity: %f - height: %f - acceleration: %f - deltaT: %f - deltaV: %f - deltaX: %f%n", velocity, height, acc);
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

        }
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
}