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

import com.tachyon.bindaas.Main_Menu.Custom_ViewPager;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.FavouriteSounds.Favourite_Sound_F;

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

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2000){
                Uri uri = data.getData();
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
