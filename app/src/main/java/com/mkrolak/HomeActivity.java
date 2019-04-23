package com.mkrolak;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeActivity extends FragmentActivity {

    private int[] LAYOUT_ARRAY;
    private int[] LAYOUT_TAGS;
    private float[] currentLocation = {0,0};

    private TabLayout tabLayout;

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private FusedLocationProviderClient fusedLocationClient;

    public static final String RIDER_REFERENCE = "RiderReference";
    public static final String REQUEST_REFERENCE = "GetRiderReference";

    FirebaseDatabase database;
    FirebaseAuth mAuth;
    FirebaseFunctions functions;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseFunctions.getInstance();
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_home);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);





        LAYOUT_ARRAY = new int[]{R.layout.fragment_rider,R.layout.fragment_profile,R.layout.fragment_driver};
        LAYOUT_TAGS = new int[]{R.drawable.taxi,R.drawable.account,R.drawable.car};
        tabLayout = findViewById(R.id.tabLayout);

        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),LAYOUT_ARRAY);
        mPager.setAdapter(mPagerAdapter);


        tabLayout.setupWithViewPager(mPager);
        for(int i = 0; i < LAYOUT_ARRAY.length; i++){
            tabLayout.getTabAt(i).setIcon(LAYOUT_TAGS[i]);

        }
        mPager.setCurrentItem(1);


        ((LoginFragment)mPagerAdapter.getItem(0)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                //TODO: Check for active pickUpRequest.
            }
        });

        ((LoginFragment)mPagerAdapter.getItem(1)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                ((ImageView)findViewById(R.id.profilePic)).setImageResource(ProfilePictures.getPictureDrawableFromUri(mAuth.getCurrentUser().getPhotoUrl()));
                ((TextView)findViewById(R.id.username)).setText(mAuth.getCurrentUser().getDisplayName());
                ((TextView)findViewById(R.id.email)).setText(mAuth.getCurrentUser().getEmail());
            }
        });

        ((LoginFragment)mPagerAdapter.getItem(2)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(final View v) {
                //TODO This might break the code because adding more and more listeners is bad mkay. Just trying to see if it works at all mkay.
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(final DataSnapshot data : dataSnapshot.getChildren()){
                            final PickUpRequest p = data.getValue(PickUpRequest.class);
                            LinearLayout l = v.findViewById(R.id.searchLayout);

                            CardView cardView = new CardView(HomeActivity.this);
                            LinearLayout linearLayoutInside = new LinearLayout(HomeActivity.this);
                            final ImageView profilePic = new ImageView(HomeActivity.this);
                            TextView time = new TextView(HomeActivity.this);
                            final TextView extraTime = new TextView(HomeActivity.this);

                            profilePic.setLayoutParams(new LinearLayout.LayoutParams(200,200));

                            cardView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

                            linearLayoutInside.setLayoutParams(new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.MATCH_PARENT));


                            time.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                            extraTime.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT,1.0f));


                            database.getReference(getString(R.string.USERS_DATABASE_REFERENCE)).child(p.name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()){
                                        DatabaseUser user = dataSnapshot1.getValue(DatabaseUser.class);
                                        profilePic.setImageResource(ProfilePictures.getPictureDrawableFromInt(user.photoUri));
                                        profilePic.setBackgroundColor(Color.parseColor(user.getColorInHex()));
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                            linearLayoutInside.setBackgroundColor(ContextCompat.getColor(HomeActivity.this,R.color.colorPrimary));



                            time.setTextAppearance(R.style.infoTextView);
                            extraTime.setTextAppearance(R.style.infoTextView);

                            time.setBackgroundColor(Color.parseColor("#00ff0000"));
                            extraTime.setBackgroundColor(Color.parseColor("#00ff0000"));

                            extraTime.setGravity(Gravity.RIGHT);


                            ViewGroup.MarginLayoutParams cardViewMarginParams = (ViewGroup.MarginLayoutParams) cardView.getLayoutParams();
                            cardViewMarginParams.setMargins(30,30,30,30);
                            cardView.requestLayout();

                            time.setTextSize(50);
                            extraTime.setTextSize(50);


                            time.setText(p.time);


                            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                                fusedLocationClient.getLastLocation()
                                        .addOnSuccessListener(HomeActivity.this, new OnSuccessListener<Location>() {
                                            @Override
                                            public void onSuccess(Location location) {
                                                // Got last known location. In some rare situations this can be null.
                                                if (location != null) {
                                                    currentLocation = new float[]{(float)location.getLatitude(),(float)location.getLongitude()};

                                                    Task<String> timeTask = getExtraTime(p.latOfPerson,p.lonOfPerson);
                                                    if(timeTask!=null){
                                                        timeTask.addOnCompleteListener(new OnCompleteListener<String>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<String> task) {
                                                                if(task.isSuccessful()){
                                                                    String[] s = task.getResult().split(",");
                                                                    extraTime.setText(""+((-Integer.parseInt(s[1])+Integer.parseInt(s[0])+Integer.parseInt(s[3]))/60)+" Minutes Extra");
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            }
                                        });
                            }







                            linearLayoutInside.setOrientation(LinearLayout.HORIZONTAL);


                            linearLayoutInside.addView(profilePic,0);
                            linearLayoutInside.addView(time,1);
                            linearLayoutInside.addView(extraTime,2);

                            cardView.addView(linearLayoutInside);

                            cardView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    startActivity(new Intent(HomeActivity.this, RequestActivity.class).putExtra(REQUEST_REFERENCE,data.getKey()));
                                }
                            });

                            l.addView(cardView);



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

        functions = FirebaseFunctions.getInstance();
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

            databaseReference.push().setValue(new PickUpRequest(time,mAuth.getCurrentUser().getDisplayName(),numberOfPeople,(float)address.getLatitude(),(float)address.getLongitude()));

        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    private Task<String> getExtraTime(float tempLat, float tempLon){
        if(currentLocation[0]!=currentLocation[1]){
            Map<String, Object> data = new HashMap<>();
            data.put("destination", ""+tempLat+","+tempLon);
            data.put("location", ""+currentLocation[0]+","+currentLocation[1]);
            data.put("post", true);

            return functions
                    .getHttpsCallable("getExtraTime")
                    .call(data)
                    .continueWith(new Continuation<HttpsCallableResult, String>() {
                        @Override
                        public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                            // This continuation runs on either success or failure, but if the task
                            // has failed then getResult() will throw an Exception which will be
                            // propagated down.
                            String result =(String) ((Map) task.getResult().getData()).get("result");

                            return result;
                        }
                    });
        }
        return null;
    }


}
