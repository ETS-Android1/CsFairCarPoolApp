package com.mkrolak;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;

public class EditProfileActivity extends FragmentActivity {

    private FirebaseAuth mAuth;

    private NonSwipingPagerView mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;


    private int[] LAYOUT_ARRAY;

    private Uri tempImageUri;

    //TODO fix the fragment that doesnt display the images in the table view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        LAYOUT_ARRAY = new int[]{R.layout.fragment_edit_profile,R.layout.fragment_profile_pictures};

        tempImageUri = mAuth.getCurrentUser().getPhotoUrl();
        mPager = findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),LAYOUT_ARRAY);
        mPager.setAdapter(mPagerAdapter);
        mPager.setPagingEnabled(false);

        ((LoginFragment)mPagerAdapter.getItem(0)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                ((ImageView)findViewById(R.id.profileEdit)).setImageResource(ProfilePictures.getPictureDrawableFromUri(mAuth.getCurrentUser().getPhotoUrl()));
            }
        });

        ((LoginFragment)mPagerAdapter.getItem(1)).setOnStartListener(new OnStartListener() {
            @Override
            public void onStart(View v) {
                TableLayout layout = findViewById(R.id.tableView);
                TableRow tableRow = null;
                for(int i=0;i<ProfilePictures.LIST_OF_DRAWABLES.length;i++) {
                    if(i%3==0){
                        tableRow = new TableRow(EditProfileActivity.this);
                        tableRow.setLayoutParams(new TableLayout.LayoutParams(TabLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        layout.addView(tableRow,i/3);
                    }
                    ImageView image = new ImageView(EditProfileActivity.this);
                    image.setLayoutParams(new TableRow.LayoutParams(360, 360,1f));
                    image.setImageResource(ProfilePictures.LIST_OF_DRAWABLES[i]);
                    image.setId(i);
                    image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ((ImageView)mPagerAdapter.getItem(0).getView().findViewById(R.id.profileEdit)).setImageDrawable(((ImageView)view).getDrawable());
                            tempImageUri = Uri.parse("android.resource://"+R.class.getPackage().getName()+"/user" + view.getId());
                            mPager.setCurrentItem(0);
                        }
                    });


                    tableRow.addView(image);

                }

            }
        });

    }

    @Override
    public void onBackPressed(){
        if(mPager.getCurrentItem() != 0){
            mPager.setCurrentItem(mPager.getCurrentItem()-1);

        }else{
            super.onBackPressed();
        }
    }

    public void onApplyButtonPressed(View v){
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(tempImageUri).build();
        mAuth.getCurrentUser().updateProfile(userProfileChangeRequest);
        Intent homeActivity = new Intent(EditProfileActivity.this,HomeActivity.class);
        homeActivity.putExtra("PIC",tempImageUri);
        startActivity(homeActivity);
    }

    public void onPicturePress(View v){
        mPager.setCurrentItem(1);

    }

    public void resetPassword(View v){

        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());
    }

}
