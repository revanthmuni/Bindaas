package com.tachyon.bindaas.Profile.UserVideos;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Home.MessageEvent;
import com.tachyon.bindaas.Profile.MyVideos_Adapter;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserVideo_F extends Fragment {
    private static final String TAG = "NewsFeedFragment";

    public RecyclerView recyclerView;
    ArrayList<Home_Get_Set> data_list;
    MyVideos_Adapter adapter;
    View view;
    Context context;
    String user_id;

    RelativeLayout no_data_layout;
    NewVideoBroadCast mReceiver;

    public UserVideo_F() {

    }

    boolean is_my_profile=true;
    String video_type="public";

    @SuppressLint("ValidFragment")
    public UserVideo_F(boolean is_my_profile, String user_id) {
        this.is_my_profile = is_my_profile;
        this.user_id = user_id;
    }
    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventTrggered(MessageEvent item){
        Log.d(TAG, "onEventTrggered:NewsFeed "+new Gson().toJson(item));
        user_id = item.getUser_id();
        Call_Api_For_get_Allvideos();
    }
    private class NewVideoBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Variables.Reload_my_videos_inner=false;
            Call_Api_For_get_Allvideos();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoUploadService(String res) {
        Call_Api_For_get_Allvideos();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_user_video, container, false);
            context = getContext();
            Log.d(TAG, "onCreateView: after User id"+user_id);
        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }

        try {


            recyclerView = view.findViewById(R.id.recylerview);
            final GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);


            data_list = new ArrayList<>();
            adapter = new MyVideos_Adapter(context, data_list, new MyVideos_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int postion, Home_Get_Set item, View view) {

                    OpenWatchVideo(postion);

                }
            });

            recyclerView.setAdapter(adapter);

            no_data_layout = view.findViewById(R.id.no_data_layout);


            Call_Api_For_get_Allvideos();
            mReceiver = new NewVideoBroadCast();
            getActivity().registerReceiver(mReceiver, new IntentFilter("newVideo"));
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;

    }

    Boolean isVisibleToUser = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            this.isVisibleToUser = isVisibleToUser;
            if (view != null && isVisibleToUser) {
                Call_Api_For_get_Allvideos();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            EventBus.getDefault().register(this);
            if ((view != null && isVisibleToUser) && !is_api_run) {
                Call_Api_For_get_Allvideos();
            } else if ((view != null && Variables.Reload_my_videos_inner) && !is_api_run) {
                Variables.Reload_my_videos_inner = false;
                Call_Api_For_get_Allvideos();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onPause() {
        super.onPause();
        try {
            EventBus.getDefault().unregister(this);
        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(mReceiver!=null) {
            getActivity().unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }
    Boolean is_api_run = false;

    //this will get the all videos data of user and then parse the data
    private void Call_Api_For_get_Allvideos() {
        try {
            is_api_run = true;
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("my_user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
                if (is_my_profile) {
                    parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
                } else {
                    parameters.put("user_id", user_id);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequest.Call_Api(context, Variables.showMyAllVideos, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    is_api_run = false;
                    Parse_data(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    public void Parse_data(String responce) {

        data_list.clear();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                JSONObject data = msgArray.getJSONObject(0);
                JSONObject user_info = data.optJSONObject("user_info");


                JSONArray user_videos = data.getJSONArray("user_videos");
                if (!user_videos.toString().equals("[" + "]")) {

                    no_data_layout.setVisibility(View.GONE);

                    for (int i = 0; i < user_videos.length(); i++) {
                        JSONObject itemdata = user_videos.optJSONObject(i);

                        Home_Get_Set item = new Home_Get_Set();
                        item.user_id = itemdata.optString("user_id");
                        Log.d("USR_ID::CHECK:", "Parse_data: in UserFragment:"+itemdata.optString("user_id"));

//                        JSONObject user_info1 = itemdata.optJSONObject("user_info");

                        JSONObject follow_status = itemdata.optJSONObject("follow_Status");
                        item.follow_status_button = follow_status.optString("follow_status_button");
                        item.follow = follow_status.optString("follow");

                        item.first_name = user_info.optString("first_name");
                        item.last_name = user_info.optString("last_name");
                        item.profile_pic = user_info.optString("profile_pic");
                        item.verified = user_info.optString("verified");

                        Log.d("resp", item.user_id + " " + item.first_name);

                        JSONObject count = itemdata.optJSONObject("count");
                        item.like_count = count.optString("like_count");
                        item.video_comment_count = count.optString("video_comment_count");
                        item.views = count.optString("view");

                        JSONObject sound_data = itemdata.optJSONObject("sound");
                        item.sound_id = sound_data.optString("id");
                        item.sound_name = sound_data.optString("sound_name");
                        item.sound_pic = sound_data.optString("thum");
                        if (sound_data != null) {
                            JSONObject audio_path = sound_data.optJSONObject("audio_path");
                            item.sound_url_mp3 = audio_path.optString("mp3");
                            item.sound_url_acc = audio_path.optString("aac");
                        }


                        item.privacy_type=itemdata.optString("privacy_type");
                        item.allow_comments=itemdata.optString("allow_comments");
                        item.video_id = itemdata.optString("id");
                        item.liked = itemdata.optString("liked");
                        item.gif = itemdata.optString("gif");
                        item.video_url = itemdata.optString("video");
                        item.thum = itemdata.optString("thum");
                        item.created_date = itemdata.optString("created");

                        item.video_description = itemdata.optString("description");
                        JSONArray tagged_users = itemdata.optJSONArray("tagged_users");
                        Log.d("TAG::>", "In UserF Parse_data: tagged users : "+tagged_users.toString());
                        item.tagged_users = tagged_users.toString();

                        data_list.add(item);
                    }

                   // Toast.makeText(context, ""+data_list.size(), Toast.LENGTH_SHORT).show();
                    //myvideo_count = data_list.size();

                } else {
                    no_data_layout.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();

            } else {
                //Toast.makeText(context, ""+jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void OpenWatchVideo(int postion) {
        try {
            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("arraylist", data_list);
            intent.putExtra("position", postion);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


}
