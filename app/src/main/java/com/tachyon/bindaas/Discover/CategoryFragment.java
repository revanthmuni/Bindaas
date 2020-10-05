package com.tachyon.bindaas.Discover;

import android.content.Context;
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

import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Profile.MyVideos_Adapter;
import com.tachyon.bindaas.R;

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
        loadDummyCatList();
        loadDummyData();
        listAdapter = new CategoryListAdapter(catList, context, new CategoryListAdapter.OnCategoryClick() {
            @Override
            public void gotoDetails(int position) {
                openDetails(position);
            }
        });
        categoryListRecycler.setAdapter(listAdapter);

        categoryDetailsRecycler = view.findViewById(R.id.categoryDetailsRecycler);
        final GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        categoryDetailsRecycler.setLayoutManager(layoutManager);
        categoryDetailsRecycler.setHasFixedSize(true);
        openDetails(0);
        return view;
    }

    private void openDetails(int position) {
        Collections.shuffle(data_list);
        adapter = new MyVideos_Adapter(context, data_list, new MyVideos_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int postion, Home_Get_Set item, View view) {

                OpenWatchVideo(postion);

            }
        });
        categoryDetailsRecycler.setAdapter(adapter);
    }
    private void OpenWatchVideo(int postion) {
        Toast.makeText(context, "it will open the videos page", Toast.LENGTH_SHORT).show();
        /*try {
            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("arraylist", data_list);
            intent.putExtra("position", postion);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }*/

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