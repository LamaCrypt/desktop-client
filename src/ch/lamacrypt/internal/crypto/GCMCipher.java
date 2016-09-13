/* 
 * Copyright (c) 2016, LamaCrypt
 * All rights reserved.
 *
 * The LamaCrypt client software and its source code are available
 * under the LamaCrypt Software License: 
 * https://github.com/LamaCrypt/desktop-client/blob/master/LICENSE.md
 */
package ch.lamacrypt.internal.crypto;

import ch.lamacrypt.internal.Settings;
import ch.lamacrypt.internal.network.NTP;
import ch.lamacrypt.visual.DefaultFrame;
import ch.lamacrypt.visual.DownloadFrame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import org.bouncycastle.crypto.generators.SCrypt;
import org.bouncycastle.util.encoders.Hex;

/**
 * General purpose class used for encryption and decryption of files, using
 * AES-256 in the GCM mode of operation
 * <p>
 * Contains methods for the following encryption schemes:
 * <ul>
 * <li>v00</li>
 * </ul>
 *
 * @author LamaGuy
 */
public final class GCMCipher {

    private static final String CIPHER = "AES/GCM/NoPadding",
            CRYPTO_PROVIDER = "BC";
    private static final int CIPHER_KEY_BITS = 256,
            GCM_NONCE_BYTES = 12,
            GCM_TAG_BITS = 128,
            Sk_BYTES = 64,
            Sn_BYTES = 6,
            R_BYTES = 64,
            BUFFER_SIZE = 8192,
            KDF_r = 8,
            KDF_p = 1,
            VS1 = Sk_BYTES,
            S1N1 = VS1 + GCM_NONCE_BYTES,
            N1K1N = S1N1 + 1,
            K1NR = N1K1N + R_BYTES + GCM_TAG_BITS / 8,
            RS2 = K1NR + Sk_BYTES,
            S2N2 = RS2 + GCM_NONCE_BYTES,
            N2K2N = S2N2 + 1,
            AEAD_EXCEPTION = 4;

    private static int K1_KDF_N = 21,
            K2_KDF_N = 19,
            intFileCnt = 0;

    private final byte[] buf = new byte[BUFFER_SIZE];
    private final DataOutputStream dos;
    private final DataInputStream dis;
    private final Cipher cipher;

    private int r;

    /**
     * Sets the new CPU/RAM cost parameter for scrypt when deriving K1, read as
     * a power of two
     * <p>
     * Default value: 21
     *
     * @param N new CPU/RAM cost for scrypt, as a power of two
     */
    protected static void setK1N(int N) {
        GCMCipher.K1_KDF_N = N;
    }

    /**
     * Returns the current scrypt CPU/RAM work factor (N)
     *
     * @return
     */
    protected static int getK1N() {
        return GCMCipher.K1_KDF_N;
    }

    /**
     * Sets the new CPU/RAM cost parameter for scrypt when deriving K2, read as
     * a power of two
     * <p>
     * Default value: 19
     *
     * @param N new CPU/RAM cost for scrypt, as a power of two
     */
    protected static void setK2N(int N) {
        GCMCipher.K2_KDF_N = N;
    }

    /**
     * Instantiates a Cipher object using AES-256 in GCM mode of operation,
     * allowing subsequent use for file encryption and decryption
     *
     * @param dis
     * @param dos
     * @throws java.lang.ClassNotFoundException if the class
     * javax.crypto.JceSecurity does not exist
     * @throws java.lang.NoSuchFieldException if the field isRestricted does not
     * exist in the class javax.crypto.JceSecurity
     * @throws java.lang.IllegalAccessException if the isRestricted field is
     * enforcing Java language access control and it is either inaccessible or
     * final
     * @throws java.security.NoSuchAlgorithmException
     * @throws java.security.NoSuchProviderException
     * @throws javax.crypto.NoSuchPaddingException
     *
     */
    protected GCMCipher(DataOutputStream dos, DataInputStream dis) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException {
        /*
        suppresses the restriction over keys larger than 128 bits due to the
        JCE Unlimited Strength Jurisdiction Policy
        see http://v.gd/HN1qpB
         */
        Field field = Class.forName("javax.crypto.JceSecurity").
                getDeclaredField("isRestricted");
        field.setAccessible(true);
        field.set(null, java.lang.Boolean.FALSE);

        // instantiating AES-256 w/ GCM from Bouncy Castle
        this.cipher = Cipher.getInstance(CIPHER, CRYPTO_PROVIDER);

        // settings the I/O streams
        this.dos = dos;
        this.dis = dis;
    }

