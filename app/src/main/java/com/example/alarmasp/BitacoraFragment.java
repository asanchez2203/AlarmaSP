package com.example.alarmasp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.alarmasp.commands.CommandBitacora;

public class BitacoraFragment extends Fragment {
    public BitacoraFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_bitacora, container, false);
        LinearLayout ly = v.findViewById(R.id.BitacoraLayout);
        CommandBitacora commandBitacora = new CommandBitacora(getActivity(),ly);
        commandBitacora.execute();
        // Inflate the layout for this fragment
        return v;
    }
}