package com.tachyon.bindaas.Home;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Outline;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.gson.Gson;
import com.tachyon.bindaas.Discover.Discover_F;
import com.tachyon.bindaas.Home.ReportVideo.ReportVideo;
import com.tachyon.bindaas.LinearLayoutManagerWithSmoothScroller;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Bindaas;
import com.tachyon.bindaas.SoundLists.VideoSound_A;
import com.google.android.material.tabs.TabLayout;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tachyon.bindaas.Comments.Comment_F;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Data_Send;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.Taged.Taged_Videos_F;
import com.tachyon.bindaas.VideoAction.VideoAction_F;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

// this is the main view which is show all  the video in list
public class Home_F extends RootFragment implements Player.EventListener, Fragment_Data_Send {

    View view;
    Context context;


    ViewPager2 recyclerView;
    ArrayList<Home_Get_Set> data_list;
    int currentPage = -1;
    LinearLayoutManager layoutManager;

    ProgressBar p_bar;

    SwipeRefreshLayout swiperefresh;

    boolean is_user_stop_video = false;

    public Home_F() {
        // Required empty public constructor
    }

    int swipe_count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();

        p_bar = view.findViewById(R.id.p_bar);

        recyclerView = view.findViewById(R.id.recylerview);
        layoutManager = new LinearLayoutManager(context);
        // layoutManager = new LinearLayoutManagerWithSmoothScroller(context);
        // recyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(false);

        SnapHelper snapHelper = new PagerSnapHelper();
        //snapHelper.attachToRecyclerView(recyclerView);

        // this is the scroll listener of recycler view which will tell the current item number

        recyclerView.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
               /* final int scrollOffset = recyclerView.getCurrentItem();
                final int height = recyclerView.getHeight();
                int page_no = scrollOffset / height;*/
                Log.d("TAG", "onPageScrolled: outside block");

                if (position != currentPage) {
                    Log.d("TAG", "onPageScrolled: inside block");
                    currentPage = position;
                    Release_Privious_Player();
                    Set_Player(currentPage);
                }
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                Log.d("TAG", "onPageScrolled: onPageScrollStateChanged");

