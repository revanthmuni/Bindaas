package com.tachyon.bindaas.Profile;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class MyVideos_Adapter extends RecyclerView.Adapter<MyVideos_Adapter.CustomViewHolder> {

    public Context context;
    private MyVideos_Adapter.OnItemClickListener listener;
    private ArrayList<Home_Get_Set> dataList;


    public interface OnItemClickListener {
        void onItemClick(int postion, Home_Get_Set item, View view);
    }

    public MyVideos_Adapter(Context context, ArrayList<Home_Get_Set> dataList, MyVideos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public MyVideos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_myvideo_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        MyVideos_Adapter.CustomViewHolder viewHolder = new MyVideos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {


        ImageView thumb_image;
        TextView view_txt;

        public CustomViewHolder(View view) {
            super(view);
            try {
                thumb_image = view.findViewById(R.id.thumb_image);
                view_txt = view.findViewById(R.id.view_txt);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }

        }

        public void bind(final int position, final Home_Get_Set item, final MyVideos_Adapter.OnItemClickListener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(position, item, v);
                    }
                });
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

    }


    @Override
    public void onBindViewHolder(final MyVideos_Adapter.CustomViewHolder holder, final int i) {
        try {
            final Home_Get_Set item = dataList.get(i);
            holder.setIsRecyclable(false);

            holder.view_txt.setText(item.views);
            holder.view_txt.setText(Functions.GetSuffix(item.views));

            if(Variables.is_show_gif) {
                Glide.with(context)
                        .asGif()
                        .load(item.gif)
                        .skipMemoryCache(true)
                        .thumbnail(new RequestBuilder[]{Glide
                                .with(context)
                                .load(item.thum)})
                        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE)
                                .placeholder(context.getResources().getDrawable(R.drawable.image_placeholder)).centerCrop())
                        .into(holder.thumb_image);
            }
            else {
                if(item.thum!=null && !item.thum.equals("")) {
                    Uri uri = Uri.parse(item.thum);
                    holder.thumb_image.setImageURI(uri);
                }
            }


            holder.bind(i, item, listener);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}