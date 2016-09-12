/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal.network;

import ch.lamacrypt.visual.ErrorHandler;
import java.io.IOException;
import java.net.InetAddress;
import org.apache.commons.net.ntp.NTPUDPClient;

/**
 * Contains NTP-related code
 *
 * Primarily used by the *Cipher classes for nonce generation
 *
 * @author LamaGuy
 */
public class NTP {

    private static final String[] hosts = new String[2];

    /**
     * Returns the current Unix epoch (in milliseconds), obtained by polling the
     * following NTP server:
     * <ul>
     * <li>0.ch.pool.ntp.org</li>
     * <li>0.is.pool.ntp.org</li>
     *
     * </ul>
     * <p>
     * Waits up to 10 seconds to get a response
     *
     * @return current Unix epoch, in milliseconds
     */
    public static long getTime() {
        NTPUDPClient client = new NTPUDPClient();
        client.setDefaultTimeout(10000);
        hosts[0] = "0.ch.pool.ntp.org";
        hosts[1] = "0.is.pool.ntp.org";
        boolean done = false;
        long epoch = 0;
        int i = 0;

        while (!done) {
            try {
                InetAddress hostAddr = InetAddress.getByName(hosts[i]);
                epoch = client.getTime(hostAddr).getReturnTime();
                done = true;
            } catch (IOException ex) {
                ErrorHandler.showError(ex);
            }
            i++;
        }

        client.close();
        return epoch;
    }
}
