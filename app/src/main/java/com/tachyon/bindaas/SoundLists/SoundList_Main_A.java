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

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tachyon.bindaas.Main_Menu.Custom_ViewPager;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.FileUtils;
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

    protected TabLayout tablayout;

    protected Custom_ViewPager pager;

    FloatingActionButton fabAddAudioFromLocal;

    private ViewPagerAdapter adapter;
    private ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_list_main);

        initViews();
        initialiseClickListeners();
        initialiseTabLayout();

    }

    private void initialiseTabLayout() {
        pager.setOffscreenPageLimit(2);
        pager.setPagingEnabled(false);

        // Note that we are passing childFragmentManager, not FragmentManager
        adapter = new ViewPagerAdapter(getResources(), getSupportFragmentManager());
        pager.setAdapter(adapter);
        tablayout.setupWithViewPager(pager);
    }

    private void initialiseClickListeners() {
        goBack.setOnClickListener(this);
        fabAddAudioFromLocal.setOnClickListener(this);
    }

    private void initViews() {
        tablayout = (TabLayout) findViewById(R.id.groups_tab);
        fabAddAudioFromLocal = findViewById(R.id.fabAddAudioFromLocal);
        pager = findViewById(R.id.viewpager);
        goBack = findViewById(R.id.Goback);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Goback:
                onBackPressed();
                break;

            case R.id.fabAddAudioFromLocal:
                if (checkReadExternalStoragePermission()) {
                    getAudioFileFromLocal();
                } else {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
                }
        }
    }

    private void getAudioFileFromLocal() {
        Intent intent_upload = new Intent();
        intent_upload.setType("audio/*");
        intent_upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent_upload, 2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1000) {
            if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAudioFileFromLocal();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2000) {
                Uri uri = data.getData();
                if (uri != null) {
                    JSONObject params = new JSONObject();
                    final File oldFile = new File(uri.toString());
                    final File file = CommonUtils.getAudioFilePath(this, uri);
                    String audioString;
                    try {
                        audioString = CommonUtils.encodeFileToBase64Binary(Uri.fromFile(file));
                        Functions.Show_loader(this, false, false);
                        String user_id = Variables.sharedPreferences.getString(Variables.u_id, "0");

                        params.put("user_id", user_id);
                        params.put("file_name", oldFile.getName());
                        params.put("data", audioString);
                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    ApiRequest.Call_Api(this, Variables.POST_AUDIO, params, new Callback() {
                        @Override
                        public void Responce(String resp) {
                            Functions.cancel_loader();
                            try {
                                JSONObject jsonObject = new JSONObject(resp);
                                String status = jsonObject.optString("code");
                                JSONArray jsonArray = jsonObject.getJSONArray("msg");
                                JSONObject data = jsonArray.getJSONObject(0);
                                if (status.equals("200")) {
                                    String soundID = data.optString("sound_id");
                                    Intent output = new Intent();
                                    output.putExtra("isSelected", "yes");
                                    output.putExtra("sound_name", oldFile.getName());
                                    output.putExtra("sound_id", soundID);
                                    setResult(RESULT_OK, output);
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
