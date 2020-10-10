package com.tachyon.bindaas.SoundLists.Adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SoundLanguageAdapter extends RecyclerView.Adapter<SoundLanguageAdapter.ViewHolder> {

    Context context;
    List<String> langList;

    public SoundLanguageAdapter(Context context, List<String> langList) {
        this.context = context;
        this.langList = langList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.sound_language_row,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.lang_name.setText(langList.get(position));
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
