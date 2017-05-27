package com.liuwei1995.red.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;

import java.util.List;

/**
 * Created by liuwei on 2017/3/17
 */

public class TabFragmentAdapter extends FragmentPagerAdapter {

    private final List<String> lists;
    private Context context;
    private List<Fragment> fragments;
    private FragmentManager fm;

    public TabFragmentAdapter(List<Fragment> fragments, List<String> lists, FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
        this.fragments = fragments;
        this.lists = lists;
        this.fm = fm;
    }

    public void setFragments(List<Fragment> fragments) {
        if (this.fragments != null) {
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment f : this.fragments) {
                ft.remove(f);
            }
            ft.commit();
            ft = null;
            fm.executePendingTransactions();
        }
        this.fragments = fragments;
        notifyDataSetChanged();
    }

    public void addFragment(Fragment fragment, String title) {
        lists.add(title);
        fragments.add(fragment);
        notifyDataSetChanged();
    }

    public void remove(String title) {
        int index = lists.indexOf(title);
        lists.remove(index);
        fragments.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return lists.get(position);
    }
}
