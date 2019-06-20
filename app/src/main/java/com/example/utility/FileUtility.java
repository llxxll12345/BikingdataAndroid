package com.example.utility;

import android.content.Context;
import android.graphics.Point;
import android.os.Environment;
import android.util.Log;

import com.example.objects.Points;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class FileUtility {
    public static String formatData(ArrayList<Points> arr) {
        String base = "";
        for (Points point: arr) {
            base += point.toString() + "@";
        }
        return base;
    }

    public static ArrayList<Points> decodeData(String s) {
        ArrayList<Points> arr = new ArrayList<>();
        String[] pointData = s.split("@");
        for (String pointStr: pointData) {
            Points pt = new Points(pointStr);
            arr.add(pt);
        }
        return arr;
    }

    public static void saveFiletoLocal(Context context, String fileName, String content) {
        File file = new File(context.getFilesDir(), fileName);
        FileOutputStream outputStream;
        try {
            // passing mode_private makes it private to your app
            outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String readFilefromLocal(Context context, String fileName) {
        StringBuilder text = new StringBuilder();
        File file = new File(context.getFilesDir(), fileName);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
            return text.toString();
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicStorageDir(Context context, String fileName) {
        if (!isExternalStorageReadable() || !isExternalStorageWritable()) {
            Log.e("Err", "External storage unavailable");
            return null;
        }
        // Get the directory for the user's public pictures directory.
        File path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File file = new File(path, fileName);
        if (!file.mkdirs()) {
            Log.e("Err", "Directory not created");
        }
        return file;
    }

}
