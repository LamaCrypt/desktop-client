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
import ch.lamacrypt.visual.ErrorHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Contains file I/O methods, used for uploading/downloading binary data
 *
 * @author LamaGuy
 */
public abstract class IO {

    private static final byte DISCONNECT = 0x00,
            UPLOAD = 0x10,
            DOWNLOAD = 0x11,
            DOWNLOAD_SHARE = 0x12;
    private static final int EXEC_OK = 0;

    private static DataOutputStream dos;
    private static DataInputStream dis;

    public static void init(DataOutputStream dos, DataInputStream dis) throws IOException, Exception {
        DefaultCipher.init(dos, dis);
        IO.dos = dos;
        IO.dis = dis;
    }

    /**
     * Disconnects from the I/O server
     *
     * @throws IOException if an I/O error occurs
     */
    public static void disconnect() throws IOException {
        dos.writeByte(DISCONNECT);
    }

    /**
     * Sends a file to the I/O server
     *
     * @param input
     * @param remoteFilePath
     * @return
     * @throws Exception
     */
    public static int upload(File input, String remoteFilePath) throws Exception {
        try {
            dos.writeByte(UPLOAD);
            dos.writeUTF(remoteFilePath);
            dos.writeLong(input.length());

            int reply = dis.readInt();

            if (reply == EXEC_OK) {
                return DefaultCipher.encrypt(input);
            } else {
                return reply;
            }
        } catch (IOException ex) {
            ErrorHandler.showError(ex);
            return -2;
        }
    }

    /**
     * Downloads a file from the I/O server
     *
     * @param fileName
     * @param output
     * @return
     * @throws Exception
     */
    public static int download(String fileName, File output) throws Exception {
        try {
            dos.writeByte(DOWNLOAD);
            dos.writeUTF(fileName);

            int reply = dis.readInt();

            if (reply == EXEC_OK) {
                return DefaultCipher.decrypt(output);
            } else {
                return reply;
            }
        } catch (IOException ex) {
            ErrorHandler.showError(ex);
            return -2;
        }
    }

    public static int downloadshare(String uuid, String key, File output, boolean normal) throws Exception {
        try {
            if(normal){
                dos.writeByte(DOWNLOAD_SHARE);
            }
            dos.writeUTF(uuid);

            int reply = dis.readInt();

            if (reply == EXEC_OK) {
                return DefaultCipher.decryptShare(key, output);
            } else {
                return reply;
            }
        } catch (IOException ex) {
            ErrorHandler.showError(ex);
            return -1;
        }
    }
}
