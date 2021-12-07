package com.example.mappeinnlevering3_s341868;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class DisplayBottomFragment extends BottomSheetDialogFragment {
    JSONObject jsonObject;
    Marker marker;
    //Get all id's from bottom fragment
    ImageButton endreBtn, slettBtn;
    TextView txtAddresse,txtEtasjer,txtBeskrivelse;

    public DisplayBottomFragment(JSONObject jsonObject,Marker marker) {
        this.jsonObject = jsonObject;
        this.marker = marker;
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
        slettBtn = (ImageButton) v.findViewById(R.id.slettBtn);
        strukturerInfo(v);
        displayEndreFragment(v,jsonObject);
        slettObject(v,jsonObject);
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
                VisEndreFragment visEndreFragment = new VisEndreFragment(jsonObject, marker);
                visEndreFragment.show(getFragmentManager().beginTransaction(),"Endre fragment");
                dismiss();
            }
        });
    }

    public void slettObject(View v, JSONObject jsonObject){
        slettBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.print("clicked");
                Log.d("","clicked");
                visAlert();
            }
        });
    }

    public void visAlert(){
        new AlertDialog.Builder(getContext())
                .setTitle("Ønsker du å slette huset?")
                .setMessage("Trykk på 'Ja', dersom du ønsker å slette info om huset i databasen.")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        System.out.println("trukket på ja");
                        postReq();
                        dismiss();
                    }
                })
                .setNegativeButton("Nei", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                }).show();
    }

    public void postReq(){
        try {
            String innId = jsonObject.getString("id");
            Log.d("ID",innId+"");
            getJSON task = new getJSON();
            //task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/testinn.php/?adresse=testeeer&latitude=1&longitude=2&etasjer=3&beskrivelse=test"});
            task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/sletthus.php/?id="+innId});
            marker.remove();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Get information from database
    private class getJSON extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            //String retur = "";
            String s = "";
            String output = "";
            for (String url : urls) {
                try {
                    URL urlen = new URL(urls[0]);
                    HttpURLConnection conn = (HttpURLConnection)
                            urlen.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept",
                            "application/json");
                    if (conn.getResponseCode() != 200) {
                        throw new RuntimeException("Failed : HTTP error code : "
                                + conn.getResponseCode());
                    }
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (conn.getInputStream())));
                    System.out.println("Output from Server .... \n");
                    while ((s = br.readLine()) != null) {
                        output = output + s;
                    }
                    conn.disconnect();
                    return output;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return output;
        }

        @Override
        protected void onPostExecute(String ss) {
            Log.d("", ss);
        }
    }

}
