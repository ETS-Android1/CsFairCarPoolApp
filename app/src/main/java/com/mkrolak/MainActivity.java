package com.mkrolak;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;


public class MainActivity extends FragmentActivity {
    private int[] LAYOUT_ARRAY;

    private int maxSlide = 1;

    private boolean signup = false;

    FirebaseAuth mAuth;

    public ViewPager mPager;
    public ScreenSlidePagerAdapter mPagerAdapter;
    public Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        FirebaseAuth.getInstance();

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
                ((LoginFragment)((ScreenSlidePagerAdapter)mPagerAdapter).getItem(mPager.getCurrentItem())).saveInfo();
                ((ScreenSlidePagerAdapter)mPagerAdapter).addElement(new LoginFragment(LAYOUT_ARRAY[maxSlide]));
                maxSlide++;
            }
            mPager.setCurrentItem(mPager.getCurrentItem()+1);

        }

    }

    public void signUp(View v){
        signup = true;
        continueToNext(v);
    }

    //TODO:Make more clear on where stuff went wrong.
    public void continueSignUp(View v){
        String email = ((((LoginFragment)((ScreenSlidePagerAdapter)mPagerAdapter).getItem(1)).getInfo()));
        String password = (((LoginFragment)((ScreenSlidePagerAdapter)mPagerAdapter).getItem(2)).getInfo());
        if(email.length() == 0 || password.length() == 0)return;//TODO:make say please fill in.
        email+="@cps.edu";


        if(signup) {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {


                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName((((LoginFragment)(mPagerAdapter).getItem(1)).getInfo())).setPhotoUri( Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + resources.getResourcePackageName(R.drawable.user0) + '/' + resources.getResourceTypeName(R.drawable.user0) + "/user" + (int)(Math.random()*24) +".png" )).build();
                        mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
                        verifyEmail();


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
                        ((LoginFragment) ((ScreenSlidePagerAdapter) (mPagerAdapter)).getItem(2)).setErrorText(task.getException().toString());
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
                    }
                }else{
                    ((LoginFragment)mPagerAdapter.getItem(mPager.getCurrentItem())).setErrorText(task.getException().toString());
                }

            }
        });

    }
}
