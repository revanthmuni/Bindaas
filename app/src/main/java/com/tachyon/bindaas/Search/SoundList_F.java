package com.tachyon.bindaas.Search;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.tachyon.bindaas.Discover.Discover_F;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Adapter_Click_Listener;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundsAdapter.mCurrentPlayingPosition;

/**
 * A simple {@link Fragment} subclass.
 */
public class SoundList_F extends RootFragment implements Player.EventListener {

    View view;
    Context context;
    String type;
    ShimmerFrameLayout shimmerFrameLayout;
    ArrayList<Object> data_list;
    RecyclerView recyclerView;
    SoundList_Adapter adapter;
    static boolean active = false;

    DownloadRequest prDownloader;

    public static String running_sound_id;
    private int adapter_position;

    public SoundList_F(String type) {
        this.type = type;
    }

    public SoundList_F() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try{
            getActivity().setTheme(Functions.getSavedTheme());
            view = inflater.inflate(R.layout.fragment_sound_list, container, false);

        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        context = getContext();
        try {
            shimmerFrameLayout = view.findViewById(R.id.shimmer_view_container);
            shimmerFrameLayout.startShimmer();

            recyclerView = view.findViewById(R.id.recylerview);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(linearLayoutManager);
            data_list = new ArrayList<>();
            adapter = new SoundList_Adapter(context, data_list, new Adapter_Click_Listener() {
                @Override
                public void onItemClick(View view, int pos, Object object) {

                    previous_view = view;
                    adapter_position = pos;
                    Sounds_GetSet item = (Sounds_GetSet) object;

                    if (view.getId() == R.id.done) {
                        StopPlaying();
                        Down_load_mp3(item.id, item.sound_name, item.acc_path);
                    } else if (view.getId() == R.id.fav_btn) {
                        StopPlaying();
                        Call_Api_For_Fav_sound(pos, item);
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
            recyclerView.setAdapter(adapter);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        try {
            if ((view != null && isVisibleToUser) && data_list.isEmpty()) {
                Call_Api();
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Call_Api() {

        JSONObject params = new JSONObject();
        try {
            params.put("user_id",Variables.user_id);
            params.put("type", type);
            params.put("keyword", Discover_F.search_edit.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.search, params, new Callback() {
            @Override
            public void Responce(String resp) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if (type.equals("sound"))
                    Parse_sounds(resp);
            }
        });

    }


    public void Parse_sounds(String responce) {


        try {
            JSONObject jsonObject = new JSONObject(responce);
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
                    item.section = itemdata.optString("section");

                    String thum_image = itemdata.optString("thum");

                    if (thum_image != null && thum_image.contains("http"))
                        item.thum = itemdata.optString("thum");
                    else
                        item.thum = Variables.base_url + itemdata.optString("thum");

                    item.date_created = itemdata.optString("created");
                    item.fav = itemdata.optString("fav");

                    data_list.add(item);
                }

                if (data_list.isEmpty()) {
                    view.findViewById(R.id.no_data_image).setVisibility(View.VISIBLE);
                } else
                    view.findViewById(R.id.no_data_image).setVisibility(View.GONE);

                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    private void Call_Api_For_Fav_sound(final int pos, final Sounds_GetSet item) {

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

                data_list.remove(item);
                data_list.add(pos, item);
                adapter.notifyDataSetChanged();

                adapter.notifyDataSetChanged();

            }
        });

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

            prDownloader = PRDownloader.download(url, Variables.app_folder, sound_name + id)
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
                    Toast.makeText(context, "audio saved into your phone", Toast.LENGTH_SHORT).show();
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
