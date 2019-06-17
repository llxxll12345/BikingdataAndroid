package com.example.objects;

import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.example.MainActivity;

import java.util.ArrayList;

public class Points {
    private String latitude;
    private String longitude;
    private int id;
    private String locationName;
    private String description;
    private int upvotes;
    private ArrayList<String> Tags;
    private ArrayList<String> photoUrls;

    public Points(String latitude, String longitude, int id, String locationName, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationName = locationName;
        this.id = id;
        this.description = description;

        Tags = new ArrayList<>();
        photoUrls = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }
}
