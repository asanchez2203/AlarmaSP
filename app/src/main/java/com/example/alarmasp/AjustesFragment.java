package com.example.alarmasp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.example.alarmasp.commands.GetIntensidadLuces;
import com.example.alarmasp.commands.GetTiempoActivacion;
import com.example.alarmasp.commands.SetIntensidadLuces;
import com.example.alarmasp.commands.SetStatusLight;
import com.example.alarmasp.commands.SetTiempoActivacion;
import com.example.alarmasp.commands.VerifyStatusLight;
import com.example.alarmasp.utils.Preferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AjustesFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Preferences preferences = new Preferences(getActivity());
        View v = inflater.inflate(R.layout.fragment_ajustes, container, false);
        EditText tiempoActivacion =  v.findViewById(R.id.etTiempo); //Tiempo de activacion de la alarma
        Switch notificacion = v.findViewById(R.id.swNotificacion); //0 1 de notificacion
        Switch luces = v.findViewById(R.id.swLuces); //Prender o apagar luces
        Switch copiaSeguridad = v.findViewById(R.id.swCopia); // 0 1 a la copia de seguridad
        TextView fecha = v.findViewById(R.id.tvFecha);
        EditText nivelLuz =  v.findViewById(R.id.etNivel); //Nivel de la luz
        Button guardar = v.findViewById(R.id.btActualizar);

        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        fecha.setText(date_n);

        notificacion.setChecked(preferences.getNotificationEnabled());
        notificacion.setOnCheckedChangeListener((buttonView, isChecked) -> preferences.setNotificationEnabled(isChecked));

        copiaSeguridad.setChecked(preferences.getSecurityCopyEnabled());
        copiaSeguridad.setOnCheckedChangeListener((buttonView, isChecked) -> preferences.setSecurityCopyEnabled(isChecked));

        VerifyStatusLight  verifyStatusLight = new VerifyStatusLight(getActivity(),luces);
        verifyStatusLight.execute();

        GetIntensidadLuces getIntensidadLuces = new GetIntensidadLuces(getActivity(),nivelLuz);
        getIntensidadLuces.execute();

        GetTiempoActivacion getTiempoActivacion = new GetTiempoActivacion(getActivity(),tiempoActivacion);
        getTiempoActivacion.execute();

        luces.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SetStatusLight setStatusLight = new SetStatusLight(getActivity(),luces);
                setStatusLight.execute();
            }
        });

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SetTiempoActivacion setTiempoActivacion = new SetTiempoActivacion(getActivity(),tiempoActivacion);
                setTiempoActivacion.execute();

                SetIntensidadLuces setIntensidadLuces = new SetIntensidadLuces(getActivity(),nivelLuz);
                setIntensidadLuces.execute();
            }
        });

        // Inflate the layout for this fragment
        return v;
    }
}