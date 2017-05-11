package com.liuwei1995.red.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liuwei1995.red.R;
import com.liuwei1995.red.adapter.TabFragmentAdapter;
import com.liuwei1995.red.fragment.EyeFragment;

import java.util.ArrayList;
import java.util.List;

import static com.liuwei1995.red.fragment.EyeFragment.ACCOUNT;
import static com.liuwei1995.red.fragment.EyeFragment.TYPE;


public class EyeActivity extends AppCompatActivity {

    private TabLayout tablayout;


    public static void newStartActivity(@NonNull Context c, @IntRange(from = 0,to = 3) int type, String account) {
        Intent intent = new Intent(c, EyeActivity.class);
        intent.putExtra(TYPE,type);
        intent.putExtra(ACCOUNT,account);
        c.startActivity(intent);
    }

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private int item = 0;

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
        List<String> lists = new ArrayList<>();
        lists.add("本地");
        lists.add("网络");
        lists.add("未提交");

        List<Fragment> fragments = new ArrayList<>();
        Intent intent = getIntent();
        if(intent != null){
            int type = intent.getIntExtra(TYPE, -1);
            if(type > -1 && type <= lists.size()){
                item = type;
            }
            String account = intent.getStringExtra(ACCOUNT);
            if(account != null && !TextUtils.isEmpty(account)){
                fragments.add(EyeFragment.newInstance(0,null,true));
                fragments.add(EyeFragment.newInstance(1,account,false));
                fragments.add(EyeFragment.newInstance(2,null,true));
            }else {
                fragments.add(EyeFragment.newInstance(0,null,false));
                fragments.add(EyeFragment.newInstance(1,null,true));
                fragments.add(EyeFragment.newInstance(2,null,true));
            }
        }else {
            fragments.add(EyeFragment.newInstance(0,null,false));
            fragments.add(EyeFragment.newInstance(1,null,true));
            fragments.add(EyeFragment.newInstance(2,null,true));
        }


        TabFragmentAdapter adapter = new TabFragmentAdapter(fragments, lists, getSupportFragmentManager(), this);
        mViewPager.setOffscreenPageLimit(lists.size());
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(item);
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
    public static void setSnackbarColor(Snackbar snackbar, int messageColor, int backgroundColor) {
        View view = snackbar.getView();//获取Snackbar的view
        if(view!=null){
            view.setBackgroundColor(backgroundColor);//修改view的背景色
            ((TextView) view.findViewById(R.id.snackbar_text)).setTextColor(messageColor);//获取Snackbar的message控件，修改字体颜色
        }
    }

    public static void SnackbarAddView(Snackbar snackbar,int layoutId,int index) {
        View snackbarview = snackbar.getView();//获取snackbar的View(其实就是SnackbarLayout)

        Snackbar.SnackbarLayout snackbarLayout=(Snackbar.SnackbarLayout)snackbarview;//将获取的View转换成SnackbarLayout

        View add_view = LayoutInflater.from(snackbarview.getContext()).inflate(layoutId,null);//加载布局文件新建View

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);//设置新建布局参数

        p.gravity= Gravity.CENTER_VERTICAL;//设置新建布局在Snackbar内垂直居中显示

        snackbarLayout.addView(add_view,index,p);//将新建布局添加进snackbarLayout相应位置
    }


}
