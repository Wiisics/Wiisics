package wiisics;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

/**
 * Created by funstein on 08/01/15.
 */
public class ConnectProcessor implements Runnable {
    private Dialog_Connecting parent;
    private String macAddress;

    public ConnectProcessor(Dialog_Connecting dialog, String MAC) {
        this.parent = dialog;
        this.macAddress = MAC;
    }

    public void run() {
        int errors = 0;
        WiiRemote wr = null;
        while (wr == null && !Thread.currentThread().isInterrupted()) {
            if (errors >= 50) {
                System.out.println("Could not connect to the device.");
                parent.findFailed();
                return;
            }
            try {
                wr = WiiRemoteJ.connectToRemote(macAddress); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                parent.foundRemote(wr);
                return;
            } catch (Exception e) {
                wr = null;
                //e.printStackTrace();
                System.out.println("Failed to connect remote, trying again: Attempt " + (errors + 1) + ".");
                errors++;
            }
        }

        System.out.println("Ending thread");
        parent.findFailed();
        return;
    }
}
