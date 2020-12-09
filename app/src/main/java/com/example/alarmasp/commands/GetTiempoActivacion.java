package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;
import com.example.alarmasp.values.TypeRequest;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetTiempoActivacion extends AsyncTask<Object,Integer,Integer> {

    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private int timeActivation = -1;
    private EditText edTimeActivtion;

    public GetTiempoActivacion(Context context, EditText ed){
        progressDialog = new ModalProgressDialog(context,"Verificando el tiempo de activación",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.edTimeActivtion = ed;
    }

    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected Integer doInBackground(Object... objects) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Estatus de Luces OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.GET_TIME_TO_ACTIVATE +",'',0)",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet keyGenerated = st.getGeneratedKeys();
                keyGenerated.next();
                int idRequest = keyGenerated.getInt(1);
                Log.println(Log.INFO,"Tiempo de activación","Request Number: "+ idRequest);
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
                    Log.println(Log.INFO,"Tiempo de activación","Value: "+ response);
                }while(response ==null && attemp<=TypeRequest.ATTEMPS_TRY);
                st.close();
                if(response!=null){
                    timeActivation = Integer.parseInt(response);
                }
                Log.println(Log.INFO,"Tiempo de activación","Final value: "+ response);

                cnEnv.close();
                cn.close();
                return timeActivation;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Get Light Status",
                        throwables.getMessage());
                timeActivation = -1;
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            timeActivation = -1;
        }
        return timeActivation;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        modalDialog.setTitle("Intensidad de las luces");
        progressDialog.hideProgressDialog();
        if(timeActivation == -1) {
            modalDialog.setMessage("Hubo un error al obtener el tiempo de activación");
            modalDialog.showModalDialog();
        }else{
            edTimeActivtion.setText(String.valueOf(timeActivation));
        }
    }
}
