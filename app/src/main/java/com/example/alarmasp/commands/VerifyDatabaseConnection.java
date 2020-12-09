package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;

public class VerifyDatabaseConnection extends AsyncTask<Void,Integer,Boolean> {

    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;

    public VerifyDatabaseConnection(Context context){
        progressDialog = new ModalProgressDialog(context,"Verificando Conexión con Base de Datos",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
    }

    @Override
    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        cn = new Connection();
        if(cn.connect()!=null){
            cn.close();
            return true;
        }
        else
            return false;
    }

    @Override
    protected void onPostExecute(Boolean serviceResponse) {
        modalDialog.setTitle("Estado de la Conexión");
        progressDialog.hideProgressDialog();
        if(serviceResponse) {
            modalDialog.setMessage("Conexión Exitosa");
        }else{
            modalDialog.setMessage("Conexión Fallida");
        }
        modalDialog.showModalDialog();
    }
}