package com.example.alarmasp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alarmasp.commands.ChangeAlarmStatus;
import com.example.alarmasp.commands.VerifyAlarmaStatusFragment;
import com.example.alarmasp.commands.VerifyDatabaseConnection;
import com.example.alarmasp.values.AlarmValues;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EstadoFragment extends Fragment {

    VerifyAlarmaStatusFragment  verifyAlarmaStatusFragment;
    Handler handler;
    int accion = -1;
    ChangeAlarmStatus changeAlarmStatus;
    ImageView img;
    TextView status;
    TextView comntSuceso;

    public EstadoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_estado, container, false);

        TextView fecha = v.findViewById(R.id.tvFecha); //TextView para la fecha
        comntSuceso = v.findViewById(R.id.tvSuceso); //TextView para notificar sucesos
        status = v.findViewById(R.id.tvStatus); //Alarma prendida - Alarma apagada
        img = v.findViewById(R.id.ivImagen);

        String date_n = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date());
        fecha.setText(date_n);

        handler = new Handler();
        verifyAlarmaStatusFragment = new VerifyAlarmaStatusFragment(getActivity(),img,status,comntSuceso);
        verifyAlarmaStatusFragment.execute();


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "";
                Integer integer = (Integer) img.getTag();
                switch (integer){
                    case R.drawable.ic_baseline_alarma_off_80:
                        accion = AlarmValues.TURN_ON_ALARM;
                        message="¿Está seguro de activar la alarma?";
                        break;
                    case R.drawable.ic_baseline_alarma_ok_80:
                        accion = AlarmValues.SHUTOFF_ALARM;
                        message="¿Está seguro de apagar la alarma?";
                        break;
                    case R.drawable.ic_baseline_info_80:
                        accion = AlarmValues.SHUTOFF_ALARM;
                        message="¿Está seguro de apagar la alarma?";
                        break;
                    default : accion = -1; break;
                }

                AlertDialog.Builder builder;
                AlertDialog alert;
                builder = new AlertDialog.Builder(getActivity());

                builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changeAlarmStatus= new ChangeAlarmStatus(getActivity(),accion);
                        changeAlarmStatus.execute();
                        verifyAlarmaStatusFragment= new VerifyAlarmaStatusFragment(getActivity(),img,status,comntSuceso);
                        verifyAlarmaStatusFragment.execute();
                    }
                });
                builder.setNegativeButton("No",null);
                alert = builder.create();
                alert.setTitle("Confirmación");
                alert.setMessage(message);
                alert.show();
            }
        });


        // Inflate the layout for this fragment
        return v;
    }
}