package com.tachyon.bindaas;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

public class Splash_A extends AppCompatActivity {


    CountDownTimer countDownTimer;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);
        try {
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

            Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
            countDownTimer = new CountDownTimer(3600, 100) {

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
