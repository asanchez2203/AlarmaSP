package com.example.alarmasp.utils;

import android.os.StrictMode;
import android.util.Log;

import com.example.alarmasp.values.ConnectionValues;

import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private java.sql.Connection connection;
    public Connection(){
        //Empty Construct
    }

    public java.sql.Connection connect(){
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(ConnectionValues.URL,ConnectionValues.DATABASE_USER,
                    ConnectionValues.DATABASE_PASSWORD);
            return connection;
        }catch(Exception ex){
            ex.printStackTrace();
            Log.println(Log.ERROR, "SQL Exception", ex.getMessage());
            return null;
        }
    }

    public void close(){
        try {
            connection.close();
        } catch (SQLException e) {
            Log.println(Log.ERROR,"SQL Exception", e.getMessage());
        }
    }

    private void threadPolicy(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public java.sql.Connection getConnection() {
        return connection;
    }

    public void setConnection(java.sql.Connection connection) {
        this.connection = connection;
    }

}
