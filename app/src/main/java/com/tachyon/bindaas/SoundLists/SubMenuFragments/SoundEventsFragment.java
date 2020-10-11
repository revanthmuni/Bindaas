package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.Adapters.SoundEventsAdapter;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SoundEventsFragment extends RootFragment {
    RecyclerView tops_recyclerview;
    Context context;
    SoundEventsAdapter adapter;

    public SoundEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sound_tops, container, false);
        this.context = getContext();

        tops_recyclerview = view.findViewById(R.id.tops_recyclerview);
        tops_recyclerview.setLayoutManager(new GridLayoutManager(context,2));
        adapter = new SoundEventsAdapter();
        tops_recyclerview.setAdapter(adapter);
        return view;
    }
}
