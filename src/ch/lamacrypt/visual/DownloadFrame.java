/*
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.visual;

import ch.lamacrypt.internal.file.GPFile;
import ch.lamacrypt.visual.workers.ShareWorker;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

/**
 * Shows the state of the current downloaded share file
 *
 * @author LamaGuy
 */
public class DownloadFrame extends javax.swing.JFrame {

    private static ShareWorker sw;
    private static String fileName,
            filePath,
            fileSize;
    private static boolean done = false;

    /**
     * Creates new form DownloadForm
     */
    public DownloadFrame() {
        initComponents();
        setLocationRelativeTo(null);
        updateStatus("Pending");
        filenameLabelStatus.setText(fileName);
        sizeLabelStatus.setText(fileSize);
        destinationLabelStatus.setText(filePath);
    }

    public static void setWorker(ShareWorker sw) {
        DownloadFrame.sw = sw;
    }

    public static void setText(String fileName, long size, File destFile) throws IOException {
        DownloadFrame.fileSize = GPFile.longToSize(size);
        DownloadFrame.fileName = fileName;
        DownloadFrame.filePath = destFile.getCanonicalPath();
    }

    public static void updateStatus(String status) {
        statusLabelStatus.setText(status);
    }

    public static void finish() {
        cancelButton.setEnabled(false);
        okButton.setEnabled(true);
        done = true;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        filenameLabel = new javax.swing.JLabel();
        sizeLabel = new javax.swing.JLabel();
        destinationLabel = new javax.swing.JLabel();
        filenameLabelStatus = new javax.swing.JLabel();
        destinationLabelStatus = new javax.swing.JLabel();
        sizeLabelStatus = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        statusLabelStatus = new javax.swing.JLabel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Download share file");
        setMinimumSize(new java.awt.Dimension(460, 140));
        setSize(new java.awt.Dimension(460, 110));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        filenameLabel.setText("File name");

        sizeLabel.setText("Size");

        destinationLabel.setText("Destination");

        filenameLabelStatus.setText("placeholdertxt");

        destinationLabelStatus.setText("placeholdertxt");

        sizeLabelStatus.setText("placeholdertxt");

        statusLabel.setText("Status");

        statusLabelStatus.setText("placeholdertxt");

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(destinationLabel)
                                    .addComponent(sizeLabel)
                                    .addComponent(filenameLabel))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(filenameLabelStatus)
                                    .addComponent(sizeLabelStatus)
                                    .addComponent(destinationLabelStatus)
                                    .addComponent(statusLabelStatus)))
                            .addComponent(statusLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 234, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addComponent(cancelButton))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(filenameLabel)
                    .addComponent(filenameLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sizeLabel)
                    .addComponent(sizeLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(destinationLabel)
                    .addComponent(destinationLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusLabel)
                    .addComponent(statusLabelStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        if (!done) {
            int reply = JOptionPane.showConfirmDialog(this, "There is an active download. Are you sure "
                    + "you want to exit ?", "Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (reply == JOptionPane.YES_OPTION) {
                try {
                    ShareWorker.stopDownload();
                    sw.cancel(true);
                    this.dispose();
                } catch (IOException ex) {
                    ErrorHandler.showError(ex);
                }
            }
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        cancelButtonActionPerformed(null);
    }//GEN-LAST:event_formWindowClosed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DownloadFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DownloadFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DownloadFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DownloadFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new DownloadFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JButton cancelButton;
    private static javax.swing.JLabel destinationLabel;
    private static javax.swing.JLabel destinationLabelStatus;
    private static javax.swing.JLabel filenameLabel;
    private static javax.swing.JLabel filenameLabelStatus;
    private static javax.swing.JButton okButton;
    private static javax.swing.JLabel sizeLabel;
    private static javax.swing.JLabel sizeLabelStatus;
    private static javax.swing.JLabel statusLabel;
    private static javax.swing.JLabel statusLabelStatus;
    // End of variables declaration//GEN-END:variables

}
