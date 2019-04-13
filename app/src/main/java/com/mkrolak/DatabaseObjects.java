package com.mkrolak;

public class DatabaseObjects {
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


    public class DatabaseUser{
        String color;
        String photoUri;

        public DatabaseUser(String pColor, String uri){
            color = pColor;
            photoUri = uri;
        }

        public DatabaseUser(){
            color="#FFFFFF";
            photoUri = "";
        }
    }

}
