package no.aegisdynamics.habitat.util;


import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.StrongBoxUnavailableException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.google.common.base.Charsets;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class KeyStoreHelper {

    private static final String keyAlias = "Habitat_Credentials_Key";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String TAG = "Habitat KeyStoreHelper";

    private byte[] iv;

    public byte[] getIv() {
        return iv;
    }

    private KeyStore initKeyStore() throws KeyStoreException, CertificateException,
            NoSuchAlgorithmException, IOException {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
        keyStore.load(null);
        return keyStore;
    }

    private SecretKey getSecretKeyFromKeyStore(KeyStore keyStore) throws KeyStoreException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, NoSuchProviderException, UnrecoverableEntryException {
        SecretKey secretKey;

        if (!keyStore.containsAlias(keyAlias)) {
            // If key doesnt exist, generate a new one.
            secretKey = generateNewEncryptionKey();
        } else {
            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore
                    .getEntry(keyAlias, null);
            secretKey = secretKeyEntry.getSecretKey();
        }

        return secretKey;
    }

    public byte[] encryptStringWithKeyStoreKey(final String clearText) throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException, NoSuchProviderException, InvalidKeyException, NoSuchPaddingException, UnrecoverableEntryException, BadPaddingException, IllegalBlockSizeException {
        // Check if key alias exists.
        KeyStore keyStore = initKeyStore();
        SecretKey secretKey = getSecretKeyFromKeyStore(keyStore);

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        // Generate IV
        iv = cipher.getIV();
        // Encrypt cleartext;
        return cipher.doFinal(clearText.getBytes(StandardCharsets.UTF_8));
    }

    public String decryptStringWithKeyStore(final byte[] ciphertext, final byte[] encryptionIV) throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, UnrecoverableEntryException, BadPaddingException, IllegalBlockSizeException {
        KeyStore keyStore = initKeyStore();
        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        final GCMParameterSpec spec = new GCMParameterSpec(128, encryptionIV);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKeyFromKeyStore(keyStore), spec);

        return new String(cipher.doFinal(ciphertext), Charsets.ISO_8859_1);

    }

    private SecretKey generateNewEncryptionKey() throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        final KeyGenParameterSpec keyGenParameterSpec;
        keyGenParameterSpec = getKeyGenParameterSpec();


        keyGenerator.init(keyGenParameterSpec);
        return keyGenerator.generateKey();
    }

    private KeyGenParameterSpec getKeyGenParameterSpec() {
        KeyGenParameterSpec.Builder keygenBuilder = new KeyGenParameterSpec.Builder(keyAlias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true);

        if (android.os.Build.VERSION.SDK_INT >= 28) {
            keygenBuilder = tryToSetStrongBoxBacking(keygenBuilder);
        }

        return keygenBuilder.build();
    }

    @RequiresApi(api = 28)
    private KeyGenParameterSpec.Builder tryToSetStrongBoxBacking(KeyGenParameterSpec.Builder keygenBuilder) {
        try {
            keygenBuilder.setIsStrongBoxBacked(true);
        } catch (StrongBoxUnavailableException ex) {
            // Strongbox is not available. Return default keygen spec.
            Log.i(TAG, "StrongBox not supported on this device");
        }
        return keygenBuilder;
    }
}
