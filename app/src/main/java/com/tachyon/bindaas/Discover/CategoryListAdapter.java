package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> {
    List<String> catList;
    Context context;
    OnCategoryClick listener;
    boolean is_first = true;
    public CategoryListAdapter(List<String> catList, Context context,OnCategoryClick listener) {
        this.catList = catList;
        this.context = context;
        this.listener = listener;
    }

    public interface OnCategoryClick{
        void gotoDetails(int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == 0 && is_first){
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.themecolor));
        }
        holder.cat_textview.setText(catList.get(position));
        holder.cat_textview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                is_first = false;
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
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cat_textview = itemView.findViewById(R.id.cat_text);
        }
    }
}
