package com.tachyon.bindaas.SoundLists.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.Models.EventsModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SoundEventsAdapter extends RecyclerView.Adapter<SoundEventsAdapter.ViewHolder> {
    Context context;
    List<EventsModel> list;
    OnItemClick listener;
    public SoundEventsAdapter(Context context, List<EventsModel> list,OnItemClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }
    public interface OnItemClick{
        void onClick(int position,String section_id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_tops_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getDiscoverImage()).placeholder(R.drawable.image_placeholder).into(holder.event_image);
        holder.event_title.setText(list.get(position).getEventName());
        holder.event_description.setText(list.get(position).getShortDescription());
        holder.itemView.setOnClickListener(view -> listener.onClick(position,list.get(position).getSoundSectionId().get$oid()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView event_title;
        TextView event_description;
        ImageView event_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            event_image = itemView.findViewById(R.id.event_image);
            event_title = itemView.findViewById(R.id.event_title);
            event_description = itemView.findViewById(R.id.event_description);

        }
    }

}
