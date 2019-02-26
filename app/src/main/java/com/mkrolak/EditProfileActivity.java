package com.mkrolak;


import android.graphics.drawable.Drawable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.google.firebase.auth.FirebaseAuth;

public class EditProfileActivity extends FragmentActivity {

    private FirebaseAuth mAuth;

    private NonSwipingPagerView mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;

    private int[] LAYOUT_ARRAY;

    //TODO fix the fragment that doesnt display the images in the table view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();

        LAYOUT_ARRAY = new int[]{R.layout.fragment_edit_profile,R.layout.fragment_profile_pictures};

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
                    image.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
                    image.setMaxHeight(150);
                    image.setMaxWidth(150);
                    image.setImageResource(ProfilePictures.LIST_OF_DRAWABLES[i]);

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

    public void onPicturePress(View v){
        mPager.setCurrentItem(1);

    }

    public void resetPassword(View v){

        mAuth.sendPasswordResetEmail(mAuth.getCurrentUser().getEmail());
    }

}
