/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal;

import ch.lamacrypt.visual.ErrorHandler;
import com.sun.management.OperatingSystemMXBean;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Provides means for getting and setting settings, used in the GUI
 *
 * @author LamaGuy
 */
public abstract class Settings {

    private static final OperatingSystemMXBean os = (OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
    private static final File config = new File("desktop-client.conf");
    private static byte version = 0x00;
    private static long quotaSize;
    private static String DLDir = null;
    private static int startScryptN;
    private static boolean isDLDIR = false,
            isWorking = false,
            isNew,
            TOSAgreed = false,
            logged = false;

    public static void setQuotaSize(long quotaSize) {
        Settings.quotaSize = quotaSize;
    }

    public static long getQuotaSize() {
        return quotaSize;
    }

    public static void setVersion(byte version) {
        Settings.version = version;
    }

    public static byte getVersion() {
        return version;
    }

    public static String getDLDir() {
        return DLDir;
    }

    public static void setDLDir(String newDir) {
        DLDir = newDir;
        isDLDIR = true;
    }

    public static boolean isDLDir() {
        return isDLDIR;
    }

    public static void setIsDLDIR(boolean newVal) {
        isDLDIR = newVal;
    }

    public static void setIsWorking(boolean newVal) {
        isWorking = newVal;
    }

    public static boolean isWorking() {
        return isWorking;
    }

    public static void setIsNew(boolean newVal) {
        isNew = newVal;
    }

    public static boolean isNew() {
        return isNew;
    }

    public static void setTOSAgreed(boolean newVal) {
        TOSAgreed = newVal;
    }

    public static boolean isTOSAgreed() {
        return TOSAgreed;
    }

    public static void setLogged(boolean newVal) {
        logged = newVal;
    }

    public static boolean getLogged() {
        return logged;
    }

    public static int getStartupScryptN() {
        return startScryptN;
    }

    public static void setStartupScryptN(int N) {
        startScryptN = N;
    }

    /**
     * Updates the scrypt CPU/memory parameter in the config file
     *
     * @param N new CPU/memory parameter for scrypt
     */
    public static void updateScryptN(int N) {
        try {
            config.delete();
            config.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(config));
            out.write("agreedTOS=yes\n");
            out.write("scryptfactor=" + N + "\n");
            out.close();
        } catch (IOException ex) {
            ErrorHandler.showError(ex);
        }
    }

    public static long getMaxRAM() {
        return os.getTotalPhysicalMemorySize();
    }

}
