package com.tachyon.bindaas;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tachyon.bindaas.Main_Menu.MainMenuActivity;

public class ExceptionDisplay extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception_display);


        TextView exception_text = (TextView) findViewById(R.id.exception_text);
        Button btnBack = (Button) findViewById(R.id.btnBack);
        exception_text.setText(getIntent().getExtras().getString("error"));

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intentData();
            }
        });
        new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setCancelable(false)
                .setMessage("we noticed an error : we are working on it ,please try again later")
                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        intentData();
                    }
                }).show();
    }

    @Override
    public void onBackPressed() {
        intentData();
    }

    public void intentData() {

        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(ExceptionDisplay.this, MainMenuActivity.class);
        setIntent.addCategory(Intent.CATEGORY_HOME);
        setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setIntent);
    }
}