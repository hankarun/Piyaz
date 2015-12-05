package com.hankarun.piyangoogren;

import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ResultsActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    public static ResultsActivityFragment newInstance(int index) {
        ResultsActivityFragment f = new ResultsActivityFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    private ArrayList<Game> mGames = new ArrayList<>();
    private NumbersAdapter adapter;

    @Bind(R.id.resultRecycler)
    FastScrollRecyclerView mResultRecycler;

    public ResultsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        ButterKnife.bind(this, rootView);

        int index = 0;
        Bundle args = getArguments();
        if (args != null) {
            index = args.getInt("index", 0);
        }

        Bundle bundle = new Bundle();
        bundle.putString("game", Statics.menuOriginal.get(index));

        getLoaderManager().initLoader(0, bundle, this);

        mResultRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NumbersAdapter(mGames,calculateIndexesForName(mGames));
        mResultRecycler.setAdapter(adapter);

        FastScrollRecyclerItemDec decoration = new FastScrollRecyclerItemDec(getActivity());
        mResultRecycler.addItemDecoration(decoration);
        mResultRecycler.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

    private HashMap<String, Integer> calculateIndexesForName(ArrayList<Game> items){
        HashMap<String, Integer> mapIndex = new LinkedHashMap<>();
        ArrayList<String> test = new ArrayList<>();
        for (int i = 0; i < items.size(); i++){
            String name = items.get(i).getmDate().substring(0,4);

            boolean contains = false;
            for(String t:test){
                if(t.equals(name)){
                    contains = true;
                }
            }

            if (!contains) {
                test.add(name);
                mapIndex.put(name, i);
            }
        }
        return mapIndex;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String gameName = args.getString("game");
        return new CursorLoader(getActivity(),
                GamesContentProvider.CONTENT_URI,
                GamesDatabaseHelper.GAMES_PROJECTION,
                GamesDatabaseHelper.GAMES_TYPE + " = '" + gameName + "'",
                null,
                GamesDatabaseHelper.GAMES_DATE + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            mGames.clear();
            while (data.moveToNext()) {
                mGames.add(Game.fromCursor(data));
            }
            adapter = new NumbersAdapter(mGames,calculateIndexesForName(mGames));
            mResultRecycler.swapAdapter(adapter,true);
            mResultRecycler.setupThings();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.ViewHolder> implements FastScrollInterface{
        ArrayList<Game> array;
        HashMap<String, Integer> mMapIndex;

        public NumbersAdapter(ArrayList<Game> array, HashMap<String, Integer> mapIndex) {
            this.array = array;
            mMapIndex = mapIndex;
        }

        @Override
        public NumbersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.result_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NumbersAdapter.ViewHolder holder, int position) {
            holder.date.setText(array.get(position).getmDateDetail());
            holder.result.setText(array.get(position).getmNumbers());
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        @Override
        public HashMap<String, Integer> getMapIndex() {
            return this.mMapIndex;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.resultDateView)
            TextView date;
            @Bind(R.id.resultItemView)
            TextView result;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
