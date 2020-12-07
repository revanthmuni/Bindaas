package com.tachyon.bindaas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Home.MessageEvent;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simp.
 */
public class NewsFeedFragment extends Fragment {

    private static final String TAG = "NewsFeedFragment";
    private ViewPager2 news_feed_viewpager;
    private List<String> list;
    NewsFeedAdapter adapter;
    ProgressBar p_bar;
    String user_id;

    public NewsFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Subscribe(threadMode = ThreadMode.POSTING)
    public void onEventTrggered(MessageEvent item){
        Log.d(TAG, "onEventTrggered:NewsFeed "+new Gson().toJson(item));
        user_id = item.getUser_id();
        getNewsFeed();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
        }catch (Exception e){
            Functions.showLogMessage(getContext(), getContext().getClass().getSimpleName(), e.getMessage());
        }


        View view = inflater.inflate(R.layout.fragment_news_feed, container, false);
        view.findViewById(R.id.back_btn).setOnClickListener(view1 -> getActivity().onBackPressed());
        news_feed_viewpager = view.findViewById(R.id.news_feed_viewpager);
        p_bar = view.findViewById(R.id.p_bar);
        Log.d("User_ID", "onCreateView: " + Variables.user_id);

        user_id = Variables.user_id;

        getNewsFeed();
        news_feed_viewpager.setAdapter(adapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
    private void getNewsFeed() {
        try {
            list = new ArrayList<>();
            adapter = new NewsFeedAdapter(getContext(), list);
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ApiRequest.Call_Api(getContext(), Variables.NEWS_FEED, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    parseNewsFeed(resp);
                    p_bar.setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            Functions.showLogMessage(getContext(), this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void parseNewsFeed(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    String url = itemdata.optString("news_url");
                    list.add(url);
                }

            } else {
                Toast.makeText(getContext(), "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }
            adapter.setList(list);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
        }
    }
}