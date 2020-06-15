/*
 * MIT License
 *
 * Copyright (c) 2018 Yuriy Budiyev [yuriy.budiyev@yandex.ru]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.acn2020.chatAppAndroid;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acn2020.chatAppAndroid.base.BaseActivity;
import com.acn2020.chatAppAndroid.codescanner.CodeScannerActivity;
import com.acn2020.chatAppAndroid.cryptography.AES;
import com.acn2020.chatAppAndroid.cryptography.Decryptor;
import com.acn2020.chatAppAndroid.cryptography.Encryptor;
import com.acn2020.chatAppAndroid.dto.Message;
import com.acn2020.chatAppAndroid.dto.ResponseDto;
import com.acn2020.chatAppAndroid.helper.ConnectionKeeper;
import com.acn2020.chatAppAndroid.helper.RestHelper;
import com.acn2020.chatAppAndroid.model.ClientConnection;

import org.json.JSONException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MainActivity extends BaseActivity {
    private ConnectionKeeper keeper;
    private Encryptor encryptor;
    private Decryptor decryptor;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CHAT_APP_ALIAS = "CHAT_APP_ALIAS";
    private ClientConnection connection;
    private SharedPreferences preferences;
    private static final String PREFERENCES = "ChatAppPreferences";
    private static final String KEY_CLIENT_ID = "key_client_id";
    private static final String KEY_IP_ADDRESS = "key_ip_address";
    private static final String KEY_HOST_NAME = "key_host_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferences = getSharedPreferences(PREFERENCES, 0);
        encryptor = new Encryptor(preferences);
        connection = null;
        keeper = ConnectionKeeper.getInstance();

        setContentView(R.layout.activity_main);
        findViewById(R.id.code_scanner)
                .setOnClickListener(v -> startActivityForResult(new Intent(this, CodeScannerActivity.class), 1));
        EditText message = (EditText)findViewById(R.id.message);

        // TODO: own thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            decryptor = new Decryptor();
            if(decryptor.containsAlias(CHAT_APP_ALIAS)) {
                String aesKey = decryptKey();
                if(aesKey != null) {
                    ClientConnection connection = new ClientConnection();
                    String stringFromSharedPrefs = this.preferences.getString(KEY_CLIENT_ID, null);
                    if(stringFromSharedPrefs != null) {
                        connection.setClientId(stringFromSharedPrefs);
                    }
                    stringFromSharedPrefs = preferences.getString(KEY_IP_ADDRESS, null);
                    if(stringFromSharedPrefs != null) {
                        connection.setIpAddress(stringFromSharedPrefs);
                    }
                    stringFromSharedPrefs = preferences.getString(KEY_HOST_NAME, null);
                    if(stringFromSharedPrefs != null) {
                        connection.setHostName(stringFromSharedPrefs);
                        ((TextView) findViewById(R.id.status)).setText("paired with " + connection.getHostName());
                    }
                    connection.setAesKey(aesKey);
                    keeper.setConnection(connection);
                }
            }
        } catch (CertificateException | NoSuchAlgorithmException | KeyStoreException |
                IOException e) {
            e.printStackTrace();
        }

        findViewById(R.id.sendMessage).setOnClickListener(v -> {
            if(connection != null) {
                sendMessage(message.getText().toString());
            } else {
                Toast.makeText(this, "Phone not paired", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.unpair).setOnClickListener(v -> {
            if(connection != null) {
                sendMessage("unpair:" + getDeviceName());
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(KEY_CLIENT_ID);
                editor.remove(KEY_IP_ADDRESS);
                editor.remove(KEY_HOST_NAME);
                editor.commit();
                connection = null;
                keeper.setConnection(null);
                encryptor.removeEncryption();
                ((TextView) findViewById(R.id.status)).setText("not paired yet");
            } else {
                Toast.makeText(this, "Phone not paired", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendMessage(String text) {
        try {
            connection = keeper.getConnection();
            byte[] encryptedMessage = AES.encryptCBC(text, connection.getAesKey());
            System.out.println("Sending encrypted message to client...");
            Message newMessage = new Message(connection.getClientId(), Base64.encodeToString(encryptedMessage, Base64.DEFAULT), new Date());
            ResponseDto response = (ResponseDto) RestHelper.restCall("http://192.168.8.100:3000/sendMessage", "POST", newMessage, ResponseDto.class, null);
            if(response.getStatus().equals(200)) {
                System.out.println("Server responded:");
                System.out.println("Message for client " + connection.getClientId());
                System.out.println("Encrypted Message: " + Base64.encodeToString(encryptedMessage, Base64.DEFAULT));
            } else {
                System.out.println("Error sending message to server...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ClientConnection connection = keeper.getConnection();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(KEY_CLIENT_ID, connection.getClientId());
                editor.putString(KEY_IP_ADDRESS, connection.getIpAddress());
                editor.putString(KEY_HOST_NAME, connection.getHostName());
                editor.commit();
                ((TextView) findViewById(R.id.status)).setText("paired with " + connection.getHostName());
                encryptKey(connection.getAesKey());
                sendMessage("pair:" + getDeviceName());
            }
            else{
                Toast.makeText(this, "Error reading QR Code!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String decryptKey() {
        if(encryptor.getEncryption() != null && encryptor.getIv() != null) {
            try {
                return decryptor.decryptData(CHAT_APP_ALIAS, encryptor.getEncryption(), encryptor.getIv());
            } catch (UnrecoverableEntryException | NoSuchAlgorithmException |
                    KeyStoreException | NoSuchPaddingException |
                    IOException | InvalidKeyException e) {
                Log.e(TAG, "decryptData() called with: " + e.getMessage(), e);
            } catch (IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void encryptKey(String text) {
        try {
            encryptor.encryptText(CHAT_APP_ALIAS, text);
            encryptor.storeEncryption();
        } catch (NoSuchAlgorithmException | NoSuchProviderException |
                IOException | NoSuchPaddingException | InvalidKeyException e) {
            Log.e(TAG, "onClick() called with: " + e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
}
