package com.tachyon.bindaas.Discover;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.Search.Search_F;
import com.tachyon.bindaas.Search.Search_Main_F;
import com.tachyon.bindaas.Search.SoundList_F;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SimpleClasses.ViewPagerAdapter;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Discover_F extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

//    RecyclerView recyclerView;
    public static EditText search_edit;

    FrameLayout search_frame;
    TextView search_btn;

//    SwipeRefreshLayout swiperefresh;

    public Discover_F() {
        // Required empty public constructor
    }

    ArrayList<Discover_Get_Set> datalist;

    Discover_Adapter adapter;

    SliderView imageSlider;
    TabLayout mainTabLayout;
    ViewPager mainViewPager;
    List<EventItems> eventlist;
    EventSliderAdapter eventAdapter;
    NestedScrollView initial_layout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discover, container, false);
        try {
            context = getContext();


            datalist = new ArrayList<>();


            search_frame = view.findViewById(R.id.search_frame);
            initial_layout = view.findViewById(R.id.nest_scrollview);
            imageSlider = view.findViewById(R.id.imageSlider);
            mainTabLayout = view.findViewById(R.id.tabLayout2);
            mainViewPager = view.findViewById(R.id.main_viewpager);

            initial_layout.setFillViewport (true);
           /* recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(true);*/

            adapter = new Discover_Adapter(context, datalist, new Discover_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(ArrayList<Home_Get_Set> datalist, int postion) {

                    OpenWatchVideo(postion, datalist);

                }
            });

            eventlist = new ArrayList<>();

            loadEventBanners();

            //Image Slider Code
            eventAdapter = new EventSliderAdapter(context,eventlist);
            imageSlider.setSliderAdapter(eventAdapter);
            imageSlider.startAutoCycle();
            imageSlider.setIndicatorAnimation(IndicatorAnimationType.WORM);
            imageSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
            //   recyclerView.setAdapter(adapter);

            //Tabs - Trending,Tops and Categories
            mainViewPagerAdapter = new EventStatePagerAdapter(getChildFragmentManager());
            mainViewPager.setOffscreenPageLimit(3);
            mainViewPagerAdapter.addFrag(new TrendingFragment(), "Trendings");
            mainViewPagerAdapter.addFrag(new TopsFragment(), "Tops");
            mainViewPagerAdapter.addFrag(new CategoryFragment(), "Categories");
            mainViewPager.setAdapter(mainViewPagerAdapter);
            mainTabLayout.setupWithViewPager(mainViewPager);

            search_edit = view.findViewById(R.id.search_edit);
            search_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.toString().length() == 0) {
                        //swiperefresh.setVisibility(View.VISIBLE);
                        search_frame.setVisibility(View.GONE);
                        initial_layout.setVisibility(View.VISIBLE);
                    }else Set_tabs();/*
                    if (!s.equals("")) Set_tabs();
                    else {
                        swiperefresh.setVisibility(View.VISIBLE);
                        search_frame.setVisibility(View.GONE);
                    }*/
                   /* if (search_edit.getText().toString().length() > 0) {
                        search_btn.setVisibility(View.VISIBLE);
                    } else {
                        search_btn.setVisibility(View.GONE);
                    }*/
                    /*String query = search_edit.getText().toString();
                    if (adapter != null)
                        adapter.getFilter().filter(s);*/

                    // tag,follow on watch videos

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });


           /* swiperefresh = view.findViewById(R.id.swiperefresh);
            swiperefresh.setColorSchemeResources(R.color.black);
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {

                    Call_Api_For_get_Allvideos();
                }
            });*/

            view.findViewById(R.id.search_layout).setOnClickListener(this);
            view.findViewById(R.id.search_edit).setOnClickListener(this);

            Call_Api_For_get_Allvideos();
           /* search_edit.setFocusable(true);
            UIUtil.showKeyboard(context, search_edit);*/
            search_edit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        Perform_search();
                        return true;
                    }
                    return false;
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return view;
    }


    // Bottom two function will get the Discover videos
    // from api and parse the json data which is shown in Discover tab

    private void Call_Api_For_get_Allvideos() {
        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("resp", parameters.toString());

            ApiRequest.Call_Api(context, Variables.discover, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Parse_data(resp);
                    //swiperefresh.setRefreshing(false);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


    public void Parse_data(String responce) {
        try {
            datalist.clear();

            try {
                JSONObject jsonObject = new JSONObject(responce);
                String code = jsonObject.optString("code");
                if (code.equals("200")) {
                    JSONArray msgArray = jsonObject.getJSONArray("msg");
                    for (int d = 0; d < msgArray.length(); d++) {

                        Discover_Get_Set discover_get_set = new Discover_Get_Set();
                        JSONObject discover_object = msgArray.optJSONObject(d);
                        discover_get_set.title = discover_object.optString("section_name");

                        JSONArray video_array = discover_object.optJSONArray("sections_videos");

                        ArrayList<Home_Get_Set> video_list = new ArrayList<>();
                        for (int i = 0; i < video_array.length(); i++) {
                            JSONObject itemdata = video_array.optJSONObject(i);
                            Home_Get_Set item = new Home_Get_Set();


                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.user_id = user_info.optString("user_id");
                            item.username = user_info.optString("username");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");
                            item.verified = user_info.optString("verified");

                            JSONObject count = itemdata.optJSONObject("count");
                            item.like_count = count.optString("like_count");
                            item.video_comment_count = count.optString("video_comment_count");


                            JSONObject sound_data = itemdata.optJSONObject("sound");
                            item.sound_id = sound_data.optString("id");
                            item.sound_name = sound_data.optString("sound_name");
                            item.sound_pic = sound_data.optString("thum");
                            if (sound_data != null) {
                                JSONObject audio_path = sound_data.optJSONObject("audio_path");
                                item.sound_url_mp3 = audio_path.optString("mp3");
                                item.sound_url_acc = audio_path.optString("aac");
                            }


                            item.video_id = itemdata.optString("id");
                            item.liked = itemdata.optString("liked");


                            item.video_url = itemdata.optString("video");


                            item.thum = itemdata.optString("thum");
                            item.gif = itemdata.optString("gif");
                            item.created_date = itemdata.optString("created");
                            item.video_description = itemdata.optString("description");

                            video_list.add(item);
                        }

                        discover_get_set.arrayList = video_list;

                        datalist.add(discover_get_set);

                    }

                    adapter.notifyDataSetChanged();

                } else {
                    Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    // When you click on any Video a new activity is open which will play the Clicked video
    private void OpenWatchVideo(int postion, ArrayList<Home_Get_Set> data_list) {
        try {
            Intent intent = new Intent(getActivity(), WatchVideos_F.class);
            intent.putExtra("arraylist", data_list);
            intent.putExtra("position", postion);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }

    public void Open_search() {
        try {
            Search_Main_F search_main_f = new Search_Main_F();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.addToBackStack(null);
            transaction.replace(R.id.MainMenuFragment, search_main_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.search_layout:
                    //Open_search();
                    break;
                case R.id.search_edit:
                    //Open_search();
                    //Perform_search();
                    break;

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();try {
            String s = search_edit.getText().toString();
            if (s.length() == 0) {

            } else {
                // Toast.makeText(context, search_edit.getText().toString(), Toast.LENGTH_SHORT).show();
                Perform_search();
            }
        /*if (search_edit.getText().toString().length() != 0){
            search_frame.setVisibility(View.VISIBLE);
            swiperefresh.setVisibility(View.GONE);
            Set_tabs();
        }*/
        }catch (Exception e){

        }
    }

    public void Perform_search() {
        if (menu_pager != null) {
            menu_pager.removeAllViews();
        }
        Set_tabs();
    }

    protected TabLayout tabLayout;
    protected ViewPager menu_pager;
    ViewPagerAdapter adapter1;
    EventStatePagerAdapter mainViewPagerAdapter;

    public void Set_tabs() {
        try {
            search_frame.setVisibility(View.VISIBLE);
            initial_layout.setVisibility(View.GONE);
          //swiperefresh.setVisibility(View.GONE);
            adapter1 = new ViewPagerAdapter(getChildFragmentManager());
            menu_pager = (ViewPager) view.findViewById(R.id.viewpager);
            menu_pager.setOffscreenPageLimit(3);
            tabLayout = (TabLayout) view.findViewById(R.id.tabs);

            adapter1.addFrag(new Search_F("users"), "Users");
            adapter1.addFrag(new Search_F("video"), "Videos");
            adapter1.addFrag(new SoundList_F("sound"), "Sounds");

            menu_pager.setAdapter(adapter1);
            tabLayout.setupWithViewPager(menu_pager);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }
    private void loadEventBanners() {
        try {
            //Functions.Show_loader(context, true, true);
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", "");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_EVENT_BANNERS, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.optString("code");
                        if (code.equals("200")) {
                            JSONArray msgArray = jsonObject.getJSONArray("msg");
                            for (int d = 0; d < msgArray.length(); d++) {
                                JSONObject index = msgArray.optJSONObject(d);
                                EventItems items = new EventItems();
                                items.setId(index.optString("id"));
                                items.setEvent_name(index.optString("event_name"));
                                items.setShort_description(index.optString("short_description"));
                                items.setStart_date(index.optString("start_date"));
                                items.setEnd_date(index.optString("end_date"));
                                items.setActive(index.optString("active"));
                                items.setSound_image(index.optString("sound_image"));
                                items.setDiscover_image(index.optString("discover_image"));
                                items.setCreated(index.optString("created"));
                                if (index.optString("active").equals("true")){
                                    eventlist.add(items);
                                }
                            }
                            eventAdapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    android.util.Log.d("TAG", "Event data: " + resp);
                  //  Functions.cancel_loader();
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }

    }

}
