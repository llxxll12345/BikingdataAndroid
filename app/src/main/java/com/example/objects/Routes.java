package com.example.objects;

import java.util.ArrayList;

public class Routes {
    private int id;
    private String creator;
    private String routeName;
    private String description;
    private int upvotes;
    private ArrayList<String[]> route;
    private ArrayList<String> Tags;
    private ArrayList<String> photoUrls;

    private Routes(ArrayList<String[]> route) {
        this.route = route;
    }

    private Routes(String routeName, String description, String creator, ArrayList<String> photoUrls) {

    }
}
