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
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.acn2020.chatAppAndroid.base.BaseActivity;
import com.acn2020.chatAppAndroid.codescanner.CodeScannerActivity;
import com.acn2020.chatAppAndroid.cryptography.AES;
import com.acn2020.chatAppAndroid.dto.Message;
import com.acn2020.chatAppAndroid.dto.ResponseDto;
import com.acn2020.chatAppAndroid.helper.ConnectionKeeper;
import com.acn2020.chatAppAndroid.helper.RestHelper;
import com.acn2020.chatAppAndroid.model.ClientConnection;

import org.json.JSONException;

import java.io.IOException;
import java.util.Date;

public class MainActivity extends BaseActivity {
    private ConnectionKeeper keeper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        keeper = ConnectionKeeper.getInstance();
        setContentView(R.layout.activity_main);
        findViewById(R.id.code_scanner)
                .setOnClickListener(v -> startActivityForResult(new Intent(this, CodeScannerActivity.class), 1));
        EditText message = (EditText)findViewById(R.id.message);

        // TODO: own thread
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        findViewById(R.id.sendMessage).setOnClickListener(v -> {
            try {
                ClientConnection connection = keeper.getLastConnection();
                byte[] encryptedMessage = AES.encryptCBC(message.getText().toString(), connection.getAesKey());
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
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ClientConnection connection = keeper.getLastConnection();
                ((TextView) findViewById(R.id.status)).setText("paired with " + connection.getHostName());
            }
            else{
                Toast.makeText(this, "Error reading QR Code!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
