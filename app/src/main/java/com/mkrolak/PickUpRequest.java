package com.mkrolak;

public class PickUpRequest{
    String time;
    String name;
    int numberOfPeople;
    float latOfPerson;
    float lonOfPerson;

    public PickUpRequest(String t, String n, int num, float lat, float lon){
        time = t;
        name = n;
        numberOfPeople=num;
        latOfPerson=lat;
        lonOfPerson=lon;
    }

    public PickUpRequest(){
        time = "";
        name = "";
        numberOfPeople=0;
        latOfPerson=0;
        lonOfPerson=0;
    }
}