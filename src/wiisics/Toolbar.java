package wiisics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.InputStream;

public class Toolbar extends JFrame {
    public Toolbar () {
        super();
        initComponents();
    }

    public void initComponents() {
        JToolBar toolbar = new JToolBar("Applications");
        setLayout(new FlowLayout());

        try {
            InputStream playStream = getClass().getResourceAsStream("start.png");
            JButton btnPlay = new JButton(new ImageIcon(ImageIO.read(playStream)));
            btnPlay.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    play();
                }
            });

            InputStream pauseStream = getClass().getResourceAsStream("pause.png");
            JButton btnPause = new JButton(new ImageIcon(ImageIO.read(pauseStream)));
            btnPause.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    pause();
                }
            });

            InputStream resetStream = getClass().getResourceAsStream("reset.png");
            JButton btnReset = new JButton(new ImageIcon(ImageIO.read(resetStream)));
            btnReset.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    reset();
                }
            });

            InputStream saveStream = getClass().getResourceAsStream("save.png");
            JButton btnSave = new JButton(new ImageIcon(ImageIO.read(saveStream)));
            btnSave.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    save();
                }
            });

            InputStream recalibrateStream = getClass().getResourceAsStream("recalibrate.png");
            JButton btnRecalibrate = new JButton(new ImageIcon(ImageIO.read(recalibrateStream)));
            btnRecalibrate.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    recalibrate();
                }
            });

            getContentPane().add(btnPlay);
            getContentPane().add(btnPause);
            getContentPane().add(btnReset);
            getContentPane().add(btnSave);
            getContentPane().add(btnRecalibrate);
        } catch (Exception e) {}


        //getContentPane().add(toolbar, BorderLayout.PAGE_START);
        //setSize(500, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        pack();
    }


    private static void play() {
    }

    private static void pause() {
    }

    private static void reset() {
    }

    private static void save() {
    }

    private static void recalibrate() {
    }
}