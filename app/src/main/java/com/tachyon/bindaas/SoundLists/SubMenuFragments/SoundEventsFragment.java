package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Discover.Models.TopUsers;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Adapters.SoundEventsAdapter;
import com.tachyon.bindaas.SoundLists.Models.EventsModel;
import com.tachyon.bindaas.SoundLists.Models.GetEvents;
import com.tachyon.bindaas.SoundLists.SoundListActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SoundEventsFragment extends RootFragment {
    RecyclerView tops_recyclerview;
    Context context;
    SoundEventsAdapter adapter;
    List<EventsModel> list;
    private static final String TAG = "SoundEventsFragment";

    public SoundEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_tops, container, false);
        this.context = getContext();

        list = new ArrayList<>();
        loadEvents();
        tops_recyclerview = view.findViewById(R.id.tops_recyclerview);
        tops_recyclerview.setLayoutManager(new GridLayoutManager(context,2));

        return view;
    }

    private void loadEvents() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_EVENT_BANNERS, params, new Callback() {
                @Override
                public void Responce(String resp) {

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
        Log.d(TAG, "openSoundsList: "+section_id);
        Intent intent = new Intent(context, SoundListActivity.class);
        intent.putExtra("section_id",section_id);
        intent.putExtra("title","Event Sounds");
        startActivity(intent);
    }
}
