package com.blemeterkai.meterkai.dlms;

// AccSecurity.java

import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AccSecurity {

    private static final String TAG = AccSecurity.class.getSimpleName();
    private static final String ANDROID_KEYSTORE_PROVIDER = "AndroidKeyStore";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;

    private final Context context;
    private KeyStore keyStore;

    // Potentially store session-specific information if needed
    private SecretKey sessionEncryptionKey;
    private SecretKey sessionAuthenticationKey; // If separate MACing is used
    private final byte[] clientSystemTitle; // Needed for IV construction
    private final byte[] serverSystemTitle; // Needed for IV construction
    private long clientInvocationCounter;
    private final long serverInvocationCounter;


    public AccSecurity(Context context, byte[] clientSystemTitle, byte[] serverSystemTitle) {
        this.context = context.getApplicationContext();
        this.clientSystemTitle = clientSystemTitle;
        this.serverSystemTitle = serverSystemTitle;
        this.clientInvocationCounter = 0; // Initialize appropriately
        this.serverInvocationCounter = 0; // Initialize appropriately
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE_PROVIDER);
            keyStore.load(null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 IOException e) {
            // Handle Keystore initialization errors
            e.printStackTrace();
            // Consider throwing a custom exception or setting an error state
        }
    }

    // --- Key Management Methods ---

    /**
     * Generates a new AES-GCM key and stores it in the Android Keystore.
     *
     * @param keyAlias The alias for the key in the Keystore.
     * @return The generated SecretKey, or null on failure.
     */
    public SecretKey generateAndStoreAesGcmKey(String keyAlias) {
        if (keyAlias == null || keyAlias.isEmpty()) return null;
        try {
            if (keyStore.containsAlias(keyAlias)) {
                // Key already exists, decide whether to return it or overwrite (careful!)
                return (SecretKey) keyStore.getKey(keyAlias, null);
            }

            KeyGenerator keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE_PROVIDER);

            KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(
                    keyAlias,
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT);
            builder.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(256); // Or 128, 192 as per your security policy
            // .setUserAuthenticationRequired(true) // If key use requires user auth
            // .setRandomizedEncryptionRequired(true) // GCM requires this by default

            keyGenerator.init(builder.build());
            return keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                 InvalidAlgorithmParameterException | KeyStoreException |
                 UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves a secret key from the Android Keystore.
     *
     * @param keyAlias The alias of the key.
     * @return The SecretKey, or null if not found or on error.
     */
    public SecretKey getSecretKeyFromKeystore(String keyAlias) {
        try {
            if (!keyStore.containsAlias(keyAlias)) {
                return null;
            }
            return (SecretKey) keyStore.getKey(keyAlias, null);
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Imports a raw key into the Android Keystore.
     * Note: This is generally less secure than generating within Keystore if the raw key
     * is exposed. Use for pre-shared keys that must be imported.
     *
     * @param keyAlias Alias for the key.
     * @param keyBytes Raw key material.
     * @return The imported SecretKey, or null on failure.
     */
    public SecretKey importSecretKeyToKeystore(String keyAlias, byte[] keyBytes) {
        try {
            if (keyStore.containsAlias(keyAlias)) {
                // Handle existing alias, e.g., delete or throw error
                // For example, you might want to delete it before importing a new one:
                // keyStore.deleteEntry(keyAlias);
                // Or return the existing key if that's the desired behavior:
                // return (SecretKey) keyStore.getKey(keyAlias, null);
            }
            SecretKey secretKey = new SecretKeySpec(keyBytes, KeyProperties.KEY_ALGORITHM_AES);
            KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);

            // Protection parameters define how the key can be used.
            // For AndroidKeyStore, KeyProtection is used.
            KeyStore.ProtectionParameter protectionParameter;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                protectionParameter = new KeyProtection.Builder(
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        // Add other constraints as needed (e.g., setUserAuthenticationRequired)
                        .build();
            } else {
                // For older versions, you might not have the same level of granularity
                // or might need to rely on the default protection of the AndroidKeyStore.
                // Passing null might be acceptable if no specific protection is needed beyond
                // what the provider offers by default. However, be aware of security implications.
                protectionParameter = null; // Or handle appropriately for pre-M
            }

            keyStore.setEntry(keyAlias, secretKeyEntry, protectionParameter);
            return secretKey;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        }
        // NoSuchAlgorithmException or UnrecoverableKeyException if you were calling getKey() here.
    }



    // --- HLS Authentication Methods (Conceptual) ---

    /**
     * Initiates the HLS authentication process. (Details depend on DLMS spec)
     * This might involve:
     * - Preparing client challenge (eSTO)
     * - Receiving server challenge (sTOC)
     * - Generating responses using authentication keys
     *
     * @param authenticationKeyAlias Alias for the authentication key in Keystore
     * @return true if authentication step is successful, false otherwise.
     */
    public boolean performHlsAuthenticationStep(String authenticationKeyAlias /*, other params */) {
        // ... Retrieve authenticationKey from Keystore ...
        // ... Implement challenge-response logic ...
        // ... Update session keys if derived during authentication ...
        // this.sessionEncryptionKey = ...;
        return false; // Placeholder
    }


    // --- APDU Security Methods (using AES-GCM) ---

    private byte[] getNextClientIv() {
        // IV for GCM: Client System Title (8 bytes) + Client Invocation Counter (4 bytes)
        ByteBuffer ivBuffer = ByteBuffer.allocate(GCM_IV_LENGTH_BYTES);
        ivBuffer.put(this.clientSystemTitle, 0, Math.min(this.clientSystemTitle.length, 8)); // Ensure 8 bytes
        ivBuffer.putInt((int) this.clientInvocationCounter); // Ensure 4 bytes
        this.clientInvocationCounter++; // CRITICAL: Increment for next use
        return ivBuffer.array();
    }

    private byte[] getNextServerIv(long frameCounterFromMeter) {
        // IV for GCM: Server System Title (8 bytes) + Server Invocation Counter (4 bytes)
        // The frameCounterFromMeter should be extracted from the secured APDU header
        ByteBuffer ivBuffer = ByteBuffer.allocate(GCM_IV_LENGTH_BYTES);
        ivBuffer.put(this.serverSystemTitle, 0, Math.min(this.serverSystemTitle.length, 8));
        ivBuffer.putInt((int) frameCounterFromMeter);
        // It's good practice to also track the expected server invocation counter
        // to detect replay attacks or out-of-order messages, if the protocol supports it.
        return ivBuffer.array();
    }

    /**
     * Encrypts an APDU using AES-GCM with the session encryption key.
     *
     * @param plaintextApdu The APDU to encrypt.
     * @param encryptionKey The symmetric key to use for encryption (e.g., session key).
     * @param systemTitle   The system title of the sender (e.g., client's system title).
     * @param frameCounter  The current frame/invocation counter for this sender.
     * @return The encrypted APDU (ciphertext + GCM tag), or null on failure.
     */
    public byte[] encryptApdu(byte[] plaintextApdu, SecretKey encryptionKey, byte[] systemTitle, long frameCounter) {
        if (encryptionKey == null || plaintextApdu == null || systemTitle == null) return null;

        try {
            // Construct IV: System Title (8 bytes) + Frame Counter (4 bytes)
            ByteBuffer ivBuffer = ByteBuffer.allocate(GCM_IV_LENGTH_BYTES);
            // Ensure systemTitle is padded or truncated to 8 bytes if necessary.
            // This is a simplified example; DLMS spec might have precise padding rules.
            byte[] effectiveSystemTitle = new byte[8];
            System.arraycopy(systemTitle, 0, effectiveSystemTitle, 0, Math.min(systemTitle.length, 8));
            ivBuffer.put(effectiveSystemTitle);
            ivBuffer.putInt((int) frameCounter); // Low 4 bytes of the counter
            byte[] iv = ivBuffer.array();

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, gcmParameterSpec);

            // Optional: Add Authenticated Additional Data (AAD) if required by your DLMS security policy
            // byte[] aad = ... ; // e.g., security header parts that are authenticated but not encrypted
            // cipher.updateAAD(aad);

            return cipher.doFinal(plaintextApdu); // Contains ciphertext + authentication tag
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Decrypts an APDU using AES-GCM with the session encryption key.
     *
     * @param securedApdu   The secured APDU (ciphertext + GCM tag).
     * @param decryptionKey The symmetric key to use for decryption.
     * @param systemTitle   The system title of the sender (e.g., server's system title).
     * @param frameCounter  The frame/invocation counter from the secured APDU header.
     * @return The decrypted APDU (plaintext), or null on failure (e.g., tag mismatch).
     */
    public byte[] decryptApdu(byte[] securedApdu, SecretKey decryptionKey, byte[] systemTitle, long frameCounter) {
        if (decryptionKey == null || securedApdu == null || systemTitle == null) return null;

        try {
            // Construct IV: System Title (8 bytes) + Frame Counter (4 bytes)
            ByteBuffer ivBuffer = ByteBuffer.allocate(GCM_IV_LENGTH_BYTES);
            byte[] effectiveSystemTitle = new byte[8];
            System.arraycopy(systemTitle, 0, effectiveSystemTitle, 0, Math.min(systemTitle.length, 8));
            ivBuffer.put(effectiveSystemTitle);
            ivBuffer.putInt((int) frameCounter);
            byte[] iv = ivBuffer.array();

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv);
            cipher.init(Cipher.DECRYPT_MODE, decryptionKey, gcmParameterSpec);

            // Optional: Add Authenticated Additional Data (AAD) if used during encryption
            // byte[] aad = ... ; // Must be the same AAD as used for encryption
            // cipher.updateAAD(aad);

            return cipher.doFinal(securedApdu);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 InvalidAlgorithmParameterException | IllegalBlockSizeException |
                 BadPaddingException e) {
            // BadPaddingException or AEADBadTagException (subclass of BadPaddingException for GCM)
            // often indicates authentication failure (tag mismatch).
            e.printStackTrace();
            return null;
        }
    }


    // --- Methods to manage session keys and counters ---

    public SecretKey getSessionEncryptionKey() {
        return this.sessionEncryptionKey;
    }

    public void setSessionEncryptionKey(SecretKey key) {
        this.sessionEncryptionKey = key;
    }

    public long getNextClientInvocationCounterAndIncrement() {
        long currentCounter = this.clientInvocationCounter;
        this.clientInvocationCounter++;
        return currentCounter;
    }

    // You might also need methods to update/validate server invocation counter
    // based on received messages to prevent replay attacks.

    /**
     * Resets security context (e.g., on disconnect or failed authentication)
     */
    public void resetSecurityContext() {
        this.sessionEncryptionKey = null;
        this.sessionAuthenticationKey = null;
        this.clientInvocationCounter = 0; // Or other initial value
        // Reset other relevant state
    }
}
