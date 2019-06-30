package com.example.objects;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.example.MainActivity;
import com.example.utility.UserStatus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Points {
    private String creator = "Charlie";
    private String date;
    private String latitude;
    private String longitude;
    private int type;
    private int id;
    private String locationName;
    private String description;
    private int upvotes;
    private ArrayList<String> photoUrls;

    public Points(String latitude, String longitude, int id, String locationName, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.id = id;
        this.description = description;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.date = dt.format(date);
        photoUrls = new ArrayList<>();

        this.creator = UserStatus.getUserName();
    }

    public Points(String toDecode) {
        String[] pt = toDecode.split("$");
        this.creator = pt[0];
        this.date = pt[1];
        this.latitude = pt[2];
        this.longitude = pt[3];
        this.locationName = pt[4];
        this.description = pt[5];
        this.type = Integer.parseInt(pt[6]);
        photoUrls = new ArrayList<>();
        for (int i = 7; i < pt.length; i++) {
            if (pt[i] != null) {
                photoUrls.add(pt[i]);
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setPhotoUrls(String paths) {
        String[] pathList = paths.split(";");
        for (int i = 0; i < pathList.length; i++) {
            photoUrls.add(paths);
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }

    public String getPhotoPath() {
        String base = "";
        for (String url: photoUrls) {
            base += url + ";";
        }
        return base;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    @Override
    public String toString() {
        String base = creator + "$" + date + "$" + latitude + "$" + longitude + "$" + locationName + "$" + description + "$" + type;
        for (String url: photoUrls) base += url + "$";
        return base;
    }
}
