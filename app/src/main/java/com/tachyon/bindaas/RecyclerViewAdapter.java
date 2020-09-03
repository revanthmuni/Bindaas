package com.tachyon.bindaas;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private OnVideoClick onVideoClick;
    private ArrayList<File> mainList;

    public RecyclerViewAdapter(Context mContext,ArrayList<File> mainList, OnVideoClick onVideoClick){
        this.mContext = mContext;
        this.mainList = mainList;
        this.onVideoClick = onVideoClick;
    }
    public void updateList(ArrayList<File> list){
        mainList = list;
        notifyDataSetChanged();
    }
    public interface OnVideoClick{
        void onVideoSelected(File file);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.files_list,parent,false);
        return new FileLayoutHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FileLayoutHolder)holder).videoTitle.setText(mainList.get(position).getName());
        //we will load thumbnail using glid library
        Uri uri = Uri.fromFile(mainList.get(position));

        Glide.with(mContext)
                .load(uri).thumbnail(0.1f).into(((FileLayoutHolder)holder).thumbnail);
    }

    @Override
    public int getItemCount() {
        return mainList.size();
    }

    class FileLayoutHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView thumbnail;
        TextView videoTitle;
        ImageView ic_more_btn;

        public FileLayoutHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            videoTitle = itemView.findViewById(R.id.videotitle);
            ic_more_btn = itemView.findViewById(R.id.ic_more_btn);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (view == itemView){
                onVideoClick.onVideoSelected(Constant.allMediaList.get(getAdapterPosition()));
                Log.d("ADAPTER", "onClick: "+Constant.allMediaList.get(getAdapterPosition()));
            }
        }
    }
}
