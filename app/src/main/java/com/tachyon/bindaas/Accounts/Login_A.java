package com.tachyon.bindaas.Accounts;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.Signup.SignUpActivity;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tachyon.bindaas.helper.CommonUtils;
import com.tachyon.bindaas.helper.KeyboardUtils;
import com.tachyon.bindaas.helper.Validation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class Login_A extends AppCompatActivity {
    private static final String TAG = "Login_A";
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;

    Activity activity;


    SharedPreferences sharedPreferences;

    View top_view;

    TextView login_title_txt, loginTitleTxt, tvSignUpText;
    RelativeLayout rlLoginOptions;
    ConstraintLayout clLoginLayout;

    TextInputLayout tilLoginEmail, tilLoginPassword;
    TextInputEditText etLoginEmail, etLoginPassword;
    String email, password;

    ImageView ivBack3;
    TextInputEditText editForgotEmail2;
    Button btnForgot;
    TextView forgot_password_textview;
    ConstraintLayout forgot_pswd_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setTheme(Functions.getSavedTheme());
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            activity = this;

            /*if (Build.VERSION.SDK_INT == 26) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_USER);
            }*/

            getWindow().setBackgroundDrawable(
                    new ColorDrawable(android.graphics.Color.TRANSPARENT));

            this.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);


            setContentView(R.layout.activity_login);


            mAuth = FirebaseAuth.getInstance();
            firebaseUser = mAuth.getCurrentUser();

            // if the user is already login trought facebook then we will logout the user automatically
            LoginManager.getInstance().logOut();

            sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
            initViews();

            forgot_password_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    forgot_pswd_layout.setVisibility(View.VISIBLE);
                    clLoginLayout.setVisibility(View.GONE);
                }
            });
            ivBack3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    forgot_pswd_layout.setVisibility(View.GONE);
                    clLoginLayout.setVisibility(View.VISIBLE);
                }
            });
            btnForgot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sendResetMail();
                }
            });

            findViewById(R.id.facebook_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Loginwith_FB();
                }
            });


            findViewById(R.id.google_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Sign_in_with_gmail();
                }
            });

            findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlLoginOptions.setVisibility(View.GONE);
                    clLoginLayout.setVisibility(View.VISIBLE);
                }
            });

            findViewById(R.id.ivBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rlLoginOptions.setVisibility(View.VISIBLE);
                    clLoginLayout.setVisibility(View.GONE);
                }
            });

            findViewById(R.id.btSignIn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    KeyboardUtils.hideSoftInput(activity);

                    if (checkValidations()) {
                        if (CommonUtils.isNetworkAvailable(activity)) {
                            //callLoginApi(email, password);
                            callLoginApiWithFirebase(email, password);
                        } else {
                            Toast.makeText(activity, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });


            login_title_txt.setText("You need a " + getString(R.string.app_name) + "\naccount to Continue");
            loginTitleTxt.setText("You need a " + getString(R.string.app_name) + "\naccount to Continue");


            SpannableString ss = new SpannableString("By signing up, you confirm that you agree to our \n Terms of Use and have read and understood \n our Privacy Policy.");
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    Open_Privacy_policy();
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(false);
                }
            };


            ss.setSpan(clickableSpan, 99, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            TextView textView = (TextView) findViewById(R.id.login_terms_condition_txt);
            textView.setText(ss);
            textView.setClickable(true);
            textView.setMovementMethod(LinkMovementMethod.getInstance());


            printKeyHash();


            String text = "New to Bindaas? Signup here";

            SpannableString signupString = new SpannableString(text);

            ClickableSpan clickableSpan1 = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {

                    Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                    startActivity(intent);

                }
            };

            signupString.setSpan(clickableSpan1,
                    text.indexOf("Signup here"),
                    text.indexOf("Signup here") + "Signup here".length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvSignUpText.setText(signupString);
            tvSignUpText.setMovementMethod(LinkMovementMethod.getInstance());
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());
        }

    }

    private void sendResetMail() {
        if (!TextUtils.isEmpty(editForgotEmail2.getText().toString())) {
            Functions.Show_loader(this,false,true);
            FirebaseAuth.getInstance().sendPasswordResetEmail(editForgotEmail2.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Functions.cancel_loader();
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Email sent.");
                                Toast.makeText(activity, "Password reset mail sent", Toast.LENGTH_SHORT).show();
                                forgot_pswd_layout.setVisibility(View.GONE);
                                clLoginLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }else {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
        }
    }

    private void callLoginApiWithFirebase(String email, String password) {
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("firebase", "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("firebase", "signInWithEmail:failure", task.getException());
                                Toast.makeText(activity, R.string.authentication_failed,
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());
        }

    }

    private void updateUI(FirebaseUser user) {
        try {

            if (user != null) {
                // Name, email address, and profile photo Url
                String id = user.getUid();

                String username = user.getDisplayName();
                String sep[] = username.split(" ");
                String fname = sep[0];
                String lname = sep[1];
                Log.d("firebase", "updateUI:" + username);
                String pic_url;
                if (user.getPhotoUrl() != null) {
                    pic_url = user.getPhotoUrl().toString();
                } else {
                    pic_url = "null";
                }
                if (fname.equals("") || fname.equals("null"))
                    fname = getResources().getString(R.string.app_name);
                if (lname.equals("") || lname.equals("null"))
                    lname = "User";

                Call_Api_For_Signup(id, fname, lname, pic_url, "local");
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void callLoginApi(String email, String password) {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String appversion = packageInfo.versionName;

        JSONObject parameters = new JSONObject();
        try {
            parameters.put("user_id", "");
            parameters.put("first_name", "");
            parameters.put("last_name", "");
            parameters.put("profile_pic", "");
            parameters.put("gender", "");
            parameters.put("action", "sign_in");
            parameters.put("version", appversion);
            parameters.put("signup_type", "local");
            parameters.put("device", Variables.device);
            parameters.put("username", email);
            parameters.put("password", password);
            parameters.put("deviceid", Variables.sharedPreferences.getString(Variables.device_id, ""));
            parameters.put("token", MainMenuActivity.token);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Functions.Show_loader(this, false, false);
        ApiRequest.Call_Api(this, Variables.SIGN_UP, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Toast.makeText(activity, resp, Toast.LENGTH_SHORT).show();
                Functions.cancel_loader();
                Parse_signup_data(resp);

            }
        });

    }


    private void initViews() {
        try {

            rlLoginOptions = findViewById(R.id.rlLoginOptions);
            clLoginLayout = findViewById(R.id.clLoginLayout);
            tvSignUpText = findViewById(R.id.tvSignUpText);
            top_view = findViewById(R.id.top_view);

            login_title_txt = findViewById(R.id.login_title_txt);
            loginTitleTxt = findViewById(R.id.loginTitleTxt);

            tilLoginEmail = findViewById(R.id.tilLoginEmail);
            tilLoginPassword = findViewById(R.id.tilLoginPassword);
            etLoginEmail = findViewById(R.id.etLoginEmail);
            etLoginPassword = findViewById(R.id.etLoginPassword);

            btnForgot = findViewById(R.id.btnForgot);
            ivBack3 = findViewById(R.id.ivBack3);
            editForgotEmail2 = findViewById(R.id.editForgotEmail2);
            forgot_pswd_layout = findViewById(R.id.forgot_layout);
            forgot_password_textview = findViewById(R.id.textView16);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }


    }

    private Boolean checkValidations() {
        try {
            email = etLoginEmail.getText().toString().trim();
            password = etLoginPassword.getText().toString().trim();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
        if (CommonUtils.validEmail(email) == Validation.IS_VALID &&
                CommonUtils.validPassword(password) == Validation.IS_VALID) {
            CommonUtils.disableError(tilLoginEmail);
            CommonUtils.disableError(tilLoginPassword);
            return true;
        } else {
            switch (CommonUtils.validEmail(email)) {
                case IS_INVALID:
                    CommonUtils.enableError(tilLoginEmail, "Enter valid email");
                    break;
                case IS_EMPTY:
                    CommonUtils.enableError(tilLoginEmail, "Enter email");
                    break;
                case IS_VALID:
                    CommonUtils.disableError(tilLoginEmail);
                    break;
            }

            switch (CommonUtils.validPassword(password)) {
                case IS_VALID:
                    CommonUtils.disableError(tilLoginPassword);
                    break;
                case IS_EMPTY:
                    CommonUtils.enableError(tilLoginPassword, "Enter password");
                    break;
                case IS_INVALID:
                    CommonUtils.enableError(tilLoginPassword, "Password must contain 8 characters, a number and a letter");
                    break;
            }
            return false;
        }
    }


    public void Open_Privacy_policy() {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Variables.privacy_policy));
            startActivity(browserIntent);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        try {
            AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
            anim.setDuration(200);
            top_view.startAnimation(anim);
            top_view.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    @Override
    public void onBackPressed() {
        try {
            top_view.setVisibility(View.GONE);
            finish();
            overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    // Bottom two function are related to Fb implimentation
    private CallbackManager mCallbackManager;

    //facebook implimentation
    public void Loginwith_FB() {
        try {
            LoginManager.getInstance()
                    .logInWithReadPermissions(Login_A.this,
                            Arrays.asList("public_profile", "email"));

            // initialze the facebook sdk and request to facebook for login
            FacebookSdk.sdkInitialize(this.getApplicationContext());
            mCallbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                    Log.d("resp_token", loginResult.getAccessToken() + "");
                }

                @Override
                public void onCancel() {
                    // App code
                    Toast.makeText(Login_A.this, R.string.login_cancel, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d("resp", "" + error.toString());
                    Toast.makeText(Login_A.this, getString(R.string.login_error) + error.toString(), Toast.LENGTH_SHORT).show();
                }

            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

    private void handleFacebookAccessToken(final AccessToken token) {
        try {
            // if user is login then this method will call and
            // facebook will return us a token which will user for get the info of user
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            Log.d("resp_token", token.getToken() + "");
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Functions.Show_loader(Login_A.this, false, false);
                                        final String id = Profile.getCurrentProfile().getId();
                                        GraphRequest request = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                                            @Override
                                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {

                                                Functions.cancel_loader();
                                                Log.d("resp", user.toString());
                                                //after get the info of user we will pass to function which will store the info in our server

                                                String fname = "" + user.optString("first_name");
                                                String lname = "" + user.optString("last_name");


                                                if (fname.equals("") || fname.equals("null"))
                                                    fname = getResources().getString(R.string.app_name);

                                                if (lname.equals("") || lname.equals("null"))
                                                    lname = "";

                                                Call_Api_For_Signup("" + id, fname
                                                        , lname,
                                                        "https://graph.facebook.com/" + id + "/picture?width=500&width=500",
                                                        "facebook");

                                            }
                                        });

                                        // here is the request to facebook sdk for which type of info we have required
                                        Bundle parameters = new Bundle();
                                        parameters.putString("fields", "last_name,first_name,email");
                                        request.setParameters(parameters);
                                        request.executeAsync();
                                    } else {
                                        Functions.cancel_loader();
                                        Toast.makeText(Login_A.this, R.string.authentication_failed,
                                                Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result back to the Facebook SDK

        try {
            if (requestCode == 123) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } else if (mCallbackManager != null)
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    //google Implimentation
    GoogleSignInClient mGoogleSignInClient;

    public void Sign_in_with_gmail() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Login_A.this);
            if (account != null) {
                String id = account.getId();
                String fname = "" + account.getGivenName();
                String lname = "" + account.getFamilyName();

                String pic_url;
                if (account.getPhotoUrl() != null) {
                    pic_url = account.getPhotoUrl().toString();
                } else {
                    pic_url = "null";
                }


                if (fname.equals("") || fname.equals("null"))
                    fname = getResources().getString(R.string.app_name);

                if (lname.equals("") || lname.equals("null"))
                    lname = "User";
                Call_Api_For_Signup(id, fname, lname, pic_url, "gmail");


            } else {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 123);
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }


    //Relate to google login
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                String id = account.getId();
                String fname = "" + account.getGivenName();
                String lname = "" + account.getFamilyName();

                // if we do not get the picture of user then we will use default profile picture

                String pic_url;
                if (account.getPhotoUrl() != null) {
                    pic_url = account.getPhotoUrl().toString();
                } else {
                    pic_url = "null";
                }


                if (fname.equals("") || fname.equals("null"))
                    fname = getResources().getString(R.string.app_name);

                if (lname.equals("") || lname.equals("null"))
                    lname = "User";

                Call_Api_For_Signup(id, fname, lname, pic_url, "gmail");


            }
        } catch (ApiException e) {
            Log.w("Error message", "signInResult:failed code=" + e.getStatusCode());
        }

    }


    // this function call an Api for Signin
    private void Call_Api_For_Signup(String id,
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
            parameters.put("gender", "m");
            parameters.put("version", appversion);
            parameters.put("signup_type", singnup_type);
            parameters.put("device", Variables.device);
            parameters.put("deviceid", Variables.sharedPreferences.getString(Variables.device_id, ""));
            parameters.put("token", MainMenuActivity.token);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("firebase", "Call_Api_For_Signin: request" + new Gson().toJson(parameters));
        Functions.Show_loader(this, false, false);
        ApiRequest.Call_Api(this, Variables.SignUp, parameters, new Callback() {
            @Override
            public void Responce(String resp) {
                Functions.cancel_loader();
                Parse_signup_data(resp);
                Log.d("firebase", "Responce: " + resp);

            }
        });

    }

    // if the signup successfull then this method will call and it store the user info in local
    public void Parse_signup_data(String loginData) {
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
                editor.putString(Variables.language,userdata.optString("language"));
                editor.putBoolean(Variables.auto_scroll_key, Boolean.parseBoolean(userdata.optString("auto_scroll")));
                editor.putBoolean(Variables.show_preview_key, Boolean.parseBoolean(userdata.optString("show_video_preview")));
                editor.putString(Variables.anyone_can_message, userdata.optString("anyone_can_message"));
                editor.putString(Variables.who_can_tagme, userdata.optString("who_can_tag_me"));
                editor.putString(Variables.fb_link,userdata.optString("fb_link"));
                editor.putString(Variables.insta_link,userdata.optString("insta_link"));
                editor.putString(Variables.bio,userdata.optString("bio"));
                editor.putString(Variables.gender,userdata.optString("gender"));
                editor.putString(Variables.themes_key,userdata.optString("theme"));

                editor.putBoolean(Variables.islogin, true);
                editor.commit();
                Variables.sharedPreferences = getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
                Variables.user_id = Variables.sharedPreferences.getString(Variables.u_id, "");
                Variables.Reload_my_videos = true;
                Variables.Reload_my_videos_inner = true;
                Variables.Reload_my_likes_inner = true;
                Variables.Reload_my_notification = true;
                top_view.setVisibility(View.GONE);
                //finish();

                Toast.makeText(this, R.string.login_successful, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,MainMenuActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

                //startActivity(new Intent(this, MainMenuActivity.class));


            } else {
                CommonUtils.showAlert(Login_A.this, userdata.optString("response"));
                //Toast.makeText(this, ""+userdata.optString("response"), Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "" + userdata.optString("msg"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    // this function will print the keyhash of your project
    // which is very helpfull during Fb login implimentation
    public void printKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.i("keyhash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


}
