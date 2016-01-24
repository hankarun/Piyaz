package com.hankarun.piyangoogren;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PiyangoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    @Bind(R.id.piyangoRecycler)
    RecyclerView mRecyclerView;
    @Bind(R.id.piyangoSpinner)
    Spinner mSpinner;

    private OnFragmentInteractionListener mListener;

    public PiyangoFragment() {
        // Required empty public constructor
    }

    public static PiyangoFragment newInstance(String param1, String param2) {
        PiyangoFragment fragment = new PiyangoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_piyango, container, false);
        ButterKnife.bind(this, rootView);
        mSpinner.setOnItemSelectedListener(this);
        return rootView;
    }

    PiyangoAdapter mAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new PiyangoAdapter(mList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mRecyclerView.setAdapter(mAdapter);

        TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                // TODO Track text move to the next
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };

        getLoaderManager().initLoader(0, null, this);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case 0:
                return new CursorLoader(getActivity(),
                        GamesContentProvider.CONTENT_URI,
                        new String[]{GamesDatabaseHelper.GAMES_DATE, GamesDatabaseHelper.GAMES_DATEVIEW},
                        GamesDatabaseHelper.GAMES_TYPE + " = 'piyango'",
                        null,
                        null);
            case 1:
                return new CursorLoader(getActivity(),
                        GamesContentProvider.CONTENT_URI,
                        new String[]{GamesDatabaseHelper.GAMES_NUMBERS},
                        GamesDatabaseHelper.GAMES_TYPE + " = 'piyango' AND "
                                + GamesDatabaseHelper.GAMES_DATE + " ='" + dates.get(mSpinner.getSelectedItemPosition()) + "'",
                        null,
                        null);
        }
        return null;
    }

    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> datesNormal = new ArrayList<>();

    public class Piyango {
        public String ikramiye;
        public String hane;
        public ArrayList<String> numaralar;

        public Piyango(String _ik, String _hane, String _num) {
            ikramiye = _ik;
            hane = _hane;
            numaralar = new ArrayList<>();
            try {
                _num = _num.replace("\"","");
                _num = _num.replace("]","");
                _num = _num.replace("[","");
                String[] ts = _num.split(",");
                for (String s:ts) {
                    numaralar.add(s);
                }
                Collections.sort(numaralar);
            } catch (Exception e) {
                Log.d("piyango", "object problem");
            }
        }

        public String toString() {
            String temp = "";
            if (hane.equals("6")) {
                temp += ikramiye + " TL İktamiye Kazanan Numara";
            } else {
                temp += "Son " + hane + " rakamına göre " + ikramiye + " TL Kazanan Numara";
            }

            if(numaralar.size()>1)
                temp += "lar";

            temp += "\n\n";

            for(String s:numaralar){
                temp += s + " ";
            }
            temp += "\n\n";
            return temp;
        }

        public String getIkramiye() {
            String temp = "";
            if (hane.equals("6") || hane.equals("0")) {
                temp += ikramiye + " TL İktamiye Kazanan Numara";
            } else {
                temp += "Son " + hane + " rakamına göre " + ikramiye + " TL Kazanan Numara";
            }

            if(numaralar.size()>1)
                temp += "lar";;
            return temp;
        }

        public String getNumbers() {
            String temp = "";
            for(String s:numaralar){
                temp += s + " ";
            }
            return temp;
        }
    }

    ArrayList<Piyango> mList = new ArrayList<>();

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            switch (loader.getId()) {
                case 0:
                    do {
                        dates.add(data.getString(data.getColumnIndex(GamesDatabaseHelper.GAMES_DATE)));
                        datesNormal.add(data.getString(data.getColumnIndex(GamesDatabaseHelper.GAMES_DATEVIEW)));
                    } while (data.moveToNext());
                    ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_item, datesNormal);
                    mSpinner.setAdapter(spinner_adapter);
                    mSpinner.setSelection(0);
                    getLoaderManager().initLoader(1, null, this);
                    break;
                case 1:
                    try {
                        mList.clear();
                        JSONObject json = new JSONObject(data.getString(data.getColumnIndex(GamesDatabaseHelper.GAMES_NUMBERS)));
                        JSONArray ar = json.getJSONArray("sonuclar");
                        String temp = "";
                        for (int i = 0; i < ar.length(); i++) {
                            JSONObject jObject = ar.getJSONObject(i);
                            try {
                                Piyango tmp = new Piyango(jObject.getString("ikramiye"),
                                        jObject.getString("haneSayisi"),
                                        jObject.getString("numaralar"));
                                mList.add(tmp);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        mAdapter.array = mList;
                        mAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.d("piyangoer", e.toString());
                    }
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class PiyangoAdapter extends RecyclerView.Adapter<PiyangoAdapter.ViewHolder>{
        ArrayList<Piyango> array;

        public PiyangoAdapter(ArrayList<Piyango> array) {
            this.array = array;
        }

        @Override
        public PiyangoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.piyango_recycler_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PiyangoAdapter.ViewHolder holder, int position) {
            holder.main.setText(array.get(position).getIkramiye());
            holder.numbers.setText(array.get(position).getNumbers());
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.piyangoIkramiye)
            TextView main;
            @Bind(R.id.piyangoNumaralar)
            TextView numbers;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
