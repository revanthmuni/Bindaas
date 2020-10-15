package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tachyon.bindaas.Discover.Models.Users;
import com.tachyon.bindaas.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TopsAdapter extends RecyclerView.Adapter<TopsAdapter.ViewHolder> {
    Context context;
    List<Users> topsList;
    OnProfileClick listener;

    public void setTopsList(List<Users> topsList) {
        this.topsList = topsList;
    }

    public TopsAdapter(Context context, List<Users> topsList,OnProfileClick listener) {
        this.context = context;
        this.topsList = topsList;
        this.listener = listener;
    }

    public interface OnProfileClick{
        void onClick(int position,Users users);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.tops_list_row, null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
         Glide.with(context).load(topsList.get(position).getProfilePic()).placeholder(R.drawable.image_placeholder).into(holder.profilePic);
         holder.views.setText(String.format("%s views", topsList.get(position).getTotalViews()));
         holder.likes.setText(String.format("%s likes", topsList.get(position).getTotalLikes()));
         holder.video_uploads.setText(String.format("%s videos Uploaded", topsList.get(position).getTotalVideoUploads()));
         holder.user_score.setText(String.format("User Score :%s", topsList.get(position).getTotalUserScore()));

        holder.userName.setText(topsList.get(position).getFirstName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(position,topsList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return topsList != null ? topsList.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        TextView userName, likes, views , video_uploads , user_score;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            likes = itemView.findViewById(R.id.likes);
            views = itemView.findViewById(R.id.views);
            video_uploads = itemView.findViewById(R.id.video_uploads);
            userName = itemView.findViewById(R.id.user_id);
            user_score = itemView.findViewById(R.id.user_score);

        }
    }
}
