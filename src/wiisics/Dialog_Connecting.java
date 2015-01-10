package wiisics;

import wiiremotej.WiiRemote;
import wiiremotej.WiiRemoteJ;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class Dialog_Connecting extends JDialog {

    private Thread finderThread;

    /**
     * Creates new form Initial
     */
    public Dialog_Connecting(java.awt.Frame parent, boolean modal, String MAC) {
        super(parent, modal);
        macAddress = MAC;
        initComponents();
        setLocationRelativeTo(parent);
        setVisible(true);
        finderThread = new Thread(new ConnectProcessor(this, macAddress));
        finderThread.run();
    }

    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cancel = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Wiisics Initialization");

        jLabel2.setForeground(new java.awt.Color(0, 102, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Connecting to Wiimote at " + macAddress + "...");

        cancel.setText("Cancel");
        cancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 440, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(cancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 440, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(16, 16, 16)
                                .add(jLabel1)
                                .add(6, 6, 6)
                                .add(jLabel2)
                                .add(6, 6, 6)
                                .add(cancel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {
        remote = null;
        finderThread.interrupt();
        WiiRemoteJ.stopFind();
        //finderThread.stop();
        findFailed();
    }

    public WiiRemote getRemote() {
        return remote;
    }

    public void foundRemote(WiiRemote wr) {
        remote = wr;
        dispose();
        WiisicsHandler.connectResults(this);
    }

    public void findFailed() {
        try {
            finderThread.join();
        } catch (Exception e) {}
        dispose();
        WiisicsHandler.connectResults(this);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton cancel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;

    private WiiRemote remote;
    private String macAddress;
    // End of variables declaration
}