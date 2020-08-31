package com.tachyon.bindaas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
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

import androidx.fragment.app.FragmentTransaction;

import static android.content.ContentValues.TAG;

public class PreferencesFragment extends RootFragment implements View.OnClickListener {

    View view;
    Context context;

    Switch auto_scrool_enabled;
    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_preferences, container, false);
        context = getContext();
        auto_scrool_enabled = view.findViewById(R.id.auto_scroll_switch);
        auto_scrool_enabled.setChecked(Variables.sharedPreferences.getBoolean(Variables.auto_scroll_key,false));
        auto_scrool_enabled.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
            editor.putBoolean(Variables.auto_scroll_key, b);
            editor.commit();
            Toast.makeText(context, ""+b, Toast.LENGTH_SHORT).show();
        });


        return view;
    }
    public void Call_Api_For_Edit_profile() {
        try {
            Functions.Show_loader(context, false, false);

            JSONObject parameters = new JSONObject();
            try {
                if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals("0")) {
                    parameters.put("user_id", CommonUtils.generateRandomID() + Calendar.getInstance().getTimeInMillis());
                } else {
                    parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                }
                parameters.put("first_name", Variables.sharedPreferences.getString(Variables.f_name,""));
                parameters.put("last_name",Variables.sharedPreferences.getString(Variables.l_name,""));
                parameters.put("bio",Variables.sharedPreferences.getString(Variables.bio,""));
                parameters.put("fb_link",Variables.sharedPreferences.getString(Variables.fb_link,""));
                parameters.put("insta_link", Variables.sharedPreferences.getString(Variables.insta_link,""));
                parameters.put("gender",Variables.sharedPreferences.getString(Variables.gender,""));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Call_Api_For_Edit_profile: "+new Gson().toJson(parameters));
            ApiRequest.Call_Api(context, Variables.editProfile, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    Log.d(TAG, "Responce: "+resp);
                    try {
                        JSONObject response = new JSONObject(resp);
                        String code = response.optString("code");
                        JSONArray msg = response.optJSONArray("msg");
                        if (code.equals("200")) {

                            SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
                            JSONObject object = msg.getJSONObject(0);

                            editor.putBoolean(Variables.auto_scroll_key, Boolean.parseBoolean(object.optString("auto_scroll")));

                            editor.commit();

                            getActivity().onBackPressed();
                        } else {

                            if (msg != null) {
                                JSONObject jsonObject = msg.optJSONObject(0);
                                Toast.makeText(context, jsonObject.optString("response"), Toast.LENGTH_SHORT).show();
                            }


                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                    getActivity().onBackPressed();
                    break;

            }
        }catch (Exception e){
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

        }
    }

    @Override
    public boolean onBackPressed() {
        return super.onBackPressed();
    }
}