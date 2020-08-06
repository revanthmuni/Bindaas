package com.tachyon.bindaas.Search;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Adapter_Click_Listener;
import com.tachyon.bindaas.SimpleClasses.Functions;

import java.util.ArrayList;

public class VideosList_Adapter extends RecyclerView.Adapter<VideosList_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Object> datalist;
    Adapter_Click_Listener adapter_click_listener;

    public VideosList_Adapter(Context context, ArrayList<Object> arrayList, Adapter_Click_Listener adapter_click_listener) {
        this.context = context;
        datalist = arrayList;
        this.adapter_click_listener = adapter_click_listener;
    }

    @Override
    public VideosList_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_video_layout, viewGroup, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        VideosList_Adapter.CustomViewHolder viewHolder = new VideosList_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return datalist != null ? datalist.size() : 0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image,user_image;

        TextView username_txt,description_txt,first_last_name_txt,likes_count_txt;


        public CustomViewHolder(View view) {
            super(view);
            try {
                user_image = view.findViewById(R.id.user_image);
                image=view.findViewById(R.id.image);
                username_txt=view.findViewById(R.id.username_txt);
                description_txt=view.findViewById(R.id.description_txt);

                first_last_name_txt=view.findViewById(R.id.first_last_name_txt);
                likes_count_txt=view.findViewById(R.id.likes_count_txt);

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int pos, final Home_Get_Set item, final Adapter_Click_Listener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, pos, item);
                    }
                });

            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }


    }

    @Override
    public void onBindViewHolder(final VideosList_Adapter.CustomViewHolder holder, final int i) {
        try {
            holder.setIsRecyclable(false);

            final Home_Get_Set item = (Home_Get_Set) datalist.get(i);

            holder.username_txt.setText(item.username);
            holder.description_txt.setText(item.video_description);

            if(item.thum!=null && !item.thum.equals("")) {
                Uri uri = Uri.parse(item.thum);
                holder.image.setImageURI(uri);
            }

            if(item.profile_pic!=null && !item.profile_pic.equals("")) {
                Uri uri = Uri.parse(item.profile_pic);
                holder.user_image.setImageURI(uri);
            }

            holder.first_last_name_txt.setText(item.first_name+" "+item.last_name);
            holder.likes_count_txt.setText(Functions.GetSuffix(item.like_count));

            holder.bind(i, item, adapter_click_listener);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}