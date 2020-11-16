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
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.app.Activity.RESULT_OK;
import static com.tachyon.bindaas.SoundLists.SubMenuFragments.SoundsAdapter.mCurrentPlayingPosition;

public class SoundCategoryFragment extends RootFragment implements Player.EventListener {


    Context context;
    SoundsAdapter adapter;

    RecyclerView category_recycler;
    String section_id;
    ArrayList<Sounds_GetSet> datalist;

    View previous_view;
    Thread thread;
    SimpleExoPlayer player;
    String previous_url = "none";
    DownloadRequest prDownloader;
    public static String running_sound_id;
    static boolean active = false;
    private static final String TAG = "SoundCategoryFragment";
    private int adapter_position;
    ShimmerFrameLayout shimmer_layout;

    public SoundCategoryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try{
            getActivity().setTheme(Functions.getSavedTheme());
        }catch (Exception e){
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
        }
        View view = inflater.inflate(R.layout.fragment_sound_category, container, false);
        try {
            this.context = getContext();
            category_recycler = view.findViewById(R.id.category_recycler);
            shimmer_layout = view.findViewById(R.id.shimmer_layout);
            shimmer_layout.startShimmer();
            category_recycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            category_recycler.setNestedScrollingEnabled(false);

            Bundle bundle = getArguments();
            if (bundle != null) {
                section_id = bundle.getString("section_id");
                // Toast.makeText(context, "" + section_id, Toast.LENGTH_SHORT).show();
            }
            loadCategoriesData();
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
        return view;
    }

    private void loadCategoriesData() {
        try {
            JSONObject params = new JSONObject();
            try {
                //params.put("user_id", "");
                params.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, ""));
                params.put("sound_section_id", section_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.GET_SOUND_BY_DISCOVERY_SECTION, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    shimmer_layout.stopShimmer();
                    shimmer_layout.setVisibility(View.GONE);
                    parseTrendingSounds(resp);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void parseTrendingSounds(String responce) {

        Log.d(TAG, "parseTrendingSounds: full data:" + responce);

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {

                JSONArray msgArray = jsonObject.getJSONArray("msg");
                datalist = new ArrayList<>();
                for (int i = msgArray.length() - 1; i >= 0; i--) {
                    JSONObject object = msgArray.getJSONObject(i);

                    Log.d("resp", object.toString());

                    JSONArray section_array = object.optJSONArray("sounds_of_section");

                    if (!section_array.toString().equals("[]")) {

                        for (int j = 0; j < section_array.length(); j++) {
                            JSONObject itemdata = section_array.optJSONObject(j);

                            Sounds_GetSet item = new Sounds_GetSet();

                            item.id = itemdata.optString("id");

                            JSONObject audio_path = itemdata.optJSONObject("audio_path");
                            item.acc_path = audio_path.optString("aac");
                            item.mp3_path = audio_path.optString("mp3");


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

                            datalist.add(item);
                        }
                    }

                }

                Set_adapter();


            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {

            e.printStackTrace();
        }

    }

    public void Set_adapter() {
        try {
            /*adapter = new Sounds_Adapter(context, datalist, new Sounds_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int postion, Sounds_GetSet item) {

                    Log.d("resp", item.acc_path);

                    if (view.getId() == R.id.done) {
                        StopPlaying();
                        Down_load_mp3(item.id, item.sound_name, item.acc_path);
                    } else if (view.getId() == R.id.fav_btn) {
                        StopPlaying();
                        Call_Api_For_Fav_sound(postion, item.id);

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
                        Toast.makeText(context, R.string.pause_pressed, Toast.LENGTH_SHORT).show();
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
            });*/
            adapter = new SoundsAdapter(context, datalist, "sound_list", new SoundsAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int postion, Sounds_GetSet item) {
                    previous_view = view;
                    adapter_position = postion;
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
                        Toast.makeText(context, R.string.play_pressed, Toast.LENGTH_SHORT).show();
                    } else if (view.getId() == R.id.pause_arrow) {
                        if (thread != null && !thread.isAlive()) {
                            StopPlaying();
                            playaudio(view, item);
                        } else if (thread == null) {
                            StopPlaying();
                            playaudio(view, item);
                        }
                        Toast.makeText(context, R.string.pause_pressed, Toast.LENGTH_SHORT).show();
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

            category_recycler.setAdapter(adapter);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

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

    private void Call_Api_For_Fav_sound(final int pos, Sounds_GetSet item) {
        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("sound_id", item.id);
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
                previous_view.findViewById(R.id.pause_arrow).setVisibility(View.GONE);
                previous_view.findViewById(R.id.play_arrow).setVisibility(View.VISIBLE);
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
}
