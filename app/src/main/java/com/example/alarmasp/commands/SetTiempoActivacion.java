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

public class SetTiempoActivacion extends AsyncTask<Void,Integer,String> {
    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private String lightIntensity = "-1";
    private EditText edTiempoActivacion;

    public SetTiempoActivacion(Context context, EditText ed){
        progressDialog = new ModalProgressDialog(context,"Cambiando el tiempo de activación",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.edTiempoActivacion = ed;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected String doInBackground(Void... voids) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Estatus de Luces OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();
                int intensity = Integer.parseInt(edTiempoActivacion.getText().toString());
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.SET_TIME_TO_ACTIVATE +",'"+ intensity +"',0)",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet keyGenerated = st.getGeneratedKeys();
                keyGenerated.next();
                int idRequest = keyGenerated.getInt(1);
                Log.println(Log.INFO,"Tiempo de Activacion","Request Number: "+ idRequest);
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
                    Log.println(Log.INFO,"Tiempo de Activacion","Value: "+ response);
                }while(response ==null && attemp<=TypeRequest.ATTEMPS_TRY);
                st.close();
                if(response!=null){
                    lightIntensity = response;
                }
                Log.println(Log.INFO,"Tiempo de Activacion","Final value: "+ response);

                cnEnv.close();
                cn.close();
                return lightIntensity;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Tiempo de Acti",
                        throwables.getMessage());
                lightIntensity = "-1";
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            lightIntensity = "-1";
        }
        return lightIntensity;
    }

    @Override
    protected void onPostExecute(String s) {
        modalDialog.setTitle("Intensidad de las luces");
        progressDialog.hideProgressDialog();
        if(lightIntensity.equals("-1")) {
            modalDialog.setMessage("Hubo un error al cambiar la intensidad de las luces");
            modalDialog.showModalDialog();
        }else{
            //edIntensidadLuces.setText(lightIntensity);
        }
    }
}
