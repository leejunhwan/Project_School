package com.kakao.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RestUtil {

    public String get(String strUrl) {
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(strUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);
            con.setReadTimeout(10000);
            con.setRequestMethod("GET");
            con.setDoOutput(false);

            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), "utf-8"));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                br.close();
            } else {
                return null;
            }

        } catch (Exception e) {
            return null;
        }

        return sb.toString();
    }
}
