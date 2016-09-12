package ch.lamacrypt.internal.file;

import java.io.File;

/**
 * Contains general purpose methods related to file handling
 *
 * @author LamaGuy
 */
public abstract class GPFile {

    /**
     * Converts a file size in long to a human-friendly format
     * <p>
     * Ex: 12543 -> 12.54 kB
     *
     * @param usageBytes file size in long
     * @return human readable size format
     */
    public static String longToSize(long usageBytes) {
        String usageString = Long.toString(usageBytes),
                suffix = " ",
                prefix = "";
        long step = 0L;

        if (usageBytes < 1e3) {
            suffix += usageString + " B";
        } else if (usageBytes < 1e6) {
            step = 1000L;
            suffix += "kB";
        } else if (usageBytes < 1e9) {
            step = 1000000L;
            suffix += "MB";
        } else if (usageBytes < 1e12) {
            step = 1000000000L;
            suffix += "GB";
        } else {
            step = 1000000000000L;
            suffix += "TB";
        }

        if (usageBytes > 1e3) {
            if (usageBytes / step < 10) {
                prefix = usageString.substring(0, 1) + "." + usageString.substring(1, 3);
            } else if (usageBytes / step < 100) {
                prefix = usageString.substring(0, 2) + "." + usageString.substring(2, 4);
            } else if (usageBytes / step < 1000) {
                prefix = usageString.substring(0, 3) + "." + usageString.substring(3, 5);
            }
        }

        return prefix + suffix;
    }

    /**
     * Recursively checks the usable space of a file's nearest parent directory
     *
     * @param f file to check
     * @return Usable space of the first existing parent directory
     */
    public static long checkDirSize(File f) {
        if (f.getParentFile().exists()) {
            return f.getParentFile().getUsableSpace();
        } else {
            return checkDirSize(f.getParentFile());
        }
    }

    /**
     * Recursively checks for an existing parent directory
     *
     * @param f file to check
     * @return First existing parent directory of a file
     */
    public static File getNearestParentDirectory(File f) {
        if (f.getParentFile().exists()) {
            return f.getParentFile();
        } else {
            return getNearestParentDirectory(f.getParentFile());
        }
    }
}
