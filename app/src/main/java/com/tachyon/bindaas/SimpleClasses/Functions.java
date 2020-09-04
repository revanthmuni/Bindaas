package com.tachyon.bindaas.SimpleClasses;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.gson.Gson;
import com.tachyon.bindaas.Comments.Comment_Get_Set;
import com.tachyon.bindaas.Constant;
import com.tachyon.bindaas.R;
import com.gmail.samehadar.iosdialog.CamomileSpinner;
import com.googlecode.mp4parser.authoring.Track;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class Functions {


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String GetSuffix(String value) {
        try {

            int count = Integer.parseInt(value);
            if (count < 1000) return "" + count;
            int exp = (int) (Math.log(count) / Math.log(1000));
            return String.format("%.1f %c",
                    count / Math.pow(1000, exp),
                    "kMGTPE".charAt(exp - 1));
        } catch (Exception e) {
            return value;
        }

    }

    public static void Show_Alert(Context context, String title, String Message) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(title)
                    .setMessage(Message)
                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
            Log.e("Exception:", "" + e.getMessage());
//            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    public static void Show_Alert(Context context, String title, String Message, String postivebtn, String negitivebtn, final Callback callback) {
        try {
            new AlertDialog.Builder(context)
                    .setTitle(null)
                    .setMessage(Message)
                    .setNegativeButton(negitivebtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callback.Responce("no");
                        }
                    })
                    .setPositiveButton(postivebtn, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                            callback.Responce("yes");

                        }
                    }).show();
        } catch (Exception e) {
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());
        }
    }

    public static void showLogMessage(Context context, String class_name, String msg) {

        Log.d("Crash Exections:", class_name + ": " + msg);


        //send custom crash report

    }

    public static Dialog dialog;

    public static void downloadFile(String url, String folderpath, String fileName) {
        PRDownloader.initialize(getApplicationContext());

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        PRDownloader.download(url, folderpath, fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {

                    }

                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                    }

                    @Override
                    public void onError(Error error) {
                    }

                });
    }

    public static void Show_loader(Context context, boolean outside_touch, boolean cancleable) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.item_dialog_loading_view);
        dialog.getWindow().setBackgroundDrawable(context.getResources().getDrawable(R.drawable.d_round_white_background));


        CamomileSpinner loader = dialog.findViewById(R.id.loader);
        loader.start();


        if (!outside_touch)
            dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            dialog.setCancelable(false);

        dialog.show();
    }

    public static void cancel_loader() {
        if (dialog != null) {
            dialog.cancel();
        }
    }


    public static float dpToPx(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("isMyServiceRunning?", true + "");
                return true;
            }
        }
        Log.i("isMyServiceRunning?", false + "");
        return false;
    }


    public static void Share_through_app(final Activity activity, final String link) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, link);
                activity.startActivity(Intent.createChooser(intent, ""));

            }
        }).start();
    }


    public static Bitmap Uri_to_bitmap(Activity activity, Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = activity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path = uri.getPath();
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                exif = new ExifInterface(path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);

        return rotatedBitmap;
    }

    public static String ChangeDate_to_today_or_yesterday(Context context, String date) {
        try {
            Calendar current_cal = Calendar.getInstance();

            Calendar date_cal = Calendar.getInstance();

            SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ");
            Date d = null;
            try {
                d = f.parse(date);
                date_cal.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            long difference = (current_cal.getTimeInMillis() - date_cal.getTimeInMillis()) / 1000;

            if (difference < 86400) {
                if (current_cal.get(Calendar.DAY_OF_YEAR) - date_cal.get(Calendar.DAY_OF_YEAR) == 0) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    return sdf.format(d);
                } else
                    return "yesterday";
            } else if (difference < 172800) {
                return "yesterday";
            } else
                return (difference / 86400) + " day ago";

        } catch (Exception e) {
            return date;
        }
    }

    public static String Bitmap_to_base64(Activity activity, Bitmap imagebitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imagebitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }

    public static Bitmap Base64_to_bitmap(String base_64) {
        Bitmap decodedByte = null;
        try {

            byte[] decodedString = Base64.decode(base_64, Base64.DEFAULT);
            decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {

        }
        return decodedByte;
    }

    public static String Uri_to_base64(Activity activity, Uri uri) {
        InputStream imageStream = null;
        try {
            imageStream = activity.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        final Bitmap imagebitmap = BitmapFactory.decodeStream(imageStream);

        String path = uri.getPath();
        Matrix matrix = new Matrix();
        ExifInterface exif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                exif = new ExifInterface(path);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Bitmap rotatedBitmap = Bitmap.createBitmap(imagebitmap, 0, 0, imagebitmap.getWidth(), imagebitmap.getHeight(), matrix, true);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return base64;
    }


    public static double correctTimeToSyncSample(Track track, double cutHere, boolean next) {
        double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
        long currentSample = 0;
        double currentTime = 0;
        for (int i = 0; i < track.getSampleDurations().length; i++) {
            long delta = track.getSampleDurations()[i];

            if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
            }
            currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
            currentSample++;

        }
        double previous = 0;
        for (double timeOfSyncSample : timeOfSyncSamples) {
            if (timeOfSyncSample > cutHere) {
                if (next) {
                    return timeOfSyncSample;
                } else {
                    return previous;
                }
            }
            previous = timeOfSyncSample;
        }
        return timeOfSyncSamples[timeOfSyncSamples.length - 1];
    }


    public static void make_directry(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }


    public static String getRandomString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 10) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    public static long getfileduration(Context context, Uri uri) {
        try {

            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(context, uri);
            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            final int file_duration = Integer.parseInt(durationStr);

            return file_duration;
        } catch (Exception e) {

        }
        return 0;
    }

    // Bottom is all the Apis which is mostly used in app we have add it
    // just one time and whenever we need it we will call it

    public static void Call_Api_For_like_video(final Activity activity,
                                               String video_id, String action,
                                               final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("video_id", video_id);
            parameters.put("action", action);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(activity, Variables.likeDislikeVideo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                api_callBack.OnSuccess(resp);
            }
        });


    }


    public static void Call_Api_For_Send_Comment(final Activity activity, String video_id, String comment, final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("video_id", video_id);
            parameters.put("comment", comment);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(activity, Variables.postComment, parameters, new Callback() {
            @Override
            public void Responce(String resp) {

                ArrayList<Comment_Get_Set> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Comment_Get_Set item = new Comment_Get_Set();
                            item.user_id = itemdata.optString("user_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");


                            item.video_id = itemdata.optString("id");
                            item.comments = itemdata.optString("comments");
                            item.created = itemdata.optString("created");
                            item.comment_id = itemdata.optString("comment_id");


                            arrayList.add(item);
                        }

                        api_callBack.ArrayData(arrayList);

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }

            }
        });


    }

    public static void callApiForShareVideo(final Activity activity, String video_id) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(activity, Variables.SHARE_VIDEO, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Toast.makeText(activity, resp, Toast.LENGTH_SHORT).show();
               /* ArrayList<Comment_Get_Set> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Comment_Get_Set item = new Comment_Get_Set();
                            item.user_id = itemdata.optString("user_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");


                            item.video_id = itemdata.optString("id");
                            item.comments = itemdata.optString("comments");
                            item.created = itemdata.optString("created");
                            item.comment_id = itemdata.optString("comment_id");


                            arrayList.add(item);
                        }

                        api_callBack.ArrayData(arrayList);

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }*/

            }
        });


    }

    public static void Call_Api_For_get_Comment(final Activity activity, String video_id, final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("video_id", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(activity, Variables.showVideoComments, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                ArrayList<Comment_Get_Set> arrayList = new ArrayList<>();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        JSONArray msgArray = response.getJSONArray("msg");
                        for (int i = 0; i < msgArray.length(); i++) {
                            JSONObject itemdata = msgArray.optJSONObject(i);
                            Comment_Get_Set item = new Comment_Get_Set();
                            item.user_id = itemdata.optString("user_id");

                            JSONObject user_info = itemdata.optJSONObject("user_info");
                            item.first_name = user_info.optString("first_name");
                            item.last_name = user_info.optString("last_name");
                            item.profile_pic = user_info.optString("profile_pic");


                            item.video_id = itemdata.optString("id");
                            item.comments = itemdata.optString("comments");
                            item.created = itemdata.optString("created");
                            item.comment_id = itemdata.optString("comment_id");


                            arrayList.add(item);
                        }

                        api_callBack.ArrayData(arrayList);

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });

    }


    public static void Call_Api_For_update_view(final Activity activity,
                                                String video_id) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            parameters.put("id", video_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(activity, Variables.updateVideoView, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Log.d("Test Call_Api", "Response :updateVideoView" + resp);
            }
        });

    }

    public static void Call_Api_For_Follow_or_unFollow
            (final Activity activity,
             String user_id,
             String followed_user_id,
             String status,
             final API_CallBack api_callBack) {

        Functions.Show_loader(activity, false, false);


        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", user_id);
            parameters.put("followed_user_id", followed_user_id);
            parameters.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("Follow_Test", "Call_Api_For_Follow_or_unFollow: input" + new Gson().toJson(parameters));
        ApiRequest.Call_Api(activity, Variables.followUsers, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Log.d("Follow_Test", "Responce: follow " + resp);
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.OnSuccess(response.toString());

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });


    }


    public static void Call_Api_For_Get_User_data
            (final Activity activity,
             String user_id,
             final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", user_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("resp", parameters.toString());

        ApiRequest.Call_Api(activity, Variables.getUserData, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");
                    if (code.equals("200")) {
                        api_callBack.OnSuccess(response.toString());
                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }
            }
        });

    }


    public static void Call_Api_For_Delete_Video
            (final Activity activity,
             String video_id,
             final API_CallBack api_callBack) {

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("id", video_id);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        ApiRequest.Call_Api(activity, Variables.DeleteVideo, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Log.d(TAG, "Responce: " + resp);
                try {
                    JSONObject response = new JSONObject(resp);
                    String code = response.optString("code");

                    if (code.equals("200")) {
                        if (api_callBack != null)
                            api_callBack.OnSuccess(response.toString());

                    } else {
                        Toast.makeText(activity, "" + response.optString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    if (api_callBack != null)
                        api_callBack.OnFail(e.toString());
                    e.printStackTrace();
                }

            }
        });


    }


    public static Dialog indeterminant_dialog;

    public static void Show_indeterminent_loader(Context context, boolean outside_touch, boolean cancleable) {

        indeterminant_dialog = new Dialog(context);
        indeterminant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        indeterminant_dialog.setContentView(R.layout.item_indeterminant_progress_layout);
        indeterminant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));


        if (!outside_touch)
            indeterminant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            indeterminant_dialog.setCancelable(false);

        indeterminant_dialog.show();

    }


    public static void cancel_indeterminent_loader() {
        if (indeterminant_dialog != null) {
            indeterminant_dialog.cancel();
        }
    }


    public static Dialog determinant_dialog;
    public static ProgressBar determinant_progress;

    public static void Show_determinent_loader(Context context, boolean outside_touch, boolean cancleable) {

        determinant_dialog = new Dialog(context);
        determinant_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        determinant_dialog.setContentView(R.layout.item_determinant_progress_layout);
        determinant_dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.d_round_white_background));

        determinant_progress = determinant_dialog.findViewById(R.id.pbar);

        if (!outside_touch)
            determinant_dialog.setCanceledOnTouchOutside(false);

        if (!cancleable)
            determinant_dialog.setCancelable(false);

        determinant_dialog.show();

    }

    public static void Show_loading_progress(int progress) {
        if (determinant_progress != null) {
            determinant_progress.setProgress(progress);

        }
    }


    public static void cancel_determinent_loader() {
        if (determinant_dialog != null) {
            determinant_progress = null;
            determinant_dialog.cancel();
        }
    }


    public static boolean Checkstoragepermision(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;

            } else {

                activity.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {

            return true;
        }
    }

    public static boolean check_permissions(Activity context) {

        String[] PERMISSIONS = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        };

        if (!hasPermissions(context, PERMISSIONS)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                context.requestPermissions(PERMISSIONS, 2);
            }
        } else {

            return true;
        }

        return false;
    }


    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    // these function are remove the cache memory which is very helpfull in memmory managmet
    public static void deleteCache(Context context) {
        Glide.get(context).clearMemory();

        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }
    public static void load_Directory_Files(File directory){
        File[] fileList = directory.listFiles();
        if(fileList != null && fileList.length > 0){
            for (int i=0; i<fileList.length; i++){
                if(fileList[i].isDirectory()){
                    load_Directory_Files(fileList[i]);
                }
                else {
                    String name = fileList[i].getName().toLowerCase();
                    for (String extension: Constant.videoExtensions){
                        //check the type of file
                        if(name.endsWith(extension)){
                            Constant.allMediaList.add(fileList[i]);
                            //when we found file
                            break;
                        }
                    }
                }
            }
        }
    }
    public static void openBrowser(Context context,String url) {
        Log.d("URL->", "openBrowser: "+url);
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            context.startActivity(i);
        }catch (Exception e){
            Functions.showLogMessage(context,"OpenBrowser",e.getMessage());
        }
    }

}
