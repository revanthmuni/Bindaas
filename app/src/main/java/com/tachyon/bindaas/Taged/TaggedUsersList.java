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
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TaggedUsersList extends RootFragment {
    Context context;
    View view;
    RecyclerView recyclerView;
    ImageButton goBack;
    String data = "";
    String flag = "";

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
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_tagged_users, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        context = getContext();

        Bundle bundle = getArguments();
        if (bundle != null) {
            data = bundle.getString("data");
            flag = bundle.getString("flag");
            Log.d("TaggedUsers", "onCreateView: " + data);
        }

        recyclerView = view.findViewById(R.id.tagged_recyclerview);
        goBack = view.findViewById(R.id.Goback);
        goBack.setOnClickListener(view -> {
            getActivity().onBackPressed();
        });
        List<UserInfo> list = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(data);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                UserInfo info = new UserInfo();
                info.setFirst_name(jsonObject.optString("first_name"));
                info.setLast_name(jsonObject.optString("last_name"));
                info.setProfile_pic(jsonObject.optString("profile_pic"));
                info.setUser_id(jsonObject.optString("user_id"));
                info.setUsername(jsonObject.optString("username"));
                info.setVerified(jsonObject.optString("verified"));
                list.add(info);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        TaggedUsersAdapter adapter = new TaggedUsersAdapter(list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    class TaggedUsersAdapter extends RecyclerView.Adapter<TaggedUsersAdapter.ViewHolder> {

        List<UserInfo> list;

        public TaggedUsersAdapter(List<UserInfo> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public TaggedUsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tagged_user_row, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull TaggedUsersAdapter.ViewHolder holder, int position) {
            holder.name.setText(list.get(position).first_name + " " + list.get(position).last_name);
            Glide.with(context).load(list.get(position).profile_pic).placeholder(R.drawable.ic_profile_image_placeholder).into(holder.profile);
            holder.itemView.setOnClickListener(view -> {
                openProfile(list.get(position));
            });
        }

        private void openProfile(UserInfo item) {
            try {
                if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.user_id)) {
                    try {
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                        getActivity().onBackPressed();
                        TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                        profile.select();
                    } catch (Exception e) {
                        Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
                        Log.d("Exception:", "OpenProfile: " + e.getMessage());
                    }

                } else {
                    Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                        @Override
                        public void Responce(Bundle bundle) {
                            //Call_Api_For_Singlevideos(currentPage);
                        }
                    });
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

                    Bundle args = new Bundle();
                    args.putString("user_id", item.user_id);
                    args.putString("user_name", item.first_name + " " + item.last_name);
                    args.putString("user_pic", item.profile_pic);
                    profile_f.setArguments(args);
                    transaction.addToBackStack(null);
                    if (flag.equals("home")) {
                        transaction.replace(R.id.MainMenuFragment, profile_f).commit();
                    }else{
                        getActivity().onBackPressed();
                        transaction.replace(R.id.WatchVideo_F, profile_f).commit();
                    }

                }

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            CircleImageView profile;
            TextView name;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                profile = itemView.findViewById(R.id.profile_pic);
                name = itemView.findViewById(R.id.name);
            }
        }
    }
}
