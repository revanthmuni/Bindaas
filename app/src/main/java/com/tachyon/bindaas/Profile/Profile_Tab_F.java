package com.tachyon.bindaas.Profile;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import com.tachyon.bindaas.Inbox.Inbox_F;
import com.tachyon.bindaas.Notifications.Notification_F;
import com.tachyon.bindaas.Settings.Setting_F;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tachyon.bindaas.Following.Following_F;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Liked_Videos.Liked_Video_F;
import com.tachyon.bindaas.Profile.UserVideos.UserVideo_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.See_Full_Image_F;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.Video_Recording.GalleryVideos.GalleryVideos_A;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile_Tab_F extends RootFragment implements View.OnClickListener {
    View view;
    Context context;

    public TextView username, username2_txt, video_count_txt, tvUserNotifications, tvUserChat;
    public ImageView imageView;
    public TextView follow_count_txt, fans_count_txt, heart_count_txt, draft_count_txt, tvVideosCount, tvLikesCount;

    String videosCount = "0", likesCount = "0";

    ImageView setting_btn;
    ImageView refresh;


    protected TabLayout tabLayout;

    protected ViewPager pager;

    private ViewPagerAdapter adapter;

    public boolean isdataload = false;
    SwipeRefreshLayout swiperefresh;


    RelativeLayout tabs_main_layout;

    ConstraintLayout top_layout;


    public String pic_url;


    public LinearLayout create_popup_layout;

    public Profile_Tab_F() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_tab, container, false);
        context = getContext();
        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);

        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "Refresh", Toast.LENGTH_SHORT).show();
                //update_profile();
                Call_Api_For_get_Allvideos();
            }
        });
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //update_profile();
                Call_Api_For_get_Allvideos();
            }
        });
        return init();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_image:
                OpenfullsizeImage(pic_url);
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

            case R.id.draft_btn:
                Intent upload_intent = new Intent(getActivity(), GalleryVideos_A.class);
                startActivity(upload_intent);
                getActivity().overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                break;

            case R.id.tvUserChat:
                Open_inbox_F();
                break;
            case R.id.tvUserNotifications:
                openNotificationFragment();
                break;

        }
    }

    private void openNotificationFragment() {
        Notification_F notification_F = new Notification_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, notification_F).commit();

    }

    private void Open_inbox_F() {
        Inbox_F inbox_f = new Inbox_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, inbox_f).commit();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if ((view != null && isVisibleToUser)) {

            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                update_profile();

                Call_Api_For_get_Allvideos();

            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Show_draft_count();

        if (view != null && Variables.Reload_my_videos) {
            Variables.Reload_my_videos = false;
            Call_Api_For_get_Allvideos();
        }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    public View init() {

        username = view.findViewById(R.id.username);
        username2_txt = view.findViewById(R.id.username2_txt);
        imageView = view.findViewById(R.id.user_image);
        imageView.setOnClickListener(this);
        refresh = view.findViewById(R.id.refresh);

        video_count_txt = view.findViewById(R.id.video_count_txt);

        follow_count_txt = view.findViewById(R.id.follow_count_txt);
        fans_count_txt = view.findViewById(R.id.fan_count_txt);
        heart_count_txt = view.findViewById(R.id.heart_count_txt);
        draft_count_txt = view.findViewById(R.id.draft_count_txt);

        Show_draft_count();

        setting_btn = view.findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);


        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        pager = view.findViewById(R.id.pager);
        pager.setOffscreenPageLimit(2);

        adapter = new ViewPagerAdapter(getResources(), getChildFragmentManager());
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        tvUserChat = view.findViewById(R.id.tvUserChat);
        tvUserNotifications = view.findViewById(R.id.tvUserNotifications);


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


        create_popup_layout = view.findViewById(R.id.create_popup_layout);


        view.findViewById(R.id.following_layout).setOnClickListener(this);
        view.findViewById(R.id.fans_layout).setOnClickListener(this);
        tvUserNotifications.setOnClickListener(this);
        tvUserChat.setOnClickListener(this);

        setupTabIcons();


        return view;
    }

    public void Show_draft_count() {
        try {

            String path = Variables.draft_app_folder;
            File directory = new File(path);
            File[] files = directory.listFiles();
            draft_count_txt.setText("" + files.length);
            if (files.length <= 0) {
                view.findViewById(R.id.draft_btn).setVisibility(View.GONE);
            } else {
                view.findViewById(R.id.draft_btn).setVisibility(View.VISIBLE);
                view.findViewById(R.id.draft_btn).setOnClickListener(this);
            }
        } catch (Exception e) {
            view.findViewById(R.id.draft_btn).setVisibility(View.GONE);
        }
    }

    public void update_profile() {

        try {
            username2_txt.setText(Variables.sharedPreferences.getString(Variables.u_name, ""));
            username.setText(Variables.sharedPreferences.getString(Variables.f_name, "") + " " + Variables.sharedPreferences.getString(Variables.l_name, ""));
            pic_url = Variables.sharedPreferences.getString(Variables.u_pic, "null");

            if (pic_url != null && !pic_url.equals(""))
                Picasso.with(context).load(pic_url)
                        .resize(200, 200)
                        .placeholder(R.drawable.profile_image_placeholder)
                        .centerCrop()
                        .into(imageView);

        } catch (Exception e) {

        }
    }


    private void setupTabIcons() {

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
                TextView count = v.findViewById(R.id.tvCount);

                switch (tab.getPosition()) {
                    case 0:

                        if (UserVideo_F.myvideo_count > 0) {
                            create_popup_layout.setVisibility(View.GONE);
                        } else {
                            create_popup_layout.setVisibility(View.VISIBLE);
                            Animation aniRotate = AnimationUtils.loadAnimation(context, R.anim.up_and_down_animation);
                            create_popup_layout.startAnimation(aniRotate);
                        }

                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_color));
                        break;

                    case 1:
                        create_popup_layout.clearAnimation();
                        create_popup_layout.setVisibility(View.GONE);
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_color));
                        break;
                }
                tab.setCustomView(v);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View v = tab.getCustomView();
                ImageView image = v.findViewById(R.id.image);
                TextView count = v.findViewById(R.id.tvCount);

                switch (tab.getPosition()) {
                    case 0:
//                        count.setText("(" + videosCount + ")");
                        count.setTextColor(getResources().getColor(R.color.black));
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_my_video_gray));
                        break;
                    case 1:
