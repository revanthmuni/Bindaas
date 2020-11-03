package com.tachyon.bindaas.Home;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import pl.droidsonroids.gif.GifImageView;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.squareup.picasso.Picasso;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.CustomViewHolder> {

    public Context context;
    private Home_Adapter.OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;

    public void setDataList(ArrayList<Home_Get_Set> dataList) {
        this.dataList = dataList;
    }

    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Home_Get_Set item, View view);
    }

    public interface VideoDurationListner {
        void onLoadDuration(long mills);
    }

    public Home_Adapter(Context context, ArrayList<Home_Get_Set> dataList, Home_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
        // this.videoDurationListner = videoDurationListner;

    }

    @Override
    public Home_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        context.setTheme(Functions.getSavedTheme());
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        Home_Adapter.CustomViewHolder viewHolder = new Home_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    @Override
    public void onBindViewHolder(final Home_Adapter.CustomViewHolder holder, final int i) {
        try {
            final Home_Get_Set item = dataList.get(i);
            holder.setIsRecyclable(false);

            // holder.setVideoData(item);
            holder.bind(i, item, listener);

            holder.username.setText(item.username);


            Log.d("Home_F", "onBindViewHolder: "+item.follow_status_button);
           /* MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14) {
                retriever.setDataSource(item.video_url, new HashMap<String, String>());
            }
            else {
                retriever.setDataSource(item.video_url);
            }
            String mVideoDuration =  retriever .extractMetadata(retriever.METADATA_KEY_DURATION);
            long mTimeInMilliseconds= Long.parseLong(mVideoDuration);
            Log.d("META_DATA", "onBindViewHolder: video url:"+item.video_url);
            Log.d("META_DATA", "onBindViewHolder: Duration"+mTimeInMilliseconds);

            videoDurationListner.onLoadDuration(mTimeInMilliseconds);*/

            if ((item.sound_name == null || item.sound_name.equals("") || item.sound_name.equals("null"))) {
                holder.sound_name.setText("original sound - " + item.first_name + " " + item.last_name);
            } else {
                holder.sound_name.setText(item.sound_name);
            }
            holder.sound_name.setSelected(true);

//          holder.desc_txt.setText(item.video_description);
            Log.d("VideoDEC", "onBindViewHolder: " + item.video_description);
            if (holder.desc_txt.getLineCount() > 2) {
                holder.desc_txt.setText(item.video_description);
                holder.show_more.setVisibility(View.VISIBLE);
            } else {
                holder.desc_txt.setText(item.video_description);
                holder.show_more.setVisibility(View.GONE);
            }
            holder.show_more.setOnClickListener(view -> {
                if (holder.show_more.getText().equals("Show More")) {
                    holder.show_more.setText("Show Less");
                    holder.desc_txt.setMaxLines(holder.desc_txt.getLineCount());
                    holder.desc_txt.setText(item.video_description);
                } else {
                    holder.show_more.setText("Show More");
                    holder.desc_txt.setMaxLines(2);
                    holder.desc_txt.setText(item.video_description);
                }
            });

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

            //Toast.makeText(context, ""+item.user_id, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onBindViewHolder: userId:"+item.user_id+" video id:"+item.video_id);
            if (item.liked.equals("1")) {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like_fill));
            } else {
                holder.like_image.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_like));
            }
            Log.d("TAG", "onBindViewHolder:views count " + item.views);
            holder.view_txt.setText(item.views);
            if (item.allow_comments != null && item.allow_comments.equalsIgnoreCase("false"))
                holder.comment_layout.setVisibility(View.GONE);
            else
                holder.comment_layout.setVisibility(View.VISIBLE);

            Log.d("LikeData", "onBindViewHolder: " + item.like_count + "::" + item.username + "::" + new Gson().toJson(item));
            if (dataList.get(i).like_count.equals("0")) {
                holder.like_txt.setText("0");
            } else {
                holder.like_txt.setText(Functions.GetSuffix(dataList.get(i).like_count));
            }
            //            holder.like_txt.setText(""+((Integer.parseInt(item.like_count)>0)?Functions.GetSuffix(item.like_count):0));
            holder.comment_txt.setText(Functions.GetSuffix(item.video_comment_count));


            if (item.verified != null && item.verified.equalsIgnoreCase("1")) {
                holder.varified_btn.setVisibility(View.VISIBLE);
            } else {
                holder.varified_btn.setVisibility(View.GONE);
            }


            if (!item.follow_status_button.equalsIgnoreCase("Follow") ||
                    item.user_id.equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {
                holder.add_follow.setVisibility(View.GONE);
            } else {
                holder.add_follow.setVisibility(View.VISIBLE);
            }
            if (item.tagged_users.equals("[]")) {
                holder.tag_users_layout.setVisibility(View.GONE);
            } else {
                holder.tag_users_layout.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView username, desc_txt, sound_name, view_txt;
        ImageView user_pic, varified_btn;
        GifImageView sound_image;

        LinearLayout like_layout, comment_layout, shared_layout, sound_image_layout;
        ImageView like_image, comment_image, ivSearch, ivRefresh;
        TextView like_txt, comment_txt;

        VideoView videoView;
        TextView show_more;
        ImageView tag_users_layout;
        ImageView add_follow;

        public CustomViewHolder(View view) {
            super(view);
            try {

//            videoView = view.findViewById(R.id.playerview);
                ivRefresh = view.findViewById(R.id.ivRefresh);
                view_txt = view.findViewById(R.id.view_txt);
                username = view.findViewById(R.id.username);
                user_pic = view.findViewById(R.id.user_pic);
                sound_name = view.findViewById(R.id.sound_name);
                sound_image = view.findViewById(R.id.sound_image);
                varified_btn = view.findViewById(R.id.varified_btn);
                ivSearch = view.findViewById(R.id.ivSearch);
                show_more = view.findViewById(R.id.show_more);

                tag_users_layout = view.findViewById(R.id.tagged_users);
                add_follow = view.findViewById(R.id.add_follow);
                like_layout = view.findViewById(R.id.like_layout);
                like_image = view.findViewById(R.id.like_image);
                like_txt = view.findViewById(R.id.like_txt);

                desc_txt = view.findViewById(R.id.desc_txt);

                comment_layout = view.findViewById(R.id.comment_layout);
                comment_image = view.findViewById(R.id.comment_image);
                comment_txt = view.findViewById(R.id.comment_txt);

                sound_image_layout = view.findViewById(R.id.sound_image_layout);
                shared_layout = view.findViewById(R.id.shared_layout);

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        void setVideoData(Home_Get_Set item) {
            try {
                videoView.setVideoPath(item.video_url);
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();

                        float videoRatio = mediaPlayer.getVideoWidth() / (float) mediaPlayer.getVideoHeight();

                        float screenRatio = videoView.getWidth() / (float) videoView.getHeight();
                        float scale = videoRatio / screenRatio;
                        if (scale >= 1f) {
                            videoView.setScaleX(scale);
                        } else {
                            videoView.setScaleY(1f / scale);
                        }
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                    }
                });
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int postion, final Home_Get_Set item,
                         final Home_Adapter.OnItemClickListener listener) {
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

                ivSearch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });
                ivRefresh.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });

                tag_users_layout.setOnClickListener(view -> {
                    listener.onItemClick(postion, item, view);
                });
                add_follow.setOnClickListener(view -> {
                    listener.onItemClick(postion, item, view);
                });

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }


    }

}