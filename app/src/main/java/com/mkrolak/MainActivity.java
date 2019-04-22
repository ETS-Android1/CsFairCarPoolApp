package com.mkrolak;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;


public class MainActivity extends FragmentActivity {
    private int[] LAYOUT_ARRAY;

    private int maxSlide = 1;

    private boolean signup = false;

    FirebaseAuth mAuth;
    FirebaseDatabase database;

    public ViewPager mPager;
    public ScreenSlidePagerAdapter mPagerAdapter;
    public Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.clearInstancesForTest();
        FirebaseApp.initializeApp(this);
        FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }


        if(FirebaseAuth.getInstance().getCurrentUser()!=null && FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            startActivity(new Intent(MainActivity.this,HomeActivity.class));
        }

        setContentView(R.layout.activity_main);
        LAYOUT_ARRAY = new int[] {R.layout.fragment_new_user,R.layout.fragment_username,R.layout.fragment_password,R.layout.fragment_verify};


        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),LAYOUT_ARRAY,maxSlide);
        mPager.setAdapter(mPagerAdapter);

        resources =  this.getResources();



    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem() != 0){
            mPager.setCurrentItem(mPager.getCurrentItem()-1);

        }else{
            super.onBackPressed();
        }
    }

    public void continueToNext(View v){
        if(mPager.getCurrentItem()+1 != LAYOUT_ARRAY.length){
            if(mPager.getCurrentItem()+1 == maxSlide){
                ((LoginFragment)mPagerAdapter.getItem(mPager.getCurrentItem())).saveInfo();
                mPagerAdapter.addElement(new LoginFragment(LAYOUT_ARRAY[maxSlide]));
                maxSlide++;
            }
            mPager.setCurrentItem(mPager.getCurrentItem()+1);

        }

    }

    public void signUp(View v){
        signup = v.getId()!=R.id.old;
        continueToNext(v);
    }

    public void continueSignUp(final View v){
        String email = ((((LoginFragment)(mPagerAdapter).getItem(1)).getInfo()));
        String password = (((LoginFragment)(mPagerAdapter).getItem(2)).getInfo());
        if(email.length() == 0 || password.length() == 0)return;//TODO:make say please fill in.
        email+="@cps.edu";


        if(signup) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {


                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName((((LoginFragment)(mPagerAdapter).getItem(1)).getInfo())).build();
                        database.getReference(getString(R.string.USERS_DATABASE_REFERENCE)).child((((LoginFragment)(mPagerAdapter).getItem(1)).getInfo())).push().setValue(new DatabaseUser((""+(int)(256*Math.random())+","+(int)(256*Math.random())+","+(int)(256*Math.random())),(int)(Math.random()*24)));
                        mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                        verifyEmail();

                    }else if(task.getException().getClass().equals(FirebaseAuthUserCollisionException.class)){
                        //This if statement might not be secure TODO:CHECK
                        signup = false;
                        continueSignUp(v);
                    }
                }
            });
        }else {
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if (mAuth.getCurrentUser().isEmailVerified()) {
                            startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        } else {
                            verifyEmail();
                        }

                    } else {
                        ((LoginFragment) ((mPagerAdapter)).getItem(2)).setErrorText(task.getException().toString());
                    }
                }
            });
        }
    }

    public void verifyEmail(){

        continueToNext(null);

        mAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    ((TextView)(mPagerAdapter).getItem(3).getView().findViewById(R.id.verifyName)).setText("Please confirm the verification message that was sent to your cps email");
                    mPagerAdapter.getItem(mPager.getCurrentItem()).getView().findViewById(R.id.verifyButton).setVisibility(View.VISIBLE);

                }

            }
        });


    }

    public void isVerified(View v){
        mAuth.getCurrentUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(mAuth.getCurrentUser().isEmailVerified()){
                        startActivity(new Intent(MainActivity.this,HomeActivity.class));
                    }else{
                        ((LoginFragment)mPagerAdapter.getItem(mPager.getCurrentItem())).setErrorText("You have not verified your email address");
                        //TODO fix visibility
                        mPagerAdapter.getItem(mPager.getCurrentItem()).getView().findViewById(R.id.verifyButton).setVisibility(View.VISIBLE);
                    }
                }else{
                    ((LoginFragment)mPagerAdapter.getItem(mPager.getCurrentItem())).setErrorText(task.getException().toString());
                }

            }
        });

    }
}
