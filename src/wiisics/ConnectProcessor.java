package wiisics;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

/*
 * Wiisics
 * ConnectProcessor.java
 * Uskudar American Academy, 2015
 *
 * Cem Gokmen <cem@cemgokmen.com>
 * Hakan Alpan <hakan.alpan@hotmail.com>
 */

class ConnectProcessor implements Runnable {
    private final WiisicsHandler handler;
    private final String macAddress;
    private final Dialog_Connecting dialog;

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
                errors++;
            }
        }

        dialog.dispose();
        handler.findFailed();
    }
}
