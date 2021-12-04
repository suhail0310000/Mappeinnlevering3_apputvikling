package com.example.mappeinnlevering3_s341868;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

public class VisEndreFragment extends DialogFragment {
    JSONObject jsonObject;
    TextView txtEtasjer,txtBeskrivelse,txtAdresse;
    Button oppdaterBtn;


    public VisEndreFragment(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Jsonobject i endre fragment: ",jsonObject+ "");
        View v = inflater.inflate(R.layout.fragment_endre, container, false);
        oppdaterInfo(v);
        return v;
    }

    public void oppdaterInfo(View v) {
        txtAdresse = (TextView) v.findViewById(R.id.endre_txtAdresse);
        txtEtasjer = (TextView) v.findViewById(R.id.endre_etasjer);
        txtBeskrivelse = (TextView) v.findViewById(R.id.endre_beskrivelse);
        try {
            String getAddresse = jsonObject.getString("adresse");
            String getEtasjer = jsonObject.getString("etasjer");
            String getBeskrivelse = jsonObject.getString("beskrivelse");
            txtAdresse.setText(getAddresse);
            txtEtasjer.setText(getEtasjer);
            txtBeskrivelse.setText(getBeskrivelse);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        oppdaterBtn = (Button) v.findViewById(R.id.btnendreInfo);
        oppdaterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Clicked","clicked on click listener");

            }
        });
    }
}
