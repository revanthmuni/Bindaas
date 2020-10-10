package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    List<String> catList;
    Context context;
    OnCategoryClick listener;
    int row_index = 0;

    public CategoryListAdapter(List<String> catList, Context context, OnCategoryClick listener) {
        this.catList = catList;
        this.context = context;
        this.listener = listener;
    }

    public interface OnCategoryClick {
        void gotoDetails(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list_row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (row_index == position) {
            holder.background.setBackgroundColor(Color.parseColor("#567845"));
            holder.cat_textview.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.background.setBackgroundColor(Color.parseColor("#ffffff"));
            holder.cat_textview.setTextColor(Color.parseColor("#000000"));
        }
        holder.cat_textview.setText(catList.get(position));
        holder.cat_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                row_index = position;
                notifyDataSetChanged();
                listener.gotoDetails(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cat_textview;
        CardView background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_textview = itemView.findViewById(R.id.cat_text);
            background = itemView.findViewById(R.id.background);
        }
    }
}
