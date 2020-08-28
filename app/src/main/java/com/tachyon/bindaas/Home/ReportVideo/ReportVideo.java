package com.tachyon.bindaas.Home.ReportVideo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.helper.CommonUtils;
import com.tachyon.bindaas.model.DefaultResponse;
import com.tachyon.bindaas.model.FlagVideoRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class ReportVideo extends AppCompatActivity {
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button submit;
    private ImageButton back;
    private EditText reason;

    Home_Get_Set videoItem;
    private String commentId, flagOptions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_flag_video);
        try {
            init();
            setListeners();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void init() {

        flagOptions = getIntent().getStringExtra("FLAG_OPTIONS");
        if (flagOptions != null && flagOptions.equals("REPORT_COMMENT")) {
            commentId = getIntent().getStringExtra("COMMENT_ID");
        } else
            videoItem = (Home_Get_Set) getIntent().getSerializableExtra("VIDEO_ITEM");

        radioGroup = findViewById(R.id.rg_reports);
        submit = findViewById(R.id.bt_submit);
        reason = findViewById(R.id.et_reason);
        reason.setVisibility(View.GONE);
        back = findViewById(R.id.back_btn);

    }

    public void setListeners() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                if (radioButton.getText().equals("Others")) {
                    reason.setVisibility(View.VISIBLE);
                } else {
                    reason.setVisibility(View.GONE);
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = findViewById(selectedId);
                if (flagOptions != null && flagOptions.equals("REPORT_COMMENT")) {
                    callFlagCommentApi(commentId, radioButton.getText().toString());
                } else {
                    FlagVideoRequest flagVideoRequest = new FlagVideoRequest();
                    flagVideoRequest.setUser_id(Variables.sharedPreferences.getString(Variables.u_id, "0"));
//                    flagVideoRequest.setUser_id(videoItem.user_id);
                    flagVideoRequest.setVideo_id(videoItem.video_id);
                    flagVideoRequest.setReason(radioButton.getText().toString());
                    callFlagVideoApi(flagVideoRequest);
                }

//                Toast.makeText(ReportVideo.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void callFlagCommentApi(String commentId, String reason) {
        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("comment_id", commentId);
                parameters.put("reason", reason);
                parameters.put("user_id", Variables.sharedPreferences.getString(Variables.u_id, "0"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Functions.Show_loader(this, false, false);
            ApiRequest.Call_Api(this, Variables.FLAG_COMMENT, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    DefaultResponse response = CommonUtils.parseDefaultResponse(resp);
                    if (response != null) {
                        Toast.makeText(getBaseContext(), response.getMsg(), Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
            });
//        flagCommentRequest.setComment_id(comment.);
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void callFlagVideoApi(FlagVideoRequest flagVideoRequest) {
        try {
            String flagRequest = new Gson().toJson(flagVideoRequest);
            Log.d("FlagVideoRequest", flagRequest);
            JSONObject flagObject = null;
            try {
                flagObject = new JSONObject(flagRequest);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Functions.Show_loader(this, false, false);
            ApiRequest.Call_Api(this, Variables.FLAG_VIDEO, flagObject, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    parseFlagVideo(resp);

                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void parseFlagVideo(String resp) {
        try {
            DefaultResponse response = CommonUtils.parseDefaultResponse(resp);
            if (response != null) {
                Toast.makeText(getBaseContext(), response.getMsg(), Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void showAlertDialog(final DefaultResponse response) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogCustom);

            builder.setTitle(null);

            builder.setMessage(response.getMsg());
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (Integer.parseInt(response.getCode()) == 200) {
                        dialog.dismiss();
                        finish();
                    } else {
                        dialog.dismiss();
                    }
                }
            });

            builder.show();
        } catch (Exception e) {
            Functions.showLogMessage(this, this.getClass().getSimpleName(), e.getMessage());

        }

    }

}
