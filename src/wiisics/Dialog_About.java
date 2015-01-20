package wiisics;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.ResourceBundle;

class Dialog_About extends JDialog {
    public Dialog_About(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    private void initComponents() {

        JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JLabel jLabel3 = new javax.swing.JLabel();
        JLabel jLabel4 = new javax.swing.JLabel();
        JLabel jLabel5 = new javax.swing.JLabel();
        JLabel jLabel6 = new javax.swing.JLabel();
        JLabel jLabel7 = new javax.swing.JLabel();
        JLabel jLabel8 = new javax.swing.JLabel();
        JLabel jLabel9 = new javax.swing.JLabel();
        JLabel jLabel10 = new javax.swing.JLabel();
        JLabel jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N NON-NLS
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("wiisics.version"));

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N NON-NLS
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("developers"));

        try {
            jLabel3.setIcon(new javax.swing.ImageIcon(ImageIO.read(getClass().getResourceAsStream("Wiisics.png")))); // NOI18N NON-NLS
        } catch (Exception e) {
        }

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel4.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("cemgokmen"));

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("hakanalpan"));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("developed"));

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("atUAA"));

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N NON-NLS
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("specialthanks"));

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel9.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("huseyinkose"));

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel10.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("micromu"));

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel11.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("rights"));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(layout.createSequentialGroup()
                                                .add(51, 51, 51)
                                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                        .add(jLabel4)
                                                        .add(jLabel2)
                                                        .add(jLabel5)
                                                        .add(jLabel7)
                                                        .add(jLabel6)
                                                        .add(jLabel8)
                                                        .add(jLabel9)
                                                        .add(jLabel10)
                                                        .add(jLabel11)))
                                        .add(layout.createSequentialGroup()
                                                .addContainerGap()
                                                .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel3)
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(layout.createSequentialGroup()
                                .add(39, 39, 39)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                        .add(jLabel3)
                                        .add(layout.createSequentialGroup()
                                                .add(jLabel1)
                                                .add(29, 29, 29)
                                                .add(jLabel2)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel4)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel5)
                                                .add(18, 18, 18)
                                                .add(jLabel6)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel7)
                                                .add(18, 18, 18)
                                                .add(jLabel8)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel9)
                                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                .add(jLabel10)
                                                .add(18, 18, 18)
                                                .add(jLabel11)))
                                .addContainerGap(32, Short.MAX_VALUE))
        );

        pack();
        setResizable(false);
        setLocationRelativeTo(getParent());
        setVisible(true);
    }
}
