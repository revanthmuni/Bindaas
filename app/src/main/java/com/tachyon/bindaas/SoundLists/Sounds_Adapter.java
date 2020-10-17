package com.tachyon.bindaas.SoundLists;

import android.content.Context;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;


public class Sounds_Adapter extends RecyclerView.Adapter<Sounds_Adapter.CustomViewHolder> {
    public Context context;

    ArrayList<Sound_catagory_Get_Set> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    public Sounds_Adapter.OnItemClickListener listener;

    public Sounds_Adapter(Context context, ArrayList<Sound_catagory_Get_Set> arrayList, Sounds_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }


    @Override
    public Sounds_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_category_sound_layout, viewGroup, false);
        Sounds_Adapter.CustomViewHolder viewHolder = new Sounds_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return datalist != null ? datalist.size() : 0;
    }


    @Override
    public void onBindViewHolder(final Sounds_Adapter.CustomViewHolder holder, final int i) {
        try{
        holder.setIsRecyclable(false);


        Sound_catagory_Get_Set item = datalist.get(i);

        holder.title.setText(item.catagory);


        Sound_Items_Adapter adapter = new Sound_Items_Adapter(context, item.sound_list,
                new Sound_Items_Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int postion, Sounds_GetSet item) {

                listener.onItemClick(view, postion, item);
            }
        });

        GridLayoutManager gridLayoutManager;
        if (item.sound_list.size() == 1)
            gridLayoutManager = new GridLayoutManager(context, 1);

        else if (item.sound_list.size() == 2)
            gridLayoutManager = new GridLayoutManager(context, 2);

        else
            gridLayoutManager = new GridLayoutManager(context, 3);

        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        holder.recyclerView.setLayoutManager(gridLayoutManager);
        holder.recyclerView.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.findSnapView(gridLayoutManager);
        snapHelper.attachToRecyclerView(holder.recyclerView);
        }catch (Exception e){
            Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());

        }

    }

    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        RecyclerView recyclerView;

        public CustomViewHolder(View view) {
            super(view);
            try{
            //  image=view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            recyclerView = view.findViewById(R.id.horizontal_recylerview);

            }catch (Exception e){
                Functions.showLogMessage(context,context.getClass().getSimpleName(),e.getMessage());
            }
        }
    }
}


class Sound_Items_Adapter extends RecyclerView.Adapter<Sound_Items_Adapter.CustomViewHolder> {
        public Context context;

    ArrayList<Sounds_GetSet> datalist;

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, Sounds_GetSet item);
    }

    public Sound_Items_Adapter.OnItemClickListener listener;


    public Sound_Items_Adapter(Context context, ArrayList<Sounds_GetSet> arrayList, Sound_Items_Adapter.OnItemClickListener listener) {
        this.context = context;
        datalist = arrayList;
        this.listener = listener;
    }

    @Override
    public Sound_Items_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_sound_layout, viewGroup, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(Variables.screen_width - 50, RecyclerView.LayoutParams.WRAP_CONTENT));
        Sound_Items_Adapter.CustomViewHolder viewHolder = new Sound_Items_Adapter.CustomViewHolder(view);
        return viewHolder;
    }


    @Override
    public int getItemCount() {
        return datalist != null ? datalist.size() : 0;
    }


    @Override
    public void onBindViewHolder(final Sound_Items_Adapter.CustomViewHolder holder, final int i) {
        try {
            holder.setIsRecyclable(false);

            Sounds_GetSet item = datalist.get(i);


            holder.bind(i, datalist.get(i), listener);

            holder.sound_name.setText(item.sound_name);
            holder.description_txt.setText(item.description);

            /*if (item.fav.equals("1"))
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_favourite));
            else
                holder.fav_btn.setImageDrawable(context.getDrawable(R.drawable.ic_my_un_favourite));*/


            if (item.thum.equals("")) {
                item.thum = "Null";
            }

            if (item.thum != null && !item.thum.equals("")&&item.thum.contains(".jpg")) {
                Log.d(Variables.tag, item.thum);
                Uri uri = Uri.parse(item.thum);
                holder.sound_image.setImageURI(uri);
            }

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
        ImageView sound_image;

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

        public void bind(final int pos, final Sounds_GetSet item, final Sound_Items_Adapter.OnItemClickListener listener) {
            try {
                /*itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // listener.onItemClick(v,pos,item);
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

