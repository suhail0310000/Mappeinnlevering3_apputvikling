package com.example.mappeinnlevering3_s341868;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.SQLSyntaxErrorException;
import java.util.Map;

public class VisRegistrerFragment extends DialogFragment {
    String innAdresse;
    double selectedLat;
    double selectedLng;
    GoogleMap mMap;
    //Get all id's from bottom fragment
    TextView txtAddresse, txtEtasjer, txtBeskrivelse;
    Button btnRegistrer;

    public VisRegistrerFragment(String innAdresse, double selectedLat, double selectedLng, GoogleMap mMap) {
        this.innAdresse = innAdresse;
        this.selectedLat = selectedLat;
        this.selectedLng = selectedLng;
        this.mMap = mMap;
    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        System.out.println("Adressen er funner i fragment"+innAdresse);
        System.out.println("Latitude er funner i fragment"+selectedLat);
        System.out.println("Langtude er funner i fragment"+selectedLng);
        View v = inflater.inflate(R.layout.fragment_registrer, container, false);
        txtAddresse = (TextView) v.findViewById(R.id.reg_txtAdresse);
        txtEtasjer = (TextView) v.findViewById(R.id.registrer_etasjer);
        txtBeskrivelse = (TextView) v.findViewById(R.id.registrer_beskrivelse);
        txtAddresse.setText(innAdresse);
        registrerHus(v);
        return v;
    }
    public void registrerHus(View v){
        btnRegistrer = (Button) v.findViewById(R.id.btn_registrer_hus);
        btnRegistrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("","Registrert");
                System.out.println(txtAddresse.getText());
                System.out.println(txtEtasjer.getText());
                System.out.println(txtBeskrivelse.getText());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(selectedLat,selectedLng));
                /*getJSON task = new getJSON();*/
                //String str = "{\"adresse\":\"txtAddresse.getText()\",\"age\":\"30\"}";
                getJSON task = new getJSON();
                //task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/testinn.php/?adresse=testeeer&latitude=1&longitude=2&etasjer=3&beskrivelse=test"});
                String innUrl;
                String innUrl1;
                try {
                    innUrl = URLEncoder.encode(txtAddresse.getText().toString(), "UTF-8");
                    innUrl1 = URLEncoder.encode(txtBeskrivelse.getText().toString(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    innUrl = "";
                    innUrl1 = "";
                    e.printStackTrace();
                }
                if(!innUrl.equals("")){
                    if(CheckAllFields()){
                        task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/registrerhus.php/?adresse="+innUrl+"&latitude="+selectedLat+"&longitude="+selectedLng+"&etasjer="+txtEtasjer.getText().toString()+"&beskrivelse="+innUrl1});
                        Toast.makeText(getContext(),"Hus lagret i db",Toast.LENGTH_SHORT).show();
                        mMap.addMarker(markerOptions);
                    }
                }
            }
        });
    }
    private boolean CheckAllFields() {
        if (txtEtasjer.getText().length() == 0) {
            txtEtasjer.setError("Skriv inn antall etasjer");
            return false;
        }

        if (txtBeskrivelse.getText().length() == 0) {
            txtBeskrivelse.setError("Skriv inn en beskrivelse for huset");
            return false;
        }
        // after all validation return true.
        return true;
    }

    //POST REQUEST
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
