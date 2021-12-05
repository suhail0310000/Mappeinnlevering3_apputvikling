package com.example.mappeinnlevering3_s341868;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.mappeinnlevering3_s341868.databinding.ActivityMapsBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private Geocoder geocoder;
    private ArrayList<JSONObject> husMarkers = new ArrayList<>();
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

        //Starting point
        LatLng startPoint = new LatLng(59.916306, 10.740548);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,16));

        //get json objects from this url
        getJSON task = new getJSON();
        task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/henthus.php"});
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override   
            public void onMapClick(LatLng latLng) {
                Log.d("clicked","clicked");
                selectedLat = latLng.latitude;
                selectedLng = latLng.longitude;
                Log.d(""+selectedLat,""+selectedLng);
                GetAddress(selectedLat,selectedLng);
                //displayMarker();
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                visInfoFragment(marker.getSnippet(), marker);
                return true;
            }
        });
    }

    public String GetAddress(double lat, double lng) {
        Log.d("", "skal finne adresse");
        String strAdd = "";
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lat, lng, 1);
            String innCity = addresses.get(0).getThoroughfare();
            String innPost = addresses.get(0).getSubThoroughfare();
            if (addresses != null && addresses.size() > 0 && innCity != null && innPost != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
                LatLng nyLatLng = new LatLng(selectedLat, selectedLng);
                /*MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(nyMarker);*/
                visRegFragment(addresses.get(0).getThoroughfare()+","+addresses.get(0).getSubThoroughfare(),selectedLat,selectedLng,mMap);
                //displayBottomFragment();
                //setContentView(R.layout.fragment_register);
            } else {
                Toast.makeText(this,"Ikke gyldig adresse",Toast.LENGTH_SHORT).show();
                Log.w("My Current loction address", "No Address returned!");
                //setContentView(R.layout.fragment_register);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"Ikke gyldig adresse",Toast.LENGTH_SHORT).show();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    public void displayMarker(){
        for(JSONObject marker : husMarkers){
            try {
                System.out.println("Jsonobject:"+marker);
                String getID = marker.getString("id");
                double getlat = marker.getDouble("latitude");
                double getLongtude = marker.getDouble("longitude");
                String title = marker.getString("adresse");
                Log.d("Tittel:",title+"");
                Log.d("Posisjon",getlat+""+getLongtude);
                LatLng nyHus = new LatLng(getlat, getLongtude);
                mMap.addMarker(new MarkerOptions().position(nyHus).title(title).snippet(getID)).showInfoWindow();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //mMap.addMarker(new MarkerOptions().position(marker));
        }
    }
    public void visInfoFragment(String snippet, Marker innMarker){
        for(JSONObject marker : husMarkers){
            String getId = null;
            try {
                getId = marker.getString("id");
                if(snippet.equals(getId)){
                    DisplayBottomFragment blankFragment = new DisplayBottomFragment(marker, innMarker);
                    blankFragment.show(getSupportFragmentManager(),blankFragment.getTag());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public void visRegFragment(String innAdresse, double selectedLat, double selectedLng, GoogleMap mMap){
        System.out.print("Vis reg fragment");
        VisRegistrerFragment visRegFragment = new VisRegistrerFragment(innAdresse,selectedLat,selectedLng,mMap);
        visRegFragment.show(getSupportFragmentManager().beginTransaction(),"registrer fragment");
    }

    //Get information from database
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
                        JSONArray hus = new JSONArray(output);
                        for (int i = 0; i < hus.length(); i++) {
                            JSONObject jsonobject = hus.getJSONObject(i);
                            husMarkers.add(jsonobject);
                            retur = retur + jsonobject + "\n";
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
            displayMarker();
        }


    }
}