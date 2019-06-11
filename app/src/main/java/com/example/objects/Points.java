package com.example.objects;

import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

public class Points {
    private LatLng point;
    private int id;
    private String locationName;
    private String description;
    private int upvotes;
    private ArrayList<String> Tags;
    private ArrayList<String> photoUrls;

    public Points(LatLng point, int id, String locationName, String description) {
        this.point = point;
        this.locationName = locationName;
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public LatLng getPoint() {
        return point;
    }

    public String getDescription() {
        return description;
    }

    public String getLocationName() {
        return locationName;
    }
}
