package com.hankarun.piyangoogren;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainFragment extends Fragment implements AdapterOnClick{

    @Bind(R.id.menuRecyclerView)
    RecyclerView mRecyclerView;

    public MainFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        String[] newArray = new String[4];
        for(int x = 0; x < Statics.menu.size(); x++)
            newArray[x] = getValue(getContext(),Statics.menu.get(x));

        newArray[3] = "1";


        CustomRecyclerAdapter adapter = new CustomRecyclerAdapter(Statics.menu,this,newArray);

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
        save(getContext(),Statics.menu.get(position),"0");
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra("index", position);
        getActivity().startActivity(intent);
    }
}
