package com.mkrolak;

import android.net.Uri;

public class ProfilePictures {
    public static int[] LIST_OF_DRAWABLES = new int[]{R.drawable.user0,R.drawable.user1,R.drawable.user2,R.drawable.user3,R.drawable.user4,R.drawable.user5,R.drawable.user6,R.drawable.user7,R.drawable.user8,R.drawable.user9,R.drawable.user10,R.drawable.user11,R.drawable.user12,R.drawable.user13,R.drawable.user14,R.drawable.user15,R.drawable.user16,R.drawable.user17,R.drawable.user18,R.drawable.user19,R.drawable.user20,R.drawable.user21,R.drawable.user22,R.drawable.user23};

    public static int getPictureDrawableFromUri(Uri uri){
        return  LIST_OF_DRAWABLES[Integer.parseInt(uri.toString().split("user")[1].replace(".png","").trim())];
    }
}
