package com.tachyon.bindaas.Services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import androidx.annotation.Nullable;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.Video_Recording.AnimatedGifEncoder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


// this the background service which will upload the video into database
public class Upload_Service extends Service {


    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public Upload_Service getService() {
            return Upload_Service.this;
        }
    }

    boolean mAllowRebind;
    ServiceCallback Callback;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }


    Uri uri;

    String video_base64 = "";
    String draft_file;
    String videopath;
    String description;
    String privacy_type;
    String allow_comment, allow_duet;

    SharedPreferences sharedPreferences;
    OnSuccessUpload onSuccessUpload;

    public Upload_Service() {
        super();
    }

    public Upload_Service(OnSuccessUpload onSuccessUpload){
        this.onSuccessUpload = onSuccessUpload;
    }
    public Upload_Service(ServiceCallback serviceCallback) {
        Callback = serviceCallback;
    }

    public void setCallbacks(ServiceCallback serviceCallback) {
        Callback = serviceCallback;
    }


    @Override
    public void onCreate() {
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
    }
    public interface OnSuccessUpload{
        void onSuccess(String msg);
    }
    private void writeToFile(String data, Context context) {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/YourFolder/"
                        );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "config.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();
            Log.d("TESTTTTT", "writeToFile: done");

        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            if (intent.getAction().equals("startservice")) {
                showNotification();

                String uri_string = intent.getStringExtra("uri");
                uri = Uri.parse(uri_string);
                videopath = intent.getStringExtra("uri");
                draft_file = intent.getStringExtra("draft_file");
                description = intent.getStringExtra("desc");
                privacy_type = intent.getStringExtra("privacy_type");
                allow_comment = intent.getStringExtra("allow_comment");
                allow_duet = intent.getStringExtra("allow_duet");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuilder base = new StringBuilder();

                        try {

                            video_base64 = encodeFileToBase64Binary(uri);
                            base.append(encodeFileToBase64Binary(uri));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        JSONObject parameters = new JSONObject();

                        try {
                            parameters.put("user_id", sharedPreferences.getString(Variables.u_id, ""));
                            parameters.put("sound_id", Variables.Selected_sound_id);
                            parameters.put("description", description);
                            parameters.put("privacy_type",privacy_type);
                            parameters.put("allow_comments",allow_comment);

                            JSONObject vidoefiledata = new JSONObject();
                            vidoefiledata.put("file_data", video_base64);
                            parameters.put("videobase64", vidoefiledata);

                            Log.d("Test", "BASE64:"+new Gson().toJson(parameters));
                            base.append("done");
                            Log.d("Test", "run: "+video_base64.length());

                          //  writeToFile(new Gson().toJson(parameters),getApplicationContext());
                          //  writeToFile(video_base64,getApplicationContext());
                           /* int maxLogSize = 10000;
                            for(int i = 0; i <= video_base64.length() / maxLogSize; i++) {
                                int start = i * maxLogSize;
                                int end = (i+1) * maxLogSize;
                                end = end > video_base64.length() ? video_base64.length() : end;
                                Log.v("Test::::>", video_base64.substring(start, end));

                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        generateNoteOnSD("parameters", parameters.toString());

                        RequestQueue rq = Volley.newRequestQueue(Upload_Service.this);
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                                (Request.Method.POST, Variables.uploadVideo, parameters, new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            String respo = response.toString();

                                            if (!Variables.is_secure_info)
                                                Log.d("responce", "uplod:"+respo);

                                            stopForeground(true);
                                            stopSelf();
                                            EventBus.getDefault().post("Your Video is uploaded Successfully");
                                            if (Callback != null)
                                                Callback.ShowResponce("Your Video is uploaded Successfully");
                                        }catch (Exception e){
                                            Log.d("Crash Exception", "onResponse: "+e.getMessage());
                                        }

                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        if (!Variables.is_secure_info)
                                            Log.d("respo", error.toString());
                                        stopForeground(true);
                                        stopSelf();
                                        EventBus.getDefault().post("Please try again later");

                                        if (Callback != null)
                                            Callback.ShowResponce("Please try again later");
                                        if (onSuccessUpload!=null)
                                            onSuccessUpload.onSuccess("Please try again later");


                                    }
                                }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("fb-id", sharedPreferences.getString(Variables.u_id, "0"));
                                headers.put("version", getResources().getString(R.string.version));
                                headers.put("device", getResources().getString(R.string.device));
                                headers.put("tokon", sharedPreferences.getString(Variables.api_token, ""));
                                headers.put("deviceid", sharedPreferences.getString(Variables.device_id, ""));
                                Log.d(Variables.tag, headers.toString());
                                return headers;
                            }
                        };

                        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(120000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                        rq.getCache().clear();
                        rq.add(jsonObjectRequest);

                    }
                }).start();


            } else if (intent.getAction().equals("stopservice")) {
                stopForeground(true);
                stopSelf();
            }

        }


        return Service.START_STICKY;
    }


    // this will show the sticky notification during uploading video
    private void showNotification() {
        try {
            Intent notificationIntent = new Intent(this, MainMenuActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            final String CHANNEL_ID = "default";
            final String CHANNEL_NAME = "Default";

            NotificationManager notificationManager = (NotificationManager) this.getSystemService(this.NOTIFICATION_SERVICE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel defaultChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(defaultChannel);
            }

            androidx.core.app.NotificationCompat.Builder builder = (androidx.core.app.NotificationCompat.Builder) new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setContentTitle("Uploading Video")
                    .setContentText("Please wait! Video is uploading....")
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            android.R.drawable.stat_sys_upload))
                    .setContentIntent(pendingIntent);

            Notification notification = builder.build();
            startForeground(101, notification);
        } catch (Exception e) {
            Log.d("Crash Exception", "showNotification: " + e.getMessage());
        }
    }


    // for thumbnail
    public String Bitmap_to_base64(Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }


    // for video base64
    private String encodeFileToBase64Binary(Uri fileName)
            throws IOException {

        File file = new File(fileName.getPath());
        byte[] bytes = loadFile(file);
        String encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encodedString;
    }

    private static byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }


    //for video gif image
    public byte[] generateGIF(ArrayList<Bitmap> bitmaps) {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        AnimatedGifEncoder encoder = new AnimatedGifEncoder();
        encoder.start(bos);
        for (Bitmap bitmap : bitmaps) {

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));

            encoder.addFrame(decoded);

        }

        encoder.finish();


        File filePath = new File(Variables.app_folder, "sample.gif");
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(filePath);
            outputStream.write(bos.toByteArray());
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return bos.toByteArray();
    }


    public void generateNoteOnSD(String sFileName, String sBody) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName + ".txt");
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}