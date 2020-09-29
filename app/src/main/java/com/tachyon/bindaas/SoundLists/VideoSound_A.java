package com.tachyon.bindaas.SoundLists;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Profile.MyVideos_Adapter;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.Video_Recording.Video_Recoder_A;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.downloader.request.DownloadRequest;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.tachyon.bindaas.WatchVideos.WatchVideos_F;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class VideoSound_A extends AppCompatActivity implements View.OnClickListener {

    Home_Get_Set item;
    TextView sound_name, description_txt;
    ImageView sound_image;

    File audio_file;
    RecyclerView recyclerView ;
    ArrayList<Home_Get_Set> dataList;
    private static final String TAG = "VideoSound_A";
    MyVideos_Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sound);

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));
        Functions.make_directry(Variables.app_hidden_folder);
        Functions.make_directry(Variables.app_folder);
        Functions.make_directry(Variables.draft_app_folder);
        try {
            Intent intent = getIntent();
            if (intent.hasExtra("data")) {
                item = (Home_Get_Set) intent.getSerializableExtra("data");
            }


            sound_name = findViewById(R.id.sound_name);
            description_txt = findViewById(R.id.description_txt);
            sound_image = findViewById(R.id.sound_image);

            if ((item.sound_name == null || item.sound_name.equals("") || item.sound_name.equals("null"))) {
                sound_name.setText("original sound - " + item.first_name + " " + item.last_name);
            } else {
                sound_name.setText(item.sound_name);
            }
            description_txt.setText(item.video_description);


            findViewById(R.id.back_btn).setOnClickListener(this);

            findViewById(R.id.save_btn).setOnClickListener(this);
            findViewById(R.id.create_btn).setOnClickListener(this);

            findViewById(R.id.play_btn).setOnClickListener(this);
            findViewById(R.id.pause_btn).setOnClickListener(this);


            Uri uri = Uri.parse(item.thum);
            sound_image.setImageURI(uri);

            Log.d(Variables.tag, item.thum);
            Log.d(Variables.tag, item.sound_url_acc);

            Save_Audio();
            dataList = new ArrayList<>();
            adapter = new MyVideos_Adapter(this, dataList, new MyVideos_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(int postion, Home_Get_Set item, View view) {
                    OpenWatchVideo(postion,dataList);
                }
            });
            recyclerView.setAdapter(adapter);

            loadVideosBySound(item.sound_id,item.user_id);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void loadVideosBySound(String sound_id, String user_id) {
        //         sound_id and user_id

        try {
            JSONObject params = new JSONObject();
            try {
                params.put("sound_id", sound_id);
                params.put("user_id", user_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Functions.Show_loader(this, false, false);
            ApiRequest.Call_Api(this, Variables.GET_VIDEOS_BY_SOUND, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    Parse_data(resp);
                    android.util.Log.d("TAG", "Video Sound : " + resp);
                    Functions.cancel_loader();
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }
    public void Parse_data(String responce) {

        //data_list = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(responce);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                //ArrayList<Home_Get_Set> temp_list = new ArrayList();
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item = new Home_Get_Set();
                    item.user_id = itemdata.optString("user_id");

                    JSONObject user_info = itemdata.optJSONObject("user_info");

                    JSONObject follow_status = itemdata.optJSONObject("follow_Status");
                    item.follow = follow_status.optString("follow");
                    item.follow_status_button = follow_status.optString("follow_status_button");

                    item.username = user_info.optString("username");
                    item.first_name = user_info.optString("first_name", this.getResources().getString(R.string.app_name));
                    item.last_name = user_info.optString("last_name", "User");
                    item.profile_pic = user_info.optString("profile_pic", "null");
                    item.verified = user_info.optString("verified");

                    JSONObject sound_data = itemdata.optJSONObject("sound");
                    item.sound_id = sound_data.optString("id");
                    item.sound_name = sound_data.optString("sound_name");
                    item.sound_pic = sound_data.optString("thum");
                    if (sound_data != null) {
                        JSONObject audio_path = sound_data.optJSONObject("audio_path");
                        item.sound_url_mp3 = audio_path.optString("mp3");
                        item.sound_url_acc = audio_path.optString("aac");
                    }


                    JSONObject count = itemdata.optJSONObject("count");
                    item.like_count = count.optString("like_count");
                    item.video_comment_count = count.optString("video_comment_count");
                    item.views = count.optString("view");
                    item.privacy_type = itemdata.optString("privacy_type");
                    item.allow_comments = itemdata.optString("allow_comments");
                    item.allow_duet = itemdata.optString("allow_duet");
                    item.video_id = itemdata.optString("id");
                    item.views = itemdata.optString("view");
                    item.liked = itemdata.optString("liked");
                    item.video_url = itemdata.optString("video");
                    itemdata.optString("video");


                    item.video_description = itemdata.optString("description");

                    item.thum = itemdata.optString("thum");
                    item.created_date = itemdata.optString("created");

                    JSONArray tagged_users = itemdata.optJSONArray("tagged_users");
                    android.util.Log.d("TAG::>", "Parse_data: tagged users : "+tagged_users.toString());
                    item.tagged_users = tagged_users.toString();
                    dataList.add(item);

                }
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }

           /* if (Variables.sharedPreferences.getBoolean(Variables.auto_scroll_key, false)) {
                autoScrollVideos();
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void OpenWatchVideo(int postion,ArrayList<Home_Get_Set> list) {
        try {
            Intent intent = new Intent(this, WatchVideos_F.class);
            intent.putExtra("arraylist", list);
            intent.putExtra("position", postion);
            startActivity(intent);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.back_btn:
                    onBackPressed();
                    break;
                case R.id.save_btn:
                    String dest = "Bindaas" + item.video_id + ".mp3";
                    if(audio_file!=null && audio_file.exists()) {
                        try {
                            Functions.copyFile(audio_file,
                                    new File(Variables.app_folder + "Saved Audio/" +item.video_id+".mp3"));
                            Toast.makeText(this, R.string.audio_saved , Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case R.id.create_btn:
                    if(audio_file!=null && audio_file.exists()) {
                        StopPlaying();
                        Open_video_recording();
                    }
                    break;

                case R.id.play_btn:
                    if (audio_file != null && audio_file.exists())
                        playaudio();

                    break;

                case R.id.pause_btn:
                    StopPlaying();
                    break;
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    SimpleExoPlayer player;

    public void playaudio() {
        try {
            DefaultTrackSelector trackSelector = new DefaultTrackSelector();
            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                    Util.getUserAgent(this, "TikTok"));

            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.fromFile(audio_file));


            player.prepare(videoSource);
            player.setPlayWhenReady(true);

            Show_playing_state();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void StopPlaying() {
        try{
        if (player != null) {
            player.setPlayWhenReady(false);
        }
        Show_pause_state();
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        StopPlaying();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try{
            StopPlaying();
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }


    public void Show_playing_state() {
        try{
        findViewById(R.id.play_btn).setVisibility(View.GONE);
        findViewById(R.id.pause_btn).setVisibility(View.VISIBLE);
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    public void Show_pause_state() {
        try{
        findViewById(R.id.play_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.pause_btn).setVisibility(View.GONE);
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    DownloadRequest prDownloader;
    ProgressDialog progressDialog;
    public void Save_Audio() {
        try{
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        prDownloader = PRDownloader.download(item.sound_url_acc, Variables.app_folder, Variables.SelectedAudio_AAC)
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
                audio_file = new File(Variables.app_folder + Variables.SelectedAudio_AAC);
            }

            @Override
            public void onError(Error error) {
                progressDialog.dismiss();
            }
        });

        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }


    public void Open_video_recording() {
        try{
        Intent intent = new Intent(VideoSound_A.this, Video_Recoder_A.class);
        intent.putExtra("sound_name", sound_name.getText().toString());
        intent.putExtra("sound_id", item.sound_id);
        startActivity(intent);
        overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {

        Log.d(TAG, "copyFile:source " + sourceFile);
        Log.d(TAG, "copyFile:dest " + destFile);
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        try (FileChannel source = new FileInputStream(sourceFile).getChannel();
             FileChannel destination = new FileOutputStream(destFile).getChannel()) {

            destination.transferFrom(source, 0, source.size());
        }
    }
}
