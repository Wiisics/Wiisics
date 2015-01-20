package wiisics;

import javax.swing.*;

class Dialog_ConnectFail extends JDialog {
    public Dialog_ConnectFail(JFrame parent, boolean modal) {
        super(parent, modal);
        initComponents();
    }

    private void initComponents() {

        JLabel jLabel1 = new javax.swing.JLabel();
        JLabel jLabel2 = new javax.swing.JLabel();
        JButton retry = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N NON-NLS
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("initialization"));

        jLabel2.setForeground(new java.awt.Color(100, 0, 0));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("connectfail"));

        retry.setText(WiisicsHandler.RESOURCE_BUNDLE.getString("retry"));
        retry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                retryActionPerformed(evt);
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
                                        .add(retry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 440, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
                                .add(retry, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 48, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
        setLocationRelativeTo(getParent());
        setResizable(false);
        setVisible(true);
    }

    private void retryActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
    }
}