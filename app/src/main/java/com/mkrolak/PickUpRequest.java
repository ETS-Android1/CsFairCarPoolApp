package com.mkrolak;

import android.net.Uri;

public class PickUpRequest {
    protected String time;
    protected String name;
    protected Uri photoUri;
    protected int numberOfPeople;
    protected float latOfPerson;
    protected float lonOfPerson;


    public PickUpRequest(){
        time = "";
        name = "";
        photoUri = Uri.parse("");
        numberOfPeople = 0;
        latOfPerson = 0;
        lonOfPerson = 0;
    }

    public PickUpRequest(String t, String n, Uri uri, int num, float lat, float lon){
        time = t;
        name = n;
        photoUri = uri;
        numberOfPeople=num;
        latOfPerson=lat;
        lonOfPerson=lon;
    }


}
