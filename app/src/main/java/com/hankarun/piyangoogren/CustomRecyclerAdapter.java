package com.hankarun.piyangoogren;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>{
    private ArrayList<String> menu;
    private AdapterOnClick itemClick;
    private String[] newArray;

    public CustomRecyclerAdapter(ArrayList<String> menu, AdapterOnClick onClick, String[] options){
        this.menu = menu;
        this.itemClick = onClick;
        newArray = options;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item.setText(menu.get(position));

        if(newArray[position].equals("1")){
            holder.newImage.setVisibility(View.VISIBLE);
        }else{
            holder.newImage.setVisibility(View.INVISIBLE);
        }

        switch (position){
            case 0:
                holder.banner.setImageResource(R.drawable.s_loto);
                break;
            case 1:
                holder.banner.setImageResource(R.drawable.super_loto);
                break;
            case 2:
                holder.banner.setImageResource(R.drawable.s_topu);
                break;
            case 3:
                holder.banner.setImageResource(R.drawable.on_n);
                break;
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClick.onClick(position, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.item)
        TextView item;
        @Bind(R.id.item_layout)
        RelativeLayout layout;
        @Bind(R.id.imageView)
        ImageView banner;
        @Bind(R.id.newImageView)
        ImageView newImage;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}