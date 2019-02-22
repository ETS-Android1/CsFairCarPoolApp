package com.mkrolak;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class HomeActivity extends FragmentActivity {

    public static int[] LIST_OF_DRAWABLES = {0};
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
        setContentView(R.layout.activity_home);

        LIST_OF_DRAWABLES = new int[]{R.drawable.user0,R.drawable.user1,R.drawable.user2,R.drawable.user3,R.drawable.user4,R.drawable.user5,R.drawable.user6,R.drawable.user7,R.drawable.user8,R.drawable.user9,R.drawable.user10,R.drawable.user11,R.drawable.user12,R.drawable.user13,R.drawable.user14,R.drawable.user15,R.drawable.user16,R.drawable.user17,R.drawable.user18,R.drawable.user19,R.drawable.user20,R.drawable.user21,R.drawable.user22,R.drawable.user23};

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
                ((ImageView)mPagerAdapter.getItem(1).getView().findViewById(R.id.profilePic)).setImageResource(HomeActivity.getPictureDrawableFromUri(mAuth.getCurrentUser().getPhotoUrl()));
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
        Geocoder geo = new Geocoder(this);
        View riderActivity = mPagerAdapter.getItem(0).getView();
        Address address = null;
        try {
            address = geo.getFromLocationName(((EditText)riderActivity.findViewById(R.id.pickUpAddress)).getText().toString(),1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String time = ((EditText)riderActivity.findViewById(R.id.arrivalTime)).getText().toString();
        int numberOfPeople = Integer.parseInt(((EditText)riderActivity.findViewById(R.id.numberOfPeople)).getText().toString());

        databaseReference.push().setValue(new PickUpRequest(time,mAuth.getCurrentUser().getDisplayName(),mAuth.getCurrentUser().getPhotoUrl(),numberOfPeople,(float)address.getLatitude(),(float)address.getLongitude()));
    }

    public static int getPictureDrawableFromUri(Uri uri){
        return  LIST_OF_DRAWABLES[Integer.parseInt(uri.toString().split("user")[1].replace(".png","").trim())];
    }
}
