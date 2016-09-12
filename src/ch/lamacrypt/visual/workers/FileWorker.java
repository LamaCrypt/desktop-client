/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.visual.workers;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.internal.crypto.GPCrypto;
import ch.lamacrypt.internal.file.GPFile;
import ch.lamacrypt.internal.network.Control;
import ch.lamacrypt.internal.network.IO;
import ch.lamacrypt.visual.DefaultFrame;
import ch.lamacrypt.visual.ErrorHandler;
import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

/**
 * Processes GUI file I/O actions by sending them to low-level classes
 *
 * @author LamaGuy
 */
public class FileWorker extends SwingWorker<Integer, String> {

    private static final String[] buttons = {"Yes", "Yes to all", "No", "Cancel"};

    private static Iterator<Map.Entry<String, Long>> dirMapIter;
    private static Iterator<Map.Entry<String, Long[]>> fileMapIter;
    private static boolean isDownload;
    private static String srcFilePath,
            dstFilePath;
    private static JTable fileQueue;
    private static File dstFile,
            srcFile;

    private static void failIfInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted() && isDownload) {
            dstFile.delete();
        }
    }

    /**
     * Creates a FileWorker with the given file queue as parameter
     *
     * @param fileQueue file queue to work with
     */
    public FileWorker(JTable fileQueue) {
        FileWorker.fileQueue = fileQueue;
    }

    @Override
    protected Integer doInBackground() throws Exception {
        Settings.setIsWorking(true);
        int returnVal = -1;
        boolean yesForAll = false,
                isQueueEmpty = false,
                existsFile = false;

        while (!isQueueEmpty) {
            fileQueue.setValueAt("In progress", 0, 2);
            srcFilePath = (String) fileQueue.getValueAt(0, 0);
            dstFilePath = (String) fileQueue.getValueAt(0, 1);

            if (fileQueue.getValueAt(0, 3).equals("Upload")) {
                srcFile = new File(srcFilePath);
                isDownload = false;

                if (srcFile.length() > 6e10) {
                    JOptionPane.showMessageDialog(null, "Error: could not upload the file " + srcFile.
                            getName() + ".\nAt the present time, uploads are capped at 60GB.", "Upload file",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    if (!yesForAll && Control.exists(dstFilePath)) {
                        returnVal = JOptionPane.showOptionDialog(null, "File "
                                + srcFile.getName() + " already exists. Overwrite ?",
                                "Overwrite", JOptionPane.DEFAULT_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, buttons,
                                buttons[2]);
                        yesForAll = returnVal == 1;
                        existsFile = true;
                    }

                    if (returnVal < 2) {
                        if (existsFile) {
                            Control.rm(dstFilePath);
                            existsFile = false;
                        }

                        String prefix = "could not upload " + (String) fileQueue.getValueAt(0, 0) + ".\n";
                        switch (IO.upload(srcFile, dstFilePath)) {
                            case 7:
                                ErrorHandler.showError(prefix + "The file size is different from what the server"
                                        + " received. Please try uploading it again.");
                                break;
                            case 6:
                                ErrorHandler.showError(prefix + "File size is too small.");
                                break;
                            case 5:
                                ErrorHandler.showError(prefix + "You have reached your plan's quota.");
                                break;
                            case 4:
                                ErrorHandler.showError(prefix + "A file with the same name is being uploaded.");
                                break;
                            case 3:
                                ErrorHandler.showError(prefix + "Remote filename is too short.");
                                break;
                            case 1:
                                ErrorHandler.showError(prefix + "File already exists.");
                                break;
                            case -2:
                                ErrorHandler.showError(prefix + "An I/O problem occured.");
                                break;
                        }
                    }
                }
            } else if (fileQueue.getValueAt(0, 3).equals("Download")) {
                dstFile = new File(dstFilePath);
                boolean error = false;
                isDownload = true;

                if (!yesForAll && dstFile.isFile() && dstFile.exists()) {
                    returnVal = JOptionPane.showOptionDialog(null, "File "
                            + dstFile.getName() + " already exists. Overwrite ?",
                            "Overwrite", JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, buttons,
                            buttons[2]);
                    yesForAll = returnVal == 1;
                }

                if (returnVal < 2) {
                    try {
                        if (dstFile.exists()) {
                            dstFile.delete();
                        }
                        dstFile.createNewFile();
                    } catch (IOException ex) {
                        error = true;
                        String errorMsg = "could not write to file '" + dstFile.getAbsolutePath() + "'.\nPlease "
                                + "check you have the right to write in the parent directory.";
                        ErrorHandler.showError(errorMsg);
                    }

                    if (GPFile.checkDirSize(dstFile) < Control.size(srcFilePath) + 5e7) {
                        ErrorHandler.showError("not enough space on '" + GPFile.
                                getNearestParentDirectory(dstFile).getCanonicalPath() + "'. "
                                + GPFile.longToSize(GPFile.checkDirSize(dstFile)) + " left.");
                        error = true;
                    }

                    if (!error) {
                        int res = -4;
                        if (Control.dirchk(srcFilePath)) {
                            recursiveDownload(srcFilePath, dstFilePath);
                        } else if (srcFilePath.startsWith("/")) {
                            String prefix = "could not download '" + (String) fileQueue.getValueAt(0, 0) + "'.\n";

                            res = IO.download(srcFilePath, dstFile);
                            switch (res) {
                                case 4:
                                    ErrorHandler.showError(prefix + "Your encryption password is probably false "
                                            + "or you have downloaded corrupted data.");
                                    break;
                                case 3:
                                    ErrorHandler.showError(prefix + "Remote filename is too short.");
                                    break;
                                case 2:
                                    ErrorHandler.showError(prefix + "File does not exist.");
                                    break;
                                case -1:
                                    ErrorHandler.showError(prefix + "I/O error occured.");
                                    break;
                                case -2:
                                    ErrorHandler.showError(prefix + "Bad file format.");
                                    break;
                            }
                        } else {
                            String prefix = "could not download share file '" + dstFile.getName() + "'.\n",
                                    key = JOptionPane.showInputDialog(new Frame(), "Enter decryption key",
                                            "Download share", JOptionPane.QUESTION_MESSAGE);

                            if (!GPCrypto.checkUUID(srcFilePath)) {
                                ErrorHandler.showError(prefix + "Bad UUID format.");
                            } else if (!GPCrypto.checkKey(key)) {
                                ErrorHandler.showError(prefix + "Bad key format.");
                            } else {
                                res = IO.downloadshare(srcFilePath, key, dstFile, true);
                                switch (res) {
                                    case 7:
                                        ErrorHandler.showError(prefix + "Encryption malfunction.");
                                        break;
                                    case 6:
                                        ErrorHandler.showError(prefix + "Bad UUID format.");
                                        break;
                                    case 5:
                                        ErrorHandler.showError(prefix + "No share exists with the UUID '"
                                                + srcFilePath + "'.");
                                        break;
                                    case 4:
                                        ErrorHandler.showError(prefix + "Your encryption password is probably false "
                                                + "or you have downloaded corrupted data.");
                                        break;
                                    case 3:
                                        ErrorHandler.showError(prefix + "UUID is too short.");
                                        break;
                                    case 2:
                                        ErrorHandler.showError(prefix + "Share file not found.");
                                        break;
                                    case -1:
                                        ErrorHandler.showError(prefix + "I/O error occured.");
                                        break;
                                    case -2:
                                        ErrorHandler.showError(prefix + "I/O error occured.");
                                        break;
                                }
                            }
                        }
                        if (res != 0) {
                            dstFile.delete();
                        }
                    }
                }
            }

            DefaultTableModel dtm = (DefaultTableModel) fileQueue.getModel();
            dtm.removeRow(0);
            fileQueue.setModel(dtm);
            isQueueEmpty = fileQueue.getRowCount() == 0;
            DefaultFrame.updateFileTable();
        }

        Settings.setIsWorking(false);
        return 1;
    }

    @Override
    protected void process(List<String> chunks) {
        chunks.stream().forEach((s) -> {
            //log.append(s + "\n");
        });
    }

    private void recursiveDownload(String remoteDirName, String dlFilePath) throws IOException, Exception {
        Map<String, Long[]> fileMap = Control.lsfile(remoteDirName);
        Map<String, Long> dirMap = Control.lsdir(remoteDirName);
        boolean spaceFreed = false;
        int res = JOptionPane.OK_OPTION;
        new File(dlFilePath).delete();
        new File(dlFilePath).mkdirs();

        if (!dirMap.isEmpty()) {
            dirMapIter = dirMap.entrySet().iterator();
            while (dirMapIter.hasNext()) {
                Map.Entry<String, Long> entry = dirMapIter.next();
                recursiveDownload(remoteDirName + "/" + entry.getKey(), dlFilePath + "/" + entry.getKey());
            }
        }

        if (!fileMap.isEmpty()) {
            fileMapIter = fileMap.entrySet().iterator();
            while (fileMapIter.hasNext()) {
                Map.Entry<String, Long[]> entry = fileMapIter.next();

                while (!spaceFreed) {
                    if (res == JOptionPane.OK_OPTION && (dstFile.getUsableSpace() + 1e8) > entry.
                            getValue()[1]) {
                        spaceFreed = true;
                        dstFile = new File(dlFilePath + "/" + entry.getKey());
                        fileQueue.setValueAt(dstFile.getCanonicalPath(), 0, 1);
                        IO.download(remoteDirName + "/" + entry.getKey(), dstFile);
                    } else {
                        res = JOptionPane.showConfirmDialog(new Frame(), "Not enough space to "
                                + "write \"" + entry.getKey() + "\".\nPlease free up some space and try again.",
                                "Download file", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
                    }
                }

                spaceFreed = false;
                res = JOptionPane.OK_OPTION;
            }
        }
    }
}
