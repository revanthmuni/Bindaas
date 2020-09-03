package com.tachyon.bindaas;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Accounts.Request_Varification_F;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SimpleClasses.Webview_F;
import com.tachyon.bindaas.helper.CommonUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Objects;

import androidx.fragment.app.FragmentTransaction;

import static android.content.ContentValues.TAG;

public class PreferencesFragment extends RootFragment implements View.OnClickListener {

    View view;
    Context context;
    Switch auto_scrool_enabled;
    Switch anyone_can_message;
    LinearLayout language_layout;
    CheckBox telugu,tamil,hindi,english,kannada;
    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_preferences, container, false);
        context = getContext();
        language_layout = view.findViewById(R.id.linearLayout5);
        auto_scrool_enabled = view.findViewById(R.id.auto_scroll_switch);
        anyone_can_message = view.findViewById(R.id.any_one_can_msg);
        view.findViewById(R.id.Goback).setOnClickListener(this);
        auto_scrool_enabled.setChecked(Variables.sharedPreferences.getBoolean(Variables.auto_scroll_key,false));
        anyone_can_message.setChecked(Variables.sharedPreferences.getBoolean(Variables.anyone_can_message,false));
        language_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openLanguageLayout();
            }
        });
        auto_scrool_enabled.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
            editor.putBoolean(Variables.auto_scroll_key, b);
            editor.commit();
            callApiForSavePreferences();
            //Toast.makeText(context, ""+b, Toast.LENGTH_SHORT).show();
        });
        anyone_can_message.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
            editor.putBoolean(Variables.anyone_can_message, b);
            editor.commit();
            callApiForSavePreferences();
           // Toast.makeText(context, ""+b, Toast.LENGTH_SHORT).show();
        });


        return view;
    }

    private void openLanguageLayout() {
        View view = LayoutInflater.from(context).inflate(R.layout.language_layout,null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Language");
        builder.setView(view);
        tamil = view.findViewById(R.id.tamil);
        telugu = view.findViewById(R.id.telugu);
        hindi = view.findViewById(R.id.hindi);
        english = view.findViewById(R.id.english);
        kannada = view.findViewById(R.id.kannada);
        loadLanguages();
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addCommaSeperatedString();
                callApiForSavePreferences();
            }
        });
        builder.show();

    }

    private void loadLanguages() {
        String languages = Variables.sharedPreferences.getString(Variables.language,"");
        if (languages.contains("Telugu")){
            telugu.setChecked(true);
        }if (languages.contains("Tamil")){
            tamil.setChecked(true);
        }if (languages.contains("Hindi")){
            hindi.setChecked(true);
        }if (languages.contains("English")){
            english.setChecked(true);
        }if (languages.contains("Kannada")){
            kannada.setChecked(true);
        }
    }

    private void addCommaSeperatedString() {
        String cSValue = "";
        if (telugu.isChecked()){
            cSValue = cSValue + telugu.getText().toString() + ",";
        }if (tamil.isChecked()){
            cSValue = cSValue +  tamil.getText().toString() + ",";
        }if (hindi.isChecked()){
            cSValue = cSValue +  hindi.getText().toString() + ",";
        }if (english.isChecked()){
            cSValue = cSValue +  english.getText().toString() + ",";
        }if (kannada.isChecked()){
            cSValue = cSValue +  kannada.getText().toString() +",";
        }
        StringBuffer sb= new StringBuffer(cSValue);
//invoking the method
        sb.deleteCharAt(sb.length()-1);

        Log.d(TAG, "addCommaSeperatedString: "+sb.toString());
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.language, sb.toString());
        editor.commit();
    }

    /* user_id,first_name,last_name,gender,bio,username,
    language,anyone_can_message,auto_scroll,fb_link,insta_link */
    public void callApiForSavePreferences() {
        /*user_id,language,anyone_can_message,auto_scroll*/
        try {
            Functions.Show_loader(context, true, true);

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("language",Variables.sharedPreferences.getString(Variables.language,""));
                parameters.put("anyone_can_message",""+anyone_can_message.isChecked());
                parameters.put("auto_scroll",""+auto_scrool_enabled.isChecked());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "save Prefernces : "+new Gson().toJson(parameters));
            ApiRequest.Call_Api(context, Variables.SAVE_PREFERENCES, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    Log.d(TAG, "Responce: "+resp);
                  //  Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }
    @Override
    public void onClick(View v) {
        try{
            switch (v.getId()) {

                case R.id.Goback:
                    Objects.requireNonNull(getActivity()).onBackPressed();
                    break;

            }
        }catch (Exception e){
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

        }
    }
}