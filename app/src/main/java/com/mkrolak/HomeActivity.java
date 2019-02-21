package com.mkrolak;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;


public class HomeActivity extends FragmentActivity {

    public int[] listOfDrawables;
    private int[] LAYOUT_ARRAY;
    private String[] LAYOUT_TAGS;

    private TabLayout tabLayout;

    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        listOfDrawables = new int[]{R.drawable.user0,R.drawable.user1,R.drawable.user2,R.drawable.user3,R.drawable.user4,R.drawable.user5,R.drawable.user6,R.drawable.user7,R.drawable.user8,R.drawable.user9,R.drawable.user10,R.drawable.user11,R.drawable.user12,R.drawable.user13,R.drawable.user14,R.drawable.user15,R.drawable.user16,R.drawable.user17,R.drawable.user18,R.drawable.user19,R.drawable.user20,R.drawable.user21,R.drawable.user22,R.drawable.user23};

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


    }

    public void logout(View v){
        mAuth.signOut();
        startActivity(new Intent(HomeActivity.this,MainActivity.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();


    }

    public void createListOfPickUps(){

    }

    //TODO:probably make return profile picture. Should change in fragment class maybe. IDK
    public void setProfilePic(){
        ((ImageView)mPagerAdapter.getItem(1).getView().findViewById(R.id.profilePic)).setImageResource(listOfDrawables[Integer.parseInt(mAuth.getCurrentUser().getPhotoUrl().toString().split("user")[1].replace(".png","").trim())]);
    }



}
