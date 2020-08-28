package com.tachyon.bindaas;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.tachyon.bindaas.Accounts.Request_Varification_F;
import com.tachyon.bindaas.Main_Menu.MainMenuActivity;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SimpleClasses.Webview_F;

import androidx.fragment.app.FragmentTransaction;

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