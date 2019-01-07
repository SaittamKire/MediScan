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
    public Dialog diag;
    public Button yes, lang;
    public TextView dialog_text,dialog_disclaimer;
    public boolean english = false;

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
        lang = (Button) findViewById(R.id.btn_lang);
        dialog_text = (TextView) findViewById(R.id.txt_dialog_text);
        dialog_disclaimer = (TextView) findViewById(R.id.txt_dialog_disclaimer);
        yes.setOnClickListener(this);
        lang.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes: //Använderen har godkänt disclaimern
                dismiss();
                //Skapa en variabel som sparas så användaren inte behöver se detta meddelande hela tiden. Spara också vilket språk användaren vill använda direkt här.
                break;
            case R.id.btn_lang:  //Användaren vill byta språk, sätter variablerna till EN/SV respektive
                if (!english) {
                    yes.setText(R.string.dialog_text_ok_en);
                    dialog_disclaimer.setText(R.string.dialog_text_disclaimer_en);
                    dialog_text.setText(R.string.dialog_text_en);
                    lang.setBackgroundResource(R.drawable.sweden_icon);
                    lang.setText("På Svenska");
                    english = true;
                }
                else{
                    yes.setText(R.string.dialog_text_ok_sv);
                    dialog_disclaimer.setText(R.string.dialog_text_disclaimer_sv);
                    dialog_text.setText(R.string.dialog_text_sv);
                    lang.setBackgroundResource(R.drawable.unitedkingdom_icon);
                    lang.setText("In English");
                    english = false;
                }
                break;
            default: //Safeswitch, stänger ner appen, användaren måste clicka JA/YES
                caller.finish();
                break;
        }
    }
}