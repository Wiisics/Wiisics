/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wiisics;

/**
 *
 * @author funstein
 */
import com.intellij.uiDesigner.core.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Random;
import javax.swing.*;

public class Display extends JFrame {

    private JMenuBar menuBar1;
    private JMenu menu1;
    private JMenuItem menuItem1;
    private JMenu menu2;
    private JMenuItem menuItem2;
    private Random rand;
    
    private GraphPanel[] displacement;
    private GraphPanel[] velocity;
    private GraphPanel[] acceleration;
    private GraphPanel s3;
    private GraphPanel v3;
    private GraphPanel a3;


    public Display() {
        rand = new Random();
        initComponents();
    }

    public void update(long time, double[] s, double[] v, double[] a) {
        Debugger.println("Update command received.");
        double sSum = 0;
        for (int i = 0; i < s.length; i++) {
            displacement[i].addGraphItem(time, s[i]);
            sSum += Math.pow(s[i], 2);
        }
        s3.addGraphItem(time, Math.sqrt(sSum));
        
        double vSum = 0;
        for (int i = 0; i < v.length; i++) {
            velocity[i].addGraphItem(time, v[i]);
            vSum += Math.pow(v[i], 2);
        }
        v3.addGraphItem(time, Math.sqrt(vSum));
        
        double aSum = 0;
        for (int i = 0; i < a.length; i++) {
            acceleration[i].addGraphItem(time, a[i]);
            aSum += Math.pow(a[i], 2);
        }
        a3.addGraphItem(time, Math.sqrt(aSum));
    }

    private void initComponents() {
        menuBar1 = new JMenuBar();
        menu1 = new JMenu();
        menuItem1 = new JMenuItem();
        menu2 = new JMenu();
        menuItem2 = new JMenuItem();

        //======== this ========
        setTitle("Wiisics v0.1");
        Container contentPane = getContentPane();
        contentPane.setLayout(new GridLayoutManager(3, 4, new Insets(0, 0, 0, 0), 0, 0, true, true));

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("File");

                //---- menuItem1 ----
                menuItem1.setText("Close");
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);

            //======== menu2 ========
            {
                menu2.setText("Help");

                //---- menuItem2 ----
                menuItem2.setText("About");
                menu2.add(menuItem2);
            }
            menuBar1.add(menu2);
        }
        setJMenuBar(menuBar1);

        displacement = new GraphPanel[3];
        velocity = new GraphPanel[3];
        acceleration = new GraphPanel[3];
        
        String[] names = {"X", "Y", "Z"};
        for (int i = 0; i < 3; i++) {
            displacement[i] = generatePanel("s" + names[i]);
            velocity[i] = generatePanel("v" + names[i]);
            acceleration[i] = generatePanel("a" + names[i]);
        }
        
        s3 = generatePanel("sMag");
        v3 = generatePanel("vMag");
        a3 = generatePanel("aMag");

        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private GraphPanel generatePanel(String name) {
        GraphPanel panel = new GraphPanel(name);
        
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

        Container contentPane = getContentPane();
        
        int numPanels = contentPane.getComponentCount();
        Debugger.println((numPanels + 1) + ". panel: column " + (numPanels % 4) + " and row " + (numPanels / 4));
        contentPane.add(panel,
                new GridConstraints(numPanels % 3, numPanels / 3, 1, 1,
                GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                null, null, null));

        return panel;
    }
}
