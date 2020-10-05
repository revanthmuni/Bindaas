package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tachyon.bindaas.R;

import java.util.ArrayList;
import java.util.List;


public class TopsFragment extends Fragment {

    Context context;
    RecyclerView topsRecyclerview;
    TopsAdapter topsAdapter;
    List<TopsItems> topsList;
    public TopsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("FragmentCheck", "onCreateView: its trendig");
        View view = inflater.inflate(R.layout.fragment_tops, container, false);

        context = getContext();
        topsRecyclerview = view.findViewById(R.id.topsRecyclerview);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        topsRecyclerview.setLayoutManager(layoutManager);
        topsRecyclerview.setHasFixedSize(true);
        loadDummyTops();
        topsAdapter = new TopsAdapter(context,topsList);
        topsRecyclerview.setAdapter(topsAdapter);
        return view;
    }

    private void loadDummyTops() {
        topsList = new ArrayList<>();

        for (int i = 0;i<10;i++){
            TopsItems items = new TopsItems();
            items.setUser_name("User Name@"+i);
            items.setProfile_pic("");
            items.setLine_1("this is line "+i);
            items.setLine_2("this is line no "+i);
            items.setLine_3("this is line number "+i);
            topsList.add(items);
        }
    }
}