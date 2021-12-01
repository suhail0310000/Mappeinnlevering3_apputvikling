package com.example.mappeinnlevering3_s341868;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mappeinnlevering3_s341868.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;
    private double selectedLat, selectedLng;
    List<Address> addresses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getJSON task = new getJSON();
        task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/jsonout.php"});
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(59.916306, 10.740548);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("clicked","clicked");
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;
                Log.d(""+selectedLat,""+selectedLng);
                GetAddress(selectedLat,selectedLng);
            }
        });
    }

    public String GetAddress(double lat, double lng) {
        Log.d("", "skal finne adresse");
        String strAdd = "";
        /*String innCity = addresses.get(0).getAdminArea();
        String innPost = addresses.get(0).getPostalCode();*/
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String innCity = addresses.get(0).getThoroughfare();
            String innPost = addresses.get(0).getSubThoroughfare();
            //&& addresses.get(0).getPostalCode() != null
            //&& addresses.get(0).getPostalCode() != null
            if (addresses != null && addresses.size() > 0 && innCity != null && innPost != null) {
                Log.d("", "Adressenn: "+ addresses.get(0).getLocality());
                Log.d("", "Adressenn2: "+addresses.get(0).getAdminArea());
                Log.d("", "Adressenn3: "+addresses.get(0).getAddressLine(0));
                Log.d("", "Adressenn4: "+addresses.get(0).getPostalCode());
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
                //displayBottomFragment();
                //visInfoFragment();
                //setContentView(R.layout.fragment_register);
            } else {
                Log.w("My Current loction address", "No Address returned!");
                //setContentView(R.layout.fragment_register);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public void visInfoFragment(){
        DisplayBottomFragment blankFragment = new DisplayBottomFragment();
        blankFragment.show(getSupportFragmentManager(),blankFragment.getTag());
        /*VisInfoFragment visInfoFragment = new VisInfoFragment();
        visInfoFragment.show(getSupportFragmentManager().beginTransaction(),"Info fragment");*/
    }

    private class getJSON extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            String retur = "";
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
                    try {
                        JSONArray mat = new JSONArray(output);
                        for (int i = 0; i < mat.length(); i++) {
                            JSONObject jsonobject = mat.getJSONObject(i);
                            String name = jsonobject.getString("name");
                            retur = retur + name + "\n";
                        }
                        return retur;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return retur;
                } catch (Exception e) {
                    return "Noe gikk feil";
                }
            }
            return retur;
        }

        @Override
        protected void onPostExecute(String ss) {
            //textView.setText(ss);
            Log.d("", ss);
            /*String retur = "";
            try{
                JSONArray mat=new JSONArray(ss);
                for (int i=0;i <mat.length();i++) {
                    JSONObject jsonobject=mat.getJSONObject(i);
                    String name=jsonobject.getString("name");
                    retur = retur + name+"\n" ;
                    Log.d(retur,"prinut");
                }
            }
            catch(Exception e){
                Log.d(ss,"feil");
            }*/


        }
    }
}