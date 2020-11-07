package com.tachyon.bindaas;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tachyon.bindaas.Inbox.Inbox_F;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Notifications.Notification_Adapter;
import com.tachyon.bindaas.Notifications.Notification_Get_Set;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabsView extends RootFragment {

    View view;
    Context context;
    TextView notification_view, messages_view;
    FrameLayout frameLayout;
    public TabsView() {
        // Required empty public constructor
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_tabs_view, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        try {
            context = getContext();

            frameLayout = view.findViewById(R.id.tabs_container);
            notification_view = view.findViewById(R.id.textView3);
            messages_view = view.findViewById(R.id.textView4);
            loadNoficationTab();
            notification_view.setOnClickListener(view -> {
                frameLayout.setVisibility(View.GONE);

                /*  messages_view.setBackground(getResources().getDrawable(R.color.white));
                messages_view.setAlpha(0.5f);*/

                notification_view.setBackground(getResources().getDrawable(R.color.themecolor));
                notification_view.setAlpha(1f);
                if (Functions.getSavedTheme() == R.style.WhiteTheme){
                    messages_view.setBackground(getResources().getDrawable(R.color.white));
                    messages_view.setAlpha(0.5f);
                }else{
                    messages_view.setBackground(getResources().getDrawable(R.color.blackPrimaryColor));
                    messages_view.setTextColor(getResources().getColor(R.color.white));
                }
                loadNoficationTab();
            });
            messages_view.setOnClickListener(view -> {
                messages_view.setBackground(getResources().getDrawable(R.color.themecolor));
                messages_view.setAlpha(1f);
                /*notification_view.setBackground(getResources().getDrawable(R.color.white));
                notification_view.setAlpha(0.5f);*/
                if (Functions.getSavedTheme() == R.style.WhiteTheme){
                    notification_view.setBackground(getResources().getDrawable(R.color.white));
                    notification_view.setAlpha(0.5f);
                }else{
                    notification_view.setBackground(getResources().getDrawable(R.color.blackPrimaryColor));
                    notification_view.setTextColor(getResources().getColor(R.color.white));
                }                loadMessageTab();
            });
            view.findViewById(R.id.back_btn2).setOnClickListener(v -> {
                EventBus.getDefault().post("done");
                getActivity().onBackPressed();
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    private void loadMessageTab() {
        try {
            MessagesTab fragment = new MessagesTab();
//            Notification_F notification_F = new Notification_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            // transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
//            transaction.addToBackStack(null);
            transaction.replace(R.id.tabs_container, fragment).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void loadNoficationTab() {

        try {
            NotificationTab fragment = new NotificationTab();
//            Notification_F notification_F = new Notification_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            //transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            transaction.replace(R.id.tabs_container, fragment).commit();
            frameLayout.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}
