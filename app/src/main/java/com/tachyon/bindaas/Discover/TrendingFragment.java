package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Profile.MyVideos_Adapter;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TrendingFragment extends Fragment {
    Context context;
    RecyclerView trendingRecyclerview;
    MyVideos_Adapter adapter;
    ArrayList<Home_Get_Set> data_list;

    public TrendingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getContext();
        // Inflate the layout for this fragment
        Log.d("FragmentCheck", "onCreateView: its trendig");
        View view = inflater.inflate(R.layout.fragment_trending, container, false);
        trendingRecyclerview = view.findViewById(R.id.trendingRecyclerview);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 3);
        trendingRecyclerview.setLayoutManager(layoutManager);
        trendingRecyclerview.setHasFixedSize(true);

        data_list = new ArrayList<>();
//        loadDummyData();
        loadTrendigVideos();


        return view;
    }

    private void loadDummyData() {
        data_list = new ArrayList<>();

        Home_Get_Set item1 = new Home_Get_Set();
        item1.views = "16";
        item1.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601715164_301882676.jpg";
        data_list.add(item1);

        Home_Get_Set item2 = new Home_Get_Set();
        item2.views = "8";
        item2.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601565500_1834951108.jpg";
        data_list.add(item2);

        Home_Get_Set item3 = new Home_Get_Set();
        item3.views = "55";
        item3.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601553275_1301290917.jpg";
        data_list.add(item3);

        Home_Get_Set item4 = new Home_Get_Set();
        item4.views = "43";
        item4.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601552599_2125616103.jpg";
        data_list.add(item4);

        Home_Get_Set item5 = new Home_Get_Set();
        item5.views = "3";
        item5.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601553275_1301290917.jpg";
        data_list.add(item5);

        Home_Get_Set item6 = new Home_Get_Set();
        item6.views = "23";
        item6.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601552599_2125616103.jpg";
        data_list.add(item6);

        Home_Get_Set item7 = new Home_Get_Set();
        item7.views = "432";
        item7.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601553275_1301290917.jpg";
        data_list.add(item7);

        Home_Get_Set item8 = new Home_Get_Set();
        item8.views = "444";
        item8.thum = "https://bindaas-dev-data.s3.ap-south-1.amazonaws.com/thum/1601552599_2125616103.jpg";
        data_list.add(item8);

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

    private void loadTrendigVideos() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        ApiRequest.Call_Api(context, Variables.GET_TRENDING_VIDEOS, params, new Callback() {
            @Override
            public void Responce(String resp) {

                parseTrendingVideosData(resp);
            }
        });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    private void parseTrendingVideosData(String responce) {

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                ArrayList<Home_Get_Set> temp_list = new ArrayList();
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.user_id = itemdata.optString("user_id");

                    JSONObject follow_status = itemdata.optJSONObject("follow_Status");
                    item.follow = follow_status.optString("follow");
                    item.follow_status_button = follow_status.optString("follow_status_button");

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
                        item.sound_url_acc = audio_path.optString("aac");
                    }

                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");
                    item.views = count.optString("view");
                    item.privacy_type = itemdata.optString("privacy_type");
                    item.allow_comments = itemdata.optString("allow_comments");
                    item.allow_duet = itemdata.optString("allow_duet");
                    item.video_id = itemdata.optString("id");
                    item.views = itemdata.optString("view");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");

                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    JSONArray tagged_users = itemdata.optJSONArray("tagged_users");
                    Log.d("TAG::>", "Parse_data: trending video users : "+tagged_users.toString());
                    item.tagged_users = tagged_users.toString();
                    data_list.add(item);

                }
                adapter = new MyVideos_Adapter(context, data_list, new MyVideos_Adapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int postion, Home_Get_Set item, View view) {

                        OpenWatchVideo(postion);
                    }
                });
                trendingRecyclerview.setAdapter(adapter);
            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

           /* if (Variables.sharedPreferences.getBoolean(Variables.auto_scroll_key, false)) {
                autoScrollVideos();
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}