//                        count.setText("(" + likesCount + ")");
                        count.setTextColor(getResources().getColor(R.color.black));
                        image.setImageDrawable(getResources().getDrawable(R.drawable.ic_liked_video_gray));
                        break;
                }

                tab.setCustomView(v);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


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
                    result = new UserVideo_F(true, Variables.sharedPreferences.getString(Variables.u_id, ""));
                    break;
                case 1:
                    result = new Liked_Video_F(true, Variables.sharedPreferences.getString(Variables.u_id, ""));
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


    //this will get the all videos data of user and then parse the data
    private void Call_Api_For_get_Allvideos() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("my_user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showMyAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Parse_data(resp);
                swiperefresh.setRefreshing(false);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        getActivity().getMenuInflater().inflate(R.menu.profile_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.profile_refresh) {
            Toast.makeText(context, "rerjskdj", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void Parse_data(String responce) {

        Log.d("Test", responce);
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");

                JSONObject data = msgArray.getJSONObject(0);
                JSONObject user_info = data.optJSONObject("user_info");
                username2_txt.setText(user_info.optString("username"));
                username.setText(user_info.optString("first_name") + " " + user_info.optString("last_name"));

                pic_url = user_info.optString("profile_pic");
                if (pic_url != null && !pic_url.equals(""))
                    Picasso.with(context)
                            .load(pic_url)
                            .centerCrop()
                            .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder)).resize(200, 200)
                            .into(imageView);

                follow_count_txt.setText(data.optString("total_following"));
                fans_count_txt.setText(data.optString("total_fans"));
                heart_count_txt.setText(data.optString("total_heart"));
                String notifications = data.optString("total_notifications");
                String messages = data.optString("total_messages");
                // tvUserNotifications.setText(notifications);
                // tvUserChat.setText(messages);

                tvLikesCount.setText("(" + data.optString("my_liked_videos") + ")");


                JSONArray user_videos = data.getJSONArray("user_videos");
                if (!user_videos.toString().equals("[" + "0" + "]")) {
                    video_count_txt.setText(user_videos.length() + " Videos");
                    tvVideosCount.setText("(" + user_videos.length() + ")");
                    create_popup_layout.setVisibility(View.GONE);

                } else {
                    tvVideosCount.setText("(" + 0 + ")");
                    create_popup_layout.setVisibility(View.VISIBLE);
                    Animation aniRotate = AnimationUtils.loadAnimation(context, R.anim.up_and_down_animation);
                    create_popup_layout.startAnimation(aniRotate);
                }

                String verified = user_info.optString("verified");
                if (verified != null && verified.equalsIgnoreCase("1")) {
                    view.findViewById(R.id.varified_btn).setVisibility(View.VISIBLE);
                }

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void Open_Setting() {

        Open_menu_tab(setting_btn);


    }


    public void Open_Edit_profile() {

        Edit_Profile_F edit_profile_f = new Edit_Profile_F(new Fragment_Callback() {
            @Override
            public void Responce(Bundle bundle) {

                update_profile();
            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, edit_profile_f).commit();

    }


    public void Open_setting() {

        Setting_F setting_f = new Setting_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, setting_f).commit();

    }


    //this method will get the big size of profile image.
    public void OpenfullsizeImage(String url) {

        See_Full_Image_F see_image_f = new See_Full_Image_F();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        Bundle args = new Bundle();
        args.putSerializable("image_url", url);
        see_image_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, see_image_f).commit();

    }


    public void Open_menu_tab(View anchor_view) {
        Context wrapper = new ContextThemeWrapper(context, R.style.AlertDialogCustom);
        PopupMenu popup = new PopupMenu(wrapper, anchor_view);
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            popup.setGravity(Gravity.TOP | Gravity.RIGHT);
        }
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.edit_Profile_id:
                        Open_Edit_profile();
                        break;

                    case R.id.setting_id:
                        Open_setting();
                        break;

                    case R.id.logout_id:
                        Logout();
                        break;
                    case R.id.profile_refresh:
                        Toast.makeText(getContext(), "Refresh ", Toast.LENGTH_SHORT).show();
                        break;

                }
                return true;
            }
        });

    }


    public void Open_Following() {

        Following_F following_f = new Following_F(new Fragment_Callback() {
            @Override
            public void Responce(Bundle bundle) {

                Call_Api_For_get_Allvideos();

            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
        args.putString("from_where", "following");
        following_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, following_f).commit();
    }

    public void Open_Followers() {

        Following_F following_f = new Following_F(new Fragment_Callback() {
            @Override
            public void Responce(Bundle bundle) {
                Call_Api_For_get_Allvideos();
            }
        });
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("id", Variables.sharedPreferences.getString(Variables.u_id, ""));
        args.putString("from_where", "fan");
        following_f.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, following_f).commit();

    }

    // this will erase all the user info store in locally and logout the user
    public void Logout() {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.u_id, "");
        editor.putString(Variables.u_name, "");
        editor.putString(Variables.u_pic, "");
        editor.putBoolean(Variables.islogin, false);
        editor.commit();
        getActivity().finish();
        startActivity(new Intent(getActivity(), MainMenuActivity.class));
    }


    @Override
    public void onDetach() {
        super.onDetach();
        Functions.deleteCache(context);
    }
}
