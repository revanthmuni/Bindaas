package com.tachyon.bindaas.SoundLists.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SoundLists.Models.LanguageModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SoundLanguageAdapter extends RecyclerView.Adapter<SoundLanguageAdapter.ViewHolder> {

    Context context;
    List<LanguageModel> langList;
    OnItemClick listener;

    public interface OnItemClick {
        void onClick(int position, String language);
    }

    public SoundLanguageAdapter(Context context, List<LanguageModel> langList, OnItemClick listener) {
        this.listener = listener;
        this.context = context;
        this.langList = langList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sound_language_row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lang_name.setText(langList.get(position).getSection_name());
        if (langList.get(position).getSection_image()!=null){
            Glide.with(context).load(langList.get(position).getSection_image()).into(holder.lang_image);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position,langList.get(position).getSection_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return langList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView lang_image;
        TextView lang_name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            lang_image = itemView.findViewById(R.id.language_image);
            lang_name = itemView.findViewById(R.id.language_text);

        }
    }
}
