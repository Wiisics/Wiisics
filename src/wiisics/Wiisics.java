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
    private static int[][] pixels;
    private static PhysicsProcessor physics;
    private static Display display;

    public static void main(String args[]) {
        //basic console logging options...
        //WiiRemoteJ.setConsoleLoggingAll();
        WiiRemoteJ.setConsoleLoggingOff();

        display = new Display();
        display.setVisible(true);
        
        try {
            //Find and connect to a Wii Remote
            System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true"); //Fix for weird bug in BlueCove
            WiiRemote remote = null;

            int errors = 0;
            while (remote == null) {
                if (errors >= 50) {
                    System.out.println("Could not connect to the device.");
                    System.exit(0);
                }

                try {
                    remote = WiiRemoteJ.connectToRemote("0022AAD458BD"); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                } catch (Exception e) {
                    remote = null;
                    //e.printStackTrace();
                    System.out.println("Failed to connect remote, trying again: Attempt " + (errors + 1) + ".");
                    errors++;
                }
            }

            physics = new PhysicsProcessor();

            remote.addWiiRemoteListener(new Wiisics(remote));
            remote.setAccelerometerEnabled(true);
            remote.setSpeakerEnabled(true);
            remote.setIRSensorEnabled(true, WRIREvent.BASIC);
            remote.setLEDIlluminated(0, true);

            final WiiRemote remoteF = remote;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    remoteF.disconnect();
                }
            }));
        } catch (Exception e) {
            System.out.println("Could not connect to Wii Remote: Trying again.");
        }
    }

    public Wiisics(WiiRemote remote) {
        this.remote = remote;
    }

    @Override
    public void disconnected() {
        System.out.println("Remote disconnected... Please Wii again.");
        System.exit(0);
    }

    @Override
    public void statusReported(WRStatusEvent evt) {
        System.out.println("Battery level: " + (double) evt.getBatteryLevel() / 2 + "%");
        System.out.println("Continuous: " + evt.isContinuousEnabled());
        System.out.println("Remote continuous: " + remote.isContinuousEnabled());
    }

    @Override
    public void accelerationInputReceived(WRAccelerationEvent evt) {
        if (accelerometerSource) {
            Debugger.println("Acceleration input received");
            AccelerationConstants consts = remote.getAccelerationConstants();
            physics.update(consts, evt.getXAcceleration(), evt.getYAcceleration(), evt.getZAcceleration(), evt.getPitch(), evt.getRoll());

            display.update(physics.getTime(), physics.getDisplacement(), physics.getVelocity(), physics.getAcceleration());
        }
    }

    /*public void extensionInputReceived(WRExtensionEvent evt) {
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
     }*/
}