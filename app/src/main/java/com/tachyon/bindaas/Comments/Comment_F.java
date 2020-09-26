package com.tachyon.bindaas.Comments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.tachyon.bindaas.Home.Home_F;
import com.tachyon.bindaas.Home.ReportVideo.ReportVideo;
import com.tachyon.bindaas.Main_Menu.MainMenuFragment;
import com.tachyon.bindaas.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.tachyon.bindaas.Profile.Profile_F;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.API_CallBack;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Data_Send;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.helper.CommonUtils;
import com.tachyon.bindaas.model.DefaultResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.tachyon.bindaas.WatchVideos.WatchVideos_F.privious_player;


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
    String flag;

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
                flag = bundle.getString("flag");
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
                    switch (view.getId()) {
                        case R.id.username:
                            openProfile(item, postion);
                            break;
                        case R.id.user_pic:
                            openProfile(item, postion);
                            break;
                        case R.id.side_menu:
                            ShowCommentOption(item, postion);
                            break;
                    }

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

    private void openProfile(Comment_Get_Set item, int postion) {
        if (privious_player != null) {
            privious_player.setPlayWhenReady(false);
        }
        //Open profile_f
        try {
            if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(item.user_id)) {
                try {
                    if (flag.equals("home")) {
                        getActivity().onBackPressed();
                    } else {
                        getActivity().finish();
                    }
                    TabLayout.Tab profile = MainMenuFragment.tabLayout.getTabAt(2);
                    profile.select();
                } catch (Exception e) {
                    Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
                    Log.d("Exception:", "OpenProfile: " + e.getMessage());
                }


            } else {
                Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                    @Override
                    public void Responce(Bundle bundle) {
                        //Call_Api_For_Singlevideos(currentPage);
                    }
                });
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();


                transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
                Bundle args = new Bundle();
                args.putString("user_id", item.user_id);
                args.putString("user_name", item.first_name + " " + item.last_name);
                args.putString("user_pic", item.profile_pic);
                profile_f.setArguments(args);
                transaction.addToBackStack(null);
                if (flag.equals("home")) {
                    transaction.replace(R.id.MainMenuFragment, profile_f).commit();
                } else {
                    if (privious_player != null) privious_player.setPlayWhenReady(false);
                    getActivity().onBackPressed();
                    transaction.replace(R.id.WatchVideo_F, profile_f).commit();
                }
            }

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    CharSequence[] options;

    private void ShowCommentOption(final Comment_Get_Set home_get_set, final int position) {
        try {
            final String userId = Variables.sharedPreferences.getString(Variables.u_id, "");

            if (!home_get_set.user_id.equals(userId))
                options = new CharSequence[]{"Flag Comment", "Cancel"};
            else
                options = new CharSequence[]{"Delete Comment", "Edit Comment", "Cancel"};

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
                    } else if (options[item].equals("Edit Comment")) {
                        dialog.dismiss();
                        editComment(home_get_set, position);
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

    private void editComment(Comment_Get_Set home_get_set, int position) {
        try {
            View view = LayoutInflater.from(context).inflate(R.layout.edit_comment, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
            dialog.setTitle("Edit Comment");
            EditText comment = view.findViewById(R.id.comment_edit);
            ImageView profile = view.findViewById(R.id.profile);
            TextView name = view.findViewById(R.id.name);
            Glide.with(context).load(home_get_set.profile_pic).into(profile);
            comment.setText(home_get_set.comments);
            name.setText(home_get_set.first_name + " " + home_get_set.last_name);
            dialog.setView(view);
            dialog.setPositiveButton("Update", (dialogInterface, i) -> {
                dialogInterface.dismiss();
                callEditCommentsApi(home_get_set, home_get_set.comment_id, comment.getText().toString(), home_get_set.video_id, home_get_set.user_id, position);
            });

            dialog.show();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());/**/
        }
    }

    private void callEditCommentsApi(Comment_Get_Set item, String comment_id, String comment, String video_id, String user_id, int position) {
        Log.d("TAG", "callEditCommentsApi: " + comment_id + " comment :" + comment
                + " video id:" + video_id + " user id:" + user_id);
        try {
            JSONObject params = new JSONObject();
            try {
                params.put("comment_id", comment_id);
                params.put("comment", comment);
                params.put("video_id", item.video_id);
                params.put("user_id", user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Functions.Show_loader(context, false, false);
            ApiRequest.Call_Api(getActivity(), Variables.EDIT_COMMENT, params, new Callback() {
                @Override
                public void Responce(String resp) {
                    Log.d("TAG", "edit comment Responce: " + resp);
                    Functions.cancel_loader();
                    data_list.remove(position);
                    item.comments = comment;
                    data_list.add(position, item);
                    adapter.notifyDataSetChanged();
                }
            });
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
        try {
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
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    // this function will call an api to upload your comment
    public void Send_Comments(String video_id, final String comment) {
        try {
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
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

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
