package wiisics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.text.MessageFormat;

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
            InputStream playStream = getClass().getResourceAsStream("start.png"); //NON-NLS
            JButton btnPlay = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("start"), new ImageIcon(ImageIO.read(playStream)));
            btnPlay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.start();
                }
            });

            InputStream pauseStream = getClass().getResourceAsStream("pause.png"); //NON-NLS
            JButton btnPause = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("pause"), new ImageIcon(ImageIO.read(pauseStream)));
            btnPause.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.stop();
                }
            });

            InputStream resetStream = getClass().getResourceAsStream("reset.png"); //NON-NLS
            JButton btnReset = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("reset"), new ImageIcon(ImageIO.read(resetStream)));
            btnReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.reset();
                }
            });

            InputStream saveStream = getClass().getResourceAsStream("save.png"); //NON-NLS
            JButton btnSave = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("save"), new ImageIcon(ImageIO.read(saveStream)));
            btnSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.save();
                }
            });

            InputStream recalibrateStream = getClass().getResourceAsStream("recalibrate.png"); //NON-NLS
            JButton btnRecalibrate = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("recalibrate"), new ImageIcon(ImageIO.read(recalibrateStream)));
            btnRecalibrate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (handler.getWiisics() != null)
                        handler.getWiisics().calibrate();
                }
            });

            InputStream connectStream = getClass().getResourceAsStream("connect.png"); //NON-NLS
            JButton btnConnect = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("connect"), new ImageIcon(ImageIO.read(connectStream)));
            btnConnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.startConnect();
                }
            });

            InputStream disconnectStream = getClass().getResourceAsStream("disconnect.png"); //NON-NLS
            JButton btnDisconnect = new JButton(WiisicsHandler.RESOURCE_BUNDLE.getString("disconnect"), new ImageIcon(ImageIO.read(disconnectStream)));
            btnDisconnect.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    handler.disconnect();
                }
            });

            Wiisics wiisics = handler.getWiisics();

            String pitchString = WiisicsHandler.RESOURCE_BUNDLE.getString("pitch") + " ";
            if (wiisics != null) {
                double[] calibratedData = wiisics.getCalibratedData();
                if (calibratedData.length == 2) {
                    pitchString += MessageFormat.format(WiisicsHandler.RESOURCE_BUNDLE.getString("0.degrees"), Math.toDegrees(calibratedData[0]));
                } else {
                    pitchString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
                }
            } else {
                pitchString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
            }
            lblPitch = new JLabel(pitchString);

            String rollString = WiisicsHandler.RESOURCE_BUNDLE.getString("roll") + " ";
            if (wiisics != null) {
                double[] calibratedData = wiisics.getCalibratedData();
                if (calibratedData.length == 2) {
                    rollString += MessageFormat.format(WiisicsHandler.RESOURCE_BUNDLE.getString("0.degrees"), Math.toDegrees(calibratedData[1]));
                } else {
                    rollString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
                }
            } else {
                rollString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
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
        double[] calibratedData = new double[0];
        if (handler.getWiisics() != null) {
            calibratedData = handler.getWiisics().getCalibratedData();
        }

        String pitchString = WiisicsHandler.RESOURCE_BUNDLE.getString("pitch") + " ";
        if (calibratedData.length == 5) {
            int pitch = (int) (Math.toDegrees(calibratedData[0]) + 0.5);
            if (pitch > 180)
                pitch = 360 - pitch;
            pitchString += String.format(WiisicsHandler.RESOURCE_BUNDLE.getString("d.degrees"), pitch);
        } else {
            pitchString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
        }
        lblPitch.setText(pitchString);

        String rollString = WiisicsHandler.RESOURCE_BUNDLE.getString("roll") + " ";
        if (calibratedData.length == 5) {
            int roll = (int) (Math.toDegrees(calibratedData[1]) + 0.5);
            if (roll > 180)
                roll = 360 - roll;
            rollString += String.format(WiisicsHandler.RESOURCE_BUNDLE.getString("d.degrees"), roll);
        } else {
            rollString += WiisicsHandler.RESOURCE_BUNDLE.getString("notavailable");
        }
        lblRoll.setText(rollString);
    }

}