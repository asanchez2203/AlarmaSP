package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;
import com.example.alarmasp.values.TypeRequest;

import java.sql.ResultSet;
import java.sql.Statement;

public class SetStatusLight extends AsyncTask<Object,String,String> {
    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private String result = "";
    private Switch aSwitch;

    public SetStatusLight(Context context, Switch aSwitch){
        progressDialog = new ModalProgressDialog(context,"Aplicando el estado de las luces",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.aSwitch = aSwitch;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected String doInBackground(Object... objects) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Estatus de Luces OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();
                String currentStatus = (aSwitch.isChecked())? "on" : "off";
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.SET_LIGHT_ON_OFF +",'"+ currentStatus +"',0)",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet keyGenerated = st.getGeneratedKeys();
                keyGenerated.next();
                int idRequest = keyGenerated.getInt(1);
                Log.println(Log.INFO,"Light Status","Request Number: "+ idRequest);
                st.close();

                String response = null;
                int attemp = 1;
                do {
                    st = cnEnv.createStatement();
                    ResultSet rs = st.executeQuery("SELECT valueRes FROM Table_Response WHERE idRequest = " + idRequest);
                    if(rs.next()){
                        response = rs.getString("valueRes");
                    }
                    rs.close();
                    attemp++;
                    Thread.sleep(1000);
                    Log.println(Log.INFO,"Light Status","Value: "+ response);
                }while(response ==null && attemp<=TypeRequest.ATTEMPS_TRY);
                st.close();
                if(response!=null){
                    result = response;
                }
                Log.println(Log.INFO,"Light Status","Final value: "+ response);

                cnEnv.close();
                cn.close();
                return result;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Get Light Status",
                        throwables.getMessage());
                result = "-1";
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            result = "-1";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        modalDialog.setTitle("Estado de las luces");
        progressDialog.hideProgressDialog();
        if(s.equals("-1")) {
            modalDialog.setMessage("Hubo un error al cambiar el estado de las luces");
            modalDialog.showModalDialog();
        }else{
            if(!s.equals("ok")) {
                aSwitch.setChecked(!aSwitch.isChecked());
            }
        }
    }
}