    /**
     * Encrypts a given file with AES-256 in GCM mode of operation
     * <p>
     * Reads data from the InputStream and writes the encrypted data to the
     * OutputStream
     *
     * @param inputFile
     * @return
     * @throws java.io.IOException
     * @throws java.security.InvalidKeyException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws javax.crypto.BadPaddingException
     * @throws javax.crypto.IllegalBlockSizeException
     */
    protected int encrypt_V00(File inputFile) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        DefaultFrame.setFileQueueItemStatus("Generating header");

        // defining input stream
        InputStream input = new FileInputStream(inputFile);

        // getting the encryption password
        char[] pass = DefaultCipher.getEncryptionPassword();
        // generating Sx, Nx, R, Kx
        final byte[] Sk1 = GPCrypto.randomGen(Sk_BYTES),
                Sk2 = GPCrypto.randomGen(Sk_BYTES),
                Sn1 = GPCrypto.randomGen(Sn_BYTES),
                Sn2 = GPCrypto.randomGen(Sn_BYTES),
                byteFileCnt = GPCrypto.intToByteArray(intFileCnt),
                epoch = DatatypeConverter.parseHexBinary(Long.toHexString(NTP.getTime() / 1000)),
                N1 = new byte[]{Sn1[0], Sn1[1], Sn1[2], Sn1[3], Sn1[4], Sn1[5], byteFileCnt[0],
                    byteFileCnt[1], epoch[0], epoch[1], epoch[2], epoch[3]},
                N2 = new byte[]{Sn2[0], Sn2[1], Sn2[2], Sn2[3], Sn1[4], Sn1[5], byteFileCnt[0],
                    (byte) (byteFileCnt[1] + 0x01), epoch[0], epoch[1], epoch[2], (byte) (epoch[3] + 0x01)},
                R = GPCrypto.randomGen(R_BYTES);
        final SecretKey K1 = new SecretKeySpec(SCrypt.generate(GPCrypto.charToByte(pass),
                Sk1, (int) Math.pow(2, GCMCipher.K1_KDF_N), KDF_r, KDF_p, CIPHER_KEY_BITS / 8), 0,
                CIPHER_KEY_BITS / 8, "AES"),
                K2 = new SecretKeySpec(SCrypt.generate(R, Sk2, (int) Math.pow(2, K2_KDF_N), KDF_r,
                        KDF_p, CIPHER_KEY_BITS / 8), 0, CIPHER_KEY_BITS / 8, "AES");

        // writing header
        this.cipher.init(Cipher.ENCRYPT_MODE, K1, new GCMParameterSpec(
                GCM_TAG_BITS, N1, 0, GCM_NONCE_BYTES));
        dos.write((byte) 0x00);
        dos.write(Sk1);
        dos.write(N1);
        dos.write(DatatypeConverter.parseHexBinary(Integer.toHexString(GCMCipher.K1_KDF_N)));
        dos.write(cipher.doFinal(R));
        dos.write(Sk2);
        dos.write(N2);
        dos.write(DatatypeConverter.parseHexBinary(Integer.toHexString(K2_KDF_N)));

        // encrypting file
        DefaultFrame.setFileQueueItemStatus("Uploading (0%)");
        this.cipher.init(Cipher.ENCRYPT_MODE, K2, new GCMParameterSpec(
                GCM_TAG_BITS, N2, 0, GCM_NONCE_BYTES));
        long fileSize = inputFile.length(),
                percentage = fileSize / 100L,
                bytesRead = 0L,
                iterCnt;

