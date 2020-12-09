package com.example.alarmasp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.alarmasp.commands.GetHumedad;
import com.example.alarmasp.commands.GetIntensidadLuces;
import com.example.alarmasp.commands.GetTemperatura;
import com.example.alarmasp.commands.VerifyStatusLight;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SensoresFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_sensores, container, false);

        String date_n = new SimpleDateFormat("MMM dd, yyyy hh:mm", Locale.getDefault()).format(new Date());

        TextView tvFecha = v.findViewById(R.id.tvFecha);
        TextView temperatura = v.findViewById(R.id.tvTemperatura);
        TextView humedad = v.findViewById(R.id.tvHumedad);
        TextView lucesCasa = v.findViewById(R.id.tvLuces);
        TextView nivelLuz = v.findViewById(R.id.tvNivel);
        //Button guardar = v.findViewById(R.id.btGuardar);

        tvFecha.setText(date_n);
        GetIntensidadLuces getIntensidadLuces = new GetIntensidadLuces(getActivity(),nivelLuz);
        getIntensidadLuces.execute();

        VerifyStatusLight verifyStatusLight = new VerifyStatusLight(getActivity(),lucesCasa);
        verifyStatusLight.execute();

        GetTemperatura getTemperatura = new GetTemperatura(getActivity(),temperatura);
        getTemperatura.execute();

        GetHumedad getHumedad = new GetHumedad(getActivity(),humedad);
        getHumedad.execute();
        // Inflate the layout for this fragment
        return v;
    }
}