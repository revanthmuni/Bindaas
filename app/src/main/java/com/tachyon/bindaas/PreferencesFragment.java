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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tachyon.bindaas.Accounts.Request_Varification_F;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Preferences.LanguageAdapter;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SimpleClasses.Webview_F;
import com.tachyon.bindaas.helper.CommonUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import kotlin.reflect.jvm.internal.impl.util.Check;

import static android.content.ContentValues.TAG;

public class PreferencesFragment extends RootFragment implements View.OnClickListener {

    View view;
    Context context;
    Switch auto_scrool_enabled;
    Switch anyone_can_message;
    Switch show_preview_switch;
    LinearLayout language_layout;
    CheckBox telugu, tamil, hindi, english, malayalam, punjabi, marathi, bengali, gujarati;
    TextView selected_languages;

    RecyclerView language_recyclerview;
    RadioGroup who_can_msg_me;
    RadioButton no_oneBtn,anyoneBtn,mutual_friendsBtn;
    RadioGroup who_can_tag_me;
    RadioButton no_oneTagme,mutual_friendsTagme;

    ConstraintLayout language_view;

    LanguageAdapter adapter;

    ArrayList<String> finalList = new ArrayList<>();
    public PreferencesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_preferences, container, false);
        try {
            context = getContext();


            selected_languages = view.findViewById(R.id.selected_languages);
            language_layout = view.findViewById(R.id.linearLayout5);
            auto_scrool_enabled = view.findViewById(R.id.auto_scroll_switch);
            show_preview_switch = view.findViewById(R.id.show_preview_switch);
            anyone_can_message = view.findViewById(R.id.any_one_can_msg);
            //right_arrow = view.findViewById(R.id.right_arrow);

            who_can_msg_me = view.findViewById(R.id.who_can_msg_rbtn);
            no_oneBtn = view.findViewById(R.id.no_one_btn);
            anyoneBtn = view.findViewById(R.id.anyone_btn);
            mutual_friendsBtn = view.findViewById(R.id.m_friends_btn);

            who_can_tag_me = view.findViewById(R.id.who_can_tag_me);
            no_oneTagme = view.findViewById(R.id.no_one_tag_me);
            mutual_friendsTagme = view.findViewById(R.id.only_mutual_friends);


            view.findViewById(R.id.Goback).setOnClickListener(this);
            selected_languages.setText("Selected Languages are: " + "\n" + Variables.sharedPreferences.getString(Variables.language, ""));
            String languages = Variables.sharedPreferences.getString(Variables.language, "");
            if (languages.equals("all")) {
                selected_languages.setText("No language selected");
            } else {
                selected_languages.setText("Selected Languages are: " + "\n" + Variables.sharedPreferences.getString(Variables.language, ""));
            }

            auto_scrool_enabled.setChecked(Variables.sharedPreferences.getBoolean(Variables.auto_scroll_key, false));
            show_preview_switch.setChecked(Variables.sharedPreferences.getBoolean(Variables.show_preview_key, false));
           // anyone_can_message.setChecked(Variables.sharedPreferences.getBoolean(Variables.anyone_can_message, false));
            String anyOnecan = Variables.sharedPreferences.getString(Variables.anyone_can_message, "anyone");
            switch (anyOnecan){
                case "no_one":
                    no_oneBtn.setChecked(true);
                    break;
                case "mutual_followers":
                    mutual_friendsBtn.setChecked(true);
                    break;
                case "anyone":
                    anyoneBtn.setChecked(true);
                    break;
                default:
                    anyoneBtn.setChecked(true);
                    break;
            }
            String whoCanTag = Variables.sharedPreferences.getString(Variables.who_can_tagme, "mutual_followers");
            switch (whoCanTag){
                case "no_one":
                    no_oneTagme.setChecked(true);
                    break;
                case "mutual_followers":
                    mutual_friendsTagme.setChecked(true);
                    break;
                default:
                    no_oneTagme.setChecked(true);
                    break;
            }
            language_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openLanguageLayout();
                }
            });

           /* right_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (language_view.getVisibility() == View.VISIBLE){
                        right_arrow.setImageResource(R.drawable.ic_down_arrow);
                        language_view.setVisibility(View.GONE);
                    }else{
                        right_arrow.setImageResource(R.drawable.ic_up_arrow);
                        language_view.setVisibility(View.VISIBLE);
                    }
                }
            });*/
            auto_scrool_enabled.setOnCheckedChangeListener((compoundButton, b) -> {
                SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
                editor.putBoolean(Variables.auto_scroll_key, b);
                editor.commit();
                callApiForSavePreferences();
                //Toast.makeText(context, ""+b, Toast.LENGTH_SHORT).show();
            });
            show_preview_switch.setOnCheckedChangeListener((compoundButton, b) -> {
                SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
                editor.putBoolean(Variables.show_preview_key, b);
                editor.commit();
                callApiForSavePreferences();
                EventBus.getDefault().post("done");
            });
            anyone_can_message.setOnCheckedChangeListener((compoundButton, b) -> {

                // Toast.makeText(context, ""+b, Toast.LENGTH_SHORT).show();
            });
            who_can_msg_me.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()){
                        case R.id.no_one_btn:
                            saveWhoCanMsgme("no_one");
//                            Toast.makeText(context, "No one", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.m_friends_btn:
                            saveWhoCanMsgme("mutual_followers");
//                            Toast.makeText(context, "Mutual", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.anyone_btn:
                            saveWhoCanMsgme("anyone");
//                            Toast.makeText(context, "Anyone", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            who_can_tag_me.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (radioGroup.getCheckedRadioButtonId()){
                        case R.id.no_one_tag_me:
                            saveWhoCanTagme("no_one");
//                            Toast.makeText(context, "No one", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.only_mutual_friends:
                            saveWhoCanTagme("mutual_followers");
//                            Toast.makeText(context, "Mutual", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, "Preferences Fragment", e.getMessage());
        }

        return view;
    }

    private void saveWhoCanTagme(String no_one) {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.who_can_tagme, no_one);
        editor.commit();
        callApiForSavePreferences();
    }

    private void saveWhoCanMsgme(String msg) {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();
        editor.putString(Variables.anyone_can_message, msg);
        editor.commit();
        callApiForSavePreferences();
    }

    private void openLanguageLayout() {
        try {
            finalList.clear();
            View view = LayoutInflater.from(context).inflate(R.layout.language_layout, null);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Select Language");
            builder.setView(view);
            language_recyclerview = view.findViewById(R.id.languages_recyclerview);

            String[] lang = getResources().getStringArray(R.array.languages);
            Log.d(TAG, "openLanguageLayout: "+lang.length);
            adapter = new LanguageAdapter(lang, new LanguageAdapter.OnItemCheckListener() {
                @Override
                public void onItemCheck(String item, int position) {
                    finalList.add(item);
                }

                @Override
                public void onItemUncheck(String item, int position) {
                    finalList.remove(item);
                }
            });
            language_recyclerview.setLayoutManager(new GridLayoutManager(context,2));
            language_recyclerview.setAdapter(adapter);
            builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    addCommaSeperatedString();
                    callApiForSavePreferences();
                }
            });
            builder.show();
        } catch (Exception e) {
            Functions.showLogMessage(context, "Preferences Fragment", e.getMessage());
        }
    }



    private void addCommaSeperatedString() {
        SharedPreferences.Editor editor = Variables.sharedPreferences.edit();

        try {
            StringBuffer sb = new StringBuffer();
            for(String i: finalList){
                sb.append(i).append(",");
            }
            if (sb.toString().equals("") ) {
                        editor.putString(Variables.language, "all");
                Log.d(TAG, "addCommaSeperatedString: " + "all");
//                Toast.makeText(context, "" + sb.toString(), Toast.LENGTH_SHORT).show();
            }else {
                sb.deleteCharAt(sb.length() - 1);
                Log.d(TAG, "addCommaSeperatedString: " + sb.toString());
                        editor.putString(Variables.language, sb.toString());
            }
            editor.commit();
            Toast.makeText(context, "Sved lan"+sb, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Functions.showLogMessage(context, "Preferences Fragment", e.getMessage());
        }
    }

    public void callApiForSavePreferences() {
        /*user_id,language,anyone_can_message,auto_scroll*/
        try {
            Functions.Show_loader(context, true, true);

            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
                parameters.put("language", Variables.sharedPreferences.getString(Variables.language, "all"));
                parameters.put("anyone_can_message", "" + Variables.sharedPreferences.getString(Variables.anyone_can_message,"anyone"));
                parameters.put("auto_scroll", "" + auto_scrool_enabled.isChecked());
                parameters.put("show_video_preview", "" + show_preview_switch.isChecked());
                parameters.put("who_can_tag_me", "" + Variables.sharedPreferences.getString(Variables.who_can_tagme,""));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "save Prefernces : " + new Gson().toJson(parameters));
            ApiRequest.Call_Api(context, Variables.SAVE_PREFERENCES, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    Log.d(TAG, "Responce: " + resp);
                    String languages = Variables.sharedPreferences.getString(Variables.language, "");
                    if (languages.equals("all")) {
                        selected_languages.setText("No Language Selected");
                    } else {
                        selected_languages.setText("Selected Languages are: " + "\n" + Variables.sharedPreferences.getString(Variables.language, ""));
                    }
                    //  Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public void onClick(View v) {
        try {
            switch (v.getId()) {

                case R.id.Goback:
                    Objects.requireNonNull(getActivity()).onBackPressed();
                    break;

            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


}