/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.visual;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.internal.crypto.DefaultCipher;
import ch.lamacrypt.internal.crypto.GPCrypto;
import ch.lamacrypt.internal.file.GPFile;
import ch.lamacrypt.internal.network.Control;
import ch.lamacrypt.internal.network.IO;
import ch.lamacrypt.visual.workers.FileWorker;
import ch.lamacrypt.visual.workers.ShareWorker;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

/**
 * Main UI
 *
 * @author LamaGuy
 */
public class DefaultFrame extends javax.swing.JFrame {

    private final long quota;
    private FileDialog fd;
    private FileWorker fw;
    private String newDirName,
            cwd;
    private long time = System.currentTimeMillis();
    private static String quotaMax;
    private static boolean disconnecting = false;
    private static DefaultTableModel dtmFileTable, dtmFileQueue;
    private static Iterator<Map.Entry<String, Long>> dirMapIter;
    private static Iterator<Map.Entry<String, Long[]>> fileMapIter;

    public DefaultFrame() throws IOException {
        initComponents();
        setLocationRelativeTo(null);
        quota = Settings.getQuotaSize();
        if (quota == 500000000000L) {
            quotaMax = "500 GB";
        } else if (quota == 1000000000000L) {
            quotaMax = "1 TB";
        } else if (quota == 2000000000000L) {
            quotaMax = "2 TB";
        } else if (quota == 4000000000000L) {
            quotaMax = "4 TB";
        }
        updateFileTable();
    }

    /**
     * Updates the file table view, so that new files and folders are shown
     *
     * @throws IOException
     */
    public static void updateFileTable() throws IOException {
        Map<String, Long> dirMap = Control.lsdir(Control.cwd());
        Map<String, Long[]> fileMap = Control.lsfile(Control.cwd());
        int itemCount = dirMap.size() + fileMap.size(),
                i = 0;

        if (!Control.isAtRoot()) {
            setFileTableSize(itemCount + 1);
            fileTable.setValueAt("..", i, 0);
            fileTable.setValueAt("", i, 1);
            fileTable.setValueAt("Directory", i, 2);
            fileTable.setValueAt("", i, 3);
            i++;
        } else {
            setFileTableSize(itemCount);
        }

        if (!dirMap.isEmpty()) {
            dirMapIter = dirMap.entrySet().iterator();
            while (dirMapIter.hasNext()) {
                Map.Entry<String, Long> entry = dirMapIter.next();
                fileTable.setValueAt(entry.getKey(), i, 0);
                fileTable.setValueAt(new Date(entry.getValue()), i, 1);
                fileTable.setValueAt("Directory", i, 2);
                fileTable.setValueAt("", i, 3);
                i++;
            }
        }

        if (!fileMap.isEmpty()) {
            fileMapIter = fileMap.entrySet().iterator();
            while (fileMapIter.hasNext()) {
                Map.Entry<String, Long[]> entry = fileMapIter.next();
                fileTable.setValueAt(entry.getKey(), i, 0);
                fileTable.setValueAt(new Date(entry.getValue()[0]), i, 1);
                fileTable.setValueAt("File", i, 2);
                fileTable.setValueAt(GPFile.longToSize(entry.getValue()[1]), i, 3);
                i++;
            }
        }

        long usageBytes = Control.quotaused();
        quotaAmount.setText(GPFile.longToSize(usageBytes) + " / " + quotaMax);
    }

    /**
     * Sets a new row count for the file table
     *
     * @param newRowCount new row count
     */
    public static void setFileTableSize(int newRowCount) {
        dtmFileTable = (DefaultTableModel) fileTable.getModel();
        dtmFileTable.setRowCount(newRowCount);
        fileTable.setModel(dtmFileTable);
    }

    /**
     * Sets a new row count for the file queue
     *
     * @param newRowCount new row count
     */
    public static void updateFileQueueSize(int newRowCount) {
        dtmFileQueue = (DefaultTableModel) fileQueue.getModel();
        dtmFileQueue.setRowCount(dtmFileQueue.getRowCount() + newRowCount);
        fileQueue.setModel(dtmFileQueue);
    }

