package com.hankarun.piyangoogren;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TicketDialog extends Dialog implements View.OnClickListener {

    @Bind(R.id.editText) EditText edit1;
    @Bind(R.id.editText2) EditText edit2;
    @Bind(R.id.editText3) EditText edit3;
    @Bind(R.id.editText4) EditText edit4;
    @Bind(R.id.editText5) EditText edit5;
    @Bind(R.id.editText6) EditText edit6;
    @Bind(R.id.editText7) EditText edit7;

    List<EditText> nameViews;

    @Bind(R.id.button)
    Button mSaveButton;
    @Bind(R.id.button2)
    Button mCancelButton;

    boolean type;

    public interface DialogReturn{
        void dialogFinished(String numbers);
    }

    private DialogReturn dialogReturn;

    Context context;

    public TicketDialog(Context context, boolean _type) {
        super(context);
        this.context = context;
        type = _type;

        //getWindow().getCurrentFocus();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilet_layout);
        ButterKnife.bind(this);

        if (context instanceof DialogReturn) {
            dialogReturn = (DialogReturn) context;
        } else {
            throw new RuntimeException(getContext().toString()
                    + " must implement OnFragmentInteractionListener");
        }

        nameViews = new ArrayList<>();
        nameViews.add(edit1);
        nameViews.add(edit2);
        nameViews.add(edit3);
        nameViews.add(edit4);
        nameViews.add(edit5);
        nameViews.add(edit6);
        if(type) {
            nameViews.add(edit7);
        }else{
            edit7.setVisibility(View.GONE);
        }

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
        if(((Button) v).getText().equals("Search")){
            String temp = "";
            for(EditText e:nameViews)
                temp += e.getText();
            dialogReturn.dialogFinished(temp);
        }
        dismiss();
    }
}
