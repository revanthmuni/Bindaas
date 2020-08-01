package com.tachyon.bindaas.Signup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.helper.CommonUtils;
import com.tachyon.bindaas.helper.KeyboardUtils;
import com.tachyon.bindaas.helper.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputLayout tilEmail, tilPassword, tilRetypePassword, tilFirstName, tilLastName;

    TextInputEditText etEmail, etPassword, etRetypePassword, etFirstName, etLastName;

    Button btSignUp;

    Activity activity;

    String firstName, LastName, email, password, retypePassword;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        activity = this;
        mAuth = FirebaseAuth.getInstance();
        initialiseToolbar();
        initViews();
    }

    private void initViews() {
        sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);

        tilEmail = findViewById(R.id.tilSignUpEmail);
        tilPassword = findViewById(R.id.tilSignUpPassword);
        tilRetypePassword = findViewById(R.id.tilSignUpRetypePassword);
        tilFirstName = findViewById(R.id.tilSignUpFirstName);
        tilLastName = findViewById(R.id.tilSignUpLastName);

        etEmail = findViewById(R.id.etSignUpEmail);
        etPassword = findViewById(R.id.etSignUpPassword);
        etRetypePassword = findViewById(R.id.etSignUpRetypePassword);
        etFirstName = findViewById(R.id.etSignUpFirstName);
        etLastName = findViewById(R.id.etSignUpLastName);

        btSignUp = findViewById(R.id.btSignUp);

        btSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtils.hideSoftInput(activity);
                if (checkValidations()) {
                    if (CommonUtils.isNetworkAvailable(activity)) {
                        //Our Server signup
                        /*Call_Api_For_Signup(
                                CommonUtils.generateRandomID() + Calendar.getInstance().getTimeInMillis(), email, password, firstName, LastName, "", "local"
                        );*/

                        userSignIn(CommonUtils.generateRandomID() + Calendar.getInstance().getTimeInMillis(), email, password, firstName, LastName, "", "local");
                    }
                    else {
                        Toast.makeText(activity, "No network connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private void userSignIn(final String s, String email, String password,
                            final String firstName, final String lastName, final String s1, String local) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion = packageInfo.versionName;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user, firstName, lastName);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("firebase", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(activity, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void updateUI(FirebaseUser user, String firstName, String lastName) {
        final String id;
        final String fname;
        final String lname;
        final Uri pic_url;
        final String email;
        if (user != null) {
            // Name, email address, and profile photo Url
            id = user.getUid();

            fname = firstName;
            lname = lastName;
            email = user.getEmail();
            pic_url = user.getPhotoUrl();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(firstName + " " + lastName)
                    .setPhotoUri(pic_url)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                if (pic_url == null) {
                                    Call_Api_For_Signup(id, email, password, fname, lname, "null", "local");
                                } else {
                                    Call_Api_For_Signup(id, email, password, fname, lname, pic_url.toString(), "local");
                                }
                                Log.d("firebase", "User profile updated.");
                            }
                        }
                    });
        }

    }

    private void Call_Api_For_Signup(String id,
                                     String email,
                                     String password,
                                     String f_name,
                                     String l_name,
                                     String picture,
                                     String singnup_type) {

        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion = packageInfo.versionName;

        JSONObject parameters = new JSONObject();
        try {

            parameters.put("user_id", id);
            parameters.put("first_name", "" + f_name);
            parameters.put("last_name", "" + l_name);
            parameters.put("profile_pic", picture);
            parameters.put("gender", "");
            parameters.put("version", appversion);
            parameters.put("signup_type", singnup_type);
            parameters.put("device", Variables.device);
            parameters.put("username", email);
            parameters.put("password", password);
            parameters.put("action", "sign_up");

            parameters.put("block", 0);
            parameters.put("deviceid", Variables.sharedPreferences.getString(Variables.device_id, ""));
            parameters.put("token", MainMenuActivity.token);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("firebase", "Call_Api_For_Signup: request :" + new Gson().toJson(parameters));
        Functions.Show_loader(this, false, false);
        ApiRequest.Call_Api(this, Variables.SIGN_UP, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });

    }

    public void Parse_signup_data(String loginData) {
        Log.d("firebase", "Response :Parse_signup_data: " + loginData);
        try {
            JSONObject jsonObject = new JSONObject(loginData);
            String code = jsonObject.optString("code");
            JSONArray jsonArray = jsonObject.getJSONArray("msg");
            JSONObject userdata = jsonArray.getJSONObject(0);
            if (code.equals("200")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Variables.u_id, userdata.optString("user_id"));
                editor.putString(Variables.f_name, userdata.optString("first_name"));
                editor.putString(Variables.l_name, userdata.optString("last_name"));
                editor.putString(Variables.u_name, userdata.optString("username"));
                editor.putString(Variables.gender, userdata.optString("gender"));
                editor.putString(Variables.u_pic, userdata.optString("profile_pic"));
                editor.putString(Variables.api_token, userdata.optString("tokon"));
                editor.putBoolean(Variables.islogin, true);
                editor.apply();
                Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");
                Variables.Reload_my_videos = true;
                Variables.Reload_my_videos_inner = true;
                Variables.Reload_my_likes_inner = true;
                Variables.Reload_my_notification = true;

                Toast.makeText(activity, "Account created successfully", Toast.LENGTH_SHORT).show();

                navigateToMainActivity();
            } else {
                CommonUtils.showAlert(this, userdata.optString("response"));
                // Toast.makeText(this, "" + userdata.optString("response"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Boolean checkValidations() {
        email = etEmail.getText().toString().trim();
        password = etPassword.getText().toString().trim();
        retypePassword = etRetypePassword.getText().toString().trim();
        firstName = etFirstName.getText().toString().trim();
        LastName = etLastName.getText().toString().trim();

        if (CommonUtils.validEmail(email) == Validation.IS_VALID &&
                CommonUtils.validPassword(password) == Validation.IS_VALID &&
                firstName.length() != 0 &&
                LastName.length() != 0 &&
                retypePassword.equals(password)) {
            CommonUtils.disableError(tilEmail);
            CommonUtils.disableError(tilPassword);
            CommonUtils.disableError(tilFirstName);
            CommonUtils.disableError(tilRetypePassword);
            CommonUtils.disableError(tilLastName);

            return true;
        } else {
            switch (CommonUtils.validEmail(email)) {
                case IS_INVALID:
                    CommonUtils.enableError(tilEmail, "Enter valid email");
                    break;
                case IS_EMPTY:
                    CommonUtils.enableError(tilEmail, "Enter email");
                    break;
                case IS_VALID:
                    CommonUtils.disableError(tilEmail);
                    break;
            }

            switch (CommonUtils.validPassword(password)) {
                case IS_VALID:
                    CommonUtils.disableError(tilPassword);
                    break;
                case IS_EMPTY:
                    CommonUtils.enableError(tilPassword, "Enter password");
                    break;
                case IS_INVALID:
                    CommonUtils.enableError(tilPassword, "Password must contain 8 characters, a number and a letter");
                    break;
            }

            if (firstName.isEmpty()) {
                CommonUtils.enableError(tilFirstName, "Enter first name");
            } else
                CommonUtils.disableError(tilFirstName);

            if (LastName.isEmpty()) {
                CommonUtils.enableError(tilLastName, "Enter last name");
            } else
                CommonUtils.disableError(tilLastName);

            if (password.equals(retypePassword)) {
                CommonUtils.disableError(tilRetypePassword);
            } else
                CommonUtils.enableError(tilRetypePassword, "Passwords not matched");

            return false;
        }


    }


    private void initialiseToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.signUpToolbar);
        //setting the title
        toolbar.setTitle("Sign Up");
        //placing toolbar in place of actionbar
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}

