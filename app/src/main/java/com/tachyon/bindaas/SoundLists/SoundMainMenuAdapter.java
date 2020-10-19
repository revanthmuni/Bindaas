package com.tachyon.bindaas.SoundLists;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.Models.SoundCategoryModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class SoundMainMenuAdapter extends RecyclerView.Adapter<SoundMainMenuAdapter.ViewHolder> {
    Context context;
    List<SoundCategoryModel> menuList;
    OnItemClick listener;
    int row_index = 0;



    public SoundMainMenuAdapter(Context context, List<SoundCategoryModel> menuList, OnItemClick listener) {
        this.context = context;
        this.menuList = menuList;
        this.listener = listener;
    }
    public void setMenuList(List<SoundCategoryModel> menuList) {
        this.menuList = menuList;
    }
    public interface OnItemClick{
        void onClick(int position);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sounds_main_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (row_index == position){
            holder.background_text.setBackgroundColor(Color.parseColor("#567845"));
            holder.main_menu_text.setTextColor(Color.parseColor("#ffffff"));
        }else{
            holder.background_text.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.main_menu_text.setTextColor(Color.parseColor("#000000"));
        }
        holder.main_menu_text.setText(menuList.get(position).getSection_name());
        holder.itemView.setOnClickListener(view -> {
            row_index=position;
            notifyDataSetChanged();
            listener.onClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView main_menu_text;
        CardView background_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            main_menu_text = itemView.findViewById(R.id.main_menu_text);
            background_text = itemView.findViewById(R.id.mainview);
        }
    }
}
