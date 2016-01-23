package com.hankarun.piyangoogren;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainFragment extends Fragment implements AdapterOnClick{

    @Bind(R.id.menuRecyclerView)
    RecyclerView mRecyclerView;

    public MainFragment() {
    }

    CustomRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        String[] newArray = new String[5];
        for(int x = 0; x < Statics.menuOriginal.size(); x++)
            newArray[x] = getValue(getContext(),Statics.menuOriginal.get(x));


        adapter = new CustomRecyclerAdapter(this,newArray);

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    public String getValue(Context context,String key) {
        SharedPreferences settings;
        String text;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        text = settings.getString(key, "");
        return text;
    }

    public void save(Context context, String name,String text) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;
        settings = PreferenceManager.getDefaultSharedPreferences(context);
        editor = settings.edit();

        editor.putString(name, text);
        editor.apply();
    }

    @Override
    public void onClick(int position, CustomRecyclerAdapter.ViewHolder holder) {
        save(getContext(), Statics.menuOriginal.get(position), "0");
        adapter.newArray[position] = "0";
        adapter.notifyDataSetChanged();
        if(!Statics.menuOriginal.get(position).equals("piyango")) {
            Intent intent = new Intent(getActivity(), DetailActivity.class);
            intent.putExtra("index", position);
            getActivity().startActivity(intent);
        } else {
            Intent intent = new Intent(getActivity(), PiyangoActivity.class);
            getActivity().startActivity(intent);
        }
    }
}
