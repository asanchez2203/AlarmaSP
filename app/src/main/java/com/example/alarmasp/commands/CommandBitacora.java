package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;
import com.example.alarmasp.values.TypeRequest;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class CommandBitacora extends AsyncTask <Void,String,String> {
    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private String result = "";
    private LinearLayout ly;
    ArrayList<String> data;
    Context context;

    public CommandBitacora(Context context, LinearLayout ly){
        progressDialog = new ModalProgressDialog(context,"Obteniendo Registros de Bitácora",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.ly = ly;
        data = new ArrayList<>();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected String doInBackground(Void... objects) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Bitacora OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();

                ResultSet keyGenerated = st.executeQuery("SELECT t.idResponse,valueRes,c.typeRequest from Table_Response t\n" +
                        "INNER JOIN Table_Request c on c.idRequest = t.idRequest\n" +
                        "where t.valueRes!='ok' and t.valueRes!='null' order by t.idRequest desc limit 20");
                while(keyGenerated.next()){
                    String s = "N. Req. " + keyGenerated.getInt("idResponse") + getRequestType(keyGenerated.getInt("typeRequest")) +" value: " +keyGenerated.getString("valueRes");
                    data.add(s);
                    result="ok";
                }
                st.close();

                cnEnv.close();
                cn.close();
                return result;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Bitacora",
                        throwables.getMessage());
                result = "-1";
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Bitácora FAIL");
            result = "-1";
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        modalDialog.setTitle("Bitacora");
        progressDialog.hideProgressDialog();
        if(s.equals("-1")) {
            modalDialog.setMessage("Hubo un error al obtener la bitácora");
            modalDialog.showModalDialog();
        }else{
            for(String st: data){
                TextView tv = new TextView(context);
                tv.setText(st);
                ly.addView(tv);
            }
        }
    }

    private String getRequestType(int type){
        switch (type){
            case TypeRequest.SET_ALARM_ON_OFF:
                return " Cambio del estado de alarma ";
            case TypeRequest.SET_LIGHT_ON_OFF:
                return " Cambio del estado de las luces ";
            case TypeRequest.SET_TIME_TO_ACTIVATE:
                return " Cambio en tiempo de activacion ";
            case TypeRequest.SET_LIGHT_DIMMER:
                return " Cambio de intensidad de las luces ";
            case TypeRequest.GET_LIGHT_DIMMER:
                return " Consulta de intensidad de las luces ";
            case TypeRequest.GET_TIME_TO_ACTIVATE:
                return " Consulta del tiempo de activacion ";
            case TypeRequest.GET_LIGHT_STATUS:
                return " Consulta del estado de las luces ";
            case TypeRequest.GET_HUMIDITY:
                return " Consulta del la humedad ";
            case TypeRequest.GET_TEMPERATURE:
                return " Consulta del la temperatura ";
            default: return "  ";
        }
    }
}
