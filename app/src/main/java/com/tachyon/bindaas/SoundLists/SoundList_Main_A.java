package com.tachyon.bindaas.SoundLists;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.AudioTrimming.AudioTrimmerActivity;
import com.tachyon.bindaas.Main_Menu.Custom_ViewPager;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.FavouriteSounds.Favourite_Sound_F;
import com.tachyon.bindaas.helper.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class SoundList_Main_A extends AppCompatActivity implements View.OnClickListener {

    private static int ADD_AUDIO = 2009;

    private static final String TAG = "Audio_Test";
    protected TabLayout tablayout;

    protected Custom_ViewPager pager;

    FloatingActionButton fabAddAudioFromLocal;

    private ViewPagerAdapter adapter;
    private ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            setTheme(Functions.getSavedTheme());
            setContentView(R.layout.activity_sound_list_main);

        }catch (Exception e){
            Functions.showLogMessage(this,getClass().getSimpleName(), e.getMessage());
        }
        try {
            initViews();
            initialiseClickListeners();
            initialiseTabLayout();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void initialiseTabLayout() {
        try {
            pager.setOffscreenPageLimit(2);
            pager.setPagingEnabled(false);

            // Note that we are passing childFragmentManager, not FragmentManager
            adapter = new ViewPagerAdapter(getResources(), getSupportFragmentManager());
            pager.setAdapter(adapter);
            tablayout.setupWithViewPager(pager);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void initialiseClickListeners() {
        try {
            goBack.setOnClickListener(this);
            fabAddAudioFromLocal.setOnClickListener(this);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void initViews() {
        try {
            tablayout = (TabLayout) findViewById(R.id.groups_tab);
            fabAddAudioFromLocal = findViewById(R.id.fabAddAudioFromLocal);
            pager = findViewById(R.id.viewpager);
            goBack = findViewById(R.id.Goback);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {
                case R.id.Goback:
                    onBackPressed();
                    break;

                case R.id.fabAddAudioFromLocal:
                    if (checkReadExternalStoragePermission()) {
                        overridePendingTransition(0, 0);
                        getAudioFileFromLocal();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, ADD_AUDIO);
                    }
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void getAudioFileFromLocal() {
        /*Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 2000);*/
        try {
            startActivityForResult(new Intent(this, AudioTrimmerActivity.class), ADD_AUDIO);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try {
            if (requestCode == ADD_AUDIO) {
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAudioFileFromLocal();
                }
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*if (requestCode == ADD_AUDIO) {
            Uri uri = data.getData();
            Log.d("Audio_file", "onActivityResult: "+uri);
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    *//*Uri uri = data.getData();
                    Log.d("Audio_file", "onActivityResult: "+uri);*//*
                    //audio trim result will be saved at below path
                    String path = data.getExtras().getString("INTENT_AUDIO_FILE");
                    Toast.makeText(this, "Audio stored at " + path, Toast.LENGTH_LONG).show();
                }
            }
        }*/
        try{
        if (requestCode == ADD_AUDIO) {
            if (resultCode == RESULT_OK) {
                String path = data.getExtras().getString("INTENT_AUDIO_FILE");
                Uri uri = Uri.fromFile(new File(path));
                if (uri != null) {
                    Log.d(TAG, "onActivityResult Uri: " + uri);
                    Log.d(TAG, "onActivityResult Uri: " + uri.toString());
                    Log.d(TAG, "onActivityResult Uri: " + String.valueOf(uri));

//                    File fi = (File) data.getExtras().get("file");
                    Toast.makeText(this, getString(R.string.audio_stored_at) + path, Toast.LENGTH_LONG).show();

                    Log.d("Audio_Test", "" + path);
                    JSONObject params = new JSONObject();
                    final File oldFile = new File(uri.toString());
                    final File file = CommonUtils.getAudioFilePath(this, uri);
                    // Log.d("Audio_Test", "cropped file Path is : "+fi.getAbsolutePath());
                    String audioString;
                    try {
                        audioString = CommonUtils.encodeFileToBase64Binary(Uri.fromFile(file));
                        Functions.Show_loader(this, false, false);
                        String user_id = Variables.sharedPreferences.getString(Variables.u_id, "0");

                        params.put("user_id", user_id);
                        params.put("file_name", oldFile.getName());
                        params.put("data", audioString);
                        Log.d("Audio_Test", "onActivityResult data: " + audioString);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    Log.d("Audio_Test", "onActivityResult: " + new Gson().toJson(params));
                    ApiRequest.Call_Api(this, Variables.POST_AUDIO, params, new Callback() {
                        @Override
                        public void Responce(String resp) {
                            Log.d("Audio_Test", "Responce: " + resp);
                            Functions.cancel_loader();
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                String status = jsonObject.optString("code");
                                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                                JSONObject data = jsonArray.getJSONObject(0);
                                if (status.equals("200")) {
                                    String soundID = data.optString("sound_id");
                                    String accURL = data.optString("sound_url");
                                    Intent output = new Intent();
                                    output.putExtra("isSelected", "yes");
                                    output.putExtra("sound_name", oldFile.getName());
                                    output.putExtra("sound_id", soundID);
                                    setResult(RESULT_OK, output);
                                    Functions.downloadFile(accURL, Variables.app_folder, Variables.SelectedAudio_AAC);
                                    finish();
                                    overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                                } else {
                                    Toast.makeText(getBaseContext(), data.optString("response"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
                }
            }
        }
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());

        }
    }

    private boolean checkReadExternalStoragePermission() {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {


        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();


        public ViewPagerAdapter(final Resources resources, FragmentManager fm) {

            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            final Fragment result;
            switch (position) {
                case 0:
                    result = new Discover_SoundList_F();
                    break;
                case 1:
                    result = new Favourite_Sound_F();
                    break;
                default:
                    result = null;
                    break;
            }

            return result;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
                case 0:
                    return "Discover";
                case 1:
                    return "My Favorites";

                default:
                    return null;

            }


        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }


        /**
         * Get the Fragment by position
         *
         * @param position tab position of the fragment
         * @return
         */


        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }


    }

}
