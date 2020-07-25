package com.tachyon.bindaas.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.model.DefaultResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Random;
import java.util.regex.Pattern;

public class CommonUtils {
    public static Validation validPassword(String password) {
        final String ONE_DIGIT = "(?=.*[0-9])";  //(?=.*[0-9]) a digit must occur at least once
        final String ONE_LETTER = "(?=.*[a-zA-Z])";  //(?=.*[a-z]) a lower case letter must occur at least once
        final String MIN_MAX_CHAR = ".{" + 8 + "," + 20 + "}";
        String passwordRex = ONE_DIGIT + ONE_LETTER + MIN_MAX_CHAR;
        Pattern pat = Pattern.compile(passwordRex);

        if (password.length() == 0) {
            return Validation.IS_EMPTY;
        } else if (pat.matcher(password).matches()) {
            return Validation.IS_VALID;
        } else {
            return Validation.IS_INVALID;
        }
    }

    public static Validation validEmail(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);

        if (email.length() == 0) {
            return Validation.IS_EMPTY;
        } else if (pat.matcher(email).matches()) {
            return Validation.IS_VALID;
        } else {
            return Validation.IS_INVALID;
        }
    }

    public static String generateRandomID() {

        Random random = new Random();
        StringBuilder randomNumbers = new StringBuilder(new StringBuilder());
        int[] sevenDigits = new int[7];
        for (int i = 0; i < 7; i++) {
            randomNumbers.append(random.nextInt(10));
            sevenDigits[i] = random.nextInt(10);
        }

        return randomNumbers.toString();
    }

    public static void disableError(TextInputLayout layout) {
        layout.setErrorEnabled(false);
    }

    public static void enableError(TextInputLayout layout, String error) {
        layout.setErrorEnabled(true);
        layout.setError(error);
        layout.setErrorIconDrawable(0);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static DefaultResponse parseDefaultResponse(String json) {
        DefaultResponse response = new DefaultResponse();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);
            response.setCode(jsonObject.optString("code"));
            JSONArray array = jsonObject.getJSONArray("msg");
            JSONObject jsonObject1 = array.getJSONObject(0);
            response.setMsg(jsonObject1.optString("response"));
            return response;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void showAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public static File getAudioFilePath(Context context, Uri uri) {
        try {
            InputStream is = context.getContentResolver().openInputStream(uri);
            File temp = new File(Variables.app_folder, Variables.SelectedAudio_AAC);
            OutputStream os = new FileOutputStream(temp);
            byte[] buffer = new byte[1024];
            int bytesRead;
            //read from is to buffer
            assert is != null;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            //flush OutputStream to write any buffered data to file
            os.flush();
            os.close();
            return temp;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String encodeFileToBase64Binary(Uri fileName)
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


}
