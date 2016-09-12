/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal.network;

import ch.lamacrypt.internal.crypto.DefaultCipher;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Contains methods which allow to probe the control server for information and
 * also to perform certain file-related tasks, such as mkdir, rm, etc.
 * 
 * @author LamaGuy
 */
public abstract class Control {

    private static final byte DISCONNECT = 0x00,
            LSFILE = 0x10,
            LSDIR = 0x11,
            CD = 0x12,
            CWD = 0x13,
            MKDIR = 0x14,
            RENAME = 0x15,
            RM = 0x16,
            EXISTS = 0x17,
            DIRCHK = 0x18,
            SIZE = 0x19,
            MKSHARE = 0x30,
            RMSHARE = 0x31,
            GETSHARE = 0x32,
            QUOTAUSED = 0x40;

    private static final int EXEC_OK = 0,
            ERROR = 10;

    private static DataOutputStream dos;
    private static DataInputStream dis;

    /**
     * Sets the I/O streams bound to the control server
     *
     * @param dos
     * @param dis
     */
    public static void init(DataOutputStream dos, DataInputStream dis) {
        Control.dos = dos;
        Control.dis = dis;
    }

    /**
     * Disconnects from the control server
     *
     * @throws IOException if an I/O error occurs
     */
    public static void disconnect() throws IOException {
        dos.writeByte(DISCONNECT);
    }

    /**
     * Lists the files in the specified directory
     *
     * @param dirPath path to directory
     * @return a map with each file's name and the last modified time, in Epoch
     * seconds
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, Long[]> lsfile(String dirPath) throws IOException {
        Map<String, Long[]> fileMap = new HashMap<>();

        dos.writeByte(LSFILE);
        dos.writeUTF(dirPath);
        int fileCount = dis.readInt();

        if (fileCount != 0) {
            for (int i = 0; i < fileCount; i++) {
                fileMap.put(dis.readUTF(), new Long[]{dis.readLong(), dis.readLong()});
            }
        }

        return fileMap;
    }

    /**
     * Lists the directories in the specified directory
     *
     * @param dirPath path to directory
     * @return a map with each directory's name and the last modified time, in
     * Epoch seconds
     * @throws IOException if an I/O error occurs
     */
    public static Map<String, Long> lsdir(String dirPath) throws IOException {
        Map<String, Long> dirMap = new HashMap<>();

        dos.writeByte(LSDIR);
        dos.writeUTF(dirPath);
        int dirCount = dis.readInt();

        if (dirCount != 0) {
            for (int i = 0; i < dirCount; i++) {
                dirMap.put(dis.readUTF(), dis.readLong());
            }
        }

        return dirMap;
    }

    /**
     * Changes the working directory
     *
     * @param path new directory path
     * @return true if the operation completed successfully, else false
     * @throws IOException if an I/O error occurs
     */
    public static boolean cd(String path) throws IOException {
        dos.writeByte(CD);
        dos.writeUTF(path);
        return dis.readBoolean();
    }

    /**
     * Returns the current working directory
     *
     * @return current working directory on the control server
     * @throws IOException if an I/O error occurs
     */
    public static String cwd() throws IOException {
        dos.writeByte(CWD);
        return dis.readUTF();
    }

    /**
     * Creates a new directory
     *
     * @param dirName directory name
     * @return true if the operation completed successfully, false if the
     * specified directory already exists or if an I/O error occurs
     * @throws IOException if an I/O error occurs
     */
    public static boolean mkdir(String dirName) throws IOException {
        dos.writeByte(MKDIR);
        dos.writeUTF(dirName);
        return dis.readBoolean();
    }

    /**
     * Renames a file or directory
     *
     * @param oldName old name
     * @param newName new name
     * @return true if the operation completed successfully, else false
     * @throws IOException if an I/O error occurs
     */
    public static boolean rename(String oldName, String newName) throws IOException {
        dos.writeByte(RENAME);
        dos.writeUTF(oldName);
        dos.writeUTF(newName);
        return dis.readBoolean();
    }

