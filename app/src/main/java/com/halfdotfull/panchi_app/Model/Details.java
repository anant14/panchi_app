package com.halfdotfull.panchi_app.Model;

/**
 * Created by anant bansal on 6/4/2017.
 */

public class Details {

    String formatted_address;
    String formatted_phone_number;

    public Details(String formatted_phone_number, String formatted_address) {
        this.formatted_phone_number = formatted_phone_number;
        this.formatted_address = formatted_address;
    }

    public String getFormatted_address() {
        return formatted_address;
    }

    public void setFormatted_address(String formatted_address) {
        this.formatted_address = formatted_address;
    }

    public String getFormatted_phone_number() {
        return formatted_phone_number;
    }

    public void setFormatted_phone_number(String formatted_phone_number) {
        this.formatted_phone_number = formatted_phone_number;
    }
}
