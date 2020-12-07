package com.tachyon.bindaas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Services.Upload_Service;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.json.JSONException;
import org.json.JSONObject;

public class Splash_A extends AppCompatActivity {


    CountDownTimer countDownTimer;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        VideoView videoView = findViewById(R.id.video_view);
        String uriPath = "android.resource://"+getPackageName()+"/raw/myvideo";
        Call_Api_For_get_Allvideos();
        videoView.setVideoURI(Uri.parse(uriPath));
        videoView.start();
        try {

            Upload_Service mService = new Upload_Service();
            if (Functions.isMyServiceRunning(this, mService.getClass())) {
                // Toast.makeText(this, "service is running", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, Upload_Service.class);
                stopService(intent);
            }
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
            countDownTimer = new CountDownTimer(3600, 100) { //3600

                public void onTick(long millisUntilFinished) {

                }

                public void onFinish() {

                    Intent intent = new Intent(Splash_A.this, MainMenuActivity.class);

                    if (getIntent().getExtras() != null) {
                        intent.putExtras(getIntent().getExtras());
                        setIntent(null);
                    }

                    startActivity(intent);
                    overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                    finish();

                }
            }.start();

            final String android_id = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            SharedPreferences.Editor editor2 = Variables.sharedPreferences.edit();
            editor2.putString(Variables.device_id, android_id).commit();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }
    private void Call_Api_For_get_Allvideos() {

        Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        JSONObject parameters = new JSONObject();
        try {
            Log.d("test--", "Call_Api_For_get_Allvideos: " + Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("token", FirebaseInstanceId.getInstance().getToken());
            parameters.put("type", "related");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("TEST", "Call_Api_For_get_Allvideos: " + new Gson().toJson(parameters));
        ApiRequest.Call_Api(this, Variables.showAllVideos, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d("Enhan:", "Responce: " + resp);
                editor.putString("big_data",resp);
                editor.commit();
            }
        });

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            countDownTimer.cancel();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

}
