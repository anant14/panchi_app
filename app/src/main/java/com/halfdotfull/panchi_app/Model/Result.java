package com.halfdotfull.panchi_app.Model;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class Result {

    String name;
    String place_id;

    public String getPlace_id() {
        return place_id;
    }

    public Result(String name, String place_id) {
        this.name = name;
        this.place_id = place_id;
    }

    public String getName() {
        return name;
    }
}
