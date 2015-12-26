package com.hankarun.piyangoogren;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CustomNumberAdapter extends RecyclerView.Adapter<CustomNumberAdapter.ViewHolder>{
    public ArrayList<String> menu;

    public CustomNumberAdapter(ArrayList<String> menu){
        this.menu = menu;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.numberitem, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.number.setText(menu.get(position));
    }

    @Override
    public int getItemCount() {
        return menu.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.number)
        TextView number;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}