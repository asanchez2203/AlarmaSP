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

public class ChangeAlarmStatus extends AsyncTask<Object,Integer,String> {
    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private String result = "";
    private int opc;

    public ChangeAlarmStatus(Context context, int opc){
        progressDialog = new ModalProgressDialog(context,"Cambiando el estado de la alarma",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.opc = opc;
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
                String value = (opc==0)? "off" : "on";
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.SET_ALARM_ON_OFF +",'"+ value +"',0)",
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
                    Log.println(Log.INFO,"Alarm Status","Value: "+ response);
                }while(response ==null && attemp<=TypeRequest.ATTEMPS_TRY);
                st.close();
                if(response!=null){
                    result = response;
                }
                Log.println(Log.INFO,"Alarm Status","Final value: "+ response);

                cnEnv.close();
                cn.close();
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Change Alarm Stat",
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
        modalDialog.setTitle("Cambio de estado de la alarma");
        progressDialog.hideProgressDialog();
        if(s.equals("-1")) {
            modalDialog.setMessage("Hubo un error cambiar el estado de la Alarma");
            modalDialog.showModalDialog();
        }else{

        }
    }
}
