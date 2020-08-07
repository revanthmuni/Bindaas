package com.tachyon.bindaas.Video_Recording;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.daasuu.gpuv.composer.GPUMp4Composer;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;
import com.googlecode.mp4parser.util.Matrix;
import com.googlecode.mp4parser.util.Path;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SegmentProgress.ProgressBarListener;
import com.tachyon.bindaas.SegmentProgress.SegmentedProgressBar;
import com.tachyon.bindaas.SimpleClasses.FileUtils;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.SoundList_Main_A;
import com.tachyon.bindaas.Video_Recording.GallerySelectedVideo.GallerySelectedVideo_A;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.wonderkiln.camerakit.CameraKit;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Video_Recoder_A extends AppCompatActivity implements View.OnClickListener {


    CameraView cameraView;
    int number = 0;

    ArrayList<String> videopaths = new ArrayList<>();

    ImageButton record_image;
    ImageButton done_btn;
    boolean is_recording = false;
    boolean is_flash_on = false;

    ImageButton flash_btn;
    SegmentedProgressBar video_progress;
    LinearLayout camera_options;
    ImageButton rotate_camera, cut_video_btn;


    public static int Sounds_list_Request_code = 1;
    TextView add_sound_txt;

    int sec_passed = 0;
    long time_in_milis = 0;

    TextView countdown_timer_txt;
    boolean is_recording_timer_enable;
    int recording_time = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hide_navigation();

        setContentView(R.layout.activity_video_recoder);
        try {
            Variables.Selected_sound_id = "null";
            Variables.recording_duration = Variables.max_recording_duration;


            cameraView = findViewById(R.id.camera);
            camera_options = findViewById(R.id.camera_options);


            record_image = findViewById(R.id.record_image);


            findViewById(R.id.upload_layout).setOnClickListener(this);


            done_btn = findViewById(R.id.done);
            done_btn.setEnabled(false);
            done_btn.setOnClickListener(this);


            rotate_camera = findViewById(R.id.rotate_camera);
            rotate_camera.setOnClickListener(this);
            flash_btn = findViewById(R.id.flash_camera);
            flash_btn.setOnClickListener(this);

            findViewById(R.id.Goback).setOnClickListener(this);

            add_sound_txt = findViewById(R.id.add_sound_txt);
            add_sound_txt.setOnClickListener(this);

            findViewById(R.id.time_btn).setOnClickListener(this);

            Intent intent = getIntent();
            if (intent.hasExtra("sound_name")) {
                add_sound_txt.setText(intent.getStringExtra("sound_name"));
                Variables.Selected_sound_id = intent.getStringExtra("sound_id");
                PreparedAudio();
            }


            // this is code hold to record the video
       /* final Timer[] timer = {new Timer()};
        final long[] press_time = {0};
       record_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    timer[0] =new Timer();
                    press_time[0] =System.currentTimeMillis();

                    timer[0].schedule(new TimerTask() {
                        @Override
                        public void run() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(!is_recording) {
                                        press_time[0] =System.currentTimeMillis();
                                        Start_or_Stop_Recording();
                                    }
                                }
                            });

                        }
                    }, 200);


                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    timer[0].cancel();
                    if(is_recording && (press_time[0] !=0 && (System.currentTimeMillis()- press_time[0])<2000)){
                        Start_or_Stop_Recording();
                    }
                }
                return false;
            }

        });*/

            record_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Start_or_Stop_Recording();
                }
            });

            countdown_timer_txt = findViewById(R.id.countdown_timer_txt);


            initlize_Video_progress();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    public void initlize_Video_progress() {

        try {
            sec_passed = 0;
            video_progress = findViewById(R.id.video_progress);
            video_progress.enableAutoProgressView(Variables.recording_duration);
            video_progress.setDividerColor(Color.WHITE);
            video_progress.setDividerEnabled(true);
            video_progress.setDividerWidth(4);
            video_progress.setShader(new int[]{Color.CYAN, Color.CYAN, Color.CYAN});

            video_progress.SetListener(new ProgressBarListener() {
                @Override
                public void TimeinMill(long mills) {
                    time_in_milis = mills;
                    sec_passed = (int) (mills / 1000);

                    if (sec_passed > (Variables.recording_duration / 1000) - 1) {
                        Start_or_Stop_Recording();
                    }

                    if (is_recording_timer_enable && sec_passed >= recording_time) {
                        is_recording_timer_enable = false;
                        Start_or_Stop_Recording();
                    }

                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    // if the Recording is stop then it we start the recording
    // and if the mobile is recording the video then it will stop the recording
    public void Start_or_Stop_Recording() {
        try {
            if (!is_recording && sec_passed < (Variables.recording_duration / 1000) - 1) {
                number = number + 1;

                is_recording = true;

                File file = new File(Variables.app_folder + "myvideo" + (number) + ".mp4");
                videopaths.add(Variables.app_folder + "myvideo" + (number) + ".mp4");
                cameraView.captureVideo(file);

                if (audio != null)
                    audio.start();

                done_btn.setBackgroundResource(R.drawable.ic_not_done);
                done_btn.setEnabled(false);

                video_progress.resume();

                record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_yes));

                camera_options.setVisibility(View.GONE);
                add_sound_txt.setClickable(false);
                rotate_camera.setVisibility(View.GONE);

            } else if (is_recording) {

                is_recording = false;

                video_progress.pause();
                video_progress.addDivider();

                if (audio != null)
                    audio.pause();

                cameraView.stopVideo();
                Check_done_btn_enable();


                record_image.setImageDrawable(getResources().getDrawable(R.drawable.ic_recoding_no));
                camera_options.setVisibility(View.VISIBLE);

            } else if (sec_passed > (Variables.recording_duration / 1000)) {
                Functions.Show_Alert(this, "Alert", "Video only can be a " + (int) Variables.recording_duration / 1000 + " S");
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Check_done_btn_enable() {
        if (sec_passed > (Variables.min_time_recording / 1000)) {
            done_btn.setBackgroundResource(R.drawable.ic_done);
            done_btn.setEnabled(true);
        } else {
            done_btn.setBackgroundResource(R.drawable.ic_not_done);
            done_btn.setEnabled(false);
        }
    }

    // this will apped all the videos parts in one  fullvideo
    private boolean append() {

        try {
            final ProgressDialog progressDialog = new ProgressDialog(Video_Recoder_A.this);
            new Thread(new Runnable() {
                @Override
                public void run() {


                    runOnUiThread(new Runnable() {
                        public void run() {

                            progressDialog.setMessage("Please wait..");
                            progressDialog.show();
                        }
                    });

                    ArrayList<String> video_list = new ArrayList<>();
                    for (int i = 0; i < videopaths.size(); i++) {

                        File file = new File(videopaths.get(i));
                        if (file.exists()) {
                            try {
                                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                                retriever.setDataSource(Video_Recoder_A.this, Uri.fromFile(file));
                                String hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO);
                                boolean isVideo = "yes".equals(hasVideo);

                                if (isVideo && file.length() > 3000) {
                                    Log.d("resp", videopaths.get(i));
                                    video_list.add(videopaths.get(i));
                                }
                            } catch (Exception e) {
                                Log.d(Variables.tag, e.toString());
                            }
                        }
                    }


                    try {

                        Movie[] inMovies = new Movie[video_list.size()];

                        for (int i = 0; i < video_list.size(); i++) {

                            inMovies[i] = MovieCreator.build(video_list.get(i));
                        }


                        List<Track> videoTracks = new LinkedList<Track>();
                        List<Track> audioTracks = new LinkedList<Track>();
                        for (Movie m : inMovies) {
                            for (Track t : m.getTracks()) {
                                if (t.getHandler().equals("soun")) {
                                    audioTracks.add(t);
                                }
                                if (t.getHandler().equals("vide")) {
                                    videoTracks.add(t);
                                }
                            }
                        }
                        Movie result = new Movie();
                        if (audioTracks.size() > 0) {
                            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
                        }
                        if (videoTracks.size() > 0) {
                            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
                        }

                        Container out = new DefaultMp4Builder().build(result);

                        String outputFilePath = null;
                        if (audio != null) {
                            outputFilePath = Variables.outputfile2;
                        } else {
                            outputFilePath = Variables.outputfile2;
                        }

                        Log.d("Audio_Test", "run: output file path " + outputFilePath);
                        FileOutputStream fos = new FileOutputStream(new File(outputFilePath));
                        out.writeContainer(fos.getChannel());
                        fos.close();

                        runOnUiThread(new Runnable() {
                            public void run() {

                                progressDialog.dismiss();
                                if (audio != null)
                                    Merge_withAudio();
                                else {
                                    Go_To_preview_Activity();
                                }

                            }
                        });


                    } catch (Exception e) {

                    }
                }
            }).start();

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
        return true;
    }


    // this will add the select audio with the video
    public void Merge_withAudio() {

        try {
            String audio_file;
            audio_file = Variables.app_folder + Variables.SelectedAudio_AAC;


            Merge_Video_Audio merge_video_audio = new Merge_Video_Audio(Video_Recoder_A.this);
//        merge_video_audio.doInBackground();
            merge_video_audio.execute(audio_file, Variables.outputfile, Variables.outputfile2);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void RotateCamera() {
        try {
            cameraView.toggleFacing();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.rotate_camera:
                    RotateCamera();
                    break;

                case R.id.upload_layout:
                    Pick_video_from_gallery();

                    break;

                case R.id.done:
                    append();
                    break;


                case R.id.flash_camera:

                    if (is_flash_on) {
                        is_flash_on = false;
                        cameraView.setFlash(0);
                        flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));

                    } else {
                        is_flash_on = true;
                        cameraView.setFlash(CameraKit.Constants.FLASH_TORCH);
                        flash_btn.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
                    }

                    break;

                case R.id.Goback:
                    onBackPressed();
                    break;

                case R.id.add_sound_txt:
                    Intent intent = new Intent(this, SoundList_Main_A.class);
                    startActivityForResult(intent, Sounds_list_Request_code);
                    overridePendingTransition(R.anim.in_from_bottom, R.anim.out_to_top);
                    break;

                case R.id.time_btn:
                    if (sec_passed + 1 < Variables.recording_duration / 1000) {
                        RecordingTimeRang_F recordingTimeRang_f = new RecordingTimeRang_F(new Fragment_Callback() {
                            @Override
                            public void Responce(Bundle bundle) {
                                if (bundle != null) {
                                    is_recording_timer_enable = true;
                                    recording_time = bundle.getInt("end_time");
                                    countdown_timer_txt.setText("3");
                                    countdown_timer_txt.setVisibility(View.VISIBLE);
                                    record_image.setClickable(false);
                                    final Animation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                    new CountDownTimer(4000, 1000) {
                                        @Override
                                        public void onTick(long millisUntilFinished) {

                                            countdown_timer_txt.setText("" + (millisUntilFinished / 1000));
                                            countdown_timer_txt.setAnimation(scaleAnimation);

                                        }

                                        @Override
                                        public void onFinish() {
                                            record_image.setClickable(true);
                                            countdown_timer_txt.setVisibility(View.GONE);
                                            Start_or_Stop_Recording();
                                        }
                                    }.start();

                                }
                            }
                        });
                        Bundle bundle = new Bundle();
                        if (sec_passed < (Variables.recording_duration / 1000) - 3)
                            bundle.putInt("end_time", (sec_passed + 3));
                        else
                            bundle.putInt("end_time", (sec_passed + 1));

                        bundle.putInt("total_time", (Variables.recording_duration / 1000));
                        recordingTimeRang_f.setArguments(bundle);
                        recordingTimeRang_f.show(getSupportFragmentManager(), "");
                    }
                    break;

            }

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Pick_video_from_gallery() {
        try {
            Intent intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            intent.setType("video/*");
            startActivityForResult(intent, Variables.Pick_video_from_gallery);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {

                if (requestCode == Sounds_list_Request_code) {
                    if (data != null) {

                        if (data.getStringExtra("isSelected").equals("yes")) {
                            add_sound_txt.setText(data.getStringExtra("sound_name"));
                            Variables.Selected_sound_id = data.getStringExtra("sound_id");
                            PreparedAudio();
                        }

                    }

                } else if (requestCode == Variables.Pick_video_from_gallery) {
                    Uri uri = data.getData();
                    try {
                        File video_file = FileUtils.getFileFromUri(this, uri);

                        if (getfileduration(uri) < Variables.max_recording_duration) {
                            Chnage_Video_size(video_file.getAbsolutePath(), Variables.gallery_resize_video);

                        } else {
                            try {
                                startTrim(video_file, new File(Variables.gallery_trimed_video), 1000, Variables.max_recording_duration);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public long getfileduration(Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(this, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
        return 0;
    }


    public void Chnage_Video_size(String src_path, String destination_path) {
        try {
            Functions.copyFile(new File(src_path),
                    new File(destination_path));

            Intent intent = new Intent(Video_Recoder_A.this, GallerySelectedVideo_A.class);
            intent.putExtra("video_path", Variables.gallery_resize_video);
            startActivity(intent);
            /*Functions.Show_determinent_loader(this, false, false);
            new GPUMp4Composer(src_path, destination_path)
                    .size(720, 1280)
                    .videoBitrate((int) (0.25 * 16 * 540 * 960))
                    .listener(new GPUMp4Composer.Listener() {
                        @Override
                        public void onProgress(double progress) {

                            Log.d("resp", "" + (int) (progress * 100));
                            Functions.Show_loading_progress((int) (progress * 100));

                        }

                        @Override
                        public void onCompleted() {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    Functions.cancel_determinent_loader();

                                    Intent intent = new Intent(Video_Recoder_A.this, GallerySelectedVideo_A.class);
                                    intent.putExtra("video_path", Variables.gallery_resize_video);
                                    startActivity(intent);

                                }
                            });


                        }

                        @Override
                        public void onCanceled() {
                            Log.d("resp", "onCanceled");
                        }

                        @Override
                        public void onFailed(Exception exception) {

                            Log.d("resp", exception.toString());

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {

                                        Functions.cancel_determinent_loader();

                                        Toast.makeText(Video_Recoder_A.this, "Try Again", Toast.LENGTH_SHORT).show();
                                    } catch (Exception e) {

                                    }
                                }
                            });

                        }
                    })
                    .start();*/
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void startTrim(final File src, final File dst, final int startMs, final int endMs) throws IOException {
        try {
            new AsyncTask<String, Void, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    try {

                        FileDataSourceImpl file = new FileDataSourceImpl(src);
                        Movie movie = MovieCreator.build(file);
                        List<Track> tracks = movie.getTracks();
                        movie.setTracks(new LinkedList<Track>());
                        double startTime = startMs / 1000;
                        double endTime = endMs / 1000;
                        boolean timeCorrected = false;

                        for (Track track : tracks) {
                            if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                                if (timeCorrected) {
                                    throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                                }
                                startTime = Functions.correctTimeToSyncSample(track, startTime, false);
                                endTime = Functions.correctTimeToSyncSample(track, endTime, true);
                                timeCorrected = true;
                            }
                        }
                        for (Track track : tracks) {
                            long currentSample = 0;
                            double currentTime = 0;
                            long startSample = -1;
                            long endSample = -1;

                            for (int i = 0; i < track.getSampleDurations().length; i++) {
                                if (currentTime <= startTime) {
                                    startSample = currentSample;
                                }
                                if (currentTime <= endTime) {
                                    endSample = currentSample;
                                } else {
                                    break;
                                }
                                currentTime += (double) track.getSampleDurations()[i] / (double) track.getTrackMetaData().getTimescale();
                                currentSample++;
                            }
                            movie.addTrack(new CroppedTrack(track, startSample, endSample));
                        }

                        Container out = new DefaultMp4Builder().build(movie);
                        MovieHeaderBox mvhd = Path.getPath(out, "moov/mvhd");
                        mvhd.setMatrix(Matrix.ROTATE_180);
                        if (!dst.exists()) {
                            dst.createNewFile();
                        }
                        FileOutputStream fos = new FileOutputStream(dst);
                        WritableByteChannel fc = fos.getChannel();
                        try {
                            out.writeContainer(fc);
                        } finally {
                            fc.close();
                            fos.close();
                            file.close();
                        }

                        file.close();
                        return "Ok";
                    } catch (IOException e) {
                        Log.d(Variables.tag, e.toString());
                        return "error";
                    }

                }

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    Functions.Show_indeterminent_loader(Video_Recoder_A.this, true, true);
                }

                @Override
                protected void onPostExecute(String result) {
                    if (result.equals("error")) {
                        Toast.makeText(Video_Recoder_A.this, "Try Again", Toast.LENGTH_SHORT).show();
                    } else {
                        Functions.cancel_indeterminent_loader();
                        Chnage_Video_size(Variables.gallery_trimed_video, Variables.gallery_resize_video);
                    }
                }


            }.execute();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will play the sound with the video when we select the audio
    MediaPlayer audio;

    public void PreparedAudio() {
        try {
            File file = new File(Variables.app_folder + Variables.SelectedAudio_AAC);
            if (file.exists()) {
                audio = new MediaPlayer();
                try {
                    audio.setDataSource(Variables.app_folder + Variables.SelectedAudio_AAC);
                    audio.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(this, Uri.fromFile(file));
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                final int file_duration = Integer.parseInt(durationStr);

                if (file_duration < Variables.max_recording_duration) {
                    Variables.recording_duration = file_duration;
                    initlize_Video_progress();
                }

            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cameraView.start();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    protected void onDestroy() {
        DeleteFile();
        super.onDestroy();
        try {

            if (audio != null) {
                audio.stop();
                audio.reset();
                audio.release();
                audio = null;
            }
            cameraView.stop();

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onBackPressed() {

        try {
            new AlertDialog.Builder(this)
                    .setTitle("Alert")
                    .setMessage("Are you Sure? if you Go back you can't undo this action")
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();

                            //DeleteFile();
                            finish();
                            overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);

                        }
                    }).show();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());
            Log.e("Exception:", "" + e.getMessage());
            //Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    public void Go_To_preview_Activity() {
        try {
            Intent intent = new Intent(this, Preview_Video_A.class);
            startActivity(intent);
            overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will delete all the video parts that is create during priviously created video
    //int delete_count = 0;

    public void DeleteFile() {
        try {
            //delete_count++;
            File output = new File(Variables.outputfile);
            File output2 = new File(Variables.outputfile2);
            //File output_filter_file = new File(Variables.output_filter_file);
            File gallery_trimed_video = new File(Variables.gallery_trimed_video);
            File gallery_resize_video = new File(Variables.gallery_resize_video);
            if (output.exists()) {
                output.delete();
            }

            if (output2.exists()) {
                output2.delete();
            }

            if (gallery_trimed_video.exists()) {
                gallery_trimed_video.delete();
            }
            if (gallery_resize_video.exists()) {
                gallery_resize_video.delete();
            }

            for (int i = 0; i <= 12; i++) {
                File file = new File(Variables.app_folder + "myvideo" + (i) + ".mp4");
                if (file.exists()) {
                    file.delete();
                    //DeleteFile();
                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this will hide the bottom mobile navigation controll
    public void Hide_navigation() {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().getDecorView().setSystemUiVisibility(flags);

                // Code below is to handle presses of Volume up or Volume down.
                // Without this, after pressing volume buttons, the navigation bar will
                // show up and won't hide
                final View decorView = getWindow().getDecorView();
                decorView
                        .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                            @Override
                            public void onSystemUiVisibilityChange(int visibility) {
                                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                    decorView.setSystemUiVisibility(flags);
                                }
                            }
                        });
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        try {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }
}
