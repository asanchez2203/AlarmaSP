package com.example.alarmasp.commands;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alarmasp.utils.Connection;
import com.example.alarmasp.utils.ModalDialog;
import com.example.alarmasp.utils.ModalProgressDialog;
import com.example.alarmasp.values.TypeRequest;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.Statement;

public class VerifyStatusLight extends AsyncTask<Object,Integer,Integer> {

    private Connection cn;
    private ModalProgressDialog progressDialog;
    private ModalDialog modalDialog;
    private int lightStatus = -1;
    private Switch aSwitch;
    private TextView textView;

    public VerifyStatusLight(Context context, Switch aSwitch){
        progressDialog = new ModalProgressDialog(context,"Verificando el estado de las luces",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.aSwitch = aSwitch;
    }
    public VerifyStatusLight(Context context, TextView textView){
        progressDialog = new ModalProgressDialog(context,"Verificando el estado de las luces",
                "Por favor espere", ProgressDialog.STYLE_SPINNER);
        modalDialog= new ModalDialog(context);
        this.textView = textView;
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
            Log.println(Log.INFO,"MySQLConnection","Conexión para Estatus de Luces OK");
            try {
                //Inserting Request
                Statement st = cnEnv.createStatement();
                st.executeUpdate("INSERT INTO Table_Request (typeRequest,valueReq,attended) VALUES ("+ TypeRequest.GET_LIGHT_STATUS +",'',0)",
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
                    if(response.equalsIgnoreCase("true")) lightStatus = 1;
                    else lightStatus = 0;
                }
                Log.println(Log.INFO,"Light Status","Final value: "+ response);

                cnEnv.close();
                cn.close();
                return lightStatus;
            } catch (Exception throwables) {
                Log.println(Log.ERROR,"Fail Get Light Status",
                        throwables.getMessage());
                lightStatus = -1;
            }
        }else{
            Log.println(Log.ERROR,"MySQLConnection","Conexión para Estatus de Alarma FAIL");
            lightStatus = -1;
        }
        return lightStatus;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        modalDialog.setTitle("Estado de las luces");
        progressDialog.hideProgressDialog();
        if(lightStatus == -1) {
            modalDialog.setMessage("Hubo un error al obtener el estado de las luces");
            modalDialog.showModalDialog();
        }else{
            if(lightStatus==1) {
                if(aSwitch!=null) aSwitch.setChecked(true);
                if(textView!=null) textView.setText("Encendidas");
            }
            if(lightStatus ==0) {
                if(aSwitch!=null) aSwitch.setChecked(false);
                if(textView!=null) textView.setText("Apagadas");
            }
        }

    }
}
