package com.hankarun.piyangoogren;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TicketDialog extends Dialog implements View.OnClickListener {

    @Bind({ R.id.editText, R.id.editText2, R.id.editText3,
            R.id.editText4, R.id.editText5, R.id.editText6, R.id.editText7})
    List<EditText> nameViews;

    @Bind(R.id.button)
    Button mSaveButton;
    @Bind(R.id.button2)
    Button mCancelButton;

    boolean type;

    public TicketDialog(Context context, boolean _type) {
        super(context);
        type = _type;

        //getWindow().getCurrentFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilet_layout);
        ButterKnife.bind(this);


        mSaveButton.setOnClickListener(this);
        mCancelButton.setOnClickListener(this);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getOwnerActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        TextWatcher watcher = new TextWatcher() {
            public void onTextChanged(CharSequence s, int start,int before, int count)
            {
                int current = nameViews.indexOf(getWindow().getCurrentFocus());
                if(current+1<nameViews.size())
                    nameViews.get(current+1).requestFocus();
                else
                    mSaveButton.requestFocus();
            }
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };

        for(EditText e:nameViews)
            e.addTextChangedListener(watcher);
    }


    @Override
    public void onClick(View v) {

    }
}
