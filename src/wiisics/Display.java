/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

/**
 *
 * @author funstein
 */

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ListIterator;
import java.util.Random;

public class Display extends JFrame {

    private final WiisicsHandler handler;

    private Toolbar toolbar;
    private final Random rand;

    private JPanel contentsPanel;

    private GraphPanel[] panels;
    private GraphList<GraphDot> graphList;

    public boolean[] viewingModes;

    public GraphList<GraphDot> getGraphList() {
        return graphList;
    }

    private void rescale() {
        ListIterator<GraphDot> it = graphList.listIterator();

        double[][] values = graphList.getLimits();
        for (int i = 0; i < values.length; i++) {
            values[i][0] = -10;
            values[i][1] = 10;
        }
        while (it.hasNext()) {
            GraphDot nextItem = it.next();
            if (!nextItem.isPause()) {
                double[][] data = nextItem.getValue();
                for (int i = 0; i < data.length; i++) {
                    double[] dataSet = data[i];
                    for (double value : dataSet) {
                        if (value > values[i][1])
                            values[i][1] = value;
                        if (value < values[i][0])
                            values[i][0] = value;
                    }
                }
            }
        }
        graphList.setLimits(values);
    }

    public void reset() {
        graphList.clear();
        rescale();
        for (GraphPanel panel : panels) {
            panel.repaint();
        }
    }

    public Display(WiisicsHandler handler) {
        this.handler = handler;
        this.graphList = new GraphList<GraphDot>();
        this.viewingModes = new boolean[3];
        for (int i = 0; i < viewingModes.length; i++) {
            viewingModes[i] = true;
        }
        rescale();
        rand = new Random();
        initComponents();
        updateViewingModes();
        setLocationRelativeTo(null);
    }

    public void update(long time, double[] s, double[] v, double[] a) {
        double sSum = 0;
        double[] newS = new double[4];
        for (int i = 0; i < s.length; i++) {
            newS[i] = s[i];
            sSum += Math.pow(s[i], 2);
        }
        newS[3] = Math.sqrt(sSum);

        double vSum = 0;
        double[] newV = new double[4];
        for (int i = 0; i < v.length; i++) {
            newV[i] = v[i];
            vSum += Math.pow(v[i], 2);
        }
        newV[3] = Math.sqrt(vSum);

        double aSum = 0;
        double[] newA = new double[4];
        for (int i = 0; i < a.length; i++) {
            newA[i] = a[i];
            aSum += Math.pow(a[i], 2);
        }
        newA[3] = Math.sqrt(aSum);

        double[][] values = new double[3][];
        values[0] = newS;
        values[1] = newV;
        values[2] = newA;

        GraphDot dot = new GraphDot(time, values);
        graphList.add(dot);
        rescale();
        for (GraphPanel panel : panels) {
            panel.repaint();
        }
    }

