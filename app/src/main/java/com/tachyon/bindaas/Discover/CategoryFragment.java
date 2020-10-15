package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.hotspot2.pps.HomeSp;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.gson.JsonObject;
import com.tachyon.bindaas.Discover.Models.Category;
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CategoryFragment extends Fragment {

    Context context;
    RecyclerView categoryListRecycler,categoryDetailsRecycler;
    CategoryListAdapter listAdapter;
    List<String> catList;
    ArrayList<Home_Get_Set> data_list;
    MyVideos_Adapter adapter;
    private static final String TAG = "CategoryFragment";

    public CategoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("FragmentCheck", "onCreateView: its trendig");

        this.context = getActivity();
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        categoryListRecycler = view.findViewById(R.id.categoryListRecycler);
        categoryListRecycler.setLayoutManager(new LinearLayoutManager(context));
        categoryListRecycler.setHasFixedSize(true);

      //  loadDummyCatList();
       // loadDummyData();
        loadDiscoverySection();

        categoryDetailsRecycler = view.findViewById(R.id.categoryDetailsRecycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        categoryDetailsRecycler.setLayoutManager(layoutManager);
        categoryDetailsRecycler.setHasFixedSize(true);
        return view;
    }

    private void loadDiscoverySection() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_CATEGORIES_AND_VIDEOS, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    Log.d(TAG, "Responce: "+resp);
                    parseData(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseData(String resp) {
        try {
            JSONObject response = new JSONObject(resp);
            String code = response.optString("code");
            ArrayList<Category> catList = new ArrayList<>();
            if (code.equals("200")){
                JSONArray msg = response.getJSONArray("msg");
                for (int i = 0;i<msg.length();i++){
                    JSONObject category = msg.getJSONObject(i);
                    Category c = new Category();
                    c.setId(category.optString("id"));
                    c.setSection_name(category.optString("section_name"));
                    JSONArray discovery_videos = category.optJSONArray("discover_videos");
                    ArrayList<Home_Get_Set> listDiscoveryVideos = new ArrayList<>();
                    for (int j = 0;j<discovery_videos.length();j++){
                        JSONObject index = discovery_videos.optJSONObject(j);
                        Home_Get_Set item = new Home_Get_Set();
                        item.user_id = index.optString("user_id");
                        item.video_id = index.optString("id");

                        JSONObject user_info = index.optJSONObject("user_info");
                        item.first_name = user_info.optString("first_name");
                        item.last_name = user_info.optString("last_name");
                        item.profile_pic = user_info.optString("profile_pic");
                        item.username = user_info.optString("username");
                        item.verified = user_info.optString("verified");

                        JSONObject follow_status = index.optJSONObject("follow_Status");
                        item.follow_status_button = follow_status.optString("follow_status_button");
                        item.follow = follow_status.optString("follow");

                        JSONObject count = index.optJSONObject("count");
                        item.like_count = count.optString("like_count");
                        item.video_comment_count = count.optString("video_comment_count");

                        item.liked = index.optString("liked");
                        item.video_url = index.optString("video");
                        item.thum = index.optString("thum");
                        item.gif = index.optString("gif");
                        item.allow_comments = index.optString("allow_comments");
                        item.allow_duet = index.optString("allow_duet");
                        item.privacy_type = index.optString("privacy_type");
                        item.views = index.optString("view");
                        item.video_description = index.optString("description");

                        JSONObject sound = index.optJSONObject("sound");
                        item.sound_id = sound.optString("id");
                        JSONObject audio_path = sound.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("aac");
                        item.sound_name = sound.optString("sound_name");
                        item.sound_description = sound.optString("description");
                        item.sound_pic = sound.optString("thum");

                        item.created_date = index.optString("created");

                        /*JSONObject tagged_users = index.optJSONObject("tagged_users");
                        item.tagged_users = tagged_users.toString();
*/
                        listDiscoveryVideos.add(item);
                    }
                    c.setList(listDiscoveryVideos);
                    catList.add(c);
                }
            }
            listAdapter = new CategoryListAdapter(catList, context, new CategoryListAdapter.OnCategoryClick() {
                @Override
                public void gotoDetails(int position) {
                    openDetails(catList.get(position).getList());
                }
            });
            categoryListRecycler.setAdapter(listAdapter);
            if (catList.size()>0){
                openDetails(catList.get(0).getList());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openDetails(ArrayList<Home_Get_Set> list) {
       // Collections.shuffle(data_list);
        adapter = new MyVideos_Adapter(context, list, new MyVideos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Home_Get_Set item, View view) {
                //Toast.makeText(context, "sze "+list.size(), Toast.LENGTH_SHORT).show();
                OpenWatchVideo(list,postion);
            }
        });
        categoryDetailsRecycler.setAdapter(adapter);
    }
    private void OpenWatchVideo(ArrayList<Home_Get_Set> list, int postion) {
//        Toast.makeText(context, "it will open the videos page", Toast.LENGTH_SHORT).show();
        try {
            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("arraylist",list);
            intent.putExtra("position", postion);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }
    private void loadDummyCatList() {
        catList = new ArrayList<>();
        for (int i = 0;i<7;i++){
            catList.add("Cat->"+i);
        }
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
}