        if (fileSize < BUFFER_SIZE) {
            r = input.read(buf, 0, (int) fileSize);
            dos.write(this.cipher.doFinal(buf, 0, r));
        } else if (fileSize % BUFFER_SIZE == 0) {
            iterCnt = (fileSize / BUFFER_SIZE) - 1;

            for (long i = 0; i < iterCnt; i++) {
                input.read(buf);
                dos.write(this.cipher.update(buf));

                bytesRead += BUFFER_SIZE;
                if (bytesRead % percentage > 1) {
                    updateStatus("Uploading (" + bytesRead / percentage + "%)");
                }
            }

            input.read(buf);
            dos.write(this.cipher.doFinal(buf));
        } else {
            iterCnt = fileSize / BUFFER_SIZE;

            for (long i = 0; i < iterCnt; i++) {
                input.read(buf);
                dos.write(this.cipher.update(buf));

                bytesRead += BUFFER_SIZE;
                if (bytesRead % percentage > 1) {
                    updateStatus("Uploading (" + bytesRead / percentage + "%)");
                }
            }

            r = input.read(buf, 0, (int) (fileSize % BUFFER_SIZE));
            dos.write(this.cipher.doFinal(buf, 0, r));
        }

        updateStatus("Finalizing");

        // cleaning up
        GPCrypto.eraseByteArrays(Sk1, Sk2, epoch, N1, N2, R);
        GPCrypto.eraseKeys(K1, K2);
        GPCrypto.sanitize(pass);
        input.close();
        updateFileCnt();

