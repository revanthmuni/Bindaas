package com.tachyon.bindaas.Video_Recording;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.tachyon.bindaas.Discover.Discover_Get_Set;
import com.tachyon.bindaas.Home.Home_Get_Set;
import com.tachyon.bindaas.Preferences.LanguageAdapter;
import com.tachyon.bindaas.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GetFollowersAdapter extends RecyclerView.Adapter<GetFollowersAdapter.ViewHolder>{
    ArrayList<Home_Get_Set> list;
    ArrayList<Home_Get_Set> datalist_filter;
    Context context;
    OnItemClick listener;
    public GetFollowersAdapter(Context context, ArrayList<Home_Get_Set> p1,OnItemClick listener) {
        this.list = p1;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.follwers_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(list.get(position).first_name);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(list.get(position).user_id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(ArrayList<Home_Get_Set> list){
        this.list = list;
        notifyDataSetChanged();
    }
    public interface OnItemClick{
        void onClick(String id);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
