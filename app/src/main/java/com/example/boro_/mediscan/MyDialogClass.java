package com.example.boro_.mediscan;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class MyDialogClass extends Dialog implements
        android.view.View.OnClickListener {

    public Activity caller;
    public Button yes;
    public TextView dialog_text,dialog_disclaimer;

    public MyDialogClass(Activity caller) {
        super(caller);
        this.caller = caller;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialogbox);
        yes = (Button) findViewById(R.id.btn_yes);
        dialog_text = (TextView) findViewById(R.id.txt_dialog_text);
        dialog_disclaimer = (TextView) findViewById(R.id.txt_dialog_disclaimer);
        yes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes: //Använderen har godkänt disclaimern
                dismiss();
                //Skapa en variabel som sparas så användaren inte behöver se detta meddelande hela tiden.
                break;
            default: //Safeswitch, stänger ner appen, användaren måste clicka JA/YES
                caller.finish();
                break;
        }
    }
}