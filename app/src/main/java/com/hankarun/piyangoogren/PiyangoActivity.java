package com.hankarun.piyangoogren;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PiyangoActivity extends AppCompatActivity implements PiyangoFragment.OnFragmentInteractionListener, TicketDialog.DialogReturn {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    PiyangoFragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_piyango);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Milli Piyango Sonuçları");

        fragment = PiyangoFragment.newInstance("","");
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment2, fragment,"piyango")
                .commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PiyangoFragment myFragment = (PiyangoFragment) getSupportFragmentManager().findFragmentById(R.id.fragment2);
                TicketDialog dialog = new TicketDialog(PiyangoActivity.this,!myFragment.returnFirstElement().equals("6"));
                dialog.show();
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void dialogFinished(String numbers) {
        fragment.findNumber(numbers);
    }
}
