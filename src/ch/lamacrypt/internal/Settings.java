/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal;

/**
 * Provides means for getting and setting settings, used in the GUI
 *
 * @author LamaGuy
 */
public abstract class Settings {

    private static byte version = 0x00;
    private static long quotaSize;
    private static int K1_N = 20,
            K2_N = 19;
    private static String DLDir = null;
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

    public static void setK1_N(int newVal) {
        K1_N = newVal;
    }

    public static void setK2_N(int newVal) {
        K2_N = newVal;
    }

    public static int getK1_N() {
        return K1_N;
    }

    public static int getK2_N() {
        return K2_N;
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
    
    public static void setLogged(boolean newVal){
        logged = newVal;
    }
    
    public static boolean getLogged(){
        return logged;
    }

}
