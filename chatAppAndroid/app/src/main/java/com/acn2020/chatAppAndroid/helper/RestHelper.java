package com.acn2020.chatAppAndroid.helper;

import com.google.gson.Gson;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestHelper {

    public static Object restCall(String uri, String method, Object data, Class returnClass, String contentType) throws IOException, JSONException, InterruptedException {
        URL url = new URL(uri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod(method);
        connection.setRequestProperty("Accept", "application/json");

        if(contentType == null) {
            connection.setRequestProperty("Content-Type", "application/json");
        } else {
            connection.setRequestProperty("Content-Type", contentType);
        }

        connection.setDoInput(true);

        Gson gson = new Gson();

        if(data != null) {
            connection.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            if(contentType != null && contentType.equals("application/x-www-form-urlencoded")) {
                out.write(data.toString());
            }
            else {
                String json = gson.toJson(data);
                out.write(json);
            }
            out.flush();
        }

        InputStream in = connection.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Object responseObject = gson.fromJson(reader, returnClass);
        connection.disconnect();

        return responseObject;
    }
}
