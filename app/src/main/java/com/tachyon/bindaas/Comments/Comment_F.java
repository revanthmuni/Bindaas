package com.tachyon.bindaas.Comments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tachyon.bindaas.Home.ReportVideo.ReportVideo;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Data_Send;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.helper.CommonUtils;
import com.tachyon.bindaas.model.DefaultResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class Comment_F extends RootFragment {

    View view;
    Context context;

    RecyclerView recyclerView;

    Comments_Adapter adapter;

    ArrayList<Comment_Get_Set> data_list;

    String video_id;
    String user_id;

    EditText message_edit;
    ImageButton send_btn;
    ProgressBar send_progress;

    TextView comment_count_txt;

    FrameLayout comment_screen;

    public static int comment_count = 0;

    public Comment_F() {

    }

    Fragment_Data_Send fragment_data_send;

    @SuppressLint("ValidFragment")
    public Comment_F(int count, Fragment_Data_Send fragment_data_send) {
        comment_count = count;
        this.fragment_data_send = fragment_data_send;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_comment, container, false);
        try {
            context = getContext();


            comment_screen = view.findViewById(R.id.comment_screen);
            comment_screen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().onBackPressed();

                }
            });

            view.findViewById(R.id.Goback).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getActivity().onBackPressed();
                }
            });


            Bundle bundle = getArguments();
            if (bundle != null) {
                video_id = bundle.getString("video_id");
                user_id = bundle.getString("user_id");
            }


            comment_count_txt = view.findViewById(R.id.comment_count);

            recyclerView = view.findViewById(R.id.recylerview);
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setHasFixedSize(false);


            data_list = new ArrayList<>();
            adapter = new Comments_Adapter(context, data_list, new Comments_Adapter.OnItemClickListener() {
                @Override
                public void onItemClick(final int postion, final Comment_Get_Set item, View view) {
                    view.findViewById(R.id.side_menu).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShowCommentOption(item, postion);
                        }
                    });
                }
            });

            recyclerView.setAdapter(adapter);


            message_edit = view.findViewById(R.id.message_edit);


            send_progress = view.findViewById(R.id.send_progress);
            send_btn = view.findViewById(R.id.send_btn);
            send_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String message = message_edit.getText().toString();
                    if (!TextUtils.isEmpty(message)) {
                        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
                            Send_Comments(video_id, message);
                            message_edit.setText(null);
                            send_progress.setVisibility(View.VISIBLE);
                            send_btn.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(context, "Please Login into the app", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            });


            Get_All_Comments();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

        return view;
    }

    CharSequence[] options;

    private void ShowCommentOption(final Comment_Get_Set home_get_set, final int position) {
        try {
            final String userId = Variables.sharedPreferences.getString(Variables.u_id, "");

            if (!home_get_set.user_id.equals(userId))
                options = new CharSequence[]{"Flag Comment", "Cancel"};
            else
                options = new CharSequence[]{"Delete Comment", "Cancel"};

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Objects.requireNonNull(getActivity()), R.style.AlertDialogCustom);

            builder.setTitle(null);
            builder.setCancelable(true);
            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Flag Comment")) {
                        Intent intent = new Intent(getActivity(), ReportVideo.class);
                        intent.putExtra("FLAG_OPTIONS", "REPORT_COMMENT");
                        intent.putExtra("COMMENT_ID", home_get_set.comment_id);
                        startActivity(intent);
                        dialog.dismiss();
//                    callFlagCommentApi(home_get_set);
//                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show();


                    } else if (options[item].equals("Delete Comment")) {
                        callDeleteCommentApi(userId, home_get_set, position);
                        dialog.dismiss();
                    } else if (options[item].equals("Cancel")) {

                        dialog.dismiss();

                    }

                }

            });

            builder.show();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    private void callDeleteCommentApi(String userId, final Comment_Get_Set item,
                                      final int position) {
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("comment_id", item.comment_id);
                params.put("user_id", userId);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Functions.Show_loader(context, false, false);
            ApiRequest.Call_Api(getActivity(), Variables.DELETE_COMMENT, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    Functions.cancel_loader();
                    DefaultResponse response = CommonUtils.parseDefaultResponse(resp);
                    if (response != null) {
                        Toast.makeText(context, response.getMsg(), Toast.LENGTH_SHORT).show();
                        data_list.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    @Override
    public void onDetach() {
        try {
            Functions.hideSoftKeyboard(getActivity());
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
        super.onDetach();
    }

    // this funtion will get all the comments against post
    public void Get_All_Comments() {
        try{
        Functions.Call_Api_For_get_Comment(getActivity(), video_id, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                ArrayList<Comment_Get_Set> arrayList1 = arrayList;
                for (Comment_Get_Set item : arrayList1) {
                    data_list.add(item);
                }
                comment_count_txt.setText(data_list.size() + " comments");
                adapter.notifyDataSetChanged();
            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }

        });
        }catch (Exception e){
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

        }

    }

    // this function will call an api to upload your comment
    public void Send_Comments(String video_id, final String comment) {
try{
        Functions.Call_Api_For_Send_Comment(getActivity(), video_id, comment, new API_CallBack() {
            @Override
            public void ArrayData(ArrayList arrayList) {
                send_progress.setVisibility(View.GONE);
                send_btn.setVisibility(View.VISIBLE);

                ArrayList<Comment_Get_Set> arrayList1 = arrayList;
                for (Comment_Get_Set item : arrayList1) {
                    data_list.add(0, item);
                    comment_count++;

                    SendPushNotification(getActivity(), user_id, comment);

                    comment_count_txt.setText(comment_count + " comments");

                    if (fragment_data_send != null)
                        fragment_data_send.onDataSent("" + comment_count);

                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void OnSuccess(String responce) {

            }

            @Override
            public void OnFail(String responce) {

            }
        });
}catch (Exception e){
    Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

}

    }


    public void SendPushNotification(Activity activity, String user_id, String comment) {
        try {
            JSONObject notimap = new JSONObject();
            try {
                notimap.put("title", Variables.sharedPreferences.getString(Variables.u_name, "") + " Comment on your video");
                notimap.put("message", comment);
                notimap.put("icon", Variables.sharedPreferences.getString(Variables.u_pic, ""));
                notimap.put("senderid", Variables.sharedPreferences.getString(Variables.u_id, ""));
                notimap.put("receiverid", user_id);
                notimap.put("action_type", "comment");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiRequest.Call_Api(context, Variables.sendPushNotification, notimap, null);

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }
}
