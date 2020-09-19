package com.tachyon.bindaas.Search;

import android.content.Context;
import android.net.Uri;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Adapter_Click_Listener;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

class SoundList_Adapter extends RecyclerView.Adapter<SoundList_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Object> datalist;
    Adapter_Click_Listener adapter_click_listener;

    public SoundList_Adapter(Context context, ArrayList<Object> arrayList, Adapter_Click_Listener listener) {
        this.context = context;
        datalist = arrayList;
        this.adapter_click_listener = listener;
    }

    @Override
    public SoundList_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        SoundList_Adapter.CustomViewHolder viewHolder = new SoundList_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist != null ? datalist.size() : 0;
    }


    @Override
    public void onBindViewHolder(final SoundList_Adapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        Sounds_GetSet item = (Sounds_GetSet) datalist.get(i);

        try {

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);

            if (item.thum != null && !item.thum.equals("")) {
                Log.d(Variables.tag, item.thum);
                Uri uri = Uri.parse(item.thum);
                holder.sound_image.setImageURI(uri);
            }


            if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_un_favourite));

            holder.bind(i, item, adapter_click_listener);


        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done;
        ImageButton fav_btn;
        ImageButton play_arrow, pause_arrow;
        ImageButton play_Btn, pause_Btn;

        TextView sound_name, description_txt;
        SimpleDraweeView sound_image;

        public CustomViewHolder(View view) {
            super(view);
            try {
                done = view.findViewById(R.id.done);
                fav_btn = view.findViewById(R.id.fav_btn);


                play_arrow = view.findViewById(R.id.play_arrow);
                pause_arrow = view.findViewById(R.id.pause_arrow);
                play_Btn = view.findViewById(R.id.play_btn);
                pause_Btn = view.findViewById(R.id.pause_btn);

                sound_name = view.findViewById(R.id.sound_name);
                description_txt = view.findViewById(R.id.description_txt);
                sound_image = view.findViewById(R.id.sound_image);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }
        }

        public void bind(final int pos, final Sounds_GetSet item, final Adapter_Click_Listener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // listener.onItemClick(v,pos,item);
                    }
                });

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, pos, item);
                    }
                });
                play_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_arrow.setVisibility(View.GONE);
                        pause_arrow.setVisibility(View.VISIBLE);
                        listener.onItemClick(itemView, pos, item);
                    }
                });
                pause_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause_arrow.setVisibility(View.GONE);
                        play_arrow.setVisibility(View.VISIBLE);
                        listener.onItemClick(itemView, pos, item);
                    }
                });
                play_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        play_Btn.setVisibility(View.GONE);
                        pause_Btn.setVisibility(View.VISIBLE);
                        listener.onItemClick(itemView, pos, item);
                    }
                });
                pause_Btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pause_Btn.setVisibility(View.GONE);
                        play_Btn.setVisibility(View.VISIBLE);
                        listener.onItemClick(itemView, pos, item);
                    }
                });
                fav_btn.setOnClickListener(new View.OnClickListener() {
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


}

