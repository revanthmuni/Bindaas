package com.tachyon.bindaas;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.tabs.TabLayout;
import com.tachyon.bindaas.Inbox.Inbox_F;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Notifications.Notification_Adapter;
import com.tachyon.bindaas.Notifications.Notification_Get_Set;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class NotificationTab extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    Notification_Adapter adapter;
    RecyclerView recyclerView;

    ArrayList<Notification_Get_Set> datalist;

    SwipeRefreshLayout swiperefresh;

    public NotificationTab() {
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
        view = inflater.inflate(R.layout.fragment_notification_tab, container, false);
        try {
            context = getContext();


            datalist = new ArrayList<>();


            recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);


            adapter = new Notification_Adapter(context, datalist, new Notification_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int postion, Notification_Get_Set item) {

                    switch (view.getId()) {
                        case R.id.watch_btn:
                            OpenWatchVideo(item);
                            break;
                        default:
                            Open_Profile(item);
                            break;
                    }
                }
            }
            );

            recyclerView.setAdapter(adapter);

            view.findViewById(R.id.inbox_btn).setOnClickListener(this);

            swiperefresh = view.findViewById(R.id.swiperefresh);
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Call_api();
                }
            });
            view.findViewById(R.id.back_btn2).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().onBackPressed();

                }
            });


            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                Call_api();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }


    AdView adView;

    @Override
    public void onStart() {
        super.onStart();
        try {
            adView = view.findViewById(R.id.bannerad);
            if (!Variables.is_remove_ads) {
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
            } else {
                adView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if (view != null && Variables.Reload_my_notification) {
                Variables.Reload_my_notification = false;
                Call_api();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Call_api() {
        try {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", Variables.user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequest.Call_Api(context, Variables.getNotifications, jsonObject, new Callback() {
                @Override
                public void Responce(String resp) {
                    swiperefresh.setRefreshing(false);
                    parse_data(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void parse_data(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msg = jsonObject.getJSONArray("msg");
                ArrayList<Notification_Get_Set> temp_list = new ArrayList<>();
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject data = msg.getJSONObject(i);
                    JSONObject user_id_details = data.optJSONObject("user_id_details");
                    JSONObject value_data = data.optJSONObject("value_data");

                    Notification_Get_Set item = new Notification_Get_Set();

                    item.user_id = data.optString("user_id");

                    item.username = user_id_details.optString("username");
                    item.first_name = user_id_details.optString("first_name");
                    item.last_name = user_id_details.optString("last_name");
                    item.profile_pic = user_id_details.optString("profile_pic");

                    item.effected_user_id = user_id_details.optString("effected_user_id");

                    item.type = data.optString("type");

                    if (item.type.equalsIgnoreCase("comment_video") || item.type.equalsIgnoreCase("video_like")) {

                        item.id = value_data.optString("id");
                        item.video = value_data.optString("video");
                        item.thum = value_data.optString("thum");
                        item.gif = value_data.optString("gif");

                    }

                    item.created = user_id_details.optString("created");

                    temp_list.add(item);


                }

                datalist.clear();
                datalist.addAll(temp_list);

                if (datalist.size() <= 0) {
                    view.findViewById(R.id.no_data_layout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.no_data_layout).setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.inbox_btn:
                Open_inbox_F();
                break;
        }
    }

    private void Open_inbox_F() {
        try {
            Inbox_F inbox_f = new Inbox_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, inbox_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void OpenWatchVideo(Notification_Get_Set item) {
        try {
            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("video_id", item.id);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Open_Profile(Notification_Get_Set item) {
        try {

            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.user_id)) {

                try {
                    getActivity().onBackPressed();
                    TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                    profile.select();
                } catch (Exception e) {
                    Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
                    Log.d("Exception:", "OpenProfile: " + e.getMessage());
                }
                /*TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(4);
                if (profile!=null) {
                    profile.select();
                }*/

            } else {

                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {

                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle args = new Bundle();
                args.putString("user_id", item.user_id);
                args.putString("user_name", item.first_name + " " + item.last_name);
                args.putString("user_pic", item.profile_pic);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, profile_f).commit();

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }
}