        return dis.readInt();
    }

    /**
     * Decrypts a given file with AES-256 in GCM mode of operation
     *
     * @param outputFile
     * @return
     * @throws java.io.IOException
     * @throws java.security.InvalidKeyException
     * @throws java.security.InvalidAlgorithmParameterException
     * @throws javax.crypto.BadPaddingException
     * @throws javax.crypto.IllegalBlockSizeException
     */
    protected int decrypt_V00(File outputFile) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, BadPaddingException, IllegalBlockSizeException {
        updateStatus("Reading header");

        // getting file size and calculating iterCnt
        long fileSize = dis.readLong(),
                dlSize = fileSize + GCM_TAG_BITS / 8,
                iterCnt = dlSize / BUFFER_SIZE,
                percentage = dlSize / 100L,
                bytesRead = 0L;

        // defining output stream
        OutputStream output = new FileOutputStream(outputFile);

        // getting the encryption password
        char[] pass = DefaultCipher.getEncryptionPassword();

        // reading header
        byte[] header = new byte[234];
        dis.read(header, 0, 234);

        // reading Sx, Nx, scrypt factors and generating K1
        final byte[] S1 = Arrays.copyOfRange(header, 0, VS1),
                N1 = Arrays.copyOfRange(header, VS1, S1N1),
                K1_N = Arrays.copyOfRange(header, S1N1, N1K1N),
                S2 = Arrays.copyOfRange(header, K1NR, RS2),
                N2 = Arrays.copyOfRange(header, RS2, S2N2),
                K2_N = Arrays.copyOfRange(header, S2N2, N2K2N);
        final int K1_N_bak = (int) Math.pow(2, Integer.valueOf(Hex.toHexString(K1_N), 16)),
                K2_N_bak = (int) Math.pow(2, Integer.valueOf(Hex.toHexString(K2_N), 16));
        final SecretKey K1 = new SecretKeySpec(SCrypt.generate(GPCrypto.charToByte(pass),
                S1, K1_N_bak, KDF_r, KDF_p, CIPHER_KEY_BITS / 8), 0, CIPHER_KEY_BITS / 8, "AES");

        // reading E(K1, N1, R) 
        this.cipher.init(Cipher.DECRYPT_MODE, K1, new GCMParameterSpec(
                GCM_TAG_BITS, N1, 0, GCM_NONCE_BYTES));
        boolean failFree = true,
                AEADBadTag = false;
        byte[] R = new byte[64];

        try {
            R = cipher.doFinal(Arrays.copyOfRange(header, N1K1N, K1NR));
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            failFree = false;
            updateStatus("Error");
            if (e instanceof javax.crypto.AEADBadTagException) {
                AEADBadTag = true;
            }
        }

        dos.writeBoolean(failFree);
        // decrypting file if no exception has been caught
        if (failFree) {
            // generating K2
            final SecretKey K2 = new SecretKeySpec(SCrypt.generate(R, S2, K2_N_bak,
                    KDF_r, KDF_p, CIPHER_KEY_BITS / 8), 0, CIPHER_KEY_BITS / 8, "AES");

            updateStatus("Downloading (0%)");
            this.cipher.init(Cipher.DECRYPT_MODE, K2, new GCMParameterSpec(
                    GCM_TAG_BITS, N2, 0, GCM_NONCE_BYTES));

            if (dlSize > BUFFER_SIZE) {
                if (dlSize % BUFFER_SIZE == 0) {
                    iterCnt--;
                }

                // reading full blocks
                for (long i = 0; i < iterCnt; i++) {
                    dis.readFully(buf);
                    output.write(this.cipher.update(buf));

                    bytesRead += BUFFER_SIZE;
                    if (bytesRead % percentage > 1) {
                        updateStatus("Downloading (" + bytesRead / percentage + "%)");
                    }
                }

                // reading last chunk
                if (dlSize % BUFFER_SIZE != 0) {
                    r = dis.read(buf, 0, (int) dlSize % BUFFER_SIZE);
                    output.write(this.cipher.doFinal(buf, 0, r));
                } else {
                    dis.readFully(buf);
                    output.write(this.cipher.doFinal(buf));
                }
            } else {
                r = dis.read(buf, 0, (int) dlSize);
                output.write(this.cipher.doFinal(buf, 0, r));
            }

            // erasing cryptographic parameters and closing streams
            updateStatus("Finalizing");
            GPCrypto.eraseByteArrays(header, S1, S2, N1, N2, R);
            GPCrypto.eraseKeys(K1, K2);
            GPCrypto.sanitize(pass);
            output.close();

            return dis.readInt();
        } else {
            // erasing cryptographic parameters and closing streams
            GPCrypto.eraseByteArrays(header, S1, S2, N1, N2, R);
            GPCrypto.eraseKeys(K1);
            GPCrypto.sanitize(pass);
            output.close();

            if (AEADBadTag) {
                return AEAD_EXCEPTION;
            } else {
                return 5;
            }
        }
    }

    protected int decryptshare_V00(String key, File outputFile) throws IOException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // defining output stream
        FileOutputStream output = new FileOutputStream(outputFile);

        // getting file size and calculating iterCnt
        long fileSize = dis.readLong(),
                dlSize = fileSize + GCM_TAG_BITS / 8,
                iterCnt = (dlSize - BUFFER_SIZE) / BUFFER_SIZE,
                percentage = (dlSize - BUFFER_SIZE) / 100L,
                bytesRead = 0L;

        // recovering K2 and N2
        updateStatus("Reading header");
        final byte[] N2 = new byte[12];
        dis.read(N2, 0, 12);
        final SecretKey K2 = new SecretKeySpec(DatatypeConverter.parseHexBinary(key), 0,
                CIPHER_KEY_BITS / 8, "AES");

        boolean AEADBadTag = false,
                failFree = true,
                done = true;

        // reading first buffer
        this.cipher.init(Cipher.DECRYPT_MODE, K2, new GCMParameterSpec(
                GCM_TAG_BITS, N2, 0, GCM_NONCE_BYTES));
        updateStatus("Downloading (0%)");

        try {
            if (dlSize > BUFFER_SIZE) {
                dis.readFully(buf);
                output.write(this.cipher.update(buf));
                done = false;
            } else {
                r = dis.read(buf, 0, (int) dlSize);
                output.write(this.cipher.doFinal(buf, 0, r));
            }
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            failFree = false;
            updateStatus("Error");
            if (e instanceof javax.crypto.AEADBadTagException) {
                AEADBadTag = true;
            }
        }

        dos.writeBoolean(failFree);

        // decrypting file if no exception has been caught
        if (failFree) {
            if (!done) {
                if (dlSize % BUFFER_SIZE == 0) {
                    iterCnt--;
                }

                // reading full blocks
                for (long i = 0; i < iterCnt; i++) {
                    dis.readFully(buf);
                    output.write(this.cipher.update(buf));

                    bytesRead += BUFFER_SIZE;
                    if (bytesRead % percentage > 1) {
                        updateStatus("Downloading (" + bytesRead / percentage + "%)");
                    }
                }

                // reading last chunk
                if (dlSize % BUFFER_SIZE != 0) {
                    r = dis.read(buf, 0, (int) dlSize % BUFFER_SIZE);
                    output.write(this.cipher.doFinal(buf, 0, r));
                } else {
                    dis.readFully(buf);
                    output.write(this.cipher.doFinal(buf));
                }

                // erasing cryptographic parameters and closing streams
                updateStatus("Finalizing");
                finish(N2, K2, output);

                return dis.readInt();
            } else {
                // erasing cryptographic parameters and closing streams
                updateStatus("Finalizing");
                finish(N2, K2, output);

                return 0;
            }
        } else {
            // erasing cryptographic parameters and closing streams
            finish(N2, K2, output);

            if (AEADBadTag) {
                return AEAD_EXCEPTION;
            } else {
                return 7;
            }
        }
    }

    protected static String getKey_V00(byte[] header) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        // getting the encryption password
        char[] pass = DefaultCipher.getEncryptionPassword();

        // reading Sx, Nx, scrypt factors and generating K1
        final byte[] S1 = Arrays.copyOfRange(header, 0, VS1),
                N1 = Arrays.copyOfRange(header, VS1, S1N1),
                K1_N = Arrays.copyOfRange(header, S1N1, N1K1N),
                S2 = Arrays.copyOfRange(header, K1NR, RS2),
                N2 = Arrays.copyOfRange(header, RS2, S2N2),
                K2_N = Arrays.copyOfRange(header, S2N2, N2K2N);
        final int K1_N_bak = (int) Math.pow(2, Integer.valueOf(Hex.toHexString(K1_N), 16)),
                K2_N_bak = (int) Math.pow(2, Integer.valueOf(Hex.toHexString(K2_N), 16));
        final SecretKey K1 = new SecretKeySpec(SCrypt.generate(GPCrypto.charToByte(pass),
                S1, K1_N_bak, KDF_r, KDF_p, CIPHER_KEY_BITS / 8), 0, CIPHER_KEY_BITS / 8, "AES");

        // reading E(K1, N1, R)
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.DECRYPT_MODE, K1, new GCMParameterSpec(
                GCM_TAG_BITS, N1, 0, GCM_NONCE_BYTES));

        byte[] R = new byte[64];
        boolean failFree = true;

        try {
            R = cipher.doFinal(Arrays.copyOfRange(header, N1K1N, K1NR));
        } catch (IllegalBlockSizeException | BadPaddingException ex) {
            failFree = false;
        }

        if (failFree) {
            // generating K2
            final SecretKey K2 = new SecretKeySpec(SCrypt.generate(R, S2, K2_N_bak,
                    KDF_r, KDF_p, CIPHER_KEY_BITS / 8), 0, CIPHER_KEY_BITS / 8, "AES");

            GPCrypto.eraseByteArrays(header, S1, S2, N1, N2, R);
            GPCrypto.eraseKeys(K1);
            GPCrypto.sanitize(pass);

            return Hex.toHexString(K2.getEncoded());
        } else {
            GPCrypto.eraseByteArrays(header, S1, S2, N1, N2, R);
            GPCrypto.eraseKeys(K1);
            GPCrypto.sanitize(pass);

            return "error";
        }
    }

    /**
     * Updates the status of the download/upload job on the DefaultFrame (if
     * logged) or the DownloadFrame (if unlogged)
     *
     * @param txt new status text
     */
    private void updateStatus(String txt) {
        if (Settings.getLogged()) {
            DefaultFrame.setFileQueueItemStatus(txt);
        } else {
            DownloadFrame.updateStatus(txt);
        }
    }

    /**
     * Cleans up in-memory cryptographic values and closes the FileOutputStream
     *
     * @param N2 nonce
     * @param K2 AES key
     * @param fos FileOutputStream
     * @throws IOException
     */
    private void finish(byte[] N2, SecretKey K2, FileOutputStream fos) throws IOException {
        GPCrypto.sanitize(N2, GPCrypto.SANITIZATION_COUNT);
        GPCrypto.eraseKeys(K2);
        fos.close();
    }

    /**
     * Increments the file counter by two if the new value is under 65535, else
     * sets it back to 0.
     */
    private void updateFileCnt() {
        if (intFileCnt + 2 < 65535) {
            intFileCnt += 2;
        } else {
            intFileCnt = 0;
        }
    }
}
