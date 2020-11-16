package com.tachyon.bindaas.SoundLists;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tachyon.bindaas.SoundLists.Models.SoundCategoryModel;
import com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundCategoryFragment;
import com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundLanguageFragment;
import com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundEventsFragment;
import com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundTrendingFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class Discover_SoundList_F extends RootFragment implements Player.EventListener {

    //    RecyclerView listview;
    Sounds_Adapter adapter;
    ArrayList<Sound_catagory_Get_Set> datalist;

    DownloadRequest prDownloader;
    static boolean active = false;

    View view;
    Context context;

    /* SwipeRefreshLayout swiperefresh;
     ProgressBar pbar;
 */
    private RecyclerView mainMenuRecycler;
    SoundMainMenuAdapter mainMenuAdapter;
    FrameLayout main_menu_view;
    List<SoundCategoryModel> list_categories;
    public static String running_sound_id;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onVideoUploadService(String res) {
        previous_url = "none";
        StopPlaying();
        //Call_Api_For_get_allsound();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.activity_discovery_sound_list, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        context = getContext();
        try {
            running_sound_id = "none";


            PRDownloader.initialize(context);
            // pbar = view.findViewById(R.id.pbar);

            datalist = new ArrayList<>();


            /*//Recyclerview old
            listview = view.findViewById(R.id.listview);
            listview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            listview.setNestedScrollingEnabled(false);
            listview.setHasFixedSize(true);
            listview.getLayoutManager().setMeasurementCacheEnabled(false);*/

            mainMenuRecycler = view.findViewById(R.id.main_Recycler);
            main_menu_view = view.findViewById(R.id.main_menu_view);

            mainMenuRecycler.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));
            mainMenuRecycler.setHasFixedSize(true);

            list_categories = new ArrayList<>();
            list_categories.add(new SoundCategoryModel("0","Languages"));
            list_categories.add(new SoundCategoryModel("1","Events"));
            list_categories.add(new SoundCategoryModel("2","Trendings"));

            loadMoreCategories();

            SoundLanguageFragment soundLanguageFragment = new SoundLanguageFragment();
            FragmentManager manager = getFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.main_menu_view, soundLanguageFragment);
            transaction.commit();
            mainMenuAdapter = new SoundMainMenuAdapter(context, list_categories, new SoundMainMenuAdapter.OnItemClick() {
                @Override
                public void onClick(int position) {
                    Fragment fragment = new SoundLanguageFragment();
                    switch (position) {
                        case 0:
                            fragment = new SoundLanguageFragment();
                            break;
                        case 1:
                            fragment = new SoundEventsFragment();
                            break;
                        case 2:
                            fragment = new SoundTrendingFragment();
                            break;
                        default:
                            fragment = new SoundCategoryFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("section_id",list_categories.get(position).getId());
                            fragment.setArguments(bundle);
                    }
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.replace(R.id.main_menu_view, fragment);
                    transaction.commit();
                }
            });
            mainMenuRecycler.setAdapter(mainMenuAdapter);

           /* swiperefresh = view.findViewById(R.id.swiperefresh);
            swiperefresh.setColorSchemeResources(R.color.black);
            swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    previous_url = "none";
                    StopPlaying();
                    //Call_Api_For_get_allsound();
                }
            });*/

            //Call_Api_For_get_allsound();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        return view;
    }

    private void loadMoreCategories() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_SOUND_CATEGORIES, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    parseMoreCategories(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseMoreCategories(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++){
                    JSONObject index = msgArray.getJSONObject(i);
                    String section_id = index.getString("id");
                    String section_name = index.getString("section_name");
                    list_categories.add(new SoundCategoryModel(section_id,section_name));
                }

                mainMenuAdapter.setMenuList(list_categories);
                mainMenuAdapter.notifyDataSetChanged();
                //Set_adapter();

//                list_categories.add();

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void Set_adapter() {
        try {
            adapter = new Sounds_Adapter(context, datalist, new Sounds_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int postion, Sounds_GetSet item) {

                    Log.d("resp", item.acc_path);

                    if (view.getId() == R.id.done) {
                        StopPlaying();
                        Down_load_mp3(item.id, item.sound_name, item.acc_path);
                    } else if (view.getId() == R.id.fav_btn) {
                        //StopPlaying();
                        Call_Api_For_Fav_sound(postion, item);

                    } else if (view.getId() == R.id.play_arrow) {

                        if (thread != null && !thread.isAlive()) {
                            StopPlaying();
                            playaudio(view, item);
                        } else if (thread == null) {
                            StopPlaying();
                            playaudio(view, item);
                        }
                    } else if (view.getId() == R.id.pause_arrow) {
                        if (thread != null && !thread.isAlive()) {
                            StopPlaying();
                            playaudio(view, item);
                        } else if (thread == null) {
                            StopPlaying();
                            playaudio(view, item);
                        }
                        Toast.makeText(getContext(), R.string.pause_pressed, Toast.LENGTH_SHORT).show();
                    } else {
                        if (thread != null && !thread.isAlive()) {
                            StopPlaying();
                            playaudio(view, item);
                        } else if (thread == null) {
                            StopPlaying();
                            playaudio(view, item);
                        }
                    }

                }
            });

            //listview.setAdapter(adapter);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }


    private void Call_Api_For_get_allsound() {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        ApiRequest.Call_Api(context, Variables.allSounds, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
               /* swiperefresh.setRefreshing(false);
                pbar.setVisibility(View.GONE);*/
                //Parse_data(resp);
            }
        });


    }


    public void Parse_data(String responce) {

        datalist = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");

                for (int i = msgArray.length() - 1; i >= 0; i--) {
                    JSONObject object = msgArray.getJSONObject(i);

                    Log.d("resp", object.toString());

                    JSONArray section_array = object.optJSONArray("sections_sounds");

                    ArrayList<Sounds_GetSet> sound_list = new ArrayList<>();

                    for (int j = 0; j < section_array.length(); j++) {
                        JSONObject itemdata = section_array.optJSONObject(j);

                        Sounds_GetSet item = new Sounds_GetSet();

                        item.id = itemdata.optString("id");

                        JSONObject audio_path = itemdata.optJSONObject("audio_path");
                        item.acc_path = audio_path.optString("aac");


                        item.sound_name = itemdata.optString("sound_name");
                        item.description = itemdata.optString("description");
                        item.section = itemdata.optString("section");
                        String thum_image = itemdata.optString("thum");

                        if (thum_image != null && thum_image.contains("http"))
                            item.thum = itemdata.optString("thum");
                        else
                            item.thum = Variables.base_url + itemdata.optString("thum");

                        item.date_created = itemdata.optString("created");
                        item.fav = itemdata.optString("fav");

                        sound_list.add(item);
                    }

                    Sound_catagory_Get_Set sound_catagory_get_set = new Sound_catagory_Get_Set();
                    sound_catagory_get_set.catagory = object.optString("section_name");
                    sound_catagory_get_set.sound_list = sound_list;

                    datalist.add(sound_catagory_get_set);

                }


                //Set_adapter();


            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }


    @Override
    public boolean onBackPressed() {
        try {
            getActivity().onBackPressed();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        return super.onBackPressed();
    }


    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";

    public void playaudio(View view, final Sounds_GetSet item) {
        try {
            previous_view = view;

            if (previous_url.equals(item.acc_path)) {

                previous_url = "none";
                running_sound_id = "none";
            } else {

                previous_url = item.acc_path;
                running_sound_id = item.id;

                DefaultTrackSelector trackSelector = new DefaultTrackSelector();
                player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);

                DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context,
                        Util.getUserAgent(context, "TikTok"));

                MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(Uri.parse(item.acc_path));


                player.prepare(videoSource);
                player.addListener(this);

                player.setPlayWhenReady(true);

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void StopPlaying() {
        try {
            if (player != null) {
                player.setPlayWhenReady(false);
                player.removeListener(this);
                player.release();
            }
            show_Stop_state();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            EventBus.getDefault().register(this);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            active = false;

            running_sound_id = "null";

            if (player != null) {
                player.setPlayWhenReady(false);
                player.removeListener(this);
                player.release();
            }

            show_Stop_state();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Show_Run_State() {
        try {
            if (previous_view != null) {
                //  previous_view.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                // previous_view.findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
                previous_view.findViewById(R.id.pause_arrow).setVisibility(View.VISIBLE);
                previous_view.findViewById(R.id.play_arrow).setVisibility(View.GONE);
                // previous_view.findViewById(R.id.done).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Show_loading_state() {
        try {
            // previous_view.findViewById(R.id.play_btn).setVisibility(View.GONE);
            previous_view.findViewById(R.id.play_arrow).setVisibility(View.GONE);
            previous_view.findViewById(R.id.pause_arrow).setVisibility(View.VISIBLE);
//            previous_view.findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void show_Stop_state() {
        try {
            if (previous_view != null) {
                //  previous_view.findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
                previous_view.findViewById(R.id.play_arrow).setVisibility(View.VISIBLE);
//                previous_view.findViewById(R.id.loading_progress).setVisibility(View.GONE);
                //   previous_view.findViewById(R.id.pause_btn).setVisibility(View.GONE);
                previous_view.findViewById(R.id.pause_arrow).setVisibility(View.GONE);
                //previous_view.findViewById(R.id.done).setVisibility(View.GONE);
            }

            running_sound_id = "none";
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    ProgressDialog progressDialog;

    public void Down_load_mp3(final String id, final String sound_name, String url) {
        try {
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            prDownloader = PRDownloader.download(url, Variables.app_folder, Variables.SelectedAudio_AAC)
                    .build()
                    .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                        @Override
                        public void onStartOrResume() {

                        }
                    })
                    .setOnPauseListener(new OnPauseListener() {
                        @Override
                        public void onPause() {

                        }
                    })
                    .setOnCancelListener(new OnCancelListener() {
                        @Override
                        public void onCancel() {

                        }
                    })
                    .setOnProgressListener(new OnProgressListener() {
                        @Override
                        public void onProgress(Progress progress) {

                        }
                    });

            prDownloader.start(new OnDownloadListener() {
                @Override
                public void onDownloadComplete() {
                    progressDialog.dismiss();
                    Intent output = new Intent();
                    output.putExtra("isSelected", "yes");
                    output.putExtra("sound_name", sound_name);
                    output.putExtra("sound_id", id);
                    getActivity().setResult(RESULT_OK, output);
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                }

                @Override
                public void onError(Error error) {
                    progressDialog.dismiss();
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    private void Call_Api_For_Fav_sound(int pos, final Sounds_GetSet item) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("sound_id", item.id);
            if (item.fav.equals("1"))
                parameters.put("fav", "0");
            else
                parameters.put("fav", "1");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Functions.Show_loader(context, false, false);
        ApiRequest.Call_Api(context, Variables.favSound, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();

                if (item.fav.equals("1"))
                    item.fav = "0";
                else
                    item.fav = "1";

                for (int i = 0; i < datalist.size(); i++) {
                    Sound_catagory_Get_Set catagory_get_set = datalist.get(i);
                    if (catagory_get_set.sound_list.contains(item)) {
                        int index = catagory_get_set.sound_list.indexOf(item);
                        catagory_get_set.sound_list.remove(item);
                        catagory_get_set.sound_list.add(index, item);
                        break;
                    }
                }

                adapter.notifyDataSetChanged();

            }
        });

    }


    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        try {
            if (playbackState == Player.STATE_BUFFERING) {
                Show_loading_state();
            } else if (playbackState == Player.STATE_READY) {
                Show_Run_State();
            } else if (playbackState == Player.STATE_ENDED) {
                show_Stop_state();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }


    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }


    @Override
    public void onPositionDiscontinuity(int reason) {

    }


    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }


    @Override
    public void onSeekProcessed() {

    }


}