                // Release_Privious_Player();
            }
        });

        /*recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //here we find the current item number

                final int scrollOffset = recyclerView.computeVerticalScrollOffset();
                final int height = recyclerView.getHeight();
                int page_no = scrollOffset / height;

                if (page_no != currentPage) {
                    currentPage = page_no;
                    Release_Privious_Player();
                    Set_Player(currentPage);
                }
            }
        });*/

        swiperefresh = view.findViewById(R.id.swiperefresh);
        swiperefresh.setProgressViewOffset(false, 0, 200);

        swiperefresh.setColorSchemeResources(R.color.black);
        swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = -1;
                Call_Api_For_get_Allvideos();
            }
        });

        Call_Api_For_get_Allvideos();

        if (!Variables.is_remove_ads)
            Load_add();

        return view;
    }

    InterstitialAd mInterstitialAd;

    public void Load_add() {

        // this is test app id you will get the actual id when you add app in your
        //add mob account
        MobileAds.initialize(context, getResources().getString(R.string.ad_app_id));


        //code for intertial add
        mInterstitialAd = new InterstitialAd(context);

        //here we will get the add id keep in mind above id is app id and below Id is add Id
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.my_Interstitial_Add));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    boolean is_add_show = false;
    Home_Adapter adapter;

    public void Set_Adapter() {
        Log.d("JsonData:", "Set_Adapter: " + new Gson().toJson(data_list));

        adapter = new Home_Adapter(context, data_list, new Home_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, final Home_Get_Set item, View view) {

                switch (view.getId()) {

                    case R.id.user_pic:
                        OpenProfile(item, false);
                        break;
                    case R.id.username:
                        onPause();
                        OpenProfile(item, false);
                        break;

                    case R.id.ivSearch:
                        onPause();
                        openDiscoverFragment();
                        break;

                    case R.id.like_layout:
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Like_Video(postion, item);
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case R.id.comment_layout:
                        OpenComment(item);
                        break;
                    case R.id.shared_layout:
                        if (!is_add_show && (mInterstitialAd != null && mInterstitialAd.isLoaded())) {
                            mInterstitialAd.show();
                            is_add_show = true;
                        } else {
                            is_add_show = false;
                            final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
                                @Override
                                public void Responce(Bundle bundle) {
                                    if (bundle.getString("action").equals("save")) {
                                        Save_Video(item);
                                    } else if (bundle.getString("action").equals("delete")) {
                                        deleteVideo(item);
                                    }
                                }
                            });

                            Bundle bundle = new Bundle();
                            bundle.putString("video_id", item.video_id);
                            bundle.putString("user_id", item.user_id);
                            fragment.setArguments(bundle);
                            fragment.show(getChildFragmentManager(), "");
                        }

                        break;


                    case R.id.sound_image_layout:
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            if (check_permissions()) {
                                Intent intent = new Intent(getActivity(), VideoSound_A.class);
                                intent.putExtra("data", item);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                        }

                        break;
                }

            }
        });

        adapter.setHasStableIds(true);

        recyclerView.setAdapter(adapter);

    }

    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos() {

        Log.d(Variables.tag, MainMenuActivity.token);

        JSONObject parameters = new JSONObject();
        try {
            Log.d("test--", "Call_Api_For_get_Allvideos: " + Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Parse_data(resp);
            }
        });

    }

    public void Parse_data(String responce) {

        data_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.user_id = itemdata.optString("user_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");

                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");
                    item.verified = user_info.optString("verified");

                    JSONObject sound_data = itemdata.optJSONObject("sound");
                    item.sound_id = sound_data.optString("id");
                    item.sound_name = sound_data.optString("sound_name");
                    item.sound_pic = sound_data.optString("thum");
                    if (sound_data != null) {
                        JSONObject audio_path = sound_data.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("acc");
                    }


                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");


                    item.video_id = itemdata.optString("id");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");


                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    data_list.add(item);
                }

                Set_Adapter();

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void Call_Api_For_Singlevideos(final int postion) {


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", Variables.sharedPreferences.getString(Variables.device_token, "Null"));
            parameters.put("video_id", data_list.get(postion).video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                swiperefresh.setRefreshing(false);
                Singal_Video_Parse_data(postion, resp);
            }
        });

    }

    public void Singal_Video_Parse_data(int pos, String responce) {

        Log.d("Home_F", "Singal_Video_Parse_data: " + responce);
        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.user_id = itemdata.optString("user_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");

                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");
                    item.verified = user_info.optString("verified");

                    JSONObject sound_data = itemdata.optJSONObject("sound");
                    item.sound_id = sound_data.optString("id");
                    item.sound_name = sound_data.optString("sound_name");
                    item.sound_pic = sound_data.optString("thum");
                    if (sound_data != null) {
                        JSONObject audio_path = sound_data.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("acc");
                    }


                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");


                    item.video_id = itemdata.optString("id");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");
                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    data_list.remove(pos);
                    data_list.add(pos, item);
                    adapter.notifyDataSetChanged();
                }


            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    // this will call when swipe for another video and
    // this function will set the player to the current video
    public void Set_Player(final int currentPage) {
        try {


            final Home_Get_Set item = data_list.get(currentPage);

            Call_cache();

            HttpProxyCacheServer proxy = Bindaas.getProxy(context);
            String proxyUrl = proxy.getProxyUrl(item.video_url);

            DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024).createDefaultLoadControl();

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(proxyUrl));

            Log.d(Variables.tag, item.video_url);
            Log.d(Variables.tag, proxyUrl);


            player.prepare(videoSource);

            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(this);


