package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.tachyon.bindaas.Discover.Models.TopUsers;
import com.tachyon.bindaas.Discover.Models.Users;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TopsFragment extends Fragment {

    Context context;
    RecyclerView topsRecyclerview;
    TopsAdapter topsAdapter;
    List<Users> topsList;
    private static final String TAG = "TopsFragment";
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
//        loadDummyTops();
        topsList = new ArrayList<>();

        loadTopUsers();
        topsAdapter = new TopsAdapter(context, topsList, new TopsAdapter.OnProfileClick() {
            @Override
            public void onClick(int position, Users users) {
                openProfile(position,users);
            }
        });

        topsRecyclerview.setAdapter(topsAdapter);
        return view;
    }

    private void openProfile(int position, Users item) {
        try {
            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.getUserId())) {
                try {
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
                args.putString("user_id", item.getUserId());
                args.putString("user_name", item.getFirstName() + " " + item.getLastName());
                args.putString("user_pic", item.getProfilePic());
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                transaction.replace(R.id.MainMenuFragment, profile_f).commit();
            }

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void loadDummyTops() {

       /* for (int i = 0;i<10;i++){
            TopsItems items = new TopsItems();
            items.setUser_name("User Name@"+i);
            items.setProfile_pic("");
            items.setLine_1("this is line "+i);
            items.setLine_2("this is line no "+i);
            items.setLine_3("this is line number "+i);
            topsList.add(items);
        }*/
    }
    public void loadTopUsers(){
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_TOP_USERS, params, new Callback() {
                @Override
                public void Responce(String resp) {

                    parseTopUsers(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseTopUsers(String resp) {
        TopUsers users = new Gson().fromJson(resp,TopUsers.class);
        if (users.getCode().equals("200")){
            //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
            topsAdapter.setTopsList(users.getMsg());
            topsAdapter.notifyDataSetChanged();
        }else{
           // Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "parseTopUsers: "+resp);
    }
}