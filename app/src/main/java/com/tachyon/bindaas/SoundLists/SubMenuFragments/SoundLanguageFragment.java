package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.Adapters.SoundLanguageAdapter;
import com.tachyon.bindaas.SoundLists.SoundListActivity;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SoundLanguageFragment extends RootFragment {

    private RecyclerView language_recycler;
    SoundLanguageAdapter adapter;
    Context context;
    private static final String TAG = "SoundLanguageFragment";

    public SoundLanguageFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_language, container, false);

        this.context = getContext();
        language_recycler = view.findViewById(R.id.language_recycler);
        language_recycler.setLayoutManager(new GridLayoutManager(context,2));

        List<String> lang_list = new ArrayList<>();
        lang_list.add("Telugu");
        lang_list.add("Tamil");
        lang_list.add("Hindi");
        lang_list.add("Kannada");
        lang_list.add("Malayalam");

        adapter = new SoundLanguageAdapter(context, lang_list, new SoundLanguageAdapter.OnItemClick() {
            @Override
            public void onClick(int position, String language) {
                openSoundsList(position,language);
            }
        });
        language_recycler.setAdapter(adapter);

        return view;
    }
    private void openSoundsList(int position, String section_id) {
        Log.d(TAG, "openSoundsList: "+section_id);
        Intent intent = new Intent(context, SoundListActivity.class);
        intent.putExtra("section_id",section_id);
        intent.putExtra("title","Language Sounds");
        startActivity(intent);
    }
}
