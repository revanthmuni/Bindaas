package com.tachyon.bindaas.Home;

import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tachyon.bindaas.Discover.CategoryFragment;
import com.tachyon.bindaas.Discover.EventStatePagerAdapter;
import com.tachyon.bindaas.Discover.TopsFragment;
import com.tachyon.bindaas.Discover.TrendingFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.NewsFeedFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class MainHomeDemo extends RootFragment {
    View view;
    Context context;
    public static ViewPager demo_viewpager;
    EventStatePagerAdapter mainViewPagerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_main_demo, container, false);
            this.context = context;
            demo_viewpager = view.findViewById(R.id.demo_viewpager2);

            mainViewPagerAdapter = new EventStatePagerAdapter(getChildFragmentManager());
            demo_viewpager.setOffscreenPageLimit(3);
            mainViewPagerAdapter.addFrag(new NewsFeedFragment(), "News Feed");
            mainViewPagerAdapter.addFrag(new Home_F(), "Home");
            mainViewPagerAdapter.addFrag(new Profile_F(), "Profile");
            demo_viewpager.setAdapter(mainViewPagerAdapter);
            demo_viewpager.setCurrentItem(1);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }

        return view;
    }
}
