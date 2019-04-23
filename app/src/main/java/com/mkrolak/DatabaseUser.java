package com.mkrolak;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
