package com.liuwei1995.red.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.liuwei1995.red.R;
import com.liuwei1995.red.adapter.TabFragmentAdapter;
import com.liuwei1995.red.fragment.EyeFragment;

import java.util.ArrayList;
import java.util.List;


public class EyeActivity extends AppCompatActivity {

    private TabLayout tablayout;

    public static void newStartActivity(@NonNull Context c) {
        Intent intent = new Intent(c, EyeActivity.class);
        c.startActivity(intent);
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eye);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // 初始化
        tablayout = (TabLayout) findViewById(R.id.tablayout);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(EyeFragment.newInstance(0,false));
        fragments.add(EyeFragment.newInstance(1,true));
        List<String> lists = new ArrayList<>();
        lists.add("本地");
        lists.add("网络");

        TabFragmentAdapter adapter = new TabFragmentAdapter(fragments, lists, getSupportFragmentManager(), this);

        mViewPager.setAdapter(adapter);
// 将ViewPager和TabLayout绑定
        tablayout.setupWithViewPager(mViewPager);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                mViewPager.setCurrentItem(position, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_eye, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
