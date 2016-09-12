/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal.network;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.internal.crypto.GPCrypto;
import ch.lamacrypt.visual.LoginForm;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import javax.net.ssl.SSLSocket;

/**
 * Contains user authentication code
 *
 * @author LamaGuy
 */
public abstract class Authentication {

    private static final String AUTH_SERVER_NAME = "data.lamacrypt.ch",
            CTRL_SERVER_NAME = "data.lamacrypt.ch",
            IO_SERVER_NAME = "data.lamacrypt.ch";
    private static final int AUTH_SERVER_PORT = 4410,
            CTRL_SERVER_PORT = 4411,
            IO_SERVER_PORT = 4412;
    private static final byte[] token_auth = new byte[128],
            token_data = new byte[128];
    private static final byte LOGIN = 0x70;

    private static SSLSocket authSocket,
            ctrlSocket,
            ioSocket;
    private static DataOutputStream authDos,
            ctrlDos,
            ioDos;
    private static DataInputStream authDis,
            ctrlDis,
            ioDis;

    /**
     * Authenticates the user against the server using TLS
     *
     * @param username username
     * @param password password
     * @return true if credentials are correct, false if they aren't
     * @throws java.io.IOException
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.KeyManagementException
     */
    public static boolean login(String username, char[] password) throws IOException, Exception {
        // initializing the authentication connection
        LoginForm.updateLoginLabel("Connecting to authentication server");
        authSocket = (SSLSocket) GPTLS.getContext().getSocketFactory().createSocket(
                InetAddress.getByName(AUTH_SERVER_NAME), AUTH_SERVER_PORT);
        GPTLS.setTLSParams(authSocket);
        authDos = new DataOutputStream(authSocket.getOutputStream());
        authDis = new DataInputStream(authSocket.getInputStream());

        // sending credentials
        LoginForm.updateLoginLabel("Sending credentials");
        authDos.writeUTF(username);
        authDos.writeInt(password.length);
        authDos.write(GPCrypto.charToByte(password));

        if (authDis.readBoolean()) {
            // checking if the user is new
            Settings.setIsNew(authDis.readBoolean());

            // getting token and returning response
            LoginForm.updateLoginLabel("Reading tokens");
            authDis.readFully(token_auth);
            authDis.readFully(token_data);
            Settings.setQuotaSize(authDis.readLong());

            LoginForm.updateLoginLabel("Opening I/O and control sockets");
            ctrlSocket = (SSLSocket) GPTLS.getContext().getSocketFactory().createSocket(
                    InetAddress.getByName(CTRL_SERVER_NAME), CTRL_SERVER_PORT);
            GPTLS.setTLSParams(ctrlSocket);
            ctrlDos = new DataOutputStream(ctrlSocket.getOutputStream());
            ctrlDis = new DataInputStream(ctrlSocket.getInputStream());

            ioSocket = (SSLSocket) GPTLS.getContext().getSocketFactory().createSocket(
                    InetAddress.getByName(IO_SERVER_NAME), IO_SERVER_PORT);
            GPTLS.setTLSParams(ioSocket);
            ioDos = new DataOutputStream(ioSocket.getOutputStream());
            ioDis = new DataInputStream(ioSocket.getInputStream());

            LoginForm.updateLoginLabel("Verifying tokens");
            ctrlDos.writeByte(LOGIN);
            ctrlDos.writeUTF(username);
            ctrlDos.write(token_auth);
            final boolean ctrlStatus = ctrlDis.readBoolean();

            ioDos.writeByte(LOGIN);
            ioDos.writeUTF(username);
            ioDos.write(token_data);
            final boolean ioStatus = ioDis.readBoolean();

            if (ctrlStatus && ioStatus) {
                LoginForm.updateLoginLabel("Authentication successful");
                Control.init(ctrlDos, ctrlDis);
                IO.init(ioDos, ioDis);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Aborts the ongoing authentication by closing the opened sockets
     *
     * @throws IOException
     */
    private static void abort() throws IOException {
        if (authSocket != null && !authSocket.isClosed()) {
            authSocket.close();
        }
        if (ctrlSocket != null && !ctrlSocket.isClosed()) {
            ctrlSocket.close();
        }
        if (ioSocket != null && !ioSocket.isClosed()) {
            ioSocket.close();
        }
    }
}
