package wiisics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

class Toolbar extends JToolBar {
    private final WiisicsHandler handler;
    private JLabel lblPitch;
    private JLabel lblRoll;

    public Toolbar(WiisicsHandler handler) {
        super();
        this.handler = handler;
        initComponents();
    }

    private void initComponents() {
        try {
            InputStream playStream = getClass().getResourceAsStream("start.png");
            JButton btnPlay = new JButton("Start", new ImageIcon(ImageIO.read(playStream)));
            btnPlay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.start();
                }
            });

            InputStream pauseStream = getClass().getResourceAsStream("pause.png");
            JButton btnPause = new JButton("Pause", new ImageIcon(ImageIO.read(pauseStream)));
            btnPause.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.stop();
                }
            });

            InputStream resetStream = getClass().getResourceAsStream("reset.png");
            JButton btnReset = new JButton("Reset", new ImageIcon(ImageIO.read(resetStream)));
            btnReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.reset();
                }
            });

            InputStream saveStream = getClass().getResourceAsStream("save.png");
            JButton btnSave = new JButton("Save", new ImageIcon(ImageIO.read(saveStream)));
            btnSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.save();
                }
            });

            InputStream recalibrateStream = getClass().getResourceAsStream("recalibrate.png");
            JButton btnRecalibrate = new JButton("Recalibrate", new ImageIcon(ImageIO.read(recalibrateStream)));
            btnRecalibrate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (handler.getWiisics() != null)
                        handler.getWiisics().calibrate();
                }
            });

            InputStream connectStream = getClass().getResourceAsStream("connect.png");
            JButton btnConnect = new JButton("Connect", new ImageIcon(ImageIO.read(connectStream)));
            btnConnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.startConnect();
                }
            });

            InputStream disconnectStream = getClass().getResourceAsStream("disconnect.png");
            JButton btnDisconnect = new JButton("Disconnect", new ImageIcon(ImageIO.read(disconnectStream)));
            btnDisconnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.disconnect();
                }
            });

            Wiisics wiisics = handler.getWiisics();

            String pitchString = "Pitch: ";
            if (wiisics != null) {
                double[] calibratedData = wiisics.getCalibratedData();
                if (calibratedData.length == 2) {
                    pitchString += Math.toDegrees(calibratedData[0]) + " degrees";
                } else {
                    pitchString += "N/A";
                }
            } else {
                pitchString += "N/A";
            }
            lblPitch = new JLabel(pitchString);

            String rollString = "Roll: ";
            if (wiisics != null) {
                double[] calibratedData = wiisics.getCalibratedData();
                if (calibratedData.length == 2) {
                    rollString += Math.toDegrees(calibratedData[1]) + " degrees";
                } else {
                    rollString += "N/A";
                }
            } else {
                rollString += "N/A";
            }
            lblRoll = new JLabel(rollString);

            add(btnPlay);
            add(btnPause);
            add(btnReset);
            addSeparator();
            add(btnSave);
            add(btnRecalibrate);
            addSeparator();
            add(btnConnect);
            add(btnDisconnect);
            addSeparator();
            add(lblPitch);
            addSeparator();
            add(lblRoll);
        } catch (Exception ignored) {
        }

        setFloatable(false);
    }

    public void refresh() {
        double[] calibratedData = handler.getWiisics().getCalibratedData();

        String pitchString = "Pitch: ";
        if (calibratedData.length == 5)
            pitchString += String.format("%d degrees", (int) (Math.toDegrees(calibratedData[0]) + 0.5));
        else
            pitchString += "N/A";
        lblPitch.setText(pitchString);

        String rollString = "Roll: ";
        if (calibratedData.length == 5)
            rollString += String.format("%d degrees", (int) (Math.toDegrees(calibratedData[1]) + 0.5));
        else
            rollString += "N/A";
        lblRoll.setText(rollString);
    }

}