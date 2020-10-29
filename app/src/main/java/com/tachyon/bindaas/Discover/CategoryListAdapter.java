package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.tachyon.bindaas.Discover.Models.Category;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    ArrayList<Category> catList;
    Context context;
    OnCategoryClick listener;
    int row_index = 0;

    public CategoryListAdapter(ArrayList<Category> catList, Context context, OnCategoryClick listener) {
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
        try {
            if (row_index == position) {
                holder.background.setBackgroundColor(Color.parseColor("#567845"));
                holder.cat_textview.setTextColor(Color.parseColor("#ffffff"));
            } else {
                holder.background.setBackgroundColor(Color.parseColor("#ffffff"));
                holder.cat_textview.setTextColor(Color.parseColor("#000000"));
            }
            holder.cat_textview.setText(catList.get(position).getSection_name());
            holder.cat_textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    row_index = position;
                    notifyDataSetChanged();
                    listener.gotoDetails(position);
                }
            });
        } catch (Exception e) {
            Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return catList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView cat_textview;
        LinearLayout background;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                cat_textview = itemView.findViewById(R.id.cat_text);
                background = itemView.findViewById(R.id.background);
            } catch (Exception e) {
                Functions.showLogMessage(context, this.getClass().getSimpleName(), e.getMessage());
            }
        }
    }
}
