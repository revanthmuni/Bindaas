package com.tachyon.bindaas.Video_Recording;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HashTagsAdapter extends RecyclerView.Adapter<HashTagsAdapter.ViewHolder> {
    Context context;
    List<String> list;
    ClickListener listener;
    public HashTagsAdapter(Context context, List<String> persons,ClickListener listener) {
        this.context = context;
        this.list = persons;
        this.listener = listener;
    }
    public interface ClickListener{
        void onClick(String item,int postion);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hashtag_chips,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chip.setText("# "+list.get(position));
        holder.chip.setOnClickListener(view -> {
          listener.onClick(list.get(position),position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chip;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = itemView.findViewById(R.id.chip4);
        }
    }
}
