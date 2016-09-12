/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.visual.LoginForm;
import ch.lamacrypt.visual.TOSDisclaimer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * Main class
 * <p>
 * Sets up the GUI, so that the user can login in and start using the service.
 * <p>
 * Also adds Bouncy Castle as provider.
 *
 * @author LamaGuy
 */
public class Main {

    public static void main(String args[]) throws Exception {
        // adding Bouncy Castle as provider
        Security.addProvider(new BouncyCastleProvider());

        // settings the TrustStore to LamaCrypt
        System.setProperty("javax.net.ssl.trustStore", "LamaCryptTrustStore");
        System.setProperty("javax.net.ssl.trustStorePassword", "LCTSPW");

        // getting the config file and checking if the TOS have been agreed to
        File config = new File("config");
        if (config.exists()) {
            BufferedReader in = new BufferedReader(new FileReader(new File("config")));
            Settings.setTOSAgreed(in.readLine().equals("agreedTOS=yes"));
            in.close();
            Settings.setIsNew(false);
        } else {
            Settings.setIsNew(true);
        }

        if (Settings.isTOSAgreed()) {
            // starting GUI
            LoginForm.main(null);
        } else {
            TOSDisclaimer.main(null);
        }
    }
}
