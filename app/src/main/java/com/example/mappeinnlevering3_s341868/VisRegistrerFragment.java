package com.example.mappeinnlevering3_s341868;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class VisRegistrerFragment extends DialogFragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_registrer, container, false);
        return v;
        //resources = getResources();

        /*txt_fornavn = (EditText) v.findViewById(R.id.txt_edit_kontakt_fornavn);
        txt_fornavn.setText(((Kontakt) kontakt).getName().split(" ")[0]);

        txt_etternavn = (EditText) v.findViewById(R.id.txt_edit_kontakt_etternavn);
        try {
            txt_etternavn.setText(((Kontakt) kontakt).getName().split(" ")[1]);
        }catch (Exception e){}
        txt_tlf = (EditText) v.findViewById(R.id.txt_edit_kontakt_tlf);
        txt_tlf.setText(((Kontakt) kontakt).getTlf());*/
    }
}
