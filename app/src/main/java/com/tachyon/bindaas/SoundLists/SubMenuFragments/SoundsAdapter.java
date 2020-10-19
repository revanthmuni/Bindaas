package com.tachyon.bindaas.SoundLists.SubMenuFragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.tachyon.bindaas.SoundLists.SoundListActivity;
import com.tachyon.bindaas.SoundLists.Sounds_GetSet;

import java.net.ContentHandler;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

public class SoundsAdapter extends RecyclerView.Adapter<SoundsAdapter.ViewHolder> {
    Context context;
    ArrayList<Sounds_GetSet> list;
    OnItemClickListener listene;
    private static final String TAG = "SoundsAdapter";
    String flag;

    public SoundsAdapter(Context context, ArrayList<Sounds_GetSet> list,String flag,OnItemClickListener listener) {
        this.context = context;
        this.list = list;
        this.listene = listener;
        this.flag = flag;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sound_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            if (flag.equals("sound_list")){
                holder.view_more_layout.setVisibility(View.GONE);
            }else
                holder.view_more_layout.setVisibility(View.VISIBLE);
            holder.section_name.setText("Section Name");
            holder.play_view.setPadding(8,8,8,8);
//            holder.section_name.setText(list.get(position).section.equals("")?"Section Bindaas":list.get(position).section);
            holder.view_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SoundListActivity.class);
                    intent.putExtra("section_id",list.get(position).id);
                    intent.putExtra("title","Trending Sounds");
                    context.startActivity(intent);
                }
            });
            holder.setIsRecyclable(false);
            Sounds_GetSet item = list.get(position);
            holder.sound_name.setText(item.sound_name);
           // holder.description_txt.setText(item.description);

            if (item.thum != null && !item.thum.equals("")&&item.thum.contains(".jpg")) {
                Log.d(Variables.tag, item.thum);
                Uri uri = Uri.parse(item.thum);
                holder.sound_image.setImageURI(uri);
            }
            Log.d(TAG, "onBindViewHolder: "+new Gson().toJson(item ));
//            holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            holder.bind(position, list.get(position), listene);
            if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_un_favourite));

        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton done;
        ImageButton fav_btn;
        TextView sound_name, description_txt;
        SimpleDraweeView sound_image;
        ImageButton play_arrow, pause_arrow;
        ImageButton play_Btn, pause_Btn;
        ConstraintLayout view_more_layout;
        TextView view_more,section_name;
        ConstraintLayout play_view;

        public ViewHolder(@NonNull View view) {
            super(view);
            try {

                view_more = view.findViewById(R.id.view_more);
                section_name = view.findViewById(R.id.section_name);
                view_more_layout = view.findViewById(R.id.view_more_layout);
                play_view = view.findViewById(R.id.play_view);

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
        public void bind(final int pos, final Sounds_GetSet item, final OnItemClickListener listener) {
            try {
               /* itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //listener.onItemClick(v,pos,item);
                    }
                });*/

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(v, pos, item);
                    }
                });
                play_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listener.onItemClick(itemView, pos, item);
                    }
                });
                pause_arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        listener.onItemClick(itemView, pos, item);
                    }
                });
               /* play_Btn.setOnClickListener(new View.OnClickListener() {
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
*/
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
