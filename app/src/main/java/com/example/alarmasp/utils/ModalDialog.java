package com.example.alarmasp.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ModalDialog {

    private AlertDialog.Builder builder;
    private AlertDialog alert;

    public ModalDialog(Context context, String title, String message){
        builder = new AlertDialog.Builder(context);
        alert = builder.create();
        alert.setTitle(title);
        alert.setMessage(message);
        addButtonModalDialog();
    }

    public ModalDialog(Context context){
        builder = new AlertDialog.Builder(context);
        addButtonModalDialog();
        alert = builder.create();
    }

    private void addButtonModalDialog(){
        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
    }

    public void showModalDialog(){
        alert.show();
    }

    public void setTitle(String title){
        alert.setTitle(title);
    }

    public void setMessage(String message){
        alert.setMessage(message);
    }
}
