package com.example.alarmasp.utils;

import android.app.ProgressDialog;
import android.content.Context;

public class ModalProgressDialog {

    private ProgressDialog dialog;


    public ModalProgressDialog(Context context, String title, String Message, int typeDialog){
        dialog = new ProgressDialog(context);
        dialog.setMessage(Message);
        dialog.setTitle(title);
        dialog.setCancelable(false);
        dialog.setProgressStyle(typeDialog);
    }

    public void showModalProgressDialog(){
        dialog.show();
    }

    public void hideProgressDialog(){
        if(dialog.isShowing()) dialog.dismiss();
    }
}
