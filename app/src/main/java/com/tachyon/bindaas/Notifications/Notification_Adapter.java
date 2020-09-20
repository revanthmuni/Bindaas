package com.tachyon.bindaas.Notifications;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;

import java.util.ArrayList;

public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.CustomViewHolder > {
    public Context context;

    ArrayList<Notification_Get_Set> datalist;
    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Notification_Get_Set item);
    }

    public Notification_Adapter.OnItemClickListener listener;

    public Notification_Adapter(Context context, ArrayList<Notification_Get_Set> arrayList, Notification_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist= arrayList;
        this.listener=listener;
    }

    @Override
    public Notification_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notification,viewGroup,false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        Notification_Adapter.CustomViewHolder viewHolder = new Notification_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return datalist!=null?datalist.size():0;
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageView user_image;

        TextView username,message,watch_btn;


        public CustomViewHolder(View view) {
            super(view);
            try{
                user_image=view.findViewById(R.id.user_image);
                username=view.findViewById(R.id.username);
                message=view.findViewById(R.id.message);
                watch_btn=view.findViewById(R.id.watch_btn);
            }catch (Exception e){
                Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

            }
        }

        public void bind(final int pos , final Notification_Get_Set item, final Notification_Adapter.OnItemClickListener listener) {
try{
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });

            watch_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(v,pos,item);
                }
            });
}catch (Exception e){
    Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

}
        }


    }

    @Override
    public void onBindViewHolder(final Notification_Adapter.CustomViewHolder holder, final int i) {
        try{
        holder.setIsRecyclable(false);
        final Notification_Get_Set item = datalist.get(i);
            holder.username.setText(item.username);

            if(item.profile_pic!=null && !item.profile_pic.equals("")) {
                Picasso.with(context)
                        .load(item.profile_pic)
                        .placeholder(R.drawable.profile_image_placeholder)
                        .into(holder.user_image);
            }

            if(item.type.equalsIgnoreCase("comment_video")){
                holder.message.setText(item.first_name+" have comment on your video");
                holder.watch_btn.setVisibility(View.VISIBLE);
            }
            else if(item.type.equalsIgnoreCase("video_like")) {
                holder.message.setText(item.first_name + " liked your video");
                holder.watch_btn.setVisibility(View.VISIBLE);
            }
            else if(item.type.equalsIgnoreCase("following_you")) {
                holder.message.setText(item.first_name + " following you");
                holder.watch_btn.setVisibility(View.GONE);
            }
            else if(item.type.equalsIgnoreCase("tagged")) {
                holder.message.setText(item.first_name + " tagged you");
                holder.watch_btn.setVisibility(View.VISIBLE);
            }


            holder.bind(i,datalist.get(i),listener);
        }catch (Exception e){
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

        }
}

}