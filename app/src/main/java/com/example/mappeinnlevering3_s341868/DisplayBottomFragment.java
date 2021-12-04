package com.example.mappeinnlevering3_s341868;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class DisplayBottomFragment extends BottomSheetDialogFragment {
    JSONObject jsonObject;
    //Get all id's from bottom fragment
    ImageButton endreBtn;
    TextView txtAddresse,txtEtasjer,txtBeskrivelse;

    public DisplayBottomFragment(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.print("jsonobject in bottomfragment"+jsonObject);
        Log.d("Printer ut Jsonovjer",jsonObject.toString()+"");
        View v = inflater.inflate(R.layout.fragment_bottominfo, container, false);

        txtAddresse = (TextView) v.findViewById(R.id.adresse);
        strukturerInfo(v);
        displayEndreFragment(v,jsonObject);
        /*registrer_etternavn = (EditText) v.findViewById(R.id.registrer_etternavn);
        registrer_tlf = (EditText) v.findViewById(R.id.registrer_tlf);*/
        return v;
    }

    public void strukturerInfo(View v){
        try {
            String getAdresse = jsonObject.getString("adresse");
            String getEtasjer = jsonObject.getString("etasjer");
            String getBeskrivelse = jsonObject.getString("beskrivelse");
            txtAddresse = (TextView) v.findViewById(R.id.adresse);
            txtEtasjer = (TextView) v.findViewById(R.id.etasjer);
            txtBeskrivelse = (TextView) v.findViewById(R.id.beskrivelse);
            txtAddresse.setText(getAdresse);
            txtEtasjer.setText(getEtasjer);
            txtBeskrivelse.setText(getBeskrivelse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void displayEndreFragment(View v, JSONObject jsonObject){
        System.out.println("clicked");
        endreBtn = (ImageButton) v.findViewById(R.id.btnEndre);
        endreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VisEndreFragment visEndreFragment = new VisEndreFragment(jsonObject);
                visEndreFragment.show(getFragmentManager().beginTransaction(),"Endre fragment");
            }
        });
    }


}
