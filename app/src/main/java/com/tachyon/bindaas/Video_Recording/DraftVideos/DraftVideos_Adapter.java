package com.tachyon.bindaas.Video_Recording.DraftVideos;

import android.content.Context;
import android.net.Uri;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;

import java.io.File;
import java.util.ArrayList;

public class DraftVideos_Adapter extends RecyclerView.Adapter<DraftVideos_Adapter.CustomViewHolder> {

    public Context context;
    private DraftVideos_Adapter.OnItemClickListener listener;
    private ArrayList<DraftVideo_Get_Set> dataList;


    public interface OnItemClickListener {
        void onItemClick(int postion, DraftVideo_Get_Set item, View view);
    }

    public DraftVideos_Adapter(Context context, ArrayList<DraftVideo_Get_Set> dataList, DraftVideos_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }


    public void setDataList(ArrayList<DraftVideo_Get_Set> dataList) {
        this.dataList = dataList;
    }

    @Override
    public DraftVideos_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_galleryvideo_layout, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        DraftVideos_Adapter.CustomViewHolder viewHolder = new DraftVideos_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {


        ImageView thumb_image;
        TextView view_txt;
        ImageButton cross_btn;

        public CustomViewHolder(View view) {
            super(view);
            try {
                thumb_image = view.findViewById(R.id.thumb_image);
                view_txt = view.findViewById(R.id.view_txt);
                cross_btn = view.findViewById(R.id.cross_btn);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int position, final DraftVideo_Get_Set item, final DraftVideos_Adapter.OnItemClickListener listener) {
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
    public void onBindViewHolder(final DraftVideos_Adapter.CustomViewHolder holder, final int i) {
        try {
            final DraftVideo_Get_Set item = dataList.get(i);

            holder.view_txt.setText(item.video_time);

            Glide.with(context)
                    .load(Uri.fromFile(new File(item.video_path)))
                    .into(holder.thumb_image);

            holder.bind(i, item, listener);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

}