package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Preferences.LanguageAdapter;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Adapters.SoundLanguageAdapter;
import com.tachyon.bindaas.SoundLists.Models.LanguageModel;
import com.tachyon.bindaas.SoundLists.SoundListActivity;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SoundLanguageFragment extends RootFragment {

    private RecyclerView language_recycler;
    SoundLanguageAdapter adapter;
    Context context;
    List<LanguageModel> lang_list;
    ShimmerFrameLayout shimmer_layout;
    private static final String TAG = "SoundLanguageFragment";

    public SoundLanguageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_language, container, false);

        this.context = getContext();
        language_recycler = view.findViewById(R.id.language_recycler);
        shimmer_layout = view.findViewById(R.id.shimmer_layout);

        shimmer_layout.startShimmer();
        language_recycler.setLayoutManager(new GridLayoutManager(context, 2));
        lang_list = new ArrayList<>();
        loadLanguages();

        return view;
    }

    private void loadLanguages() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_SOUND_LANGUAGES, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    shimmer_layout.stopShimmer();
                    shimmer_layout.setVisibility(View.GONE);
                    //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    parseSoundLanguages(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseSoundLanguages(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0;i<msgArray.length();i++){
                    JSONObject index = msgArray.getJSONObject(i);
                    LanguageModel model = new LanguageModel();
                    model.setId(index.optString("id"));
                    model.setSection_name(index.optString("section_name"));
                    model.setShort_description(index.optString("short_description"));
                    model.setSection_image(index.optString("section_image"));
                    lang_list.add(model);
                }
                setAdapter();
            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setAdapter() {
        adapter = new SoundLanguageAdapter(context, lang_list, new SoundLanguageAdapter.OnItemClick() {
            @Override
            public void onClick(int position, String language) {
                openSoundsList(position, language);
            }
        });
        language_recycler.setAdapter(adapter);
    }

    private void openSoundsList(int position, String section_id) {
        Log.d(TAG, "openSoundsList: " + section_id);
        Intent intent = new Intent(context, SoundListActivity.class);
        intent.putExtra("section_id", section_id);
        intent.putExtra("title", "Language Sounds");
        startActivity(intent);
    }
}
