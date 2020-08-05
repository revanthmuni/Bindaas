package com.tachyon.bindaas.VideoAction;

import android.content.Context;
import android.content.pm.ResolveInfo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;

import java.util.List;

public class VideoSharingApps_Adapter extends RecyclerView.Adapter<VideoSharingApps_Adapter.CustomViewHolder> {

    public Context context;
    private VideoSharingApps_Adapter.OnItemClickListener listener;
    private List<ResolveInfo> dataList;


    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, ResolveInfo item, View view);
    }


    public VideoSharingApps_Adapter(Context context, List<ResolveInfo> dataList, VideoSharingApps_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public VideoSharingApps_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_videosharingapps_layout, null);
        VideoSharingApps_Adapter.CustomViewHolder viewHolder = new VideoSharingApps_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }


    @Override
    public void onBindViewHolder(final VideoSharingApps_Adapter.CustomViewHolder holder, final int i) {

        try {
            final ResolveInfo item = dataList.get(i);
            holder.setIsRecyclable(false);

            holder.bind(i, item, listener);

            holder.image.setImageDrawable(item.loadIcon(context.getPackageManager()));

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView image;


        public CustomViewHolder(View view) {
            super(view);
            try {

                image = view.findViewById(R.id.image);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int postion, final ResolveInfo item, final VideoSharingApps_Adapter.OnItemClickListener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(postion, item, v);
                    }
                });
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}