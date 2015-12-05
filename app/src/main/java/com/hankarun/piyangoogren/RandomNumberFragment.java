package com.hankarun.piyangoogren;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RandomNumberFragment extends Fragment {
    private static int[] numbers = {6,6,6,22};
    private static int[] range = {32,49,34,80};

    @Bind(R.id.oneRandom)
    RecyclerView randomRecycler;
    @Bind(R.id.randomNumberList)
    RecyclerView randomList;

    public static RandomNumberFragment newInstance(int index) {
        RandomNumberFragment f = new RandomNumberFragment();
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);
        return f;
    }

    public RandomNumberFragment() {
    }

    int index = 0;
    NumbersAdapter numbersAdapter;
    NumbersAdapter2 numbersAdapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_random_number, container, false);
        ButterKnife.bind(this, rootView);

        Bundle args = getArguments();
        if (args != null)
            index = args.getInt("index", 0);

        randomRecycler.setLayoutManager(new WrappableGridLayoutManager(getContext(), 6));

        numbersAdapter = new NumbersAdapter(createRandom(), Statics.menuOriginal.get(index).equals("sanstopu"));
        randomRecycler.setAdapter(numbersAdapter);
        randomRecycler.setNestedScrollingEnabled(false);

        randomList.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));

        numbersAdapter1 = new NumbersAdapter2(new ArrayList<String>(), Statics.menuOriginal.get(index).equals("sanstopu"));
        randomList.setAdapter(numbersAdapter1);
        randomList.setNestedScrollingEnabled(false);

        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST);
        randomList.addItemDecoration(itemDecoration);


        return rootView;
    }

    private ArrayList<Integer> createRandom(){
        SecureRandom srand = new SecureRandom();
        ArrayList<Integer> tmp = new ArrayList<>();

        while(tmp.size()<numbers[index]){
            Integer temp = (Math.abs(srand.nextInt() % (range[index]-1)))+1;
            if(tmp.indexOf(temp)==-1)
                tmp.add(temp);
        }

        //TODO: Order them
        Collections.sort(tmp);

        return tmp;
    }

    @OnClick(R.id.randomButton)
    public void createRandomButton(){
        String teemo = "";
        for(int x:numbersAdapter.array)
            teemo = teemo + x + "-";
        teemo = teemo.substring(0,teemo.length()-1);
        numbersAdapter1.array.add(teemo);
        numbersAdapter1.notifyDataSetChanged();
        numbersAdapter.array = createRandom();
        numbersAdapter.notifyDataSetChanged();
    }

    public class NumbersAdapter extends RecyclerView.Adapter<NumbersAdapter.ViewHolder> {
        ArrayList<Integer> array;
        boolean test;

        public NumbersAdapter(ArrayList<Integer> array, boolean test) {
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
            holder.number.setText(array.get(position)+"");
        }

        @Override
        public int getItemCount() {
            return array.size();
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

    public class NumbersAdapter2 extends RecyclerView.Adapter<NumbersAdapter2.ViewHolder> {
        ArrayList<String> array;
        boolean test;

        public NumbersAdapter2(ArrayList<String> array, boolean test) {
            this.array = array;
            this.test = test;
        }

        @Override
        public NumbersAdapter2.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.spinner_item, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(NumbersAdapter2.ViewHolder holder, int position) {
            holder.number.setText(array.get(position));
        }

        @Override
        public int getItemCount() {
            return array.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            @Bind(R.id.spintextview)
            TextView number;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
