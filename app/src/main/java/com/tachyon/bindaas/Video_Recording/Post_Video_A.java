package com.tachyon.bindaas.Video_Recording;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tachyon.bindaas.Constant;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.Services.ServiceCallback;
import com.tachyon.bindaas.Services.Upload_Service;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class Post_Video_A extends AppCompatActivity implements ServiceCallback, View.OnClickListener {


    ImageView video_thumbnail;
    String video_path;
    ServiceCallback serviceCallback;
    EditText description_edit;

    String draft_file, duet_video_id;

    TextView privcy_type_txt;
    Switch comment_switch, duet_switch;
    Spinner category;
    Bitmap bmThumbnail;

    RecyclerView recyclerView;
    HashTagsAdapter adapter;

    TextView no_data_found_view;
    TextView finaltext;
    ImageView close_finaltext;
    EditText search_edit;
    RecyclerView followers_actv_recycler;
    List<String> list = new ArrayList<>();
    private GetFollowersAdapter actv_adapter;
    private ArrayList<Home_Get_Set> followerList;
    private String actual_Str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setTheme(Functions.getSavedTheme());
            setContentView(R.layout.activity_post_video);

        } catch (Exception e) {
            Functions.showLogMessage(this, getClass().getSimpleName(), e.getMessage());
        }
        try {
            Intent intent = getIntent();
            if (intent != null) {
                draft_file = intent.getStringExtra("draft_file");
                duet_video_id = intent.getStringExtra("duet_video_id");
                // video_path = intent.getStringExtra("video_path");
            }
            video_path = Variables.output_filter_file;
            search_edit = findViewById(R.id.search_edit);
            no_data_found_view = findViewById(R.id.no_data_found_view);
            finaltext = findViewById(R.id.finaltext);
            close_finaltext = findViewById(R.id.close_finaltext);
            close_finaltext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finaltext.setText("");
                    actual_Str = "";
                    close_finaltext.setVisibility(View.GONE);
                }
            });
            search_edit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                    if (s.toString().length() == 0) {
                        Log.i("Ram = ", "Empty");
                        Log.i("Ram = ", "");
                        followers_actv_recycler.setVisibility(View.GONE);
                        no_data_found_view.setVisibility(View.VISIBLE);
                    }
                    if (s.equals("")) {
                        followers_actv_recycler.setVisibility(View.GONE);
                        no_data_found_view.setVisibility(View.VISIBLE);
                    } else {
                        followers_actv_recycler.setVisibility(View.VISIBLE);
                        filter(s);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            followers_actv_recycler = findViewById(R.id.followers_actv);
            followers_actv_recycler.setLayoutManager(new LinearLayoutManager(this));
            followerList = new ArrayList<>();

            video_thumbnail = findViewById(R.id.video_thumbnail);

            description_edit = findViewById(R.id.description_edit);
            category = findViewById(R.id.cat_spinner);

            // this will get the thumbnail of video and show them in imageview
            bmThumbnail = ThumbnailUtils.createVideoThumbnail(video_path,
                    MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);

            if (bmThumbnail != null && duet_video_id != null) {
                Bitmap duet_video_bitmap = null;
                if (duet_video_id != null) {
                    duet_video_bitmap = ThumbnailUtils.createVideoThumbnail(Variables.app_folder + duet_video_id + ".mp4",
                            MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                }
                Bitmap combined = combineImages(bmThumbnail, duet_video_bitmap);
                video_thumbnail.setImageBitmap(combined);
                Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb, Functions.Bitmap_to_base64(this, combined)).commit();

            } else if (bmThumbnail != null) {
                video_thumbnail.setImageBitmap(bmThumbnail);
                Variables.sharedPreferences.edit().putString(Variables.uploading_video_thumb, Functions.Bitmap_to_base64(this, bmThumbnail)).commit();
            }


            privcy_type_txt = findViewById(R.id.privcy_type_txt);
            comment_switch = findViewById(R.id.comment_switch);
            duet_switch = findViewById(R.id.duet_switch);
            recyclerView = findViewById(R.id.hashtags);
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
            findViewById(R.id.Goback).setOnClickListener(this);

            findViewById(R.id.privacy_type_layout).setOnClickListener(this);
            findViewById(R.id.post_btn).setOnClickListener(this);
            findViewById(R.id.save_draft_btn).setOnClickListener(this);

            if (duet_video_id != null) {
                findViewById(R.id.duet_layout).setVisibility(View.GONE);
                duet_switch.setChecked(false);
            } else if (Variables.is_enable_duet)
                findViewById(R.id.duet_layout).setVisibility(View.VISIBLE);

            else {
                findViewById(R.id.duet_layout).setVisibility(View.GONE);
                duet_switch.setChecked(false);
            }

            callApiForHashTags();
            callApiForFollowersList();

        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void filter(CharSequence text) {
        if (text.toString().equals("")) {
            no_data_found_view.setVisibility(View.VISIBLE);
            followers_actv_recycler.setVisibility(View.GONE);
        }
        {
            no_data_found_view.setVisibility(View.GONE);
        }

        ArrayList<Home_Get_Set> temp = new ArrayList();
        for (Home_Get_Set d : followerList) {

            if (Pattern.compile(Pattern.quote(text.toString()),
                    Pattern.CASE_INSENSITIVE).matcher(d.first_name).find()) {
                temp.add(d);
            }
        }
        //update recyclerview
        if (temp.size() == 0) {
            no_data_found_view.setVisibility(View.VISIBLE);
            followers_actv_recycler.setVisibility(View.GONE);
        }/*else{
            no_data_found_view.setVisibility(View.GONE);
            followers_actv_recycler.setVisibility(View.VISIBLE);
        }*/
        actv_adapter.updateList(temp);
    }

    private void callApiForFollowersList() {
        try {
            Functions.Show_loader(this, true, true);

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "save Prefernces : " + new Gson().toJson(parameters));
            ApiRequest.Call_Api(this, Variables.GET_MUTUAL_FOLLOWERS, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    Log.d(TAG, "Responce: " + resp);
                    try {
                        JSONObject jsonObject = new JSONObject(resp);
                        String code = jsonObject.optString("code");
                        if (code.equals("200")) {
                            JSONArray msgArray = jsonObject.getJSONArray("msg");
                            for (int i = 0; i < msgArray.length(); i++) {
                                JSONObject index = msgArray.getJSONObject(i);
                                Home_Get_Set model = new Home_Get_Set();
                                model.user_id = index.optString("user_id");
                                model.username = index.optString("username");
                                model.verified = index.optString("verified");
                                model.first_name = index.optString("first_name");
                                model.last_name = index.optString("last_name");
                                model.profile_pic = index.optString("profile_pic");
                                model.created_date = index.optString("created");
                                followerList.add(model);
                            }
                            //Toast.makeText(getApplicationContext(), ""+list.size(), Toast.LENGTH_SHORT).show();
                        }
                       /* for (int i=0;i<4;i++){
                            Home_Get_Set model = new Home_Get_Set();
                            model.user_id = "UserId"+i;
                            model.username = "UserName"+i;
                            model.first_name ="First Name"+i;
                            model.last_name = "Last Name"+i;
                            followerList.add(model);
                        }*/
                        actv_adapter = new GetFollowersAdapter(Post_Video_A.this, followerList, new GetFollowersAdapter.OnItemClick() {
                            @Override
                            public void onClick(String username, String id) {
                                if (!finaltext.getText().toString().contains(username)) {
                                    if (finaltext.getText().toString().equals("")) {
                                        actual_Str = id;
                                        finaltext.setText(username);
                                        finaltext.setVisibility(View.VISIBLE);
                                        close_finaltext.setVisibility(View.VISIBLE);
                                    } else {
                                        actual_Str = actual_Str + "," + id;
                                        finaltext.setText(finaltext.getText().toString() + "," + username);
                                    }
                                } else {
                                    Toast.makeText(Post_Video_A.this, username + " already added", Toast.LENGTH_SHORT).show();
                                }

                                //finaltext.setText(search_edit.getText().toString()+","+id);
                            }
                        });
                        followers_actv_recycler.setAdapter(actv_adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void callApiForHashTags() {
        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(getApplicationContext(), Variables.HASH_TAGS, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    parseData(resp);
                }
            });
        } catch (Exception e) {

        }
    }

    private void parseData(String resp) {
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                JSONObject object = msgArray.getJSONObject(0);
                JSONArray array = object.getJSONArray("hashtags");
                for (int i = 0; i < array.length(); i++) {
                    list.add(array.getString(i));
                }
                //Toast.makeText(getApplicationContext(), ""+list.size(), Toast.LENGTH_SHORT).show();
            }
            adapter = new HashTagsAdapter(this, list, new HashTagsAdapter.ClickListener() {
                @Override
                public void onClick(String item, int postion) {
                    if (description_edit.getText().toString().contains(("#" + item + " "))) {
                        Toast.makeText(Post_Video_A.this, "Already added.", Toast.LENGTH_SHORT).show();
                    } else
                        description_edit.setText("#" + item + " " + description_edit.getText().toString());
                }
            });
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
        }
    }

    public Bitmap combineImages(Bitmap c, Bitmap s) { // can add a 3rd parameter 'String loc' if you want to save the new image - left some code to do that at the bottom
        Bitmap cs = null;

        int width, height = 0;

        if (c.getWidth() > s.getWidth()) {
            width = c.getWidth() + s.getWidth();
            height = c.getHeight();
        } else {
            width = s.getWidth() + s.getWidth();
            height = c.getHeight();
        }

        cs = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(c, 0f, 0f, null);
        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
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
        try {
            final CharSequence[] options = new CharSequence[]{"Public", "Private"};

            AlertDialog.Builder builder;

            if (Functions.getSavedTheme() == R.style.WhiteTheme) {
                builder = new AlertDialog.Builder(this);
            } else {
                builder = new AlertDialog.Builder(this, R.style.AlertDialogCustomTheme);
            }
            builder.setTitle(null);

            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {
                    privcy_type_txt.setText(options[item]);

                }

            });

            builder.show();
        }catch (Exception e){
            Functions.showLogMessage(this,this.getClass().getSimpleName(),e.getMessage());
        }
    }

    // this will start the service for uploading the video into database
    public void Start_Service() {

        try {
            serviceCallback = this;

            // Toast.makeText(this, ""+finaltext.getText().toString(), Toast.LENGTH_SHORT).show();
            Upload_Service mService = new Upload_Service(serviceCallback);
            if (!Functions.isMyServiceRunning(this, mService.getClass())) {
                Intent mServiceIntent = new Intent(this.getApplicationContext(), mService.getClass());
                mServiceIntent.setAction("startservice");
                mServiceIntent.putExtra("draft_file", draft_file);
                mServiceIntent.putExtra("duet_video_id", duet_video_id);
                mServiceIntent.putExtra("uri", "" + video_path);
                mServiceIntent.putExtra("desc", "" + description_edit.getText().toString());
                mServiceIntent.putExtra("cat", category.getSelectedItem().toString());
                mServiceIntent.putExtra("privacy_type", privcy_type_txt.getText().toString());
                mServiceIntent.putExtra("tagged_users", actual_Str);
//                Toast.makeText(this, ""+actual_Str, Toast.LENGTH_SHORT).show();
                /*tagged_users*/
                if (comment_switch.isChecked())
                    mServiceIntent.putExtra("allow_comments", "true");
                else
                    mServiceIntent.putExtra("allow_comments", "false");

                if (duet_switch.isChecked())
                    mServiceIntent.putExtra("allow_duet", "true");
                else
                    mServiceIntent.putExtra("allow_duet", "false");

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
        if (bmThumbnail != null) {
            bmThumbnail.recycle();
        }
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
