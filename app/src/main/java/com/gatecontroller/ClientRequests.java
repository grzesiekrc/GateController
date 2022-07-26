package com.gatecontroller;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ClientRequests {
    private static final String TAG = "ClientRequests";
    private final String host;

    public ClientRequests(String httpAddress) {
        host = httpAddress;
    }

    public String getData(String urlstring) throws IOException {

        URL url = new URL(host + urlstring);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int code = urlConnection.getResponseCode();

        Log.d(TAG, "get response code " + code);

        return getDataFromInputStream(urlConnection);
    }

    public String postData(String urlstring, String data) throws IOException {
        URL url = new URL(host + urlstring);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "text/plain");
        urlConnection.setDoOutput(true);

        Log.d(TAG, "post data " + data);

        OutputStream os = urlConnection.getOutputStream();
        os.write(data.getBytes());
        os.flush();
        os.close();

        int code = urlConnection.getResponseCode();

        Log.d(TAG, "post response code " + code);

        return getDataFromInputStream(urlConnection);
    }

    private String getDataFromInputStream(HttpURLConnection urlConnection) throws IOException {
        BufferedReader rd = new BufferedReader(new InputStreamReader( urlConnection.getInputStream()));
        StringBuilder outString = new StringBuilder();
        String line;
        while((line = rd.readLine()) != null) {
            if(outString.length() > 0) {
                outString.append('\n');
            }
            outString.append(line);
        }
        Log.d(TAG,outString.toString());
        return outString.toString();
    }
}
