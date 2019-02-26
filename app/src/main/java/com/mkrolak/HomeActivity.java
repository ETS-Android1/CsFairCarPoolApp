package com.mkrolak;

import android.app.ActivityOptions;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.Locale;


public class HomeActivity extends FragmentActivity {

    private int[] LAYOUT_ARRAY;
    private String[] LAYOUT_TAGS;

    private TabLayout tabLayout;

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    public String RIDER_REFERENCE = "RiderReference";

    FirebaseDatabase database;
    FirebaseAuth mAuth;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth.getInstance();
        FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_home);





        LAYOUT_ARRAY = new int[]{R.layout.fragment_rider,R.layout.fragment_profile,R.layout.fragment_driver};
        LAYOUT_TAGS = new String[]{"Rider","Profile","Driver"};
        tabLayout = findViewById(R.id.tabLayout);

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),LAYOUT_ARRAY);
        mPager.setAdapter(mPagerAdapter);


        tabLayout.setupWithViewPager(mPager);
        for(int i = 0; i < LAYOUT_ARRAY.length; i++){
            tabLayout.getTabAt(i).setText(LAYOUT_TAGS[i]);

        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        mPager.setCurrentItem(1);

        ((LoginFragment)mPagerAdapter.getItem(1)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                ((ImageView)mPagerAdapter.getItem(1).getView().findViewById(R.id.profilePic)).setImageResource(ProfilePictures.getPictureDrawableFromUri(mAuth.getCurrentUser().getPhotoUrl()));
                ((TextView)findViewById(R.id.username)).setText(mAuth.getCurrentUser().getDisplayName());
                ((TextView)findViewById(R.id.email)).setText(mAuth.getCurrentUser().getEmail());
            }
        });

        ((LoginFragment)mPagerAdapter.getItem(2)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                //TODO This might break the code because adding more and more listeners is bad mkay. Just trying to see if it works at all mkay.
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                            data.getValue();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });



    }


    public void goToEditProfile(View v){
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, findViewById(R.id.profilePic), "profilePic");

        startActivity(new Intent(HomeActivity.this,EditProfileActivity.class),options.toBundle());
    }

    public void logout(View v){
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference(RIDER_REFERENCE);


    }

    public void createPickUpRequest(View v){
        Geocoder geo = new Geocoder(this,Locale.getDefault());
        View riderActivity = mPagerAdapter.getItem(0).getView();
        Address address;

        try {
            address = geo.getFromLocationName(((EditText)riderActivity.findViewById(R.id.pickUpAddress)).getText().toString(),5).get(0);
            String time = ((EditText)riderActivity.findViewById(R.id.arrivalTime)).getText().toString();
            int numberOfPeople = Integer.parseInt(((EditText)riderActivity.findViewById(R.id.numberOfPeople)).getText().toString());

            databaseReference.push().setValue(new PickUpRequest(time,mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getPhotoUrl().toString(),numberOfPeople,(float)address.getLatitude(),(float)address.getLongitude()));

        } catch (IOException e) {
            e.printStackTrace();
        }



    }


}