//        View layout = layoutManager.findViewByPosition(currentPage);
            View layout = recyclerView.getRootView();
            final PlayerView playerView = layout.findViewById(R.id.playerview);
            playerView.setPlayer(player);


            player.setPlayWhenReady(is_visible_to_user);
            privious_player = player;


            final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);

            playerView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {

                    private static final int SWIPE_THRESHOLD = 1;
                    private static final int SWIPE_VELOCITY_THRESHOLD = 1;

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                        boolean result = false;
                        try {
                            float diffY = e2.getY() - e1.getY();
                            float diffX = e2.getX() - e1.getX();
                            if (Math.abs(diffX) > Math.abs(diffY)) {
                                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                    if (diffX > 0) {
                                        onSwipeRight();
                                    } else {
                                        onSwipeLeft();
                                    }
                                    result = true;
                                }
                            } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffY > 0) {
                                    onSwipeBottom();
                                } else {
                                    onSwipeTop();
                                }
                                result = true;
                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                        }
                        return result;
                    }

                    public void onSwipeRight() {
                        OpenProfile(item, true);
                    }

                    public void onSwipeLeft() {
                        OpenProfile(item, true);
                    }

                    public void onSwipeTop() {
                    }

                    public void onSwipeBottom() {
                    }


                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        super.onSingleTapUp(e);
                        if (!player.getPlayWhenReady()) {
                            is_user_stop_video = false;
                            privious_player.setPlayWhenReady(true);
                        } else {
                            is_user_stop_video = true;
                            privious_player.setPlayWhenReady(false);
                        }


                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        Show_video_option(item);

                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {

                        if (!player.getPlayWhenReady()) {
                            is_user_stop_video = false;
                            privious_player.setPlayWhenReady(true);
                        }

                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Show_heart_on_DoubleTap(item, mainlayout, e);
                            Like_Video(currentPage, item);
                        } else {
                            Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
                        }
                        return super.onDoubleTap(e);

                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            playerView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), 15);
                }
            });

            playerView.setClipToOutline(true);
            TextView desc_txt = layout.findViewById(R.id.desc_txt);
            HashTagHelper.Creator.create(context.getResources().getColor(R.color.maincolor), new HashTagHelper.OnHashTagClickListener() {
                @Override
                public void onHashTagClicked(String hashTag) {

                    onPause();
                    OpenHashtag(hashTag);

                }
            }).handle(desc_txt);


       /* LinearLayout soundimage = (LinearLayout) layout.findViewById(R.id.sound_image_layout);
        Animation sound_animation = AnimationUtils.loadAnimation(context, R.anim.d_clockwise_rotation);
        soundimage.startAnimation(sound_animation);
*/
            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                Functions.Call_Api_For_update_view(getActivity(), item.video_id);


            swipe_count++;
            if (swipe_count > 6) {
                Show_add();
                swipe_count = 0;
            }


//        Call_Api_For_Singlevideos(currentPage);

        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    public void Call_cache() {

        try {

            if (currentPage + 1 < data_list.size()) {
                HttpProxyCacheServer proxy = Bindaas.getProxy(context);
                proxy.getProxyUrl(data_list.get(currentPage + 1).video_url);
            }
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    public void Show_heart_on_DoubleTap(Home_Get_Set item, final RelativeLayout mainlayout, MotionEvent e) {

        try {


            int x = (int) e.getX() - 100;
            int y = (int) e.getY() - 100;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            final ImageView iv = new ImageView(getApplicationContext());
            lp.setMargins(x, y, 0, 0);
            iv.setLayoutParams(lp);
            if (item.liked.equals("1"))
                iv.setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_like));
            else
                iv.setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_like_fill));

            mainlayout.addView(iv);
            Animation fadeoutani = AnimationUtils.loadAnimation(context, R.anim.fade_out);

            fadeoutani.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mainlayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mainlayout.removeView(iv);
                        }
                    }, 500);

