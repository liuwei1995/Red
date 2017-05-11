package com.liuwei1995.red.fragment.iview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by liuwei on 2017/5/11
 */

public interface FragmentIView extends View.OnClickListener{
    
     void onAttach(Context context);

     void onCreate(@Nullable Bundle savedInstanceState);

     View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    
     void onActivityCreated(@Nullable Bundle savedInstanceState) ;

    
     void onStart();

     void onPause();

     void onStop();

     void onDestroyView();

     void onDestroy();

     void onDetach();

     void onAttach(Activity activity);

     void onActivityResult(int requestCode, int resultCode, Intent data);

     void onSaveInstanceState(Bundle outState);

     void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

   
     void onAttachFragment(Fragment childFragment);
}
