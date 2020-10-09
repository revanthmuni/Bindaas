package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.tachyon.bindaas.Profile.Liked_Videos.Liked_Video_F;
import com.tachyon.bindaas.Profile.Private_Videos.PrivateVideo_F;
import com.tachyon.bindaas.Profile.UserVideos.UserVideo_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class EventStatePagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();


    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public EventStatePagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment,String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
