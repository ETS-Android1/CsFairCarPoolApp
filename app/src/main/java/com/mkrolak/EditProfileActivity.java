package com.mkrolak;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
    }


    public void resetPassword(View v){
        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());

    }
}
