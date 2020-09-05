package com.tachyon.bindaas.Home;

import android.content.Context;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.Gson;
import com.nineoldandroids.view.ViewHelper;
import com.tachyon.bindaas.Chat.Chat_Activity;
import com.tachyon.bindaas.Following.Following_F;
import com.tachyon.bindaas.NewsFeedAdapter;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.See_Full_Image_F;
import com.tachyon.bindaas.SimpleClasses.ApiRequest;
import com.tachyon.bindaas.SimpleClasses.Callback;
import com.tachyon.bindaas.SimpleClasses.Fragment_Callback;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.squareup.picasso.Picasso;
import com.tachyon.bindaas.SimpleClasses.Variables;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Home_Adapter_New extends RecyclerView.Adapter<Home_Adapter_New.CustomViewHolder> {

    public FragmentActivity context;
    private OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;
    List<String> list;
    NewsFeedAdapter adapter;

    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Home_Get_Set item, View view);
    }

    public interface VideoDurationListner {
        void onLoadDuration(long mills);
    }

    public Home_Adapter_New(FragmentActivity context, ArrayList<Home_Get_Set> dataList,
                            Home_Adapter_New.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;
        // this.videoDurationListner = videoDurationListner;

    }

    @Override
    public Home_Adapter_New.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_home_layout_new, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT));
        Home_Adapter_New.CustomViewHolder viewHolder = new Home_Adapter_New.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    @Override
    public void onBindViewHolder(final Home_Adapter_New.CustomViewHolder holder, final int i) {
        try {
            final Home_Get_Set item = dataList.get(i);
            holder.setIsRecyclable(false);

            // holder.setVideoData(item);
            holder.bind(i, item, listener);

            holder.username_new.setText(item.username);
            holder.username2_txt.setText(item.username);
            holder.username.setText(item.first_name + " " + item.last_name);
            holder.username_newsfeed.setText(item.first_name + " " + item.last_name);


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

            holder.desc_txt.setText(item.video_description);
            Log.d("VideoDEC", "onBindViewHolder: " + item.video_description);

            Picasso.with(context).
                    load(item.profile_pic)
                    .centerCrop()
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(100, 100).into(holder.user_pic);
            Picasso.with(context).
                    load(item.profile_pic)
                    .centerCrop()
                    .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                    .resize(100, 100).into(holder.user_image);

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

            //  holder.like_txt.setText(""+((Integer.parseInt(item.like_count)>0)?Functions.GetSuffix(item.like_count):0));

            holder.comment_txt.setText(Functions.GetSuffix(item.video_comment_count));

            if (item.verified != null && item.verified.equalsIgnoreCase("1")) {
                holder.varified_btn.setVisibility(View.VISIBLE);
            } else {
                holder.varified_btn.setVisibility(View.GONE);
            }
            holder.sample3.addDrag(SwipeLayout.DragEdge.Top, holder.sample3.findViewWithTag("Bottom3"));
            holder.sample3.addRevealListener(R.id.bottom_wrapper_child1, new SwipeLayout.OnRevealListener() {
                @Override
                public void onReveal(View child, SwipeLayout.DragEdge edge, float fraction, int distance) {
                    if (Home_F_New.privious_player != null) {
                        Home_F_New.privious_player.setPlayWhenReady(false);
                    }
                }
            });
            // holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right,holder.swipeLayout.findViewWithTag("Profile"));
            //  holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Top,holder.swipeLayout.findViewWithTag("Newsfeed"));

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
        SwipeLayout sample3;

        //User Profile views
        TextView username_new, username2_txt;
        CircleImageView user_image;
        LinearLayout fans_layout, following_layout;
        ImageButton setting_btn;

        //User NewsFeed
        TextView username_newsfeed;
        private ViewPager2 news_feed_viewpager;

        public CustomViewHolder(View view) {
            super(view);
            try {

                sample3 = view.findViewById(R.id.sample3);
                user_image = view.findViewById(R.id.user_image);
                fans_layout = view.findViewById(R.id.fans_layout);
                following_layout = view.findViewById(R.id.following_layout);
                setting_btn = view.findViewById(R.id.setting_btn);

                username_newsfeed = view.findViewById(R.id.username_newsfeed);
                news_feed_viewpager = view.findViewById(R.id.news_feed_viewpager);
//            videoView = view.findViewById(R.id.playerview);
                ivRefresh = view.findViewById(R.id.ivRefresh);
                view_txt = view.findViewById(R.id.view_txt);
                username = view.findViewById(R.id.username);
                user_pic = view.findViewById(R.id.user_pic);
                sound_name = view.findViewById(R.id.sound_name);
                sound_image = view.findViewById(R.id.sound_image);
                varified_btn = view.findViewById(R.id.varified_btn);
                ivSearch = view.findViewById(R.id.ivSearch);

                like_layout = view.findViewById(R.id.like_layout);
                like_image = view.findViewById(R.id.like_image);
                like_txt = view.findViewById(R.id.like_txt);

                desc_txt = view.findViewById(R.id.desc_txt);

                comment_layout = view.findViewById(R.id.comment_layout);
                comment_image = view.findViewById(R.id.comment_image);
                comment_txt = view.findViewById(R.id.comment_txt);

                sound_image_layout = view.findViewById(R.id.sound_image_layout);
                shared_layout = view.findViewById(R.id.shared_layout);

                username_new = view.findViewById(R.id.username_new);
                username2_txt = view.findViewById(R.id.username2_txt);

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
                         final Home_Adapter_New.OnItemClickListener listener) {
            try {

                getNewsFeed(item.user_id);
                news_feed_viewpager.setAdapter(adapter);
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
                user_image.setOnClickListener(view -> OpenfullsizeImage(item.profile_pic));
                fans_layout.setOnClickListener(view -> {
                    Open_Followers(item.user_id);
                });
                following_layout.setOnClickListener(view -> Open_Following(item.user_id));
                setting_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Open_Setting(item.user_id, item.username, item.profile_pic);
                    }
                });

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }


    }

    public void OpenfullsizeImage(String url) {

        try {
            See_Full_Image_F see_image_f = new See_Full_Image_F();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            Bundle args = new Bundle();
            args.putSerializable("image_url", url);
            see_image_f.setArguments(args);
            transaction.addToBackStack(null);

            View view = context.findViewById(R.id.MainMenuFragment);
            if (view != null)
                transaction.replace(R.id.MainMenuFragment, see_image_f).commit();
            else
                transaction.replace(R.id.Profile_F, see_image_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Following(String user_id) {
        try {
            Following_F following_f = new Following_F();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("id", user_id);
            args.putString("from_where", "following");
            following_f.setArguments(args);
            transaction.addToBackStack(null);

            View view = context.findViewById(R.id.MainMenuFragment);

            if (view != null)
                transaction.replace(R.id.MainMenuFragment, following_f).commit();
            else
                transaction.replace(R.id.Profile_F, following_f).commit();

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    public void Open_Followers(String user_id) {
        try {
            Following_F following_f = new Following_F();
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("id", user_id);
            args.putString("from_where", "fan");
            following_f.setArguments(args);
            transaction.addToBackStack(null);


            View view = context.findViewById(R.id.MainMenuFragment);

            if (view != null)
                transaction.replace(R.id.MainMenuFragment, following_f).commit();
            else
                transaction.replace(R.id.Profile_F, following_f).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    public void Open_Setting(String user_id, String user_name, String user_pic) {

        if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
            Open_Chat_F(user_id, user_name, user_pic);
        } else {
            Toast.makeText(context, R.string.login_to_app, Toast.LENGTH_SHORT).show();
        }

    }

    public void Open_Chat_F(String user_id, String user_name, String user_pic) {

        try {
            Chat_Activity chat_activity = new Chat_Activity(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {

                }
            });
            FragmentTransaction transaction = context.getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_bottom, R.anim.out_to_top, R.anim.in_from_top, R.anim.out_from_bottom);
            Bundle args = new Bundle();
            args.putString("user_id", user_id);
            args.putString("user_name", user_name);
            args.putString("user_pic", user_pic);
            chat_activity.setArguments(args);
            transaction.addToBackStack(null);

            View view = context.findViewById(R.id.MainMenuFragment);
            if (view != null)
                transaction.replace(R.id.MainMenuFragment, chat_activity).commit();
            else
                transaction.replace(R.id.Profile_F, chat_activity).commit();
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    private void getNewsFeed(String user_id) {
        list = new ArrayList<>();
        adapter = new NewsFeedAdapter(context, list);

        try {
            JSONObject parameters = new JSONObject();
            try {
                parameters.put("user_id",user_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            ApiRequest.Call_Api(context, Variables.NEWS_FEED, parameters, new Callback() {
                @Override
                public void Responce(String resp) {
                    parseNewsFeed(resp);
                }
            });

        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void parseNewsFeed(String resp) {
        Log.d("Test", "parseNewsFeed: "+resp);
        try {
            JSONObject jsonObject = new JSONObject(resp);
            String code = jsonObject.optString("code");
            if (code.equals("200")) {
                JSONArray msgArray = jsonObject.getJSONArray("msg");
                for (int i = 0; i < msgArray.length(); i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    String url = itemdata.optString("news_url");
                    list.add(url);
                }

            } else {
                Toast.makeText(context, "" + jsonObject.optString("msg"), Toast.LENGTH_SHORT).show();
            }
            adapter.setList(list);

            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
        }
    }
}