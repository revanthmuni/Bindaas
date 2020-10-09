package com.tachyon.bindaas.Discover;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.tachyon.bindaas.R;

import java.util.ArrayList;
import java.util.List;

public class EventSliderAdapter extends SliderViewAdapter<EventSliderAdapter.SliderAdapterVH> {
    private Context context;
    private List<EventItems> mSliderItems = new ArrayList<>();

    public EventSliderAdapter(Context context,List<EventItems> listItems) {
        this.context = context;
        this.mSliderItems = listItems;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        EventItems sliderItem = mSliderItems.get(position);

        viewHolder.eventTitle.setText(sliderItem.getEvent_name());
        viewHolder.eventDescription.setText(sliderItem.getShort_description());
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getDiscover_image())
                .fitCenter()
                .into(viewHolder.eventImage);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView eventImage;
        TextView eventTitle,eventDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitle = itemView.findViewById(R.id.event_title);
            eventDescription = itemView.findViewById(R.id.eventDescription);
            this.itemView = itemView;
        }
    }
}