//                mainlayout.removeView(iv);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv.startAnimation(fadeoutani);
        } catch (Exception ee) {
            Log.d("Exception", "getMessage: " + ee
                    .getMessage());
            ee.printStackTrace();
        }

    }


    public void Show_add() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }


    @Override
    public void onDataSent(String yourData) {

        try {

            int comment_count = Integer.parseInt(yourData);
            Home_Get_Set item = data_list.get(currentPage);
            item.video_comment_count = "" + comment_count;
            data_list.remove(currentPage);
            data_list.add(currentPage, item);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    // this will call when go to the home tab From other tab.
    // this is very importent when for video play and pause when the focus is changes
    boolean is_visible_to_user;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {

            is_visible_to_user = isVisibleToUser;

            if (privious_player != null && (isVisibleToUser && !is_user_stop_video)) {
                privious_player.setPlayWhenReady(true);
            } else if (privious_player != null && !isVisibleToUser) {
                privious_player.setPlayWhenReady(false);
            }
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    // when we swipe for another video this will relaese the privious player
    SimpleExoPlayer privious_player;

    public void Release_Privious_Player() {
        if (privious_player != null) {
            privious_player.removeListener(this);
            privious_player.release();
        }
    }


    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set) {


        try {

            String action = home_get_set.liked;

            if (action.equals("1")) {
                action = "0";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) - 1);
            } else {
                action = "1";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) + 1);
            }


            data_list.remove(position);
            home_get_set.liked = action;
            data_list.add(position, home_get_set);
            adapter.notifyDataSetChanged();

            Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action, new API_CallBack() {

                @Override
                public void ArrayData(ArrayList arrayList) {

                }

                @Override
                public void OnSuccess(String responce) {

                }

                @Override
                public void OnFail(String responce) {

                }
            });
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }

    }


    // this will open the comment screen
    private void OpenComment(Home_Get_Set item) {


        try {
            int comment_counnt = Integer.parseInt(item.video_comment_count);

            Fragment_Data_Send fragment_data_send = this;

            Comment_F comment_f = new Comment_F(comment_counnt, fragment_data_send);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("video_id", item.video_id);
            args.putString("user_id", item.user_id);
            comment_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, comment_f).commit();

        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }


    }

    public void openDiscoverFragment() {

        try {

            Fragment_Data_Send fragment_data_send = this;

            Discover_F discover_F = new Discover_F();
            FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        /*Bundle args = new Bundle();
        args.putString("video_id", item.video_id);
        args.putString("user_id", item.user_id);
        comment_f.setArguments(args);*/
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, discover_F).commit();
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item, boolean from_right_to_left) {

        try {

            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.user_id)) {

                TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                profile.select();

            } else {
                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        //Call_Api_For_Singlevideos(currentPage);
                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                if (from_right_to_left) {
                    if (privious_player != null) privious_player.setPlayWhenReady(false);
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                } else {
                    if (privious_player != null) privious_player.setPlayWhenReady(false);
                    transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                }

                Bundle args = new Bundle();
                args.putString("user_id", item.user_id);
                args.putString("user_name", item.first_name + " " + item.last_name);
                args.putString("user_pic", item.profile_pic);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, profile_f).commit();
            }

        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenHashtag(String tag) {

        try {

            Taged_Videos_F taged_videos_f = new Taged_Videos_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("tag", tag);
            taged_videos_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, taged_videos_f).commit();


        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    private void Show_video_option(final Home_Get_Set home_get_set) {
        try {


            String user_id = Variables.sharedPreferences.getString(Variables.u_id, "0");
            final String[] options;
            if (home_get_set.user_id.equals(user_id))
                options = new String[]{"Save Video", "Delete Video", "Cancel"};
            else
                options = new String[]{"Save Video", "Flag Video", "Cancel"};

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

            builder.setTitle(null);

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {
                    switch (options[item]) {
                        case "Save Video":
                            if (Functions.Checkstoragepermision(getActivity()))

                                Save_Video(home_get_set);
                            break;
                        case "Flag Video":

                            try {

                                flagVideoActivity(home_get_set);
                                dialog.dismiss();

                            } catch (Exception e) {
                                Log.d("Exception", "getMessage: " + e
                                        .getMessage());
                                e.printStackTrace();
                            }
                            break;
                        case "Delete Video":
                            deleteVideo(home_get_set);
                            dialog.dismiss();
                            break;
                        default:
                            dialog.dismiss();
                            break;
                    }
                }

            });

            builder.show();

        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }

    private void deleteVideo(final Home_Get_Set item) {

        try {


            onPause();
            Functions.Show_loader(context, false, false);
            Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
                @Override
                public void ArrayData(ArrayList arrayList) {

                }

                @Override
                public void OnSuccess(String responce) {
                    Toast.makeText(context, "Deleted video successfully", Toast.LENGTH_SHORT).show();
                    Call_Api_For_get_Allvideos();
                }

                @Override
                public void OnFail(String responce) {

                }
            });
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }

    public void flagVideoActivity(Home_Get_Set home_get_set) {

        try {

            Intent intent = new Intent(context, ReportVideo.class);
            intent.putExtra("FLAG_OPTIONS", "REPORT_VIDEO");
            intent.putExtra("VIDEO_ITEM", home_get_set);
            startActivity(intent);
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
/*        final Dialog dialog = new Dialog(Objects.requireNonNull(getActivity()));
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_flag_video);

        dialog.show();*/
    }


    public void Save_Video(final Home_Get_Set item) {


        try {


            if (privious_player != null) {
                privious_player.setPlayWhenReady(false);
            }

            JSONObject params = new JSONObject();
            try {
                params.put("video_id", item.video_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Functions.Show_loader(context, false, false);
            ApiRequest.Call_Api(context, Variables.downloadFile, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    Log.d("save_video", "Responce: " + resp);
                    Functions.cancel_loader();
                    try {
                        JSONObject responce = new JSONObject(resp);
                        String code = responce.optString("code");
                        if (code.equals("200")) {
                            JSONArray msg = responce.optJSONArray("msg");
                            JSONObject jsonObject = msg.optJSONObject(0);
                            String download_url = jsonObject.getString("download_url");
                            Log.d("Url", "Responce: " + download_url);

                            if (download_url != null) {

                                Functions.Show_determinent_loader(context, false, false);
                                PRDownloader.initialize(getActivity().getApplicationContext());
                                DownloadRequest prDownloader = PRDownloader.download(download_url, Variables.app_folder, item.video_id + ".mp4")
                                        .build()
                                        .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                                            @Override
                                            public void onStartOrResume() {

                                            }
                                        })
                                        .setOnPauseListener(new OnPauseListener() {
                                            @Override
                                            public void onPause() {

                                            }
                                        })
                                        .setOnCancelListener(new OnCancelListener() {
                                            @Override
                                            public void onCancel() {

                                            }
                                        })
                                        .setOnProgressListener(new OnProgressListener() {
                                            @Override
                                            public void onProgress(Progress progress) {

                                                int prog = (int) ((progress.currentBytes * 100) / progress.totalBytes);
                                                Functions.Show_loading_progress(prog);

                                            }
                                        });


                                prDownloader.start(new OnDownloadListener() {
                                    @Override
                                    public void onDownloadComplete() {
                                        Functions.cancel_determinent_loader();
                                        Scan_file(item);
                                        if (privious_player != null) {
                                            privious_player.setPlayWhenReady(true);
                                        }
                                        Toast.makeText(context, "Video Saved", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(Error error) {
                                        Delete_file_no_watermark(item);
                                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                        Functions.cancel_determinent_loader();
                                    }


                                });

                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });


        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    public void Delete_file_no_watermark(Home_Get_Set item) {

        try {
            File file = new File(Variables.app_folder + item.video_id + ".mp4");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }

    public void Scan_file(Home_Get_Set item) {

        try {

            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{Variables.app_folder + item.video_id + ".mp4"},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {

                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    public boolean is_fragment_exits() {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 0) {
            return false;
        } else {
            return true;
        }

    }

    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onResume() {
        super.onResume();

        try {

            if ((privious_player != null && (is_visible_to_user && !is_user_stop_video)) && !is_fragment_exits()) {
                privious_player.setPlayWhenReady(true);
            }
        } catch (Exception e) {
            Log.d("Exception", "getMessage: " + e
                    .getMessage());
            e.printStackTrace();
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (privious_player != null) {
            privious_player.release();
        }
    }


    public boolean check_permissions() {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 2);
        } else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    // Bottom all the function and the Call back listener of the Expo player
    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }


    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }


    @Override
    public void onLoadingChanged(boolean isLoading) {

    }


    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

        if (playbackState == Player.STATE_BUFFERING) {
            p_bar.setVisibility(View.VISIBLE);
        } else if (playbackState == Player.STATE_READY) {
            p_bar.setVisibility(View.GONE);
        }


    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }

}
