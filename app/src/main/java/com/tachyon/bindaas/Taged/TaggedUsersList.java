package com.tachyon.bindaas.Taged;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TaggedUsersList extends RootFragment {
    Context context;
    View view;
    RecyclerView recyclerView;
    ImageButton goBack;
    public TaggedUsersList() {
        // Required empty public constructor
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tagged_users, container, false);
        context = getContext();

        Bundle bundle = getArguments();
        if (bundle != null) {
            String data = bundle.getString("data");
            Log.d("TAG", "onCreateView: "+data);
        }

        recyclerView = view.findViewById(R.id.tagged_recyclerview);
        goBack = view.findViewById(R.id.Goback);
        goBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        List<UserInfo> list = new ArrayList<>();
        list.add(new UserInfo("Ram"));
        list.add(new UserInfo("Bheem"));
        list.add(new UserInfo("Som"));
        list.add(new UserInfo("Sam"));
        list.add(new UserInfo("Tom"));
        TaggedUsersAdapter adapter = new TaggedUsersAdapter(list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    class TaggedUsersAdapter extends RecyclerView.Adapter<TaggedUsersAdapter.ViewHolder>{

        List<UserInfo> list;

        public TaggedUsersAdapter(List<UserInfo> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public TaggedUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tagged_user_row,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull TaggedUsersAdapter.ViewHolder holder, int position) {
            holder.name.setText(list.get(position).first_name);
//            Glide.with(context).load(list.get(position).profile_pic).into(holder.profile);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView profile;
            TextView name;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                profile = itemView.findViewById(R.id.profile_pic);
                name = itemView.findViewById(R.id.name);
            }
        }
    }
}
