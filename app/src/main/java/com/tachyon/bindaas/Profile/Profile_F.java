package com.tachyon.bindaas.Profile;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;

import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tachyon.bindaas.Chat.Chat_Activity;
import com.tachyon.bindaas.Following.Following_F;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Liked_Videos.Liked_Video_F;
import com.tachyon.bindaas.Profile.UserVideos.UserVideo_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.See_Full_Image_F;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

// This is the profile screen which is show in 5 tab as well as it is also call
// when we see the profile of other users

public class Profile_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    public TextView follow_unfollow_btn;
    public TextView username, username2_txt, video_count_txt;
    public ImageView imageView;
    public TextView follow_count_txt, fans_count_txt, heart_count_txt;

    ImageView back_btn, setting_btn;

    String user_id, user_name, user_pic;

    Bundle bundle;

    protected TabLayout tabLayout;

    protected ViewPager pager;

    private ViewPagerAdapter adapter;

    public boolean isdataload = false;

    RelativeLayout tabs_main_layout;

    ConstraintLayout top_layout;

    public String pic_url;
    private TextView tvVideosCount;
    private TextView tvLikesCount;
    private ImageView insta_view,fb_view,bio_view;
    private TextView bio_textview;
    private String fb_link="";
    private String inst_link="";
    TextView star_percentage;

    LinearLayout uployout,downloayout;
    public Profile_F() {
    }

    Fragment_Callback fragment_callback;

    @SuppressLint("ValidFragment")
    public Profile_F(Fragment_Callback fragment_callback) {
        this.fragment_callback = fragment_callback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getContext();
        try {
            getActivity();

            bundle = getArguments();
            if (bundle != null) {
                user_id = bundle.getString("user_id");
                user_name = bundle.getString("user_name");
                user_pic = bundle.getString("user_pic");
            }
             Log.d("USR_TST", "onCreateView: " + user_id);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return init();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        try {
            Objects.requireNonNull(getActivity()).getMenuInflater().inflate(R.menu.menu, menu);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_refresh) {
            // Refresh the Layout
            Toast.makeText(context, R.string.refresh, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.user_image:
                    OpenfullsizeImage(pic_url);
                    break;

                case R.id.follow_unfollow_btn:

                    if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                        Follow_unFollow_User();
                    else
                        Toast.makeText(context, R.string.login_to_app, Toast.LENGTH_SHORT).show();

                    break;

                case R.id.setting_btn:
                    Open_Setting();
                    break;

                case R.id.following_layout:
                    Open_Following();
                    break;

                case R.id.fans_layout:
                    Open_Followers();
                    break;

                case R.id.back_btn:
                    getActivity().onBackPressed();
                    break;
                case R.id.insta_image:
                    Functions.openBrowser(context,inst_link);
                    break;
                case R.id.fb_image:
                    Log.d("TAG", "onClick: "+fb_link);
                    Functions.openBrowser(context,fb_link);
                    break;

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public View init() {
        try {
            uployout = view.findViewById(R.id.up_layout);
            downloayout = view.findViewById(R.id.down_layout);
            star_percentage = view.findViewById(R.id.textView15);

            username = view.findViewById(R.id.username);
            username2_txt = view.findViewById(R.id.username2_txt);
            imageView = view.findViewById(R.id.user_image);
            imageView.setOnClickListener(this);
            Log.d("Test", username.toString());
            video_count_txt = view.findViewById(R.id.video_count_txt);

            follow_count_txt = view.findViewById(R.id.follow_count_txt);
            fans_count_txt = view.findViewById(R.id.fan_count_txt);
            heart_count_txt = view.findViewById(R.id.heart_count_txt);

            setting_btn = view.findViewById(R.id.setting_btn);
            setting_btn.setOnClickListener(this);

            back_btn = view.findViewById(R.id.back_btn);
            back_btn.setOnClickListener(this);

            follow_unfollow_btn = view.findViewById(R.id.follow_unfollow_btn);
            follow_unfollow_btn.setOnClickListener(this);

            tabLayout = (TabLayout) view.findViewById(R.id.tabs);
            pager = view.findViewById(R.id.pager);
            pager.setOffscreenPageLimit(2);

            adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
            pager.setAdapter(adapter);
            tabLayout.setupWithViewPager(pager);

            insta_view = view.findViewById(R.id.insta_image);
            fb_view = view.findViewById(R.id.fb_image);
            bio_textview  = view.findViewById(R.id.bio_text);
            bio_view = view.findViewById(R.id.bio_image);
            fb_view.setOnClickListener(this);
            insta_view.setOnClickListener(this);

            setupTabIcons();

           // loadStartMeter();

            tabs_main_layout = view.findViewById(R.id.tabs_main_layout);
            top_layout = view.findViewById(R.id.top_layout);

            ViewTreeObserver observer = top_layout.getViewTreeObserver();
            observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    final int height = top_layout.getMeasuredHeight();

                    top_layout.getViewTreeObserver().removeGlobalOnLayoutListener(
                            this);

                    ViewTreeObserver observer = tabs_main_layout.getViewTreeObserver();
                    observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                        @Override
                        public void onGlobalLayout() {

                            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) tabs_main_layout.getLayoutParams();
                            params.height = (int) (tabs_main_layout.getMeasuredHeight() + height);
                            tabs_main_layout.setLayoutParams(params);
                            tabs_main_layout.getViewTreeObserver().removeGlobalOnLayoutListener(
                                    this);

                        }
                    });

                }
            });

            view.findViewById(R.id.following_layout).setOnClickListener(this);
            view.findViewById(R.id.fans_layout).setOnClickListener(this);

            isdataload = true;

            Call_Api_For_get_Allvideos();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (is_run_first_time) {

                Call_Api_For_get_Allvideos();

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void setupTabIcons() {
        try {
            View view1 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
            ImageView imageView1 = view1.findViewById(R.id.image);
            imageView1.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_color));
            tvVideosCount = view1.findViewById(R.id.tvCount);
            tabLayout.getTabAt(0).setCustomView(view1);

            View view2 = LayoutInflater.from(context).inflate(R.layout.item_tabs_profile_menu, null);
            ImageView imageView2 = view2.findViewById(R.id.image);
            imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_gray));
            tvLikesCount = view2.findViewById(R.id.tvCount);
            tvLikesCount.setTextColor(getResources().getColor(R.color.black));
            tabLayout.getTabAt(1).setCustomView(view2);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    View v = tab.getCustomView();
                    ImageView image = v.findViewById(R.id.image);

                    switch (tab.getPosition()) {
                        case 0:
//                        layout.setBackgroundColor(getResources().getColor(R.color.redcolor));
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_color));
                            break;

                        case 1:
