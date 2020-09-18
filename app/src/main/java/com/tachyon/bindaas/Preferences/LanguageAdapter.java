package com.tachyon.bindaas.Preferences;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.tachyon.bindaas.R;
import com.tachyon.bindaas.SimpleClasses.Variables;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static android.content.ContentValues.TAG;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder>{

    OnItemCheckListener listener;
    String[] list;

    public LanguageAdapter(String[] languages,OnItemCheckListener listener) {
        this.list = languages;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.language_checkbox_row,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String laList = Variables.sharedPreferences.getString(Variables.language,"");
        Log.d(TAG, "onBindViewHolder: "+list[position]);
        holder.checkBox.setText(list[position]);
        if (laList.contains(holder.checkBox.getText().toString())){
            holder.checkBox.setChecked(true);
            listener.onItemCheck(holder.checkBox.getText().toString(),position);
        }else{
            listener.onItemUncheck(holder.checkBox.getText().toString(),position);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    listener.onItemCheck(holder.checkBox.getText().toString(),position);
                }else{
                    listener.onItemUncheck(holder.checkBox.getText().toString(),position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
    public interface OnItemCheckListener {
        void onItemCheck(String item,int position);
        void onItemUncheck(String item,int position);
    }
}