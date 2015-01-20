package wiisics;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

import javax.swing.*;

/*
 * Wiisics
 * ConnectProcessor.java
 * Uskudar American Academy, 2015
 *
 * Cem Gokmen <cem@cemgokmen.com>
 * Hakan Alpan <hakan.alpan@hotmail.com>
 */

class ConnectProcessor extends SwingWorker<WiiRemote, Object> {
    private final WiisicsHandler handler;
    private final String macAddress;
    private final Dialog_Connecting dialog;

    public ConnectProcessor(WiisicsHandler handler, String MAC, Dialog_Connecting dialog) {
        this.handler = handler;
        this.macAddress = MAC;
        this.dialog = dialog;
    }

    public WiiRemote doInBackground() {
        int errors = 0;
        WiiRemote wr = null;
        while (true) {
            if (errors >= 50) {
                return null;
            }
            try {
                wr = WiiRemoteJ.connectToRemote(macAddress); //WiiRemoteJ.findRemote(); // Put the Bluetooth MAC here
                return wr;
            } catch (Exception e) {
                wr = null;
                errors++;
            }
        }
    }

    @Override
    protected void done() {
        try {
            WiiRemote remote = get();
            dialog.dispose();
            handler.connectResults(remote);
        } catch (Exception ignore) {
        }
    }
}
