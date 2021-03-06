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

import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VisEndreFragment extends DialogFragment {
    JSONObject jsonObject;
    TextView txtEtasjer,txtBeskrivelse,txtAdresse;
    Button oppdaterBtn;


    public VisEndreFragment(JSONObject jsonObject, Marker marker) {
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
                if(CheckAllFields()){
                    postReq();
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


    public void postReq(){
        try {
            String innId = jsonObject.getString("id");
            String innEtasjer = txtEtasjer.getText().toString();
            String innBeskrivelse = txtBeskrivelse.getText().toString();
            String konvBeskrivelse = URLEncoder.encode(innBeskrivelse, "UTF-8");
            Log.d("ID",innId+"");
            Log.d("Etasjer",innEtasjer+"");
            Log.d("Beskrivelse",innBeskrivelse+"");
            getJSON task = new getJSON();
            if(!innBeskrivelse.equals("")) {
                task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/endrehus.php/?id="+innId+"&etasjer=" + innEtasjer + "&beskrivelse=" + konvBeskrivelse});

            }
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
    }




    //Get information from database
    private class getJSON extends AsyncTask<String, Void, String> {


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
                    jsonObject.remove("etasjer");
                    jsonObject.remove("beskrivelse");
                    jsonObject.put("etasjer",txtEtasjer.getText().toString());
                    jsonObject.put("beskrivelse",txtBeskrivelse.getText().toString());
                    conn.disconnect();
                    Log.d("Jsonobjecteeeeeet oppdatertt versjon",jsonObject+"");
                    return output;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return output;
        }

        @Override
        protected void onPostExecute(String ss) {

            if (!ss.equals("Noe gikk feil")) {
                dismiss();
                Toast.makeText(getContext(),"Dine endringer er lagret",Toast.LENGTH_SHORT).show();
            }
            Log.d("", ss);
        }
    }
}
