package com.tachyon.bindaas.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Adapter_Click_Listener;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tachyon.bindaas.Discover.Discover_F.search_edit;


/**
 * A simple {@link Fragment} subclass.
 */
public class Search_F extends RootFragment {

    View view;
    Context context;
    String type, key;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;

    public Search_F(String type) {
        this.type = type;
        this.key = key;
    }

    public Search_F() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_search, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        context = getContext();
        try {
            shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
            shimmerFrameLayout.startShimmer();

            recyclerView = view.findViewById(R.id.recylerview);
            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            //recyclerView.setLayoutManager(linearLayoutManager);

            Call_Api();

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Call_Api();
    }

    public void Call_Api() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id", Variables.user_id);
            params.put("type", type);
            params.put("keyword", search_edit.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.search, params, new Callback() {
            @Override
            public void Responce(String resp) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (type.equalsIgnoreCase("users"))
                    Parse_users(resp);

                if (type.equals("video"))
                    Parse_video(resp);


            }
        });

    }


    ArrayList<Object> data_list;

    public void Parse_users(String responce) {

        data_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equalsIgnoreCase("200")) {

                JSONArray msg = jsonObject.optJSONArray("msg");
                for (int i = 0; i < msg.length(); i++) {
                    JSONObject data = msg.optJSONObject(i);

                    Users_Model user = new Users_Model();
                    user.user_id = data.optString("user_id");
                    user.username = data.optString("username");
                    user.first_name = data.optString("first_name");
                    user.last_name = data.optString("last_name");
                    user.gender = data.optString("gender");
                    user.profile_pic = data.optString("profile_pic");
                    user.signup_type = data.optString("signup_type");
                    user.videos = data.optString("videos");

                    data_list.add(user);


                }

                if (data_list.isEmpty()) {
                    view.findViewById(R.id.no_data_image).setVisibility(View.VISIBLE);
                } else
                    view.findViewById(R.id.no_data_image).setVisibility(View.GONE);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                recyclerView.setLayoutManager(linearLayoutManager);
                Users_Adapter adapter = new Users_Adapter(context, data_list, new Adapter_Click_Listener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {

                        Users_Model item = (Users_Model) object;
                        Open_Profile(item.user_id, item.first_name, item.last_name, item.profile_pic);


                    }
                });
                recyclerView.setAdapter(adapter);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void Parse_video(String responce) {

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

                if (data_list.isEmpty()) {
                    view.findViewById(R.id.no_data_image).setVisibility(View.VISIBLE);
                } else
                    view.findViewById(R.id.no_data_image).setVisibility(View.GONE);

                GridLayoutManager linearLayoutManager = new GridLayoutManager(context, 2);
                recyclerView.setLayoutManager(linearLayoutManager);
                VideosList_Adapter adapter = new VideosList_Adapter(context, data_list, new Adapter_Click_Listener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {

                        Home_Get_Set item = (Home_Get_Set) object;
                        OpenWatchVideo(item.video_id);
                        /*if (view.getId() == R.id.watch_btn) {
                            OpenWatchVideo(item.video_id);
                        } else {
                            Open_Profile(item.user_id, item.first_name, item.last_name, item.profile_pic);
                        }*/

                    }
                });
                recyclerView.setAdapter(adapter);


            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void OpenWatchVideo(String video_id) {
        try {

            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("video_id", video_id);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Profile(String user_id, String first_name, String last_name, String profile_pic) {
        try {
            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(user_id)) {

                getActivity().onBackPressed();
                //getActivity().onBackPressed();

                TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                profile.select();

            } else {

                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {

                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
                Bundle args = new Bundle();
                args.putString("user_id", user_id);
                args.putString("user_name", first_name + " " + last_name);
                args.putString("user_pic", profile_pic);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.Search_Main_F, profile_f).commit();

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }

}
