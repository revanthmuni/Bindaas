package com.tachyon.bindaas.SoundLists.FavouriteSounds;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


class Favourite_Sound_Adapter extends RecyclerView.Adapter<Favourite_Sound_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Sounds_GetSet> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    public Favourite_Sound_Adapter.OnItemClickListener listener;


    public Favourite_Sound_Adapter(Context context, ArrayList<Sounds_GetSet> arrayList, Favourite_Sound_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public Favourite_Sound_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(Variables.screen_width - 50, RecyclerView.LayoutParams.WRAP_CONTENT));
        Favourite_Sound_Adapter.CustomViewHolder viewHolder = new Favourite_Sound_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist != null ? datalist.size() : 0;
    }


    @Override
    public void onBindViewHolder(final Favourite_Sound_Adapter.CustomViewHolder holder, final int i) {
        holder.setIsRecyclable(false);

        Sounds_GetSet item = datalist.get(i);
        try {

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);

            if (item.thum != null && !item.thum.equals("")) {
                Log.d(Variables.tag, item.thum);
                Uri uri = Uri.parse(item.thum);
                holder.sound_image.setImageURI(uri);
            }
            holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            holder.bind(i, datalist.get(i), listener);


        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        ImageButton done;
        ImageButton fav_btn;
        TextView sound_name, description_txt;
        SimpleDraweeView sound_image;
        ImageButton play_arrow, pause_arrow;
        ImageButton play_Btn, pause_Btn;

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

        public void bind(final int pos, final Sounds_GetSet item, final Favourite_Sound_Adapter.OnItemClickListener listener) {
            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //listener.onItemClick(v,pos,item);
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

