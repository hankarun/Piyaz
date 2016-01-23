package com.hankarun.piyangoogren;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.XAxisValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.hankarun.piyangoogren.Statics.setSayisal;

public class PlotActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {
    @Bind(R.id.graphLayout)
    LinearLayout layout;
    @Bind(R.id.chart)
    BarChart chart;
    @Bind(R.id.spinner)
    Spinner spinner;
    int order = 1;
    String plot = "";
    String gtype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setTitle("İstatistikler");

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gtype = extras.getString("game");
            order = Integer.parseInt(extras.getString("type"))-1;
            plot = extras.getString("plot");
            getSupportActionBar().setTitle(Statics.menu.get(extras.getInt("gname"))+" Sonuçları");
        }

        ArrayList<String> spinnerArray = returnSpinnerItems(plot);

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);

        Log.d("type", order + "");
        spinner.setSelection(order);
        spinner.setOnItemSelectedListener(this);

        getLoaderManager().initLoader(0, extras, this);
    }

    private ArrayList<String> returnSpinnerItems(String plot) {
        ArrayList<String> tmp = new ArrayList<>();

        switch (plot){
            case "hafta":
                for(int x = 1; x < 52; x++)
                    tmp.add(x+".Hafta");
                break;
            case "ay":
                tmp.add("Ocak");
                tmp.add("Şubat");
                tmp.add("Mart");
                tmp.add("Nisan");
                tmp.add("Mayıs");
                tmp.add("Haziran");
                tmp.add("Temmuz");
                tmp.add("Ağustos");
                tmp.add("Eylül");
                tmp.add("Ekim");
                tmp.add("Kasım");
                tmp.add("Aralık");
                break;
            case "sene":
                break;
        }

        return tmp;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String gameName = args.getString("game");
        return new CursorLoader(this, GamesContentProvider.CONTENT_URI_STATS,
                StatisticsDatabaseHelper.STATICS_PROJECTION,
                StatisticsDatabaseHelper.STATISTICS_TYPE + " = '" + gameName + "'",
                null,
                StatisticsDatabaseHelper.STATISTICS_GAME + " ASC");    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            ArrayList<Stats> gameStats = new ArrayList<>();
            gameStats.add(Stats.fromCursor(data));
            //Conver cursor to stat array
            while (data.moveToNext()) {
                gameStats.add(Stats.fromCursor(data));
            }
            if(plot.equals("ay")){
                compressArrayToMonths(gameStats);
            }
            if(plot.equals("sene")){
                compressArrayToYear(gameStats);
            }
            //TODO new compressed statsarray
            createPlot(gameStats);
        }
    }

    private void compressArrayToYear(ArrayList<Stats> gameStats) {
        for(int x = 0; x < gameStats.size()-1; x++){
            gameStats.get(0).setmNumber(addTwoStringArrayToFirst(gameStats.get(0).getmNumber(), gameStats.get(1).getmNumber()));
            gameStats.remove(1);
        }
    }

    /*
    * 1 - 1,2,3,4 - 4
    * 2 - 5,6,7,8 - 4
    * 3 - 8,9,10,11 - 4
    * 4 - 12,13,14,15 - 4
    * 5 - 16,17,18,19,20 - 5
    * 6 - 21,22,23,24 - 4
    * 7 - 25,26,27,28 - 4
    * 8 - 29,30,31,32,33 - 5
    * 9 - 34,35,36,37,38 - 5
    * 10 - 39,40,41,42 - 4
    * 11 - 43,44,45,46,47 - 5
    * 12 - 48,49,50,51 - 4
    * */
    int[] weeks_months = {3,3,3,3,4,3,3,4,4,3,4,3};
    private void compressArrayToMonths(ArrayList<Stats> gameStats) {
        //Create temp array
        for(int s = 0; s < 12; s++){
            for(int r = 0; r < weeks_months[s]; r++){
                gameStats.get(s).setmNumber(addTwoStringArrayToFirst(gameStats.get(s).getmNumber(), gameStats.get(s + 1).getmNumber()));
                gameStats.remove(s+1);
            }
        }
        //Convert number string to int array
        //Add desired months to each other
        //Convert to string and add gamestats
    }

    private String addTwoStringArrayToFirst(String a, String b){
        String[] numberarray1 = a.replace(" ", "").split("\\-");
        String[] numberarray2 = b.replace(" ", "").split("\\-");
        int[] sayisal = new int[setSayisal(gtype)];

        for (int i = 0; i < setSayisal(gtype); i++) {
            sayisal[i] = Integer.parseInt(numberarray1[i]);
        }

        for (int s = 0; s < setSayisal(gtype); s++) {
            sayisal[s] = sayisal[s] + Integer.parseInt(numberarray2[s]);
        }

        String test = "";
        for (int i = 0; i < setSayisal(gtype); i++) {
            test = test + sayisal[i] + "-";
        }
        return test;

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void createPlot(ArrayList<Stats> statsArrayList){

        Stats tmp = statsArrayList.get(order);
        String[] numberarray = tmp.getmNumber().replace(" ", "").split("\\-");


        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.setDescription("");

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        Typeface mTf = Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC);

        /*YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(true);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(7, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setValueFormatter(new MyYAxisValueFormatter());

        /*Legend l = chart.getLegend();
        l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);*/

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawLabels(true);
        xAxis.setLabelsToSkip(0);
        xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());


        ArrayList<String> xVals = new ArrayList<>();
        for (int i = 0; i < numberarray.length; i++) {
            xVals.add((i+1)+"");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for (int i = 0; i < numberarray.length; i++) {
            yVals1.add(new BarEntry(Integer.parseInt(numberarray[i]), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Sayıların daha önce çıktıkları miktari gösterir.");
        set1.setBarSpacePercent(35f);

        ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(8f);
        data.setValueTypeface(mTf);

        chart.setData(data);
        data.notifyDataChanged();
        chart.notifyDataSetChanged();
        chart.callOnClick();

    }

    private ArrayList<Tuple> orderList(int[] array, boolean asc) {
        ArrayList<Tuple> list = new ArrayList<>();

        int big_number;

        while (list.size() < array.length) {
            if (asc) {
                big_number = -1;
            }
            else {
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
            Log.d((current+1)+" - ", array[current]+"");
            list.add(new Tuple(current+1,array[current]));
            if(asc) {
                array[current] = -1;
            }
            else {
                array[current] = 10000;
            }
        }

        return list;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        order = position;
        Bundle extras = new Bundle();
        extras.putString("game",gtype);
        getLoaderManager().initLoader(0, extras, this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class Tuple{
        int name;
        int count;

        public Tuple(int name, int count){
            this.name = name;
            this.count = count;
        }
    }

    public class MyCustomXAxisValueFormatter implements XAxisValueFormatter {

        public MyCustomXAxisValueFormatter() {
            // maybe do something here or provide parameters in constructor
        }

        @Override
        public String getXValue(String original, int index, ViewPortHandler viewPortHandler) {

            //Log.i("TRANS", "x: " + viewPortHandler.getTransX() + ", y: " + viewPortHandler.getTransY());

            // e.g. adjust the x-axis values depending on scale / zoom level

                return original;
        }
    }
}
