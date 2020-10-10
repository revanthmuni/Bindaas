package com.tachyon.bindaas.WatchVideos;

import android.animation.ValueAnimator;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.R;
import com.google.android.exoplayer2.ui.PlayerView;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.squareup.picasso.Picasso;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;
import java.util.logging.Handler;

import static android.content.ContentValues.TAG;

public class Watch_Videos_Adapter extends RecyclerView.Adapter<Watch_Videos_Adapter.CustomViewHolder> {

    public Context context;
    private Watch_Videos_Adapter.OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;


    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Home_Get_Set item, View view);
    }

    public Watch_Videos_Adapter(Context context, ArrayList<Home_Get_Set> dataList, Watch_Videos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public Watch_Videos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_watch_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        Watch_Videos_Adapter.CustomViewHolder viewHolder = new Watch_Videos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    @Override
    public void onBindViewHolder(final Watch_Videos_Adapter.CustomViewHolder holder, final int i) {
        try {
            final Home_Get_Set item = dataList.get(i);
            holder.setIsRecyclable(false);
            Log.d("TAG", "onBindViewHolder: " + item.liked);

            /*holder.like_image.setImageDrawable(item.liked.equals("1") ? context.getResources().getDrawable(R.drawable.ic_like_fill) :
                context.getResources().getDrawable(R.drawable.ic_like));*/

            // holder.setVideoData(item);
            holder.bind(i, item, listener);

            Log.d("USR_ID::CHECK:", "onBindViewHolder: in Adapter:"+item.user_id);
            Log.d("USR_ID::CHECK:", "onBindViewHolder: REal user:"+
                    Variables.sharedPreferences.getString(Variables.u_id,""));


            Log.d("username_check", "onBindViewHolder: before"+item.username);
            holder.username.setText(item.username);


            holder.like_txt.setText("0");
            if ((item.sound_name == null || item.sound_name.equals("") || item.sound_name.equals("null"))) {
                holder.sound_name.setText("original sound - " + item.first_name + " " + item.last_name);
            } else {
                holder.sound_name.setText(item.sound_name);
            }
            holder.sound_name.setSelected(true);


            //holder.desc_txt.setText(item.video_description);
            if (holder.desc_txt.getLineCount()>2){
                holder.desc_txt.setText(item.video_description);
                holder.show_more.setVisibility(View.VISIBLE);
            }else {
                holder.desc_txt.setText(item.video_description);
                holder.show_more.setVisibility(View.GONE);
            }
            holder.show_more.setOnClickListener(view -> {
                if (holder.show_more.getText().equals("Show More")){
                    holder.show_more.setText("Show Less");
                    holder.desc_txt.setMaxLines(holder.desc_txt.getLineCount());
                    holder.desc_txt.setText(item.video_description);
                }else{
                    holder.show_more.setText("Show More");
                    holder.desc_txt.setMaxLines(2);
                    holder.desc_txt.setText(item.video_description);

                }
            });
            Log.d("VideoDEC", "onBindViewHolder: " + item.video_description);

            Picasso.with(context).
                    load(item.profile_pic)
                    .centerCrop()
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(100, 100).into(holder.user_pic);


            if ((item.sound_name == null || item.sound_name.equals(""))
                    || item.sound_name.equals("null")) {

                item.sound_pic = item.profile_pic;

            } else if (item.sound_pic.equals(""))
                item.sound_pic = "Null";

            /*Picasso.with(context).
                    load(item.sound_pic).centerCrop()
                    .placeholder(context.getResources().getDrawable(R.drawable.ic_round_music))
                    .resize(100, 100).into(holder.sound_image);
*/

            // To load Gif image files
            /*Glide.with(context).load(context.getResources().getDrawable(R.drawable.sound))
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE))
                    .into(holder.sound_image);*/

           /* if (item.liked.equals("1")) {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
            } else {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
            }*/

            Log.d("TAG", "onBindViewHolder: " + item.like_count + "::" + item.username + "::" + new Gson().toJson(item));
            if (item.like_count.equals("0")) {
                holder.like_txt.setText("0");
            } else {
                holder.like_txt.setText(Functions.GetSuffix(dataList.get(i).like_count));
            }
            if (item.liked.equals("1")) {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
            } else {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
            }

            if(item.allow_comments!=null && item.allow_comments.equalsIgnoreCase("false"))
                holder.comment_layout.setVisibility(View.GONE);
            else
                holder.comment_layout.setVisibility(View.VISIBLE);
            // holder.like_txt.setText(""+((Integer.parseInt(item.like_count)>0)?Functions.GetSuffix(item.like_count):0));
            holder.comment_txt.setText(Functions.GetSuffix(item.video_comment_count));

            holder.view_txt.setText(item.views);
       /* if (item.verified != null && item.verified.equalsIgnoreCase("1")) {
            holder.varified_btn.setVisibility(View.VISIBLE);
        } else {
            holder.varified_btn.setVisibility(View.GONE);
        }*/
            holder.side_menu.setVisibility(View.VISIBLE);

            //checking tagged users ?
            Log.d("TAG", "onBindViewHolder: tagged users:"+item.tagged_users);
            if (item.tagged_users.equals("[]")){
                holder.tag_users_layout.setVisibility(View.GONE);
            }else{
                holder.tag_users_layout.setVisibility(View.VISIBLE);
            }

            Log.d(TAG, "onBindViewHolder: user_id check:"+
                    item.user_id.equals(Variables.sharedPreferences.getString(Variables.u_id,"")));
            if (dataList.get(i).follow_status_button.equalsIgnoreCase("UnFollow") ||
                dataList.get(i).user_id.equals(Variables.sharedPreferences.getString(Variables.u_id,""))){
                holder.add_follow.setVisibility(View.GONE);
            }else{
                holder.add_follow.setVisibility(View.VISIBLE);
            }
            Log.d(TAG, "onBindViewHolder: follow status:"+item.follow_status_button);


        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        PlayerView playerview;
        TextView username, sound_name;
        ImageView user_pic, sound_image, varified_btn;

        LinearLayout like_layout, comment_layout, shared_layout, sound_image_layout, side_menu;
        ImageView like_image, comment_image;
        TextView like_txt, comment_txt,view_txt;

        TextView desc_txt;
        TextView show_more;
        ImageView tag_users_layout;
        ImageView add_follow;

        public CustomViewHolder(View view) {
            super(view);
            try {
                add_follow = view.findViewById(R.id.add_follow);
                playerview = view.findViewById(R.id.playerview);
                tag_users_layout = view.findViewById(R.id.tagged_users);
                view_txt = view.findViewById(R.id.view_txt);
                username = view.findViewById(R.id.username);
                user_pic = view.findViewById(R.id.user_pic);
                sound_name = view.findViewById(R.id.sound_name);
                sound_image = view.findViewById(R.id.sound_image);
                // varified_btn = view.findViewById(R.id.varified_btn);

                like_layout = view.findViewById(R.id.like_layout);
                like_image = view.findViewById(R.id.like_image);
                like_txt = view.findViewById(R.id.like_txt);
                side_menu = view.findViewById(R.id.side_menu);

                comment_layout = view.findViewById(R.id.comment_layout);
                comment_image = view.findViewById(R.id.comment_image);
                comment_txt = view.findViewById(R.id.comment_txt);

                desc_txt = view.findViewById(R.id.desc_txt);
                show_more = view.findViewById(R.id.show_more);

                sound_image_layout = view.findViewById(R.id.sound_image_layout);
                shared_layout = view.findViewById(R.id.shared_layout);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int postion, final Home_Get_Set item, final Watch_Videos_Adapter.OnItemClickListener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });


                user_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Log.d("USR_TST", "onClick: " + item.user_id);
                        listener.onItemClick(postion, item, v);

                    }
                });

                username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listener.onItemClick(postion, item, v);
                    }
                });

                like_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });

                comment_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listener.onItemClick(postion, item, v);
                    }
                });

                shared_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });

                sound_image_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });

                tag_users_layout.setOnClickListener(view1 -> {
                    listener.onItemClick(postion,item,view1);
                });

                add_follow.setOnClickListener(view -> {
                    listener.onItemClick(postion,item,view);
                });

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}