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
import ch.lamacrypt.internal.crypto.DefaultCipher;
import ch.lamacrypt.visual.ErrorHandler;
import ch.lamacrypt.visual.LoginForm;
import ch.lamacrypt.visual.TOSDisclaimer;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Security;
import java.util.Map;
import java.util.Scanner;
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
        if (Settings.getMaxRAM() + 256 * (int) Math.pow(2, 20) < 6 * (int) Math.pow(2, 30)) {
            ErrorHandler.showError("your system doesn't have enough memory (min. 6GiB).");
        } else {
            // adding Bouncy Castle as provider
            Security.addProvider(new BouncyCastleProvider());

            // overriding the default TrustStore
            System.setProperty("javax.net.ssl.trustStore", "LamaCryptTrustStore");
            System.setProperty("javax.net.ssl.trustStorePassword", "LCTSPW");

            // try to remove restrictions on cryptographic key lengths
            removeCryptographyRestrictions();

            // getting the config file and checking if the TOS have been agreed to
            File config = new File("desktop-client.conf");
            if (config.exists()) {
                Scanner in = new Scanner(new FileReader(config));
                while (in.hasNext()) {
                    String tmpStr = in.next();
                    if (tmpStr.startsWith("scryptfactor")) {
                        int N = Integer.parseInt(tmpStr.substring(13, 15));
                        DefaultCipher.setScryptFactor(N);
                        Settings.setStartupScryptN(N);
                    } else if (tmpStr.startsWith("agreedTOS")) {
                        Settings.setTOSAgreed(tmpStr.contains("yes"));
                    }
                }
                in.close();
                Settings.setIsNew(false);
            } else {
                config.createNewFile();
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

    /**
     * Removes any restriction on cryptographic key sizes
     * <p>
     * See <a href="https://stackoverflow.com/a/22492582/2828700">this
     * StackOverflow post</a> for more information
     */
    private static void removeCryptographyRestrictions() {
        if (!isRestrictedCryptography()) {
            Settings.setIsRestricted(false);
        } else {
            try {
                /**
                 * Does the following:
                 *
                 * JceSecurity.isRestricted = false;
                 * JceSecurity.defaultPolicy.perms.clear();
                 * JceSecurity.defaultPolicy.add(CryptoAllPermission.INSTANCE);
                 */
                final Class<?> jceSecurity = Class.forName("javax.crypto.JceSecurity");
                final Class<?> cryptoPermissions = Class.forName("javax.crypto.CryptoPermissions");
                final Class<?> cryptoAllPermission = Class.forName("javax.crypto.CryptoAllPermission");

                final Field isRestrictedField = jceSecurity.getDeclaredField("isRestricted");
                isRestrictedField.setAccessible(true);
                final Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(isRestrictedField, isRestrictedField.getModifiers() & ~Modifier.FINAL);
                isRestrictedField.set(null, false);

                final Field defaultPolicyField = jceSecurity.getDeclaredField("defaultPolicy");
                defaultPolicyField.setAccessible(true);
                final PermissionCollection defaultPolicy = (PermissionCollection) defaultPolicyField.get(null);

                final Field perms = cryptoPermissions.getDeclaredField("perms");
                perms.setAccessible(true);
                ((Map<?, ?>) perms.get(defaultPolicy)).clear();

                final Field instance = cryptoAllPermission.getDeclaredField("INSTANCE");
                instance.setAccessible(true);
                defaultPolicy.add((Permission) instance.get(null));

                Settings.setIsRestricted(false);
            } catch (final Exception e) {
                Settings.setIsRestricted(true);
            }
        }
    }

    /**
     * Checks if the JRE is one that restricts cryptographic key lengths
     *
     * @return <code>true</code> if the current JRE does not allow the use of
     * cryptographic keys larger than 128 bits
     */
    private static boolean isRestrictedCryptography() {
        // This simply matches the Oracle JRE, but not OpenJDK.
        return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
    }
}