    private void initComponents() {
        JMenuBar menuBar1 = new JMenuBar();
        JMenu menu1 = new JMenu();
        JMenuItem menuItem1 = new JMenuItem();


        JMenu menu2 = new JMenu();
        final JCheckBoxMenuItem menuItem211 = new JCheckBoxMenuItem();
        final JCheckBoxMenuItem menuItem212 = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem menuItem221 = new JCheckBoxMenuItem();
        final JCheckBoxMenuItem menuItem222 = new JCheckBoxMenuItem();
        final JCheckBoxMenuItem menuItem223 = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem menuItem231 = new JCheckBoxMenuItem();
        final JCheckBoxMenuItem menuItem232 = new JCheckBoxMenuItem();

        final JCheckBoxMenuItem menuItem241 = new JCheckBoxMenuItem();
        final JCheckBoxMenuItem menuItem242 = new JCheckBoxMenuItem();


        JMenu menu3 = new JMenu();
        JMenuItem menuItem3 = new JMenuItem();

        //======== this ========
        setTitle(WiisicsHandler.RESOURCE_BUNDLE.getString("wiisics.version"));
        Container contentPane = getContentPane();
        setLayout(new BorderLayout());
        toolbar = new Toolbar(handler);
        getContentPane().add(toolbar, BorderLayout.PAGE_START);

        contentsPanel = new JPanel();
        contentPane.add(contentsPanel, BorderLayout.PAGE_END);
        contentsPanel.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), 0, 0, true, true));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("file"));

                //---- menuItem1 ----
                menuItem1.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("close"));
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("modes"));

                //---- Standard Motion v. Harmonic Motion
                menuItem211.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("standard.motion"));
                menuItem211.setState(true);
                menu2.add(menuItem211);
                menuItem212.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("harmonic.motion"));
                menu2.add(menuItem212);
                menu2.addSeparator();

                //---- Display a, v, x
                menuItem221.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("display.acceleration"));
                menuItem221.setState(true);
                menu2.add(menuItem221);
                menuItem222.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("display.velocity"));
                menuItem222.setState(true);
                menu2.add(menuItem222);
                menuItem223.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("display.displacement"));
                menuItem223.setState(true);
                menu2.add(menuItem223);
                menu2.addSeparator();

                //---- Change sampling vs. Periodical sampling
                menuItem231.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("change.sampling"));
                menuItem231.setState(true);
                menu2.add(menuItem231);
                menuItem232.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("periodical.sampling"));
                menu2.add(menuItem232);
                menu2.addSeparator();

                //---- Functional mode vs. Demo mode
                menuItem241.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("functional.mode"));
                menuItem241.setState(true);
                menu2.add(menuItem241);
                menuItem242.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("demo.mode"));
                menu2.add(menuItem242);
                menu2.addSeparator();
            }
            menuBar1.add(menu2);

            //======== menu3 ========
            {
                menu3.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("help"));

                //---- menuItem3 ----
                menuItem3.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("about"));
                menu3.add(menuItem3);
            }
            menuBar1.add(menu3);
        }
        setJMenuBar(menuBar1);

        final Display display = this;
        menuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Dialog_About(display, true);
            }
        });

        // Viewing mode handlers!

        // Acceleration
        menuItem221.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(display.viewingModes[2] && display.countActiveViewingModes() == 1)) {
                    display.viewingModes[2] = !display.viewingModes[2];
                    display.updateViewingModes();
                }
                menuItem221.setState(display.viewingModes[2]);
            }
        });

        // Velocity
        menuItem222.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(display.viewingModes[1] && display.countActiveViewingModes() == 1)) {
                    display.viewingModes[1] = !display.viewingModes[1];
                    display.updateViewingModes();
                }
                menuItem222.setState(display.viewingModes[1]);
            }
        });

        // Displacement
        menuItem223.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!(display.viewingModes[0] && display.countActiveViewingModes() == 1)) {
                    display.viewingModes[0] = !display.viewingModes[0];
                    display.updateViewingModes();
                }
                menuItem223.setState(display.viewingModes[0]);
            }
        });

        // Standard Motion
        menuItem211.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean harmonicMode = handler.setHarmonicMode(false);
                menuItem211.setState(!harmonicMode);
                menuItem212.setState(harmonicMode);
            }
        });

        // Harmonic Motion
        menuItem212.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean harmonicMode = handler.setHarmonicMode(true);
                menuItem211.setState(!harmonicMode);
                menuItem212.setState(harmonicMode);
            }
        });

        // Change Sampling
        menuItem231.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean periodicalMode = handler.setPeriodicalMode(false);
                menuItem231.setState(!periodicalMode);
                menuItem232.setState(periodicalMode);
            }
        });

        // Periodical Sampling
        menuItem232.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean periodicalMode = handler.setPeriodicalMode(true);
                menuItem231.setState(!periodicalMode);
                menuItem232.setState(periodicalMode);
            }
        });

        // Functional Mode
        menuItem241.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean demoMode = handler.setDemoMode(false);
                menuItem241.setState(!demoMode);
                menuItem242.setState(demoMode);
            }
        });

        // Demo Mode
        menuItem242.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean demoMode = handler.setDemoMode(true);
                menuItem241.setState(!demoMode);
                menuItem242.setState(demoMode);
            }
        });

        panels = new GraphPanel[12];

        String[] names = {"X", "Y", "Z"}; //NON-NLS NON-NLS
        for (int i = 0; i < 3; i++) {
            panels[i] = generatePanel("s" + names[i], i); //NON-NLS
            panels[4 + i] = generatePanel("v" + names[i], 4 + i); //NON-NLS
            panels[8 + i] = generatePanel("a" + names[i], 8 + i); //NON-NLS
        }

        panels[3] = generatePanel("sMag", 3); //NON-NLS
        panels[7] = generatePanel("vMag", 7); //NON-NLS
        panels[11] = generatePanel("aMag", 11); //NON-NLS

        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void updateViewingModes() {
        //System.out.println("--- Starting update of viewing mode ---");
        Container contentPane = contentsPanel;
        for (int i = 0; i < panels.length; i++) {
            contentPane.remove(panels[i]);
        }

        int activeModes = countActiveViewingModes();

        contentsPanel.setLayout(new GridLayoutManager(activeModes, 4, new Insets(0, 0, 0, 0), 0, 0, true, true));

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < panels.length / 4; i++) {
                int key = j + (i * 4);
                GraphPanel panel = panels[key];
                //System.out.println("  Box " + j + "(" + panel.getName() + ") has viewing mode " + viewingModes[j / 4]);
                if (viewingModes[i]) {
                    int numPanels = contentPane.getComponentCount();
                    contentPane.add(panel,
                            new GridConstraints(numPanels % activeModes, numPanels / activeModes, 1, 1,
                                    GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                    GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                    null, null, null)
                    );
                }
            }
        }

        refresh();
        pack();
        //System.out.println("--- Ended update of viewing mode ---");
    }

    private GraphPanel generatePanel(String name, int number) {
        GraphPanel panel = new GraphPanel(name, number, handler);

        panel.setBackground(Color.getHSBColor(rand.nextFloat(), (rand.nextInt(2000) + 1000) / 10000f, 0.9f));

        panel.setLayout(null);

        Dimension preferredSize = new Dimension();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();

        preferredSize.width = (int) Math.min(width / 5, height / 4);
        preferredSize.height = preferredSize.width;

        Insets insets = panel.getInsets();
        preferredSize.width += insets.right;
        preferredSize.height += insets.bottom;
        panel.setPreferredSize(preferredSize);

        return panel;
    }

    public void refresh() {
        for (GraphPanel panel : panels) {
            panel.repaint();
        }

        toolbar.refresh();
    }

    public GraphPanel[] getPanels() {
        return panels;
    }

    private int countActiveViewingModes() {
        int result = 0;
        for (int i = 0; i < viewingModes.length; i++) {
            if (viewingModes[i]) {
                result++;
            }
        }

        return result;
    }
}
