package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;
import com.example.alarmasp.values.TypeRequest;

import java.sql.ResultSet;
import java.sql.Statement;

public class GetTemperatura extends AsyncTask<Object,Integer,Integer> {
    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private int lightIntensity = -1;
    private TextView tvTemp;

    public GetTemperatura(Context context, TextView edLightIntensity){
        progressDialog = new ModalProgressDialog(context,"Verificando la Temperatura",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.tvTemp = edLightIntensity;
    }

    @Override
    protected void onPreExecute() {
        progressDialog.showModalProgressDialog();
    }

    @Override
    protected Integer doInBackground(Object... objects) {
        cn = new Connection();
        java.sql.Connection cnEnv = cn.connect();
        if(cnEnv!=null){
            Log.println(Log.INFO,"MySQLConnection","Conexión para Temperatura OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.GET_TEMPERATURE +",'',0)",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet keyGenerated = st.getGeneratedKeys();
                keyGenerated.next();
                int idRequest = keyGenerated.getInt(1);
                Log.println(Log.INFO,"Temperature","Request Number: "+ idRequest);
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
                    Thread.sleep(2000);
                    Log.println(Log.INFO,"Temperature","Value: "+ response);
                }while(response ==null);
                st.close();
                if(response!=null || !response.equals("None")){
                    lightIntensity = (int)Double.parseDouble(response);
                }else lightIntensity = -1;
                Log.println(Log.INFO,"Temperature","Final value: "+ response);

                cnEnv.close();
                cn.close();
                return lightIntensity;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Get Temperature",
                        throwables.getMessage());
                lightIntensity = -1;
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            lightIntensity = -1;
        }
        return lightIntensity;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        modalDialog.setTitle("Temperature");
        progressDialog.hideProgressDialog();
        if(lightIntensity == -1) {
            modalDialog.setMessage("Hubo un error al obtener la Temperature");
            modalDialog.showModalDialog();
            tvTemp.setText("None");
        }else{
            tvTemp.setText(String.valueOf(lightIntensity) + " grados");
        }
    }
}
