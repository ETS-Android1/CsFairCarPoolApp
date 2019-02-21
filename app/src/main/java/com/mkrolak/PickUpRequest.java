package com.mkrolak;

public class PickUpRequest {
    protected String time;
    protected String name;
    protected String photoUri;
    protected int numberOfPeople;
    protected float latOfPerson;
    protected float lonOfPerson;


    public PickUpRequest(){
        time = "";
        name = "";
        photoUri = "";
        numberOfPeople = 0;
        latOfPerson = 0;
        lonOfPerson = 0;
    }
}