    /**
     * Deletes a file or directory
     *
     * @param fileName name of item to be deleted
     * @return true if the operation completed successfully, else false
     * @throws IOException if an I/O error occurs
     */
    public static boolean rm(String fileName) throws IOException {
        dos.writeByte(RM);
        dos.writeUTF(fileName);
        return dis.readBoolean();
    }

    /**
     * Checks whether a file or directory exists
     *
     * @param fileName name of item to look for
     * @return true if the item exists, else false
     * @throws IOException if an I/O error occurs
     */
    public static boolean exists(String fileName) throws IOException {
        dos.writeByte(EXISTS);
        dos.writeUTF(fileName);
        return dis.readBoolean();
    }

    /**
     * Checks whether the specified file is a directory or not
     *
     * @param fileName file name
     * @return true if the specified file is a directory, else false
     * @throws IOException if an I/O error occurs
     */
    public static boolean dirchk(String fileName) throws IOException {
        dos.writeByte(DIRCHK);
        dos.writeUTF(fileName);
        return dis.readBoolean();
    }

    /**
     * Returns the size of the specified file
     *
     * @param fileName
     * @return size of the specified file or -1 if not a file
     * @throws IOException
     */
    public static long size(String fileName) throws IOException {
        dos.writeByte(SIZE);
        dos.writeUTF(fileName);
        if (dis.readBoolean()) {
            return dis.readLong();
        } else {
            return -1;
        }
    }

    /**
     * Creates a share link for the specified file or directory
     *
     * @param fileName item to be shared
     * @return
     * @throws IOException if an I/O error occurs
     * @throws java.security.NoSuchAlgorithmException
     * @throws javax.crypto.NoSuchPaddingException
     * @throws java.security.InvalidKeyException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws javax.crypto.BadPaddingException
     * @throws javax.crypto.IllegalBlockSizeException
     */
    public static Object[] mkshare(String fileName) throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        dos.writeByte(MKSHARE);
        dos.writeUTF(fileName);

        int reply = dis.readInt();

        if (reply == EXEC_OK) {
            String key,
                    uuid = "";

            // reading header
            switch (dis.readByte()) {
                case 0x00:
                    byte[] header = new byte[235];
                    dis.read(header, 0, 234);

                    // getting K2
                    key = DefaultCipher.getKey(0x00, header);

                    break;
                default:
                    key = "error";
                    break;
            }

            if (!key.equals("error")) {
                dos.writeBoolean(true);
                uuid = dis.readUTF();
            } else {
                dos.writeBoolean(false);
            }

            return new Object[]{reply, uuid, key, fileName};
        } else {
            return new Object[]{reply, fileName};
        }
    }

    /**
     * Revokes the share link of the specified file or directory
     *
     * @param fileName item whose share link is to be revoked
     * @return true if the operation completed successfully, else false
     * @throws IOException if an I/O error occurs
     */
    public static int rmshare(String fileName) throws IOException {
        dos.writeByte(RMSHARE);
        dos.writeUTF(fileName);

        return dis.readInt();
    }

    /**
     * Gets information about the shared file with the specified UUID
     *
     * @param uuid share uuid
     * @param logged login status
     * @return share filename and size
     * @throws IOException
     */
    public static Object[] getshare(String uuid, boolean logged) throws IOException {
        if (logged) {
            dos.writeByte(GETSHARE);
        }

        dos.writeUTF(uuid);

        int reply = dis.readInt();

        Object[] res;
        switch (reply) {
            case EXEC_OK:
                String fileName = dis.readUTF();
                long fileSize = dis.readLong();
                res = new Object[]{reply, fileName, fileSize};
                break;
            default:
                res = new Object[]{reply};
                break;
        }

        return res;
    }

    /**
     * Gets the quota usage in MB
     *
     * @return quota usage in MB
     * @throws IOException if an I/O error occurs
     */
    public static long quotaused() throws IOException {
        dos.writeByte(QUOTAUSED);
        return dis.readLong();
    }

    /**
     * Checks whether the current working directory is the root
     *
     * @return true if the current working directory is the root, else false
     * @throws IOException
     */
    public static boolean isAtRoot() throws IOException {
        return cwd().equals("/");
    }
}
