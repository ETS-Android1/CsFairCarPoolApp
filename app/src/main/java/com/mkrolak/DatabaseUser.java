package com.mkrolak;


public class DatabaseUser{
    String color;
    int photoUri;

    public DatabaseUser(String pColor, int uri){
        color = pColor;
        photoUri = uri;
    }

    public DatabaseUser(){
        color="0,0,0";
        photoUri = 0;
    }

    public String getColorInHex(){
        String hexColor = "#";
        for(String i : color.split(",")){
            hexColor+=((Integer.toHexString(Integer.parseInt(i)).length()!=2)?"0":"")+Integer.toHexString(Integer.parseInt(i));
        }
        return hexColor.toUpperCase();
    }
}
