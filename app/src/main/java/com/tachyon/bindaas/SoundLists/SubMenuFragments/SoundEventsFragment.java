package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.Gson;
import com.tachyon.bindaas.Discover.Models.TopUsers;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.FileUtils;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Adapters.SoundEventsAdapter;
import com.tachyon.bindaas.SoundLists.Models.EventsModel;
import com.tachyon.bindaas.SoundLists.Models.GetEvents;
import com.tachyon.bindaas.SoundLists.SoundListActivity;
import com.tachyon.bindaas.SoundLists.SoundList_Main_A;
import com.tachyon.bindaas.Video_Recording.Trim_video_A;
import com.tachyon.bindaas.Video_Recording.Video_Recoder_A;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.tachyon.bindaas.Video_Recording.Video_Recoder_A.Sounds_list_Request_code;

public class SoundEventsFragment extends RootFragment {
    RecyclerView tops_recyclerview;
    Context context;
    SoundEventsAdapter adapter;
    List<EventsModel> list;
    private static final String TAG = "SoundEventsFragment";
    ShimmerFrameLayout shimmer_layout;
    public SoundEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_tops, container, false);
        this.context = getContext();

        list = new ArrayList<>();

        shimmer_layout = view.findViewById(R.id.shimmer_layout);
        shimmer_layout.startShimmer();
        tops_recyclerview = view.findViewById(R.id.tops_recyclerview);
        tops_recyclerview.setLayoutManager(new GridLayoutManager(context,2));
        loadEvents();

        return view;
    }

    private void loadEvents() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", Variables.sharedPreferences.getString(Variables.u_id,""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_EVENT_BANNERS, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    shimmer_layout.stopShimmer();
                    shimmer_layout.setVisibility(View.GONE);
                    parseEvents(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseEvents(String resp) {
        GetEvents users = new Gson().fromJson(resp, GetEvents.class);
        if (users.getCode().equals("200")){
            adapter = new SoundEventsAdapter(context, users.getEvents(), new SoundEventsAdapter.OnItemClick() {
                @Override
                public void onClick(int position, String section_id) {
                    openSoundsList(position,section_id);
                }
            });
            tops_recyclerview.setAdapter(adapter);
        }else{
            Toast.makeText(context, "failed :"+users.getCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openSoundsList(int position, String section_id) {

        Intent intent = new Intent(context, SoundListActivity.class);

        intent.putExtra("section_id",section_id);
        intent.putExtra("title","Event Sounds");
        startActivityForResult(intent, Sounds_list_Request_code);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == Sounds_list_Request_code) {
                    if (data != null) {

                        if (data.getStringExtra("isSelected").equals("yes")) {
                            Intent output = new Intent();
                            output.putExtra("isSelected", "yes");
                            output.putExtra("sound_name", data.getStringExtra("sound_name"));
                            output.putExtra("sound_id", data.getStringExtra("sound_id"));
                            getActivity().setResult(RESULT_OK, output);
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                        }

                    }

                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }
}