    /**
     * Sets a new status for the first item in the file queue
     *
     * @param status new status
     */
    public static void setFileQueueItemStatus(String status) {
        fileQueue.setValueAt(status, 0, 2);
    }

    private String sanitize(String s) {
        return s.replaceAll("/", "").replace("..", "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fileTablePopupMenu = new javax.swing.JPopupMenu();
        download = new javax.swing.JMenuItem();
        separator0 = new javax.swing.JPopupMenu.Separator();
        move = new javax.swing.JMenuItem();
        separator1 = new javax.swing.JPopupMenu.Separator();
        delete = new javax.swing.JMenuItem();
        rename = new javax.swing.JMenuItem();
        fileQueuePopupMenu = new javax.swing.JPopupMenu();
        deleteQueueItem = new javax.swing.JMenuItem();
        fileTableScrollPane = new javax.swing.JScrollPane();
        fileTable = new javax.swing.JTable();
        fileQueueLabel = new javax.swing.JLabel();
        fileQueueScrollPane = new javax.swing.JScrollPane();
        fileQueue = new javax.swing.JTable();
        quotaLabel = new javax.swing.JLabel();
        quotaAmount = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        uploadMenuItem = new javax.swing.JMenuItem();
        downloadMenuItem = new javax.swing.JMenuItem();
        downloadShareMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mkdirMenuItem = new javax.swing.JMenuItem();
        renameMenuItem = new javax.swing.JMenuItem();
        rmMenuItem = new javax.swing.JMenuItem();
        moveMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        shareMenuItem = new javax.swing.JMenuItem();
        unshareMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        disconnectMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        preferencesMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        refreshMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        encPassMenuItem = new javax.swing.JMenuItem();
        benchmarkMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        download.setMnemonic('d');
        download.setText("Download");
        download.setToolTipText("Download file to this device");
        download.setName(""); // NOI18N
        download.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadActionPerformed(evt);
            }
        });
        fileTablePopupMenu.add(download);
        fileTablePopupMenu.add(separator0);

        move.setText("Move");
        move.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveActionPerformed(evt);
            }
        });
        fileTablePopupMenu.add(move);
        fileTablePopupMenu.add(separator1);

        delete.setMnemonic('r');
        delete.setText("Delete");
        delete.setToolTipText("Deletes file(s) from the server");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });
        fileTablePopupMenu.add(delete);

        rename.setText("Rename");
        rename.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameActionPerformed(evt);
            }
        });
        fileTablePopupMenu.add(rename);

        deleteQueueItem.setText("Cancel");
        deleteQueueItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteQueueItemActionPerformed(evt);
            }
        });
        fileQueuePopupMenu.add(deleteQueueItem);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("LamaCrypt");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setLocation(new java.awt.Point(100, 100));
        setMaximumSize(new java.awt.Dimension(2000, 2000));
        setMinimumSize(new java.awt.Dimension(852, 614));
        setSize(new java.awt.Dimension(852, 614));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        fileTable.setAutoCreateRowSorter(true);
        fileTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null}
            },
            new String [] {
                "Name", "Date modified", "Type", "Size"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fileTable.setToolTipText("");
        fileTable.setDragEnabled(true);
        fileTable.setIntercellSpacing(new java.awt.Dimension(1, 5));
        fileTable.setMinimumSize(new java.awt.Dimension(525, 32));
        fileTable.setRowHeight(32);
        fileTable.setShowHorizontalLines(false);
        fileTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fileTableMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileTableMouseReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fileTableMouseClicked(evt);
            }
        });
        fileTableScrollPane.setViewportView(fileTable);
        if (fileTable.getColumnModel().getColumnCount() > 0) {
            fileTable.getColumnModel().getColumn(0).setMinWidth(250);
            fileTable.getColumnModel().getColumn(0).setMaxWidth(999999999);
            fileTable.getColumnModel().getColumn(1).setMinWidth(165);
            fileTable.getColumnModel().getColumn(1).setPreferredWidth(165);
            fileTable.getColumnModel().getColumn(1).setMaxWidth(150);
            fileTable.getColumnModel().getColumn(2).setMinWidth(100);
            fileTable.getColumnModel().getColumn(2).setPreferredWidth(100);
            fileTable.getColumnModel().getColumn(2).setMaxWidth(100);
            fileTable.getColumnModel().getColumn(3).setMinWidth(100);
            fileTable.getColumnModel().getColumn(3).setPreferredWidth(100);
            fileTable.getColumnModel().getColumn(3).setMaxWidth(100);
        }

        fileQueueLabel.setText("File queue");

        fileQueue.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Source", "Destination", "Status", "Action"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fileQueue.setRowHeight(20);
        fileQueue.setShowHorizontalLines(false);
        fileQueue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                fileQueueMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                fileQueueMouseReleased(evt);
            }
        });
        fileQueueScrollPane.setViewportView(fileQueue);
        if (fileQueue.getColumnModel().getColumnCount() > 0) {
            fileQueue.getColumnModel().getColumn(0).setPreferredWidth(200);
            fileQueue.getColumnModel().getColumn(1).setPreferredWidth(200);
            fileQueue.getColumnModel().getColumn(2).setMinWidth(150);
            fileQueue.getColumnModel().getColumn(2).setPreferredWidth(150);
            fileQueue.getColumnModel().getColumn(2).setMaxWidth(150);
            fileQueue.getColumnModel().getColumn(3).setResizable(false);
            fileQueue.getColumnModel().getColumn(3).setPreferredWidth(100);
        }

        quotaLabel.setText("Space used:");

        quotaAmount.setToolTipText("");

        menuBar.setAlignmentX(0.0F);
        menuBar.setMargin(new java.awt.Insets(0, 10, 10, 0));

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        uploadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        uploadMenuItem.setText("Upload");
        uploadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(uploadMenuItem);

        downloadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        downloadMenuItem.setText("Download");
        downloadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(downloadMenuItem);

        downloadShareMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        downloadShareMenuItem.setText("Download share");
        downloadShareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadShareMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(downloadShareMenuItem);
        fileMenu.add(jSeparator2);

        mkdirMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        mkdirMenuItem.setText("New folder");
        mkdirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mkdirMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(mkdirMenuItem);

        renameMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        renameMenuItem.setText("Rename");
        renameMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(renameMenuItem);

        rmMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        rmMenuItem.setText("Delete");
        rmMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rmMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(rmMenuItem);

        moveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        moveMenuItem.setText("Move");
        moveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(moveMenuItem);
        fileMenu.add(jSeparator3);

        shareMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        shareMenuItem.setText("Share");
        shareMenuItem.setToolTipText("");
        shareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shareMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(shareMenuItem);

        unshareMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        unshareMenuItem.setText("Unshare");
        unshareMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unshareMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(unshareMenuItem);
        fileMenu.add(jSeparator1);

        disconnectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        disconnectMenuItem.setText("Disconnect");
        disconnectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(disconnectMenuItem);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");

        preferencesMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        preferencesMenuItem.setText("Preferences");
        preferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(preferencesMenuItem);

        menuBar.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");

        refreshMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        refreshMenuItem.setText("Refresh");
        refreshMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(refreshMenuItem);

        menuBar.add(viewMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");

        encPassMenuItem.setText("Set encryption password");
        encPassMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                encPassMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(encPassMenuItem);

        benchmarkMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        benchmarkMenuItem.setText("Benchmark");
        benchmarkMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                benchmarkMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(benchmarkMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fileTableScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 828, Short.MAX_VALUE)
                    .addComponent(fileQueueScrollPane)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(fileQueueLabel)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(quotaLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(quotaAmount)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fileTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 405, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(fileQueueLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileQueueScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quotaAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)
                    .addComponent(quotaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(13, 13, 13))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        int reply;
        if (Settings.isWorking()) {
            reply = JOptionPane.showConfirmDialog(this, "There is a task "
                    + "running in the background.\nAre you sure you want to "
                    + "exit ?", "Exit", JOptionPane.YES_NO_OPTION);
        } else {
            reply = JOptionPane.showConfirmDialog(this, "Are you sure you want"
                    + " to exit ?", "Exit", JOptionPane.YES_NO_OPTION);
        }

        if (reply == JOptionPane.YES_OPTION) {
            try {
                Control.disconnect();
                IO.disconnect();
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
            DefaultCipher.sanitizeKey();
            System.exit(0);
        }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void uploadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadMenuItemActionPerformed
        fd = new FileDialog(this, "Upload file", FileDialog.LOAD);
        try {
            cwd = Control.cwd();
        } catch (IOException ex) {
            ErrorHandler.showError(ex);
        }

        fd.setMultipleMode(true);
        fd.setVisible(true);
        if (fd.getFiles().length > 0) {
            int i = fileQueue.getRowCount();
            updateFileQueueSize(fd.getFiles().length);

            for (File f : fd.getFiles()) {
                fileQueue.setValueAt(f.getAbsolutePath(), i, 0);
                fileQueue.setValueAt(cwd + f.getName(), i, 1);
                fileQueue.setValueAt("Pending", i, 2);
                fileQueue.setValueAt("Upload", i, 3);
                i++;
            }

            if (!Settings.isWorking()) {
                fw = new FileWorker(fileQueue);
                fw.execute();
            }
        }
    }//GEN-LAST:event_uploadMenuItemActionPerformed


    private void benchmarkMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_benchmarkMenuItemActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "This will benchmark your computer by "
                + "determining the time taken to derive encryption keys and process data. It may take "
                + "some time to complete.\n\nDo you wish to continue ?", "Benchmark", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (res == JOptionPane.OK_OPTION) {

        }
    }//GEN-LAST:event_benchmarkMenuItemActionPerformed

    private void preferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuItemActionPerformed
        PreferencesFrame.main(null);
    }//GEN-LAST:event_preferencesMenuItemActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        if (!disconnecting) {
            exitMenuItemActionPerformed(null);
        } else {
            this.dispose();
        }
    }//GEN-LAST:event_formWindowClosed

    private void disconnectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectMenuItemActionPerformed
        int reply;
        if (Settings.isWorking()) {
            reply = JOptionPane.showConfirmDialog(this, "There is a task "
                    + "running in the background.\nAre you sure you want to "
                    + "disconnect ?", "Disconnect", JOptionPane.YES_NO_OPTION);
        } else {
            reply = JOptionPane.showConfirmDialog(this, "Are you sure you want "
                    + "to disconnect ?", "Disconnect", JOptionPane.YES_NO_OPTION);
        }

        if (reply == JOptionPane.YES_OPTION) {
            try {
                Control.disconnect();
                IO.disconnect();
                disconnecting = true;
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }

            DefaultCipher.sanitizeKey();

            this.dispose();
            Settings.setLogged(false);

            if (Settings.isWorking()) {
                fw.cancel(true);
                Settings.setIsWorking(false);
            }

            LoginForm.main(null);
        }
    }//GEN-LAST:event_disconnectMenuItemActionPerformed

    private void fileTableMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTableMouseReleased
        fileTableMousePressed(evt);
    }//GEN-LAST:event_fileTableMouseReleased

    private void downloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadActionPerformed
        if (fileTable.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file.",
                    "Download", JOptionPane.INFORMATION_MESSAGE);
        } else {
            try {
                cwd = Control.cwd();
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }

            boolean isSetDLDir = false;
            String dlDir = new String();
            int i = fileQueue.getRowCount();

            if (!Settings.isDLDir()) {
                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setMultiSelectionEnabled(false);
                fc.setVisible(true);

                if (fc.showDialog(new JFrame(), "Choose download folder") == JFileChooser.APPROVE_OPTION) {
                    dlDir = fc.getSelectedFile().getPath().replace("\\", "/");
                    if (!dlDir.endsWith("/")) {
                        dlDir += "/";
                    }
                    isSetDLDir = true;
                }
            } else {
                dlDir = Settings.getDLDir();
                isSetDLDir = true;
            }

            if (isSetDLDir && fileTable.getSelectedRows().length > 0) {
                updateFileQueueSize(fileTable.getSelectedRows().length);

                for (int row : fileTable.getSelectedRows()) {
                    fileQueue.setValueAt(cwd + (String) fileTable.getValueAt(
                            row, 0), i, 0);
                    fileQueue.setValueAt(dlDir + fileTable.getValueAt(row, 0),
                            i, 1);
                    fileQueue.setValueAt("Pending", i, 2);
                    fileQueue.setValueAt("Download", i, 3);
                    i++;
                }

                if (!Settings.isWorking()) {
                    fw = new FileWorker(fileQueue);
                    fw.execute();
                }
            }
        }
    }//GEN-LAST:event_downloadActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        if (fileTable.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file or directory.",
                    "Delete", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int reply = JOptionPane.showOptionDialog(new Frame(),
                    "Delete selected item(s) ?", "Delete", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "No"}, "No");
            if (reply == JOptionPane.YES_OPTION) {
                try {
                    cwd = Control.cwd();
                    String fileName;
                    for (int i : fileTable.getSelectedRows()) {
                        fileName = (String) fileTable.getValueAt(i, 0);
                        if (!Control.rm(cwd + fileName)) {
                            JOptionPane.showMessageDialog(this, "Could not delete '" + fileName + "'.\n"
                                    + "It may be downloaded somewhere else or it is currently shared.", "Delete file",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    updateFileTable();
                } catch (IOException ex) {
                    ErrorHandler.showError(ex);
                }
            }
        }
    }//GEN-LAST:event_deleteActionPerformed

    private void fileTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTableMouseClicked
        if (evt.getClickCount() == 2) {
            try {
                if (!fileTable.getValueAt(fileTable.getSelectedRow(), 2).equals("File")) {
                    String newDir = (String) fileTable.getValueAt(fileTable.
                            getSelectedRow(), 0);
                    if (newDir.equals("..")) {
                        Control.cd("..");
                    } else {
                        Control.cd(Control.cwd() + newDir + "/");
                    }
                    updateFileTable();
                } else {
                    downloadActionPerformed(null);
                }
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
        }
    }//GEN-LAST:event_fileTableMouseClicked

    private void refreshMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshMenuItemActionPerformed
        if (System.currentTimeMillis() + 1000 > time) {
            time = System.currentTimeMillis();
            try {
                updateFileTable();
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
        }
    }//GEN-LAST:event_refreshMenuItemActionPerformed

    private void downloadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadMenuItemActionPerformed
        downloadActionPerformed(evt);
    }//GEN-LAST:event_downloadMenuItemActionPerformed

    private void mkdirMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mkdirMenuItemActionPerformed
        newDirName = JOptionPane.showInputDialog(this, "Name", "New folder",
                JOptionPane.QUESTION_MESSAGE);

        if (newDirName != null) {
            try {
                Control.mkdir(Control.cwd() + sanitize(newDirName));
                updateFileTable();
            } catch (IOException ex) {
                ch.lamacrypt.visual.ErrorHandler.showError(ex);
            }
        }
    }//GEN-LAST:event_mkdirMenuItemActionPerformed

    private void rmMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rmMenuItemActionPerformed
        deleteActionPerformed(evt);
    }//GEN-LAST:event_rmMenuItemActionPerformed

    private void renameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameActionPerformed
        if (fileTable.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file or directory.",
                    "Rename", JOptionPane.INFORMATION_MESSAGE);
        } else if (fileTable.getSelectedRows().length > 1) {
            JOptionPane.showMessageDialog(this, "Please select only one item.",
                    "Rename", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String newName = JOptionPane.showInputDialog(this, "New name",
                    "Rename", JOptionPane.QUESTION_MESSAGE);

            if (newName != null) {
                try {
                    cwd = Control.cwd();

                    if (Control.exists(cwd + newName)) {
                        JOptionPane.showMessageDialog(this, "There already exists a "
                                + "file with that name.\nPlease choose another one.",
                                "Rename", JOptionPane.WARNING_MESSAGE);
                    } else {
                        String currName = (String) fileTable.getValueAt(fileTable.
                                getSelectedRow(), 0);

                        if (!Control.rename(cwd + currName, cwd + sanitize(newName))) {
                            JOptionPane.showMessageDialog(this, "Could not rename '" + currName + "'.\n"
                                    + "It may be downloaded somewhere else or it is currently shared.",
                                    "Rename", JOptionPane.WARNING_MESSAGE);
                        } else {
                            updateFileTable();
                        }
                    }
                } catch (IOException ex) {
                    ch.lamacrypt.visual.ErrorHandler.showError(ex);
                }
            }
        }
    }//GEN-LAST:event_renameActionPerformed

    private void moveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveActionPerformed
        if (fileTable.getSelectedRows().length == 0) {
            JOptionPane.showMessageDialog(this, "Please select a file or directory.",
                    "Move", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] names = new String[fileTable.getSelectedRows().length];
            int j = 0;

            for (int i : fileTable.getSelectedRows()) {
                names[j] = (String) fileTable.getValueAt(i, 0);
                j++;
            }

            MoveFileFrame.setParams(names);
            MoveFileFrame.main(null);
        }
    }//GEN-LAST:event_moveActionPerformed

    private void deleteQueueItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteQueueItemActionPerformed
        if (fileQueue.getSelectedRowCount() > 0) {
            for (int row : fileQueue.getSelectedRows()) {
                if (fileQueue.getValueAt(row, 2).equals("Pending")) {
                    fileQueue.setValueAt("Cancelled", row, 2);
                }
            }
        }
    }//GEN-LAST:event_deleteQueueItemActionPerformed

    private void fileQueueMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileQueueMouseReleased
        if (fileQueue.getSelectedRowCount() > 0 && evt.isPopupTrigger()) {
            fileQueuePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_fileQueueMouseReleased

    private void shareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shareMenuItemActionPerformed
        if (fileTable.getSelectedRowCount() != 1) {
            JOptionPane.showMessageDialog(this, "Please select one file.", "Share file",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (!fileTable.getValueAt(fileTable.getSelectedRow(), 2).equals("File")) {
            JOptionPane.showMessageDialog(this, "Please select a file.", "Share file",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (JOptionPane.showConfirmDialog(this, "Share selected file ? This will take a few "
                + "seconds.", "Share file", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
                == JOptionPane.YES_OPTION) {
            try {
                new ShareWorker(Control.cwd() + (String) fileTable.getValueAt(fileTable.
                        getSelectedRow(), 0), false).execute();
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
        }

    }//GEN-LAST:event_shareMenuItemActionPerformed

    private void encPassMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_encPassMenuItemActionPerformed
        int res = JOptionPane.showConfirmDialog(this, "Are you sure you wish to set a new encryption"
                + " password ?", "Set encryption password", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (res == JOptionPane.YES_OPTION) {
            JPasswordField confirmField = new JPasswordField(),
                    passField = new JPasswordField();
            Object[] content = {
                "Password", passField,
                "Confirm", confirmField
            };

            res = JOptionPane.showOptionDialog(null, content, "Set encryption password",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                    new String[]{"Set password"}, "Set password");

            if (res != JOptionPane.CLOSED_OPTION) {
                if (passField.getPassword().length < 20 || passField.getPassword().length > 64) {
                    JOptionPane.showMessageDialog(null, "Password must be between 20 and 64 "
                            + "characters long.", "Set encryption password", JOptionPane.WARNING_MESSAGE);
                } else if (Arrays.equals(passField.getPassword(), confirmField.getPassword())) {
                    // asking for confirmation before setting the password
                    String msg2 = "Are you sure you want to use this password ? Other files may need another"
                            + " one.\n\nNo recovery is possible. Losing it means losing all your files !";
                    int confirm = JOptionPane.showConfirmDialog(new Frame(), msg2, "Disclaimer",
                            JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        DefaultCipher.setEncryptionPassword(passField.getPassword().clone());
                        GPCrypto.sanitize(passField.getPassword());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Passwords do not match", "Set encryption "
                            + "password", JOptionPane.ERROR_MESSAGE);
                    passField.setText("");
                }
            }
        }
    }//GEN-LAST:event_encPassMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        String msg = "LamaCrypt desktop client v1.0.0\n\n(C) 2016 LamaCrypt. All rights reserved.\n\n"
                + "This software and its source code are available under the LamaCrypt Software License.\n";
        JOptionPane.showMessageDialog(this, msg, "About", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void fileTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileTableMousePressed
        if (evt.isPopupTrigger() && fileTable.getSelectedRowCount() > 0) {
            fileTablePopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
    }//GEN-LAST:event_fileTableMousePressed

    private void moveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveMenuItemActionPerformed
        moveActionPerformed(evt);
    }//GEN-LAST:event_moveMenuItemActionPerformed

    private void renameMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameMenuItemActionPerformed
        renameActionPerformed(evt);
    }//GEN-LAST:event_renameMenuItemActionPerformed

    private void unshareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unshareMenuItemActionPerformed
        if (fileTable.getSelectedRowCount() != 1) {
            JOptionPane.showMessageDialog(this, "Please select one file.", "Unshare file",
                    JOptionPane.INFORMATION_MESSAGE);
        } else if (!fileTable.getValueAt(fileTable.getSelectedRow(), 2).equals("File")) {
            JOptionPane.showMessageDialog(this, "Please select a file.", "Unshare file",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            String filePath = (String) fileTable.getValueAt(fileTable.getSelectedRow(), 0);
            try {
                switch (Control.rmshare(Control.cwd() + filePath)) {
                    case 0:
                        JOptionPane.showMessageDialog(this, "Successfully deleted share link for '"
                                + filePath + "'.", "Unshare file", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    case 2:
                        ErrorHandler.showError("file '" + fileTable.getValueAt(fileTable.
                                getSelectedRow(), 0) + "' not found.");
                        break;
                    case 3:
                        ErrorHandler.showError("filename too short.");
                        break;
                    case 4:
                        ErrorHandler.showError("no share exists for file '" + fileTable.getValueAt(fileTable.
                                getSelectedRow(), 0) + "'.");
                        break;
                }
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
        }
    }//GEN-LAST:event_unshareMenuItemActionPerformed

    private void downloadShareMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadShareMenuItemActionPerformed
        String uuid = JOptionPane.showInputDialog(this, "Enter share UUID", "Download share",
                JOptionPane.QUESTION_MESSAGE);
        boolean error;

        if (uuid != null) {
            if (uuid.length() != 32) {
                error = true;
            } else {
                error = !GPCrypto.checkUUID(uuid);
            }

            if (error) {
                ErrorHandler.showError("invalid share UUID format.");
            } else {
                try {
                    Object[] info = Control.getshare(uuid, true);

                    switch ((int) info[0]) {
                        case 0:
                            String fileName = (String) info[1];

                            String msg = "File name: " + fileName + "\n"
                                    + "Size: " + GPFile.longToSize((long) info[2]) + "\n\n"
                                    + "Download ?";
                            int res = JOptionPane.showConfirmDialog(this, msg, "Download share",
                                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                            if (res == JOptionPane.YES_OPTION) {
                                boolean isSetDLDir = false;
                                String dlDir = new String();
                                int i = fileQueue.getRowCount();

                                if (!Settings.isDLDir()) {
                                    JFileChooser fc = new JFileChooser();
                                    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                                    fc.setMultiSelectionEnabled(false);
                                    fc.setVisible(true);

                                    if (fc.showDialog(new JFrame(), "Choose download folder") == JFileChooser.APPROVE_OPTION) {
                                        dlDir = fc.getSelectedFile().getPath().replace("\\", "/");
                                        if (!dlDir.endsWith("/")) {
                                            dlDir += "/";
                                        }
                                        isSetDLDir = true;
                                    }
                                } else {
                                    dlDir = Settings.getDLDir();
                                    isSetDLDir = true;
                                }

                                if (isSetDLDir) {
                                    updateFileQueueSize(1);

                                    fileQueue.setValueAt(uuid, i, 0);
                                    fileQueue.setValueAt(dlDir + fileName, i, 1);
                                    fileQueue.setValueAt("Pending", i, 2);
                                    fileQueue.setValueAt("Download", i, 3);
                                    i++;

                                    if (!Settings.isWorking()) {
                                        new FileWorker(fileQueue).execute();
                                    }
                                }
                            }
                            break;
                        case 2:
                            ErrorHandler.showError("file not found.");
                            break;
                        case 3:
                            ErrorHandler.showError("filename too short.");
                            break;
                        case 4:
                            ErrorHandler.showError("share does not exist.");
                            break;
                    }
                } catch (IOException ex) {
                    ErrorHandler.showError(ex);
                }
            }
        }
    }//GEN-LAST:event_downloadShareMenuItemActionPerformed

    private void fileQueueMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fileQueueMousePressed
        fileQueueMouseReleased(evt);
    }//GEN-LAST:event_fileQueueMousePressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            Logger.getLogger(DefaultFrame.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        }

        /* Create and display the form */
        SwingUtilities.invokeLater(() -> {
            try {
                new DefaultFrame().setVisible(true);
            } catch (Exception ex) {
                ch.lamacrypt.visual.ErrorHandler.showError(ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem benchmarkMenuItem;
    private javax.swing.JMenuItem delete;
    private javax.swing.JMenuItem deleteQueueItem;
    private javax.swing.JMenuItem disconnectMenuItem;
    private javax.swing.JMenuItem download;
    private javax.swing.JMenuItem downloadMenuItem;
    private javax.swing.JMenuItem downloadShareMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem encPassMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private static javax.swing.JTable fileQueue;
    private javax.swing.JLabel fileQueueLabel;
    private javax.swing.JPopupMenu fileQueuePopupMenu;
    private javax.swing.JScrollPane fileQueueScrollPane;
    private static javax.swing.JTable fileTable;
    private javax.swing.JPopupMenu fileTablePopupMenu;
    private javax.swing.JScrollPane fileTableScrollPane;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mkdirMenuItem;
    private javax.swing.JMenuItem move;
    private javax.swing.JMenuItem moveMenuItem;
    private javax.swing.JMenuItem preferencesMenuItem;
    private static javax.swing.JLabel quotaAmount;
    private javax.swing.JLabel quotaLabel;
    private javax.swing.JMenuItem refreshMenuItem;
    private javax.swing.JMenuItem rename;
    private javax.swing.JMenuItem renameMenuItem;
    private javax.swing.JMenuItem rmMenuItem;
    private javax.swing.JPopupMenu.Separator separator0;
    private javax.swing.JPopupMenu.Separator separator1;
    private javax.swing.JMenuItem shareMenuItem;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem unshareMenuItem;
    private javax.swing.JMenuItem uploadMenuItem;
    private javax.swing.JMenu viewMenu;
    // End of variables declaration//GEN-END:variables
}
