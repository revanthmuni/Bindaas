package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.SoundListActivity;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundsAdapter.mCurrentPlayingPosition;
import static com.tachyon.bindaas.Video_Recording.Video_Recoder_A.Sounds_list_Request_code;

public class SoundTrendingFragment extends RootFragment implements Player.EventListener {
    Context context;
    RecyclerView trending_sound_recycler;
    private static final String TAG = "SoundTrendingFragment";
    ArrayList<Sounds_GetSet> datalist;
    SoundsAdapter adapter;

    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";
    DownloadRequest prDownloader;
    public static String running_sound_id;
    static boolean active = false;
    ShimmerFrameLayout shimmer_layout;
    private int adapter_position;

    public SoundTrendingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try{
            getActivity().setTheme(Functions.getSavedTheme());
        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }

        View view = inflater.inflate(R.layout.fragment_sound_trending, container, false);
        try {
            this.context = getContext();
            shimmer_layout = view.findViewById(R.id.shimmer_layout);
            shimmer_layout.startShimmer();
            trending_sound_recycler = view.findViewById(R.id.trending_sound_recycler);
            trending_sound_recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            trending_sound_recycler.setNestedScrollingEnabled(false);
            loadTrendingSounds();
            //adapter = new SoundsAdapter(context,list);
            adapter = new SoundsAdapter(context, datalist, "sound_trending",
                    new SoundsAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int postion, Sounds_GetSet item) {
                            previous_view = view;
                            adapter_position = postion;
                            if (view.getId() == R.id.done) {
                                Log.d(TAG, "onItemClick: done clicked");
                                StopPlaying();
                                Down_load_mp3(item.id, item.sound_name, item.acc_path);
                            } else if (view.getId() == R.id.fav_btn) {
                                Log.d(TAG, "onItemClick: fav clicked");
                                //StopPlaying();
                                Call_Api_For_Fav_sound(postion, item);
                            } else if (view.getId() == R.id.play_arrow) {
                                Log.d(TAG, "onItemClick: play clicked");
                                if (thread != null && !thread.isAlive()) {
                                    StopPlaying();
                                    playaudio(view, item);
                                } else if (thread == null) {
                                    StopPlaying();
                                    playaudio(view, item);
                                }
                            } else if (view.getId() == R.id.pause_arrow) {
                                Log.d(TAG, "onItemClick: pause clicked");
                                if (thread != null && !thread.isAlive()) {
                                    StopPlaying();
                                    playaudio(view, item);
                                } else if (thread == null) {
                                    StopPlaying();
                                    playaudio(view, item);
                                }
                            } else if (view.getId() == R.id.view_more) {
                                openSoundsList(postion, item.id);
                            } else {
                                Log.d(TAG, "on itemview click ");
                                if (thread != null && !thread.isAlive()) {
                                    Log.d(TAG, "onItemClick: if");
                                    StopPlaying();
                                    playaudio(view, item);
                                } else if (thread == null) {
                                    Log.d(TAG, "onItemClick: else");
                                    StopPlaying();
                                    playaudio(view, item);
                                }
                            }

                        }
                    });
            trending_sound_recycler.setAdapter(adapter);
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
        return view;
    }

    private void loadTrendingSounds() {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_TRENDING_SOUNDS, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    shimmer_layout.stopShimmer();
                    shimmer_layout.setVisibility(View.GONE);
                    //Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                    parseTrendingSounds(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseTrendingSounds(String resp) {
        datalist = new ArrayList<>();
        Log.d(TAG, "parseTrendingSounds: " + resp);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");

                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);

                    Sounds_GetSet item = new Sounds_GetSet();

                    item.id = itemdata.optString("id");

                    JSONObject audio_path = itemdata.optJSONObject("audio_path");

                    item.acc_path = audio_path.optString("aac");


                    item.sound_name = itemdata.optString("sound_name");
                    item.description = itemdata.optString("description");
                    item.fav = itemdata.optString("fav");
                    item.section = itemdata.optString("section");

                    String thum_image = itemdata.optString("thum");

                    if (thum_image != null && thum_image.contains("http"))
                        item.thum = itemdata.optString("thum");
                    else
                        item.thum = Variables.base_url + itemdata.optString("thum");

                    item.date_created = itemdata.optString("created");

                    datalist.add(item);
                }
                adapter.setList(datalist);
                adapter.notifyDataSetChanged();
                //setAdapter();
            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }

    public void playaudio(View view, final Sounds_GetSet item) {
        Log.d(TAG, "playaudio: ");
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

    private void Call_Api_For_Fav_sound(final int pos, Sounds_GetSet item) {
        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("sound_id", item.id);
                Log.d(TAG, "Call_Api_For_Fav_sound: " + item.fav);
                if (item.fav.equals("0")) {
                    parameters.put("fav", "1");
                } else
                    parameters.put("fav", "0");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Functions.Show_loader(context, false, false);
            ApiRequest.Call_Api(context, Variables.favSound, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    EventBus.getDefault().post("done");
                    Functions.cancel_loader();

                    if (item.fav.equals("1"))
                        item.fav = "0";
                    else
                        item.fav = "1";
                    datalist.remove(pos);
                    datalist.add(pos, item);
                    adapter.notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void setAdapter() {
        try {

        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    public void StopPlaying() {
        Log.d(TAG, "StopPlaying: ");
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
    public void onResume() {
        super.onResume();
        mCurrentPlayingPosition = -1;
        adapter.notifyDataSetChanged();
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
                previous_view.findViewById(R.id.pause_arrow).setVisibility(View.VISIBLE);
                previous_view.findViewById(R.id.play_arrow).setVisibility(View.GONE);
                mCurrentPlayingPosition = adapter_position;
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Show_loading_state() {
        try {
            previous_view.findViewById(R.id.play_arrow).setVisibility(View.GONE);
            previous_view.findViewById(R.id.pause_arrow).setVisibility(View.VISIBLE);
            mCurrentPlayingPosition = adapter_position;
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void show_Stop_state() {
        Log.d(TAG, "show_Stop_state:");
        try {
            if (previous_view != null) {
                previous_view.findViewById(R.id.play_arrow).setVisibility(View.VISIBLE);
                previous_view.findViewById(R.id.pause_arrow).setVisibility(View.GONE);
                mCurrentPlayingPosition = -1;
                adapter.notifyDataSetChanged();
            }

            running_sound_id = "none";
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Down_load_mp3(final String id, final String sound_name, String url) {
        try {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Please Wait...");
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

    private void openSoundsList(int position, String section_id) {
        try {
            Intent intent = new Intent(context, SoundListActivity.class);

            intent.putExtra("section_id", section_id);
            intent.putExtra("title", "Trending Sounds");
            startActivityForResult(intent, Sounds_list_Request_code);
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {
                if (requestCode == Sounds_list_Request_code) {
                    if (data != null) {

                        if (data.getStringExtra("isSelected").equals("yes")) {
                            Intent output = new Intent();
                            output.putExtra("isSelected", "yes");
                            output.putExtra("sound_name", data.getStringExtra("sound_name"));
                            output.putExtra("sound_id", data.getStringExtra("sound_id"));
                            getActivity().setResult(RESULT_OK, output);
                            getActivity().finish();
                            getActivity().overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                        }

                    }

                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }
}