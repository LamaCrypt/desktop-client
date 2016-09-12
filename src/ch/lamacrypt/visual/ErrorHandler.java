/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.visual;

import java.awt.TrayIcon;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Contains basic error handling classes
 *
 * @author LamaGuy
 */
public final class ErrorHandler {

    public static void showError(Exception ex) {
        setUIStyle();
        JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage()
                + "\nPlease contact support.", "Error", TrayIcon.MessageType.ERROR.ordinal());
    }

    public static void showError(String message) {
        setUIStyle();
        JOptionPane.showMessageDialog(null, "Error: " + message,
                "Error", TrayIcon.MessageType.ERROR.ordinal());
    }

    private static void setUIStyle() {
        try {
            javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, "Error initializing UI style: "
                    + e.getMessage() + "\nPlease contact support.", "Error",
                    TrayIcon.MessageType.ERROR.ordinal());
        }
    }
}