//                        layout.setBackgroundColor(getResources().getColor(R.color.redcolor));
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_color));
                            break;
                    }
                    tab.setCustomView(v);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    View v = tab.getCustomView();
                    ImageView image = v.findViewById(R.id.image);

                    switch (tab.getPosition()) {
                        case 0:
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_gray));
                            break;
                        case 1:
                            image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_gray));
                            break;
                    }

                    tab.setCustomView(v);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }

            });

            Objects.requireNonNull(tabLayout.getTabAt(0)).select();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final Resources resources;

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new UserVideo_F(false, user_id);
                    break;
                case 1:
                    result = new Liked_Video_F(false, user_id);
                    break;

                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            return null;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */
        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    }
    private void loadStartMeter() {
        final int min = 1;
        final int max = 10;
        final int random = new Random().nextInt((max - min) + 1) + min;
        float value = (float)random;
        float up_value = (float) (10-random);
        Toast.makeText(context, ""+random, Toast.LENGTH_SHORT).show();
        // star_meter.setImageLevel(random);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                value
        );
        LinearLayout.LayoutParams paramup = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                up_value
        );
        downloayout.setLayoutParams(paramup);
        uployout.setLayoutParams(param);
        star_percentage.setText(value*10+"%");
    }
    boolean is_run_first_time = false;

    private void Call_Api_For_get_Allvideos() {
        try {
            if (bundle == null) {
                user_id = Variables.sharedPreferences.getString(Variables.u_id, "0");
            }

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("my_user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
                parameters.put("user_id", user_id);
                Log.d("USR_TST", "Call_Api_For_get_Allvideos: " + user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequest.Call_Api(context, Variables.showMyAllVideos, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    is_run_first_time = true;
                    Parse_data(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Parse_data(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                JSONObject data = msgArray.getJSONObject(0);
                JSONObject user_info = data.optJSONObject("user_info");
                username.setText(user_info.optString("first_name") + " " + user_info.optString("last_name"));
                username2_txt.setText(user_info.optString("username"));
//                fb_link = "https://www.google.com";
                fb_link = user_info.optString("fb_link");
                inst_link = user_info.optString("insta_link");
                String bio_text = user_info.optString("bio");
                fb_view.setVisibility(fb_link.equals("")?View.GONE:View.VISIBLE);
                insta_view.setVisibility(inst_link.equals("")?View.GONE:View.VISIBLE);
                bio_view.setVisibility(bio_text.equals("")?View.GONE:View.VISIBLE);
                bio_textview.setText(bio_text.equals("")?"":bio_text);
                bio_textview.setVisibility(bio_text.equals("")?View.GONE:View.VISIBLE);
//                bio_textview.setText(bio_text.equals("")?"[Add About-me in Edit Profile]":bio_text);
                String anyone_can_message = user_info.optString("anyone_can_message");


                String has_new_notification = data.optString("has_new_notification");
                Log.d("TAG", "Parse_data: "+has_new_notification);


                pic_url = user_info.optString("profile_pic");
                if (pic_url != null && !pic_url.equals(""))
                    Picasso.with(context)
                            .load(pic_url)
                            .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                            .resize(200, 200).centerCrop().into(imageView);

                follow_count_txt.setText(data.optString("total_following"));
                fans_count_txt.setText(data.optString("total_fans"));

                heart_count_txt.setText(data.optString("total_heart"));
                tvLikesCount.setText("(" + data.optString("my_liked_videos") + ")");

                if (!data.optString("user_id").
                        equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {

                    follow_unfollow_btn.setVisibility(View.VISIBLE);
                    JSONObject follow_Status = data.optJSONObject("follow_Status");
                    follow_unfollow_btn.setText(follow_Status.optString("follow_status_button"));
                    follow_status = follow_Status.optString("follow");

                   // Toast.makeText(context, ""+anyone_can_message, Toast.LENGTH_SHORT).show();
                    setting_btn.setVisibility(anyone_can_message.equals("anyone")?View.VISIBLE:View.GONE);
                    setting_btn.setVisibility(follow_Status.optString("follow_status_button").equalsIgnoreCase("UnFollow")
                            &&follow_Status.optString("follow").equals("1")?View.VISIBLE:View.GONE);
                }

                JSONArray user_videos = data.getJSONArray("user_videos");
                if (!user_videos.toString().equals("[" + "0" + "]")) {
                    video_count_txt.setText(user_videos.length() + " Videos");
                    tvVideosCount.setText("(" + user_videos.length() + ")");

                } else {
                    tvVideosCount.setText("(" + 0 + ")");
                }

                String verified = user_info.optString("verified");
                if (verified != null && verified.equalsIgnoreCase("1")) {
                    view.findViewById(R.id.varified_btn).setVisibility(View.VISIBLE);
                }

            } else {
                //Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void Open_Setting() {

        if(Variables.sharedPreferences.getBoolean(Variables.islogin,false)) {
            Open_Chat_F();
        }
        else {
            Toast.makeText(context, R.string.login_to_app, Toast.LENGTH_SHORT).show();
        }

    }

    public String follow_status = "0";

    public void Follow_unFollow_User() {
        try {
            final String send_status;
            if (follow_status.equals("0")) {
                send_status = "1";
            } else {
                send_status = "0";
            }

            Functions.Call_Api_For_Follow_or_unFollow(getActivity(),
                    Variables.sharedPreferences.getString(Variables.u_id, ""),
                    user_id,
                    send_status,
                    new API_CallBack() {
                        @Override
                        public void ArrayData(ArrayList arrayList) {

                        }

                        @Override
                        public void OnSuccess(String responce) {

                            if (send_status.equals("1")) {
                                follow_unfollow_btn.setText("Unfollow");
                                follow_status = "1";

                            } else if (send_status.equals("0")) {
                                follow_unfollow_btn.setText("Follow");
                                follow_status = "0";
                            }

                            Call_Api_For_get_Allvideos();
                        }

                        @Override
                        public void OnFail(String responce) {

                        }

                    });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    //this method will get the big size of profile image.
    public void OpenfullsizeImage(String url) {
        try {
            See_Full_Image_F see_image_f = new See_Full_Image_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            Bundle args = new Bundle();
            args.putSerializable("image_url", url);
            see_image_f.setArguments(args);
            transaction.addToBackStack(null);

            View view = getActivity().findViewById(R.id.MainMenuFragment);
            if (view != null)
                transaction.replace(R.id.MainMenuFragment, see_image_f).commit();
            else
                transaction.replace(R.id.Profile_F, see_image_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Chat_F() {

        try {
            Chat_Activity chat_activity = new Chat_Activity(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {

                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("user_id", user_id);
            args.putString("user_name", user_name);
            args.putString("user_pic", user_pic);
            chat_activity.setArguments(args);
            transaction.addToBackStack(null);

            View view = getActivity().findViewById(R.id.MainMenuFragment);
            if (view != null)
                transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
            else
                transaction.replace(R.id.Profile_F, chat_activity).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Following() {
        try {
            Following_F following_f = new Following_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("id", user_id);
            args.putString("from_where", "following");
            following_f.setArguments(args);
            transaction.addToBackStack(null);

            View view = getActivity().findViewById(R.id.MainMenuFragment);

            if (view != null)
                transaction.replace(R.id.MainMenuFragment, following_f).commit();
            else
                transaction.replace(R.id.Profile_F, following_f).commit();

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Followers() {
        try {
            Following_F following_f = new Following_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("id", user_id);
            args.putString("from_where", "fan");
            following_f.setArguments(args);
            transaction.addToBackStack(null);


            View view = getActivity().findViewById(R.id.MainMenuFragment);

            if (view != null)
                transaction.replace(R.id.MainMenuFragment, following_f).commit();
            else
                transaction.replace(R.id.Profile_F, following_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


    @Override
    public void onDetach() {
        super.onDetach();

        try {
            if (fragment_callback != null)
                fragment_callback.Responce(new Bundle());

            Functions.deleteCache(context);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }


}
