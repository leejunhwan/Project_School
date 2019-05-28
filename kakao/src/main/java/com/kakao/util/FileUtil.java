package com.kakao.util;

import java.io.*;
import java.util.List;

public class FileUtil {

    public String getInputString(String path) {
        byte[ ] readBuffer = null;
        try {
            InputStream fileStream = FileUtil.class.getResourceAsStream(path);

            readBuffer = new byte[fileStream.available()];

            while (fileStream.read( readBuffer ) != -1){}

            fileStream.close(); //스트림 닫기
        } catch (Exception e) {
            e.getStackTrace();
        }

        return new String(readBuffer);
    }

    public void putOutputString(String path, List<String> resultList) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            for(String res : resultList) {
                out.write(res); out.newLine();
            }

            out.close();
        } catch (IOException e) {
        }
    }

    public void putString(String path, String str) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write(str);
            out.close();
        } catch (IOException e) {
        }
    }
}
