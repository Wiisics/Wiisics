package wiisics;

import com.apple.laf.AquaLookAndFeel;
import com.intel.bluetooth.BlueCoveConfigProperties;
import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;
import wiiremotej.event.WRIREvent;

import javax.swing.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Created by funstein on 08/01/15.
 */
public class WiisicsHandler {
    public static void main(String args[]) throws Exception {
        WiiRemoteJ.setConsoleLoggingAll();
        System.setProperty(BlueCoveConfigProperties.PROPERTY_JSR_82_PSM_MINIMUM_OFF, "true"); //Fix for weird bug in BlueCove

        javax.swing.UIManager.setLookAndFeel(new MetalLookAndFeel());

        Display display = new Display();
        display.setVisible(true);

        Toolbar toolbar = new Toolbar();
        toolbar.setVisible(true);

        startConnect(display);
    }

    public static void startConnect(Display display) {
            String address = "";

            while (address == null || address.length() != 12) {
                Dialog_Initial dialog = new Dialog_Initial(display, true);
                address = dialog.getMACAddress();
            }

            Dialog_Connecting connecting = new Dialog_Connecting(display, false, address);
    }

    public static void connectResults(Dialog_Connecting connecting) {
        WiiRemote remote = connecting.getRemote();
        Display display = (Display) connecting.getParent();

        if (remote == null) {
            Dialog_ConnectFail failed = new Dialog_ConnectFail(display, true);
            startConnect(display);
        } else {
            System.out.println("Connected!");
            Wiisics wiisics = new Wiisics(remote, display);

            try {
                remote.addWiiRemoteListener(wiisics);
                remote.setAccelerometerEnabled(true);
                remote.setSpeakerEnabled(true);
                remote.setIRSensorEnabled(false, WRIREvent.BASIC);
                remote.setLEDIlluminated(0, true);
            } catch (Exception e) {}

            final WiiRemote remoteF = remote;
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    remoteF.disconnect();
                }
            }));
        }
    }
}
