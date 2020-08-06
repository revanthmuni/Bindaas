package com.tachyon.bindaas.Main_Menu;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Toast;

import com.tachyon.bindaas.Chat.Chat_Activity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.google.firebase.iid.FirebaseInstanceId;


public class MainMenuActivity extends AppCompatActivity {
    public static MainMenuActivity mainMenuActivity;
    private MainMenuFragment mainMenuFragment;
    long mBackPressed;


    public static String token;

    public static Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        try {
            mainMenuActivity = this;

            intent = getIntent();

            setIntent(null);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            Variables.screen_height = displayMetrics.heightPixels;
            Variables.screen_width = displayMetrics.widthPixels;

            Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

            Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");
            Variables.user_name = Variables.sharedPreferences.getString(Variables.u_name, "");
            Variables.user_pic = Variables.sharedPreferences.getString(Variables.u_pic, "");


            token = FirebaseInstanceId.getInstance().getToken();
            if (token == null || (token.equals("") || token.equals("null")))
                token = Variables.sharedPreferences.getString(Variables.device_token, "null");


            if (savedInstanceState == null) {

                initScreen();

            } else {
                mainMenuFragment = (MainMenuFragment) getSupportFragmentManager().getFragments().get(0);
            }

            Functions.make_directry(Variables.app_hidden_folder);
            Functions.make_directry(Variables.app_folder);
            Functions.make_directry(Variables.draft_app_folder);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
        try{
        if (intent != null) {
            String type = intent.getStringExtra("type");
            if (type != null && type.equalsIgnoreCase("message")) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Chat_Activity chat_activity = new Chat_Activity(new Fragment_Callback() {
                            @Override
                            public void Responce(Bundle bundle) {

                            }
                        });
                        FragmentTransaction transaction = MainMenuActivity.mainMenuActivity.getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);

                        Bundle args = new Bundle();
                        args.putString("user_id", intent.getStringExtra("user_id"));
                        args.putString("user_name", intent.getStringExtra("user_name"));
                        args.putString("user_pic", intent.getStringExtra("user_pic"));

                        chat_activity.setArguments(args);
                        transaction.addToBackStack(null);
                        transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
                    }
                }, 2000);

            }
        }
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }

    }

    private void initScreen() {

try{
        mainMenuFragment = new MainMenuFragment();
        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, mainMenuFragment)
                .commit();

        findViewById(R.id.container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
}catch (Exception e){
    Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

}
    }


    @Override
    public void onBackPressed() {
        try{
        if (!mainMenuFragment.onBackPressed()) {
            int count = this.getSupportFragmentManager().getBackStackEntryCount();
            if (count == 0) {
                if (mBackPressed + 2000 > System.currentTimeMillis()) {
                    super.onBackPressed();
                    return;
                } else {
                    Toast.makeText(getBaseContext(), "Tap Again To Exit", Toast.LENGTH_SHORT).show();
                    mBackPressed = System.currentTimeMillis();

                }
            } else {
                super.onBackPressed();
            }
        }
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Functions.deleteCache(this);
    }

}
