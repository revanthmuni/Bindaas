package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TopsAdapter extends RecyclerView.Adapter<TopsAdapter.ViewHolder> {
    Context context;
    List<TopsItems> topsList;

    public TopsAdapter(Context context, List<TopsItems> topsList) {
        this.context = context;
        this.topsList = topsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tops_list_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // Glide.with(context).load(topsList.get(position).getProfile_pic()).placeholder(R.drawable.image_placeholder).into(holder.profilePic);
        holder.line_1.setText(topsList.get(position).getLine_1());
        holder.line_2.setText(topsList.get(position).getLine_2());
        holder.line_3.setText(topsList.get(position).getLine_3());
        holder.userName.setText(topsList.get(position).getUser_name());
    }

    @Override
    public int getItemCount() {
        return topsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        TextView userName,line_1,line_2,line_3;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            line_3 = itemView.findViewById(R.id.line_3);
            line_2 = itemView.findViewById(R.id.line_2);
            line_1 = itemView.findViewById(R.id.line_1);
            userName = itemView.findViewById(R.id.user_id);

        }
    }
}
