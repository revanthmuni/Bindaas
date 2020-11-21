package com.tachyon.bindaas.WatchVideos;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.DefaultLoadControl;
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
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.tachyon.bindaas.Comments.Comment_F;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.KeyBoard.KeyboardHeightObserver;
import com.tachyon.bindaas.KeyBoard.KeyboardHeightProvider;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Notifications.Notification_Adapter;
import com.tachyon.bindaas.Notifications.Notification_Get_Set;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Data_Send;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.VideoSound_A;
import com.tachyon.bindaas.Taged.Taged_Videos_F;
import com.tachyon.bindaas.Taged.TaggedUsersList;
import com.tachyon.bindaas.VideoAction.VideoAction_F;
import com.tachyon.bindaas.Video_Recording.Video_Recoder_Duet_A;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.facebook.FacebookSdk.getApplicationContext;

public class WatchSingleVideoFragment extends RootFragment implements Player.EventListener,
        KeyboardHeightObserver, View.OnClickListener, Fragment_Data_Send {
    Context context;
    View view;

    RecyclerView recyclerView;
    public static ArrayList<Home_Get_Set> data_list;
    int position = 0;
    int currentPage = -1;
    LinearLayoutManager layoutManager;

    public static Watch_Videos_Adapter adapter;

    ProgressBar p_bar;

   // private KeyboardHeightProvider keyboardHeightProvider;

    RelativeLayout write_layout;


    EditText message_edit;
    ImageButton send_btn;
    ProgressBar send_progress;


    String video_id;
    String link;

    public WatchSingleVideoFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = getContext();
        try {
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_watchvideo, container, false);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        try {

            p_bar = view.findViewById(R.id.p_bar);
            Bundle bundle = getArguments();
            if (bundle!=null){
                video_id = bundle.getString("video_id");
                Call_Api_For_get_Allvideos(video_id);
            }

            view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onBackPressed();

                }
            });


            write_layout = view.findViewById(R.id.write_layout);
            message_edit = view.findViewById(R.id.message_edit);
            send_btn = view.findViewById(R.id.send_btn);
            send_btn.setOnClickListener(this);

            send_progress = view.findViewById(R.id.send_progress);

            //keyboardHeightProvider = new KeyboardHeightProvider(getActivity());



        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    // Bottom two function will call the api and get all the videos form api and parse the json data
    private void Call_Api_For_get_Allvideos(String id) {
        try {

            if (MainMenuActivity.token == null)
                MainMenuActivity.token = FirebaseInstanceId.getInstance().getToken();

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("token", MainMenuActivity.token);
                parameters.put("video_id", id);
                parameters.put("type", "related");
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ApiRequest.Call_Api(context, Variables.showAllVideos, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Parse_data(resp);
                }
            });

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Parse_data(String responce) {
        Log.d("username_check", "Parse_data: " + responce);
        try {
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
                        item.username = Objects.requireNonNull(user_info).optString("username");
                        item.first_name = user_info.optString("first_name", context.getResources().getString(R.string.app_name));
                        item.last_name = user_info.optString("last_name", "User");
                        item.profile_pic = user_info.optString("profile_pic", "null");
                        item.verified = user_info.optString("verified");

                        JSONObject follow_status = itemdata.optJSONObject("follow_Status");
                        item.follow = follow_status.optString("follow");
                        item.follow_status_button = follow_status.optString("follow_status_button");

                        item.video_description = itemdata.optString("description");
                        JSONArray tagged_users = itemdata.optJSONArray("tagged_users");
                        Log.d("TAG::>", "In UserF Parse_data: tagged users : " + tagged_users.toString());
                        item.tagged_users = tagged_users.toString();

                        JSONObject sound_data = itemdata.optJSONObject("sound");
                        item.sound_id = Objects.requireNonNull(sound_data).optString("id");
                        item.sound_name = sound_data.optString("sound_name");
                        item.sound_pic = sound_data.optString("thum");
                        if (sound_data != null) {
                            JSONObject audio_path = sound_data.optJSONObject("audio_path");
                            item.sound_url_mp3 = Objects.requireNonNull(audio_path).optString("mp3");
                            item.sound_url_acc = audio_path.optString("aac");
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
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Set_Adapter() {
        try {
            recyclerView = view.findViewById(R.id.recylerview);
            layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);

            SnapHelper snapHelper = new PagerSnapHelper();
            snapHelper.attachToRecyclerView(recyclerView);


            adapter = new Watch_Videos_Adapter(context, data_list, new Watch_Videos_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int postion, final Home_Get_Set item, View view) {

                    switch (view.getId()) {

                        case R.id.user_pic:
                            onPause();
                            Log.d("USR_TST", "onItemClick: " + item.user_id);

                            OpenProfile(item, false);
                            break;

                        case R.id.like_layout:
                            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                                Like_Video(postion, item);
                            } else {
                                Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                            }
                            break;

                        case R.id.comment_layout:
                            OpenComment(item, postion);
                            break;

                        case R.id.shared_layout:
                            if (privious_player != null) {
                                privious_player.setPlayWhenReady(false);
                            }
                            final VideoAction_F fragment = new VideoAction_F(item.video_id, new Fragment_Callback() {
                                @Override
                                public void Responce(Bundle bundle) {

                                    if (bundle.getString("action").equals("save")) {
                                        Save_Video(item);
                                    } else if (bundle.getString("action").equals("duet")) {
                                        Duet_video(item);
                                    }
                                    if (bundle.getString("action").equals("delete")) {

                                        Functions.Show_loader(context, false, false);
                                        Functions.Call_Api_For_Delete_Video(getActivity(), item.video_id, new API_CallBack() {
                                            @Override
                                            public void ArrayData(ArrayList arrayList) {

                                            }

                                            @Override
                                            public void OnSuccess(String responce) {

                                                Functions.cancel_loader();
                                                getActivity().finish();

                                            }

                                            @Override
                                            public void OnFail(String responce) {

                                            }
                                        });

                                    }
                                }
                            });

                            Bundle bundle = new Bundle();
                            bundle.putString("video_id", item.video_id);
                            bundle.putString("user_id", item.user_id);
                            bundle.putSerializable("data", item);
                            fragment.setArguments(bundle);

                            fragment.show(getActivity().getSupportFragmentManager(), "");

                            break;


                        case R.id.sound_image_layout:
                            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                                if (check_permissions()) {
                                    Intent intent = new Intent(context, VideoSound_A.class);
                                    intent.putExtra("data", item);
                                    startActivity(intent);
                                }
                            } else {
                                Toast.makeText(context, "Please Login.", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.tagged_users:
                            onPause();
                            showTaggedUsers(item);
                        case R.id.add_follow:
                            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                                addToFollow(item, postion);
                            } else {
                                Toast.makeText(context, "Please Login to Follow", Toast.LENGTH_SHORT).show();
                                // Open_Login();
                            }
                            //caddToFollow(item, postion);
                            break;
                    }

                }
            });

            adapter.setHasStableIds(true);
            recyclerView.setAdapter(adapter);


            // this is the scroll listener of recycler view which will tell the current item number
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                        Privious_Player();
                        Set_Player(currentPage);
                    }

                }
            });

            recyclerView.scrollToPosition(position);

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void addToFollow(Home_Get_Set item, int postion) {
        try {
            final String send_status;

            if (item.follow_status_button.equals("UnFollow")
                    || item.follow_status_button.equals("Friends")) {
                send_status = "0";
            } else {
                send_status = "1";
            }

            Functions.Call_Api_For_Follow_or_unFollow(getActivity(),
                    Variables.sharedPreferences.getString(Variables.u_id, ""),
                    item.user_id,
                    send_status,
                    new API_CallBack() {
                        @Override
                        public void ArrayData(ArrayList arrayList) {

                        }

                        @Override
                        public void OnSuccess(String responce) {
                            try {
                                JSONObject jsonObject = new JSONObject(responce);
                                String code = jsonObject.optString("code");
                                if (code.equals("200")) {
                                    JSONArray msgArray = jsonObject.getJSONArray("msg");
                                    for (int i = 0; i < msgArray.length(); i++) {
                                        JSONObject profile_data = msgArray.optJSONObject(i);

                                        JSONObject follow_Status = profile_data.optJSONObject("follow_Status");

                                        String follow = follow_Status.optString("follow");
                                        String follow_status_button = follow_Status.optString("follow_status_button");
                                        Log.d("TTTT", "OnSuccess: " + follow);
                                        Log.d("TTTT", "OnSuccess: " + follow_status_button);
                                        item.follow = follow;
                                        item.follow_status_button = follow_status_button;

                                        data_list.remove(postion);
                                        data_list.add(postion, item);

                                        Functions.refreshAdapter(item.user_id, postion, follow, follow_status_button);
                                        //Functions.refreshWatchVideosAdapter(item.user_id,postion    ,follow,follow_status_button);
                                        //datalist.add(item);
                                        //adapter.notifyItemInserted(i);
                                    }
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void OnFail(String responce) {

                        }

                    });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void showTaggedUsers(Home_Get_Set item) {
        TaggedUsersList fragment = new TaggedUsersList();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
        Bundle args = new Bundle();
        args.putString("data", item.tagged_users);
        args.putString("flag","home");
        fragment.setArguments(args);
        transaction.addToBackStack(null);
        transaction.replace(R.id.MainMenuFragment, fragment).commit();
    }

    int privious_height = 0;

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        try {

            Log.d(Variables.tag, "" + height);
            if (height < 0) {
                privious_height = Math.abs(height);
                ;
            }
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(write_layout.getWidth(), write_layout.getHeight());
            params.bottomMargin = height + privious_height;
            write_layout.setLayoutParams(params);

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.send_btn:
                    if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                        String comment_txt = message_edit.getText().toString();
                        if (!TextUtils.isEmpty(comment_txt)) {
                            Send_Comments(data_list.get(currentPage).user_id, data_list.get(currentPage).video_id, comment_txt);
                        }
                    } else {
                        Toast.makeText(context, "Please Login into app", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onDataSent(String yourData) {
        try {
            int comment_count = Integer.parseInt(yourData);
            Home_Get_Set item = data_list.get(currentPage);
            item.video_comment_count = "" + comment_count;
            data_list.add(currentPage, item);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Set_Player(final int currentPage) {
        try {
            final Home_Get_Set item = data_list.get(currentPage);
            DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(1 * 1024, 1 * 1024, 500, 1024).createDefaultLoadControl();

            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            final SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                    Util.getUserAgent(context, context.getResources().getString(R.string.app_name)));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(item.video_url));

            if (!Variables.is_secure_info)
                Log.d(Variables.tag, item.video_url);


            player.prepare(videoSource);

            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(this);


            View layout = layoutManager.findViewByPosition(currentPage);
            PlayerView playerView = layout.findViewById(R.id.playerview);
            playerView.setPlayer(player);


            player.setPlayWhenReady(true);
            privious_player = player;


            final RelativeLayout mainlayout = layout.findViewById(R.id.mainlayout);
            playerView.setOnTouchListener(new View.OnTouchListener() {
                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                    private static final int SWIPE_THRESHOLD = 0;
                    private static final int SWIPE_VELOCITY_THRESHOLD = 0;

                    @Override
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
                            privious_player.setPlayWhenReady(true);
                        } else {
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
                            privious_player.setPlayWhenReady(true);
                        }

                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {

                            Show_heart_on_DoubleTap(item, mainlayout, e);
                            Like_Video(currentPage, item);

                        } else {
                            Toast.makeText(context, "Please Login into ", Toast.LENGTH_SHORT).show();
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

            TextView desc_txt = layout.findViewById(R.id.desc_txt);
            HashTagHelper.Creator.create(context.getResources().getColor(R.color.colorAccent),
                    new HashTagHelper.OnHashTagClickListener() {
                        @Override
                        public void onHashTagClicked(String hashTag) {

                            OpenHashtag(hashTag);

                        }
                    }).handle(desc_txt);


            LinearLayout soundimage = (LinearLayout) layout.findViewById(R.id.sound_image_layout);
//        Animation aniRotate = AnimationUtils.loadAnimation(context,R.anim.d_clockwise_rotation);
//        soundimage.startAnimation(aniRotate);

            if (Variables.sharedPreferences.getBoolean(Variables.islogin, false))
                Functions.Call_Api_For_update_view(getActivity(), item.video_id);

            if (item.allow_comments != null && item.allow_comments.equalsIgnoreCase("false")) {
                write_layout.setVisibility(View.INVISIBLE);
            } else {
                write_layout.setVisibility(View.VISIBLE);
            }

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // when we swipe for another video this will relaese the privious player
    public static SimpleExoPlayer privious_player;

    public void Privious_Player() {
        try {

            if (privious_player != null) {
                privious_player.removeListener(this);
                privious_player.release();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

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
                    }, 0);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            iv.startAnimation(fadeoutani);
        } catch (Exception ee) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), ee.getMessage());

        }
    }


    // this function will call for like the video and Call an Api for like the video
    public void Like_Video(final int position, final Home_Get_Set home_get_set) {
        try {
            String action;

            if (home_get_set.liked.equals("1")) {
                action = "0";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) - 1);
            } else if (home_get_set.liked.equals("0")) {
                action = "1";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) + 1);
            } else {
                action = "0";
                home_get_set.like_count = "" + (Integer.parseInt(home_get_set.like_count) - Integer.parseInt(home_get_set.liked));
                //Toast.makeText(context, "liked count can't be > 1", Toast.LENGTH_SHORT).show();
            }

            data_list.remove(position);
            home_get_set.liked = action;
            data_list.add(position, home_get_set);
            adapter.notifyDataSetChanged();


            Functions.Call_Api_For_like_video(getActivity(), home_get_set.video_id, action, home_get_set.user_id, new API_CallBack() {

                @Override
                public void ArrayData(ArrayList arrayList) {

                }

                @Override
                public void OnSuccess(String responce) {

                    EventBus.getDefault().post("done");
                    Log.d("Test Call_Api", "OnSuccess:Call_Api_For_like_video " + responce);
                }

                @Override
                public void OnFail(String responce) {

                    EventBus.getDefault().post("done");
                    Log.d("Test Call_Api", "OnFail:Call_Api_For_like_video " + responce);

                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

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
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 2);
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


    // this will open the comment screen
    public void OpenComment(Home_Get_Set item, int position) {
        try {
            if (privious_player != null) {
                privious_player.setPlayWhenReady(false);
            }

            int comment_count = Integer.parseInt(item.video_comment_count);
            Fragment_Data_Send fragment_data_send = this;

            Comment_F comment_f = new Comment_F(comment_count, fragment_data_send);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("video_id", item.video_id);
            args.putString("user_id", item.user_id);
            args.putString("flag", "watch_videos");
            args.putInt("position", position);
            args.putString("extras", new Gson().toJson(item));

            comment_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.WatchVideo_F, comment_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will open the profile of user which have uploaded the currenlty running video
    private void OpenProfile(Home_Get_Set item, boolean from_right_to_left) {
        try {
            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.user_id)) {
                onBackPressed();
                TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                if (profile != null) {
                    profile.select();
                }
//            profile.select();

            } else {

                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {

//                        Call_Api_For_Singlevideos(currentPage);

                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                if (from_right_to_left) {
                    if (privious_player != null) privious_player.setPlayWhenReady(false);
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                } else
                    transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);

                Bundle args = new Bundle();
                Log.d("USR_TST", "OpenProfile: " + item.user_id);
                args.putString("user_id", item.user_id);
                args.putString("user_name", item.first_name + " " + item.last_name);
                args.putString("user_pic", item.profile_pic);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.WatchVideo_F, profile_f).commit();

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


    public void Send_Comments(final String user_id, String video_id, final String comment) {
        try {
            send_progress.setVisibility(View.VISIBLE);
            send_btn.setVisibility(View.GONE);

            Functions.Call_Api_For_Send_Comment(getActivity(), video_id, comment, new API_CallBack() {
                @Override
                public void ArrayData(ArrayList arrayList) {

                    message_edit.setText(null);
                    send_progress.setVisibility(View.GONE);
                    send_btn.setVisibility(View.VISIBLE);

                    int comment_count = Integer.parseInt(data_list.get(currentPage).video_comment_count);
                    comment_count++;
                    onDataSent("" + comment_count);


                }

                @Override
                public void OnSuccess(String responce) {

                }

                @Override
                public void OnFail(String responce) {

                }
            });

            SendPushNotification(user_id, comment);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Duet_video(final Home_Get_Set item) {

        Log.d(Variables.tag, item.video_url);
        if (item.video_url != null) {

            Functions.Show_determinent_loader(context, false, false);
            PRDownloader.initialize(getApplicationContext());
            DownloadRequest prDownloader = PRDownloader.download(item.video_url, Variables.app_folder, item.video_id + ".mp4")
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
                    Open_duet_Recording(item);
                }

                @Override
                public void onError(Error error) {

                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                    Functions.cancel_determinent_loader();
                }


            });

        }

    }

    public void Open_duet_Recording(Home_Get_Set item) {
        Intent intent = new Intent(context, Video_Recoder_Duet_A.class);
        intent.putExtra("data", item);
        startActivity(intent);
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
            transaction.replace(R.id.WatchVideo_F, taged_videos_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    CharSequence[] options;

    private void Show_video_option(final Home_Get_Set home_get_set) {
        try {
            options = new CharSequence[]{"Save Video", "Cancel"};

            if (home_get_set.user_id.equals(Variables.sharedPreferences.getString(Variables.u_id, "")))
                options = new CharSequence[]{"Save Video", "Delete Video", "Cancel"};

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

            builder.setTitle(null);

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Save Video")) {
                        if (Functions.Checkstoragepermision(getActivity()))

                            Save_Video(home_get_set);

                    } else if (options[item].equals("Delete Video")) {
                        if (Variables.is_secure_info) {
                            Toast.makeText(context, getString(R.string.delete_function_not_available_in_demo), Toast.LENGTH_SHORT).show();
                        } else {
                            Functions.Show_loader(context, false, false);
                            Functions.Call_Api_For_Delete_Video(getActivity(), home_get_set.video_id, new API_CallBack() {
                                @Override
                                public void ArrayData(ArrayList arrayList) {

                                }

                                @Override
                                public void OnSuccess(String responce) {

                                    Functions.cancel_loader();
                                    getActivity().finish();

                                }

                                @Override
                                public void OnFail(String responce) {

                                }
                            });
                        }
                    } else if (options[item].equals("Cancel")) {

                        dialog.dismiss();

                    }

                }

            });

            builder.show();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
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
                    Functions.cancel_loader();
                    try {
                        JSONObject responce = new JSONObject(resp);
                        String code = responce.optString("code");
                        if (code.equals("200")) {
                            JSONArray msg = responce.optJSONArray("msg");
                            JSONObject jsonObject = msg.optJSONObject(0);
                            String download_url = jsonObject.getString("download_url");

                            if (download_url != null) {

                                Functions.Show_determinent_loader(context, false, false);
                                PRDownloader.initialize(getApplicationContext());
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
                                        //Delete_file_no_watermark(item);
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
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


    public void Delete_file_no_watermark(Home_Get_Set item) {
        try {
            File file = new File(Variables.app_folder + item.video_id + ".mp4");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Scan_file(Home_Get_Set item) {
        try {
            MediaScannerConnection.scanFile(context,
                    new String[]{Variables.app_folder + item.video_id + ".mp4"},
                    null,
                    new MediaScannerConnection.OnScanCompletedListener() {

                        public void onScanCompleted(String path, Uri uri) {

                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void SendPushNotification(String user_id, String comment) {

        JSONObject notimap = new JSONObject();
        try {
            notimap.put("title", Variables.sharedPreferences.getString(Variables.u_name, "") + " Comment on your video");
            notimap.put("message", comment);
            notimap.put("icon", Variables.sharedPreferences.getString(Variables.u_pic, ""));
            notimap.put("senderid", Variables.sharedPreferences.getString(Variables.u_id, ""));
            notimap.put("receiverid", user_id);
            notimap.put("action_type", "comment");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.sendPushNotification, notimap, null);

    }


    // this is lifecyle of the Activity which is importent for play,pause video or relaese the player
    @Override
    public void onPause() {
        super.onPause();
        try {
            if (privious_player != null) {
                privious_player.setPlayWhenReady(false);
            }
            //keyboardHeightProvider.setKeyboardHeightObserver(null);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onStop() {
        super.onStop();
        try {
            if (privious_player != null) {
                privious_player.setPlayWhenReady(false);
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (privious_player != null) {
                privious_player.release();
            }

            //keyboardHeightProvider.close();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
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