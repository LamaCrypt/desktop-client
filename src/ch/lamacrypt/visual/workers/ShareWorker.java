package ch.lamacrypt.visual.workers;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.internal.crypto.GPCrypto;
import ch.lamacrypt.internal.file.GPFile;
import ch.lamacrypt.internal.network.Control;
import ch.lamacrypt.internal.network.GPTLS;
import ch.lamacrypt.internal.network.IO;
import ch.lamacrypt.visual.DownloadFrame;
import ch.lamacrypt.visual.ErrorHandler;
import java.awt.Frame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.net.ssl.SSLSocket;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

/**
 * SwingWorker for sharing-related operations
 *
 * @author LamaGuy
 */
public class ShareWorker extends SwingWorker<Integer, String> {

    private static final String IO_SERVER_NAME = "data.lamacrypt.ch";
    private static final int IO_SERVER_PORT = 4412;
    private static final byte SHARE = 0x71;

    private final String filePath;
    private final boolean download;

    private static DataOutputStream ioDos;
    private static DataInputStream ioDis;
    private static SSLSocket ioSocket;
    private static Object[] shareInfo;
    private static File dstFile;
    private static String uuid,
            key;

    public ShareWorker(String filePath, boolean download) {
        this.filePath = filePath;
        this.download = download;
    }

    public static void setParams(String uuid, Object[] shareInfo) {
        ShareWorker.uuid = uuid;
        ShareWorker.shareInfo = shareInfo;
    }

    @Override
    protected Integer doInBackground() throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, Exception {
        Settings.setIsWorking(true);

        if (!download) {
            Object[] reply = Control.mkshare(filePath);

            switch ((int) reply[0]) {
                case 0:
                    if (!reply[2].equals("error")) {
                        String msg = "Successfully created share link for '" + reply[3] + "'.\n\n"
                                + "To access the shared file, you will need this information:\n"
                                + "UUID: " + reply[1] + "\n"
                                + "Key: " + reply[2];
                        JTextArea txt = new JTextArea(msg);
                        JOptionPane.showMessageDialog(null, txt, "Share file", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        ErrorHandler.showError("your encryption password is probably false or you have"
                                + " downloaded corrupted data..");
                    }
                    break;
                case 2:
                    ErrorHandler.showError("file '" + reply[1] + "' not found.");
                    break;
                case 3:
                    ErrorHandler.showError("filename too short.");
                    break;
                case 4:
                    ErrorHandler.showError("a share already exists for file '" + reply[1] + "'.");
                    break;
            }
        } else {
            switch ((int) shareInfo[0]) {
                case 0:
                    String fileName = (String) shareInfo[1];
                    int returnVal = JOptionPane.YES_OPTION;

                    String msg = "File name: " + fileName + "\n"
                            + "Size: " + GPFile.longToSize((long) shareInfo[2]) + "\n\n"
                            + "Download ?";
                    int res = JOptionPane.showOptionDialog(new Frame(), msg, "Download share",
                            JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null,
                            new String[]{"Yes", "No"}, "No");

                    if (res == JOptionPane.YES_OPTION) {
                        key = JOptionPane.showInputDialog(new Frame(), "Enter decryption key",
                                "Download share", JOptionPane.QUESTION_MESSAGE);

                        if (!GPCrypto.checkKey(key)) {
                            ErrorHandler.showError("invalid key format.");
                        } else {
                            boolean isSetDLDir = false;
                            String dlDir = "";

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
                                    Settings.setDLDir(dlDir);
                                }
                            } else {
                                dlDir = Settings.getDLDir();
                                isSetDLDir = true;
                            }

                            if (isSetDLDir) {
                                dstFile = new File(dlDir + fileName);

                                if (GPFile.checkDirSize(dstFile) < (long) shareInfo[2] + 5e7) {
                                    ErrorHandler.showError("not enough space on '" + GPFile.
                                            getNearestParentDirectory(dstFile).getCanonicalPath() + "'. "
                                            + GPFile.longToSize(GPFile.checkDirSize(dstFile)) + " left.");
                                } else {
                                    if (dstFile.isFile() && dstFile.exists()) {
                                        returnVal = JOptionPane.showOptionDialog(null, "File "
                                                + dstFile.getName() + " already exists. Overwrite ?",
                                                "Overwrite", JOptionPane.DEFAULT_OPTION,
                                                JOptionPane.QUESTION_MESSAGE, null, new String[]{"Yes", "No"},
                                                "No");
                                    }

                                    String prefix = "could not download share file.\n";
                                    boolean error = false;

                                    if (returnVal == JOptionPane.YES_OPTION) {
                                        try {
                                            if (dstFile.exists()) {
                                                dstFile.delete();
                                            }
                                            dstFile.createNewFile();
                                        } catch (IOException ex) {
                                            error = true;
                                            String errorMsg = "could not write to file '" + dstFile.getCanonicalPath() + "'.\nPlease "
                                                    + "check you have the right to write in the parent directory.";
                                            ErrorHandler.showError(errorMsg);
                                        }

                                        if (!error) {
                                            ioSocket = (SSLSocket) GPTLS.getContext().getSocketFactory().createSocket(
                                                    InetAddress.getByName(IO_SERVER_NAME), IO_SERVER_PORT);
                                            GPTLS.setTLSParams(ioSocket);
                                            ioDos = new DataOutputStream(ioSocket.getOutputStream());
                                            ioDis = new DataInputStream(ioSocket.getInputStream());
                                            ioDos.write(SHARE);
                                            IO.init(ioDos, ioDis);

                                            DownloadFrame.setText(fileName, (long) shareInfo[2], dstFile);
                                            DownloadFrame.main(null);

                                            switch (IO.downloadshare(uuid, key, dstFile, false)) {
                                                case 7:
                                                    ErrorHandler.showError(prefix + "Encryption malfunction.");
                                                    break;
                                                case 6:
                                                    ErrorHandler.showError(prefix + "Bad UUID format.");
                                                    break;
                                                case 5:
                                                    ErrorHandler.showError(prefix + "No share exists with the UUID '"
                                                            + uuid + "'.");
                                                    break;
                                                case 4:
                                                    ErrorHandler.showError(prefix + "Your encryption password is probably false "
                                                            + "or you have downloaded corrupted data.");
                                                    dstFile.delete();
                                                    break;
                                                case 3:
                                                    ErrorHandler.showError(prefix + "UUID is too short.");
                                                    break;
                                                case 2:
                                                    ErrorHandler.showError(prefix + "Share file not found.");
                                                    break;
                                                case 0:
                                                    DownloadFrame.updateStatus("Done");
                                                    DownloadFrame.finish();
                                                    break;
                                                case -1:
                                                    ErrorHandler.showError(prefix + "I/O error occured.");
                                                    break;
                                                case -2:
                                                    ErrorHandler.showError(prefix + "You have downloaded corrupted data. Please try again.");
                                                    break;
                                            }

                                            ioSocket.close();
                                            ioDis.close();
                                            ioDos.close();
                                        }
                                    }
                                }
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
        }
        Settings.setIsWorking(false);
        return 1;
    }

    public static void stopDownload() throws IOException {
        ioSocket.close();
        ioDis.close();
        ioDos.close();
        dstFile.delete();
        Settings.setIsWorking(false);
    }
}
