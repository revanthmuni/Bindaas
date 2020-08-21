package com.tachyon.bindaas.SoundLists;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.util.Log;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.R;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class VideoSound_A extends AppCompatActivity implements View.OnClickListener {

    Home_Get_Set item;
    TextView sound_name, description_txt;
    ImageView sound_image;

    File audio_file;

    private static final String TAG = "VideoSound_A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_sound);
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
                                    new File(Variables.app_folder +item.video_id+".acc"));
                            Toast.makeText(this, R.string.audio_saved, Toast.LENGTH_SHORT).show();
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
