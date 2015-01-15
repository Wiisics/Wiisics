package wiisics;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

/**
 * Created by funstein on 08/01/15.
 */
public class ConnectProcessor implements Runnable {
    private WiisicsHandler handler;
    private String macAddress;
    public Dialog_Connecting dialog;

    public ConnectProcessor(WiisicsHandler handler, String MAC, Dialog_Connecting dialog) {
        this.handler = handler;
        this.macAddress = MAC;
        this.dialog = dialog;
    }

    public void run() {
        int errors = 0;
        WiiRemote wr = null;
        while (wr == null) {
            if (errors >= 50) {
                System.out.println("Could not connect to the device.");
                dialog.dispose();
                handler.findFailed();
                return;
            }
            try {
                wr = WiiRemoteJ.connectToRemote(macAddress); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                dialog.dispose();
                handler.connectResults(wr);
                return;
            } catch (Exception e) {
                wr = null;
                //e.printStackTrace();
                System.out.println("Failed to connect remote, trying again: Attempt " + (errors + 1) + ".");
                errors++;
            }
        }

        System.out.println("Ending thread");
        dialog.dispose();
        handler.findFailed();
        return;
    }
}
