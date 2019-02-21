package com.mkrolak;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class LoginFragment extends Fragment {
    protected int mLayoutId = 0;
    protected EditText mRootView;

    String s = "";
    @SuppressLint("ValidFragment")
    public LoginFragment(int layoutId){
        super();
        mLayoutId =layoutId;

    }

    public LoginFragment(){
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(mLayoutId, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // save views as variables in this method
        // "view" is the one returned from onCreateView
        if(view.findViewById(R.id.profilePic)!=null){
            ((HomeActivity)getActivity()).setProfilePic();
        }
        mRootView = view.findViewById(R.id.info);
    }



    public void saveInfo(){
        if(mRootView!=null) s =  mRootView.getText().toString();
    }

    public String getInfo(){


        if(getView()==null){
            return s;
        }else{
            return mRootView.getText().toString();
        }

    }



    public void setErrorText(String string){
        ((TextView)getView().findViewById(R.id.error)).setText(string);
    }


}
