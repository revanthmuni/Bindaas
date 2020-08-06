package com.tachyon.bindaas.Video_Recording;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.Services.ServiceCallback;
import com.tachyon.bindaas.Services.Upload_Service;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Post_Video_A extends AppCompatActivity implements ServiceCallback, View.OnClickListener {


    ImageView video_thumbnail;
    String video_path;
    ServiceCallback serviceCallback;
    EditText description_edit;

    String draft_file;

    TextView privcy_type_txt;
    Switch comment_switch, duet_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_video);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                draft_file = intent.getStringExtra("draft_file");
                // video_path = intent.getStringExtra("video_path");
            }
            video_path = Variables.output_filter_file;

            video_thumbnail = findViewById(R.id.video_thumbnail);

            description_edit = findViewById(R.id.description_edit);

            // this will get the thumbnail of video and show them in imageview
            Bitmap bmThumbnail;
            bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

            if (bmThumbnail != null) {
                video_thumbnail.setImageBitmap(bmThumbnail);
                Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb, Functions.Bitmap_to_base64(this, bmThumbnail)).commit();
            }


            privcy_type_txt = findViewById(R.id.privcy_type_txt);
            comment_switch = findViewById(R.id.comment_switch);
            duet_switch = findViewById(R.id.duet_switch);


            findViewById(R.id.Goback).setOnClickListener(this);

            findViewById(R.id.privacy_type_layout).setOnClickListener(this);
            findViewById(R.id.post_btn).setOnClickListener(this);
            findViewById(R.id.save_draft_btn).setOnClickListener(this);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.Goback:
                    onBackPressed();
                    break;

                case R.id.privacy_type_layout:
                    Privacy_dialog();
                    break;

                case R.id.save_draft_btn:
                    Save_file_in_draft();
                    break;

                case R.id.post_btn:
                    Start_Service();
                    break;
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void Privacy_dialog() {
        final CharSequence[] options = new CharSequence[]{"Public", "Private"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                privcy_type_txt.setText(options[item]);

            }

        });

        builder.show();

    }

    // this will start the service for uploading the video into database
    public void Start_Service() {
        try {
            serviceCallback = this;

            Upload_Service mService = new Upload_Service(serviceCallback);
            if (!Functions.isMyServiceRunning(this, mService.getClass())) {
                Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
                mServiceIntent.setAction("startservice");
                mServiceIntent.putExtra("draft_file", draft_file);
                mServiceIntent.putExtra("uri", "" + video_path);
                mServiceIntent.putExtra("desc", "" + description_edit.getText().toString());
                mServiceIntent.putExtra("privacy_type", privcy_type_txt.getText().toString());

                if (comment_switch.isChecked())
                    mServiceIntent.putExtra("allow_comment", "true");
                else
                    mServiceIntent.putExtra("allow_comment", "false");

                if (duet_switch.isChecked())
                    mServiceIntent.putExtra("allow_duet", "1");
                else
                    mServiceIntent.putExtra("allow_duet", "0");

                startService(mServiceIntent);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendBroadcast(new Intent("uploadVideo"));
                        startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));
                    }
                }, 1000);

            } else {
                Toast.makeText(this, "Please wait video already in uploading progress", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            finish();
            overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());
        }
    }


    // when the video is uploading successfully it will restart the appliaction
    @Override
    public void ShowResponce(final String responce) {
        try {
            if (mConnection != null)
                unbindService(mConnection);

            Toast.makeText(this, responce, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    // this is importance for binding the service to the activity
    Upload_Service mService;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {

            Upload_Service.LocalBinder binder = (Upload_Service.LocalBinder) service;
            mService = binder.getService();

            mService.setCallbacks(Post_Video_A.this);


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    // this function will stop the the ruuning service
    public void Stop_Service() {
        try {
            serviceCallback = this;

            Upload_Service mService = new Upload_Service(serviceCallback);

            if (Functions.isMyServiceRunning(this, mService.getClass())) {
                Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
                mServiceIntent.setAction("stopservice");
                startService(mServiceIntent);

            }

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Save_file_in_draft() {
        try {
            File source = new File(video_path);
            File destination = new File(Variables.draft_app_folder + Functions.getRandomString() + ".mp4");
            try {
                if (source.exists()) {

                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(destination);

                    byte[] buf = new byte[1024];
                    int len;

                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    in.close();
                    out.close();

                    Toast.makeText(Post_Video_A.this, "File saved in Draft", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Post_Video_A.this, MainMenuActivity.class));

                } else {
                    Toast.makeText(Post_Video_A.this, "File failed to saved in Draft", Toast.LENGTH_SHORT).show();

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    public void Delete_draft_file() {
        try {
            if (draft_file != null) {
                File file = new File(draft_file);
                file.delete();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }


    }

}
