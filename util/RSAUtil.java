package util;

import java.io.File;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class RSAUtil {
    private static final String KEYS_DIR = "dados/keys";
    private static final String PUBLIC_KEY_FILE = KEYS_DIR + "/public.key";
    private static final String PRIVATE_KEY_FILE = KEYS_DIR + "/private.key";
    private static final String ALGORITHM = "RSA";
    private static final String CIPHER = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    private static final int KEY_SIZE = 2048;

    private static PublicKey publicKey = null;
    private static PrivateKey privateKey = null;

    private static synchronized void ensureKeys() throws Exception {
        if (publicKey != null && privateKey != null) return;

        File dir = new File(KEYS_DIR);
        if (!dir.exists()) dir.mkdirs();

        File pubF = new File(PUBLIC_KEY_FILE);
        File privF = new File(PRIVATE_KEY_FILE);

        if (pubF.exists() && privF.exists()) {
            byte[] pubBytes = Files.readAllBytes(pubF.toPath());
            byte[] privBytes = Files.readAllBytes(privF.toPath());

            KeyFactory kf = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubBytes);
            PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privBytes);
            publicKey = kf.generatePublic(pubSpec);
            privateKey = kf.generatePrivate(privSpec);
        } else {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM);
            kpg.initialize(KEY_SIZE);
            KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();

            Files.write(pubF.toPath(), publicKey.getEncoded());
            Files.write(privF.toPath(), privateKey.getEncoded());
        }
    }

    public static String encryptString(String plain) throws Exception {
        ensureKeys();
        Cipher cipher = Cipher.getInstance(CIPHER);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encrypted = cipher.doFinal(plain.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public static String decryptString(String cipherText) throws Exception {
        ensureKeys();
        try {
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, "UTF-8");
        } catch (IllegalArgumentException iae) {
            // input was not Base64 -> assume plaintext stored; return as-is
            return cipherText;
        } catch (Exception e) {
            // If decryption fails for any reason, return original text to preserve compatibility
            return cipherText;
        }
    }
}
