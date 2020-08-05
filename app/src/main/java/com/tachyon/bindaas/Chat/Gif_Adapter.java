package com.tachyon.bindaas.Chat;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Functions;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;

public class Gif_Adapter extends RecyclerView.Adapter<Gif_Adapter.CustomViewHolder> {
    public Context context;
    ArrayList<String> gif_list = new ArrayList<>();
    private Gif_Adapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String item);
    }

    public Gif_Adapter(Context context, ArrayList<String> urllist, Gif_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.gif_list = urllist;
        this.listener = listener;

    }

    @Override
    public Gif_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_gif_layout, null);
        Gif_Adapter.CustomViewHolder viewHolder = new Gif_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return gif_list.size();
    }


    class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView gif_image;

        public CustomViewHolder(View view) {
            super(view);
            try {
                gif_image = view.findViewById(R.id.gif_image);
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }

        }

        public void bind(final String item, final Gif_Adapter.OnItemClickListener listener) {

            try {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(item);
                    }
                });
            } catch (Exception e) {
                Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

            }

        }

    }


    @Override
    public void onBindViewHolder(final Gif_Adapter.CustomViewHolder holder, final int i) {
        try {
            holder.bind(gif_list.get(i), listener);

            Glide.with(context)
                    .load(Variables.gif_firstpart + gif_list.get(i) + Variables.gif_secondpart)
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.NONE).placeholder(context.getResources().getDrawable(R.drawable.image_placeholder)).centerCrop())
                    .into(holder.gif_image);

            Log.d("resp", Variables.gif_firstpart + gif_list.get(i) + Variables.gif_secondpart);
        } catch (Exception e) {
            Functions.showLogMessage(context, context.getClass().getSimpleName(), e.getMessage());

        }
    }


}