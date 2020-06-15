package com.acn2020.chatAppAndroid.cryptography;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Encryptor {

    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    private static final String KEY_ENCRYPTION = "key_encryption";
    private static final String KEY_IV = "key_iv";
    private SharedPreferences preferences;
    private byte[] encryption;
    private byte[] iv;

    public Encryptor(SharedPreferences preferences) {
        this.preferences = preferences;
        String encryptionFromSharedPrefs = this.preferences.getString(KEY_ENCRYPTION, null);
        if(encryptionFromSharedPrefs != null) {
            encryption = Base64.decode(encryptionFromSharedPrefs, Base64.DEFAULT);
        }
        String ivFromSharedPrefs = preferences.getString(KEY_IV, null);
        if(ivFromSharedPrefs != null) {
            iv = Base64.decode(ivFromSharedPrefs, Base64.DEFAULT);
        }
    }

    public byte[] encryptText(final String alias, final String textToEncrypt)
            throws NoSuchAlgorithmException,
            NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, IOException,
            InvalidAlgorithmParameterException, BadPaddingException,
            IllegalBlockSizeException {

        final Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(alias));

        iv = cipher.getIV();

        return (encryption = cipher.doFinal(textToEncrypt.getBytes("UTF-8")));
    }

    @NonNull
    private SecretKey getSecretKey(final String alias) throws NoSuchAlgorithmException,
            NoSuchProviderException, InvalidAlgorithmParameterException {

        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);

        keyGenerator.init(new KeyGenParameterSpec.Builder(alias,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build());

        return keyGenerator.generateKey();
    }

    public byte[] getEncryption() {
        return encryption;
    }

    public byte[] getIv() {
        return iv;
    }

    public void storeEncryption() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_ENCRYPTION, Base64.encodeToString(encryption, Base64.DEFAULT));
        editor.putString(KEY_IV, Base64.encodeToString(iv, Base64.DEFAULT));
        editor.commit();
    }

    public void removeEncryption() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(KEY_ENCRYPTION);
        editor.remove(KEY_IV);
        editor.commit();
    }
}
