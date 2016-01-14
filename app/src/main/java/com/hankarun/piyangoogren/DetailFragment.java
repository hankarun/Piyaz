package com.hankarun.piyangoogren;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.hankarun.piyangoogren.Statics.setSayisal;

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @Bind(R.id.tarihView)
    TextView tarihView;
    @Bind(R.id.haftaRecycler)
    RecyclerView mHaftaRecycler;
    @Bind(R.id.ayRecycler)
    RecyclerView mAyRecycler;
    @Bind(R.id.tumRecycler)
    RecyclerView mTumRecycler;

    @Bind(R.id.haftaTextView)
    TextView haftaTextView;
    @Bind(R.id.ayTextView)
    TextView ayTextView;
    @Bind(R.id.tumTextView)
    TextView tumTextView;
    @Bind(R.id.numberRecycler)
    RecyclerView mNumberRecycler;
    @Bind(R.id.lukyOnes)
    TextView mLukyOnes;
    @Bind(R.id.otherView)
    TextView mOthers;



    private Game game;
    int[] sayisal;

    private CustomNumberAdapter weekAdapter;
    private CustomNumberAdapter ayAdapter;
    private CustomNumberAdapter tumAdapter;
    private NumbersAdapter numbersAdapter;

    private ArrayList<Stats> gameStats;

    public ArrayList<String> dummy = new ArrayList<String>() {{
        add("1");
        add("21");
        add("3");
        add("32");
        add("433");
        add("43");
        add("332");
        add("76");
        add("85");
        add("45");
    }};

    private static boolean ASCENDING_ORDER = true;
    private static boolean DESCENDING_ORDER = false;


    public static DetailFragment newInstance(int index) {
        DetailFragment f = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public DetailFragment() {
    }

    private int index;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            index = savedInstanceState.getInt("index");
        } else {
            // Probably initialize members with default values for a new instance
        }
        Bundle args = getArguments();
        if (args != null)
            index = args.getInt("index", 0);



        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mAyRecycler.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTumRecycler.setLayoutManager(layoutManager2);
        LinearLayoutManager layoutManager3
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mHaftaRecycler.setLayoutManager(layoutManager3);

        mNumberRecycler.setLayoutManager(new WrappableGridLayoutManager(getContext(), 6));


        ArrayList<String> tmp = new ArrayList<>();

        weekAdapter = new CustomNumberAdapter(tmp);
        mHaftaRecycler.setAdapter(weekAdapter);
        mHaftaRecycler.setNestedScrollingEnabled(false);

        ayAdapter = new CustomNumberAdapter(tmp);
        mAyRecycler.setAdapter(ayAdapter);
        mAyRecycler.setNestedScrollingEnabled(false);

        tumAdapter = new CustomNumberAdapter(tmp);
        mTumRecycler.setAdapter(tumAdapter);
        mTumRecycler.setNestedScrollingEnabled(false);


        numbersAdapter = new NumbersAdapter(new String[]{"Güncelleniyor..."}, Statics.menuOriginal.get(index).equals("sanstopu"));
        mNumberRecycler.setAdapter(numbersAdapter);
        mNumberRecycler.setNestedScrollingEnabled(false);

        Bundle bundle = new Bundle();
        bundle.putString("game", Statics.menuOriginal.get(index));

        getLoaderManager().initLoader(0, bundle, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String gameName = args.getString("game");
        switch (id) {
            case 0:
                return new CursorLoader(getActivity(),
                        GamesContentProvider.CONTENT_URI,
                        GamesDatabaseHelper.GAMES_PROJECTION,
                        GamesDatabaseHelper.GAMES_TYPE + " = '" + gameName + "'",
                        null,
                        GamesDatabaseHelper.GAMES_DATE + " DESC LIMIT 1");
            case 1:
                return new CursorLoader(getActivity(), GamesContentProvider.CONTENT_URI_STATS,
                        StatisticsDatabaseHelper.STATICS_PROJECTION,
                        StatisticsDatabaseHelper.STATISTICS_TYPE + " = '" + gameName + "'",
                        null,
                        StatisticsDatabaseHelper.STATISTICS_GAME + " ASC");
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case 0:
                if (data != null && data.moveToFirst()) {
                    game = Game.fromCursor(data);
                    numbersAdapter.array = game.getmNumbers().replace(" ", "").split("\\-");
                    numbersAdapter.notifyDataSetChanged();
                    tarihView.setText("Çekiliş Tarihi: "+game.getmDateDetail());
                }
                sayisal = new int[setSayisal(game.getmType())];
                Bundle bundle = new Bundle();
                bundle.putString("game", game.getmType());
                String[] see = game.getLukyToString().split("-");
                mLukyOnes.setText(see[0]);
                mOthers.setText(see[1]);
                getLoaderManager().initLoader(1, bundle, this);
                break;
            case 1:
                if (data != null && data.moveToFirst()) {
                    gameStats = new ArrayList<>();
                    gameStats.add(Stats.fromCursor(data));
                    //Conver cursor to stat array
                    while (data.moveToNext()) {
                        gameStats.add(Stats.fromCursor(data));
                    }
                    populateStatistics(gameStats);
                }
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


    private void populateStatistics(ArrayList<Stats> gameStats) {
        if (gameStats.size() == 52) {

            populateWeek(gameStats.get(game.returnWeek()));

            populateMonth(gameStats);

            populateTotal(gameStats);
        }

    }

    private void populateTotal(ArrayList<Stats> gameStats) {
        for (int i = 0; i < setSayisal(game.getmType()); i++) {
            sayisal[i] = 0;
        }


        for (int i = 0; i < 52; i++) {
            String[] numberarray = gameStats.get(i).getmNumber().replace(" ", "").split("\\-");
            for (int s = 0; s < setSayisal(game.getmType()); s++) {
                sayisal[s] = sayisal[s] + Integer.parseInt(numberarray[s]);
            }
        }

        if (tumTextView.getText().equals("Tum Zamanların En Çok Çıkan Şanslı Numaraları")) {
            tumTextView.setText("Tum Zamanların En Az Çıkan Şanslı Numaraları");
            tumAdapter.menu = orderList(sayisal, DESCENDING_ORDER);
        } else {
            tumTextView.setText("Tum Zamanların En Çok Çıkan Şanslı Numaraları");
            tumAdapter.menu = orderList(sayisal, ASCENDING_ORDER);
        }

        tumAdapter.notifyDataSetChanged();
    }

    int[] weeks_months = {3, 3, 3, 3, 4, 3, 3, 4, 4, 3, 4, 3};

    private void populateMonth(ArrayList<Stats> gameStats) {
        int month = Integer.parseInt(game.getmDate().substring(4, 6));


        for (int i = 0; i < setSayisal(game.getmType()); i++) {
            sayisal[i] = 0;
        }

        ArrayList<Stats> temp = new ArrayList<>(gameStats);
        compressArrayToMonths(temp);

        String[] numberarray = temp.get(month - 1).getmNumber().replace(" ", "").split("\\-");
        for (int s = 0; s < setSayisal(game.getmType()); s++) {
            sayisal[s] = sayisal[s] + Integer.parseInt(numberarray[s]);
        }

        if (ayTextView.getText().equals("Ayın En Çok Çıkan Şanslı Numaraları")) {
            ayTextView.setText("Ayın En Az Çıkan Şanslı Numaraları");
            ayAdapter.menu = orderList(sayisal, DESCENDING_ORDER);
        } else {
            ayTextView.setText("Ayın En Çok Çıkan Şanslı Numaraları");
            ayAdapter.menu = orderList(sayisal, ASCENDING_ORDER);
        }

        ayAdapter.notifyDataSetChanged();
    }

    private void compressArrayToMonths(ArrayList<Stats> gameStats) {
        for (int s = 0; s < 12; s++) {
            for (int r = 0; r < weeks_months[s]; r++) {
                gameStats.get(s).setmNumber(addTwoStringArrayToFirst(gameStats.get(s).getmNumber(), gameStats.get(s + 1).getmNumber()));
                gameStats.remove(s + 1);
            }
        }
    }

    private String addTwoStringArrayToFirst(String a, String b) {
        String[] numberarray1 = a.replace(" ", "").split("\\-");
        String[] numberarray2 = b.replace(" ", "").split("\\-");
        int[] sayisal = new int[setSayisal(game.getmType())];

        for (int i = 0; i < setSayisal(game.getmType()); i++) {
            sayisal[i] = Integer.parseInt(numberarray1[i]);
        }

        for (int s = 0; s < setSayisal(game.getmType()); s++) {
            sayisal[s] = sayisal[s] + Integer.parseInt(numberarray2[s]);
        }

        String test = "";
        for (int i = 0; i < setSayisal(game.getmType()); i++) {
            test = test + sayisal[i] + "-";
        }
        return test;
    }


    private void populateWeek(Stats stats) {
        String[] numberarray = stats.getmNumber().replace(" ", "").split("\\-");

        for (int i = 0; i < setSayisal(game.getmType()); i++) {
            sayisal[i] = 0;
        }

        for (int s = 0; s < setSayisal(game.getmType()); s++) {
            sayisal[s] = sayisal[s] + Integer.parseInt(numberarray[s]);
        }

        if (haftaTextView.getText().equals("Haftanın En Çok Çıkan Şanslı Numaraları")) {
            haftaTextView.setText("Haftanın En Az Çıkan Şanslı Numaraları");
            weekAdapter.menu = orderList(sayisal, DESCENDING_ORDER);
        } else {
            haftaTextView.setText("Haftanın En Çok Çıkan Şanslı Numaraları");
            weekAdapter.menu = orderList(sayisal, ASCENDING_ORDER);
        }

        weekAdapter.notifyDataSetChanged();
    }

    private ArrayList<String> orderList(int[] array, boolean asc) {
        ArrayList<String> list = new ArrayList<>();

        int big_number;

        while (list.size() < array.length) {
            if (asc) {
                big_number = -1;
            } else {
                big_number = 10000;
            }
            int current = 0;
            for (int x = 0; x < array.length; x++) {
                if (asc) {
                    if (array[x] > big_number) {
                        //Bu büyük
                        big_number = array[x];
                        current = x;
                    }
                } else {
                    if (array[x] < big_number) {
                        //Bu kiçik
                        big_number = array[x];
                        current = x;
                    }
                }
            }
            if (asc) {
                array[current] = -1;
            } else {
                array[current] = 10000;
            }
            list.add((current + 1) + "");
        }

        return list;
    }


    @OnClick({R.id.haftaTextView, R.id.ayTextView, R.id.tumTextView})
    public void changeList(TextView listname) {
        switch (listname.getId()) {
            case R.id.haftaTextView:
                populateWeek(gameStats.get(game.returnWeek()));
                break;
            case R.id.ayTextView:
                populateMonth(gameStats);
                break;
            case R.id.tumTextView:
                populateTotal(gameStats);
                break;
        }

    }

    @OnClick({R.id.haftaAyrinti, R.id.ayAyrinti, R.id.seneAyrinti})
    public void openStatistics(TextView statsview) {
        Intent intent = new Intent(getActivity(), PlotActivity.class);
        intent.putExtra("game", game.getmType());
        intent.putExtra("gname",Statics.menu.get(index));

        String tmp = "";
        switch (statsview.getId()) {
            case R.id.ayAyrinti:
                intent.putExtra("type", game.getmDate().substring(4, 6));
                tmp = "ay";
                break;
            case R.id.haftaAyrinti:
                intent.putExtra("type", (game.returnWeek() + 1) + "");
                tmp = "hafta";
                break;
            case R.id.seneAyrinti:
                intent.putExtra("type", "1");
                tmp = "sene";
                break;
        }
        intent.putExtra("plot", tmp);
        getActivity().startActivity(intent);
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("index", index);

        super.onSaveInstanceState(savedInstanceState);

    }

    public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.ViewHolder> {
        String[] array;
        boolean test;

        public NumbersAdapter(String[] array, boolean test) {
            this.array = array;
            this.test = test;
        }

        @Override
        public NumbersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.numberitem2, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NumbersAdapter.ViewHolder holder, int position) {
            if (test && position == 5) {
                holder.number.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            } else {
                holder.number.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            }
            holder.number.setText(array[position]);
        }

        @Override
        public int getItemCount() {
            return array.length;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.number2)
            TextView number;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    @OnClick(R.id.randomButtonActivity)
    public void onButtonClick(){
        Intent intent = new Intent(getActivity(), RandomNumberActivity.class);
        intent.putExtra("index", index);
        getActivity().startActivity(intent);
    }

    @OnClick(R.id.detailView)
    public void openDetailActivity(){
        Intent intent = new Intent(getActivity(), ResultsActivity.class);
        intent.putExtra("index", index);
        getActivity().startActivity(intent);
    }
}
