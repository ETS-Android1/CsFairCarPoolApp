package com.mkrolak;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {


    public ArrayList<Fragment> fragmentList;
    public ScreenSlidePagerAdapter(FragmentManager fm, int[] layoutArray, int slides) {
        super(fm);
        fragmentList = new ArrayList<>();
        for(int i =0; i < slides;i++){
            fragmentList.add( new LoginFragment(layoutArray[i]));
        }
    }

    public ScreenSlidePagerAdapter(FragmentManager fm, int[] layoutArray) {
        this(fm,layoutArray,layoutArray.length);

    }


    @Override
    public Fragment getItem(int position) {

        return fragmentList.get(position);
    }

    public void addElement(Fragment f){
        fragmentList.add(f);
        this.notifyDataSetChanged();

    }



    @Override
    public int getCount() {
        return fragmentList.size();
    }




}