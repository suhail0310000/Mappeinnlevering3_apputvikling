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
    private MarkerOptions options = new MarkerOptions();
    //private ArrayList<LatLng> husMarkers = new ArrayList<>();
    private ArrayList<JSONObject> husMarkers = new ArrayList<>();
    /*LatLng pakistan = new LatLng(59.916306, 10.740548);
    LatLng oslomet = new LatLng( 59.9211, 10.7334);*/
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
        /*husMarkers.add(pakistan);
        husMarkers.add(oslomet);*/

        /*getJSON task = new getJSON();
        task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/jsonout.php"});*/
        //task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/test1.php"});
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
        //mMap.addMarker(new MarkerOptions().position(startPoint).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,15));

        //get json objects from this url
        getJSON task = new getJSON();
        task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/test1.php"});
        //task.execute(new String[]{"http://studdata.cs.oslomet.no/~dbuser23/test3.php"});
        //displayMarker();
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
                visInfoFragment(marker.getSnippet());
                return true;
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
                Log.d("", "Adressenn4: "+addresses.get(0).getThoroughfare()+" "+addresses.get(0).getSubThoroughfare());
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
                visRegFragment(addresses.get(0).getThoroughfare()+","+addresses.get(0).getSubThoroughfare(),selectedLat,selectedLng);
                //displayBottomFragment();
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

    public void displayMarker(){
        for(JSONObject marker : husMarkers){
            try {
                System.out.println("Jsonobject:"+marker);
                String getID = marker.getString("id");
                double getlat = marker.getDouble("latitude");
                double getLongtude = marker.getDouble("longitude");
                Log.d("Posisjon",getlat+""+getLongtude);
                LatLng nyHus = new LatLng(getlat, getLongtude);
                mMap.addMarker(new MarkerOptions().position(nyHus).snippet(getID));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //mMap.addMarker(new MarkerOptions().position(marker));
        }
    }
    public void visInfoFragment(String snippet){
        for(JSONObject marker : husMarkers){
            String getId = null;
            try {
                getId = marker.getString("id");
                if(snippet.equals(getId)){
                    DisplayBottomFragment blankFragment = new DisplayBottomFragment(marker);
                    blankFragment.show(getSupportFragmentManager(),blankFragment.getTag());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                /*System.out.println("Jsonobject:"+marker);
                double getlat = marker.getDouble("latitude");
                double getLongtude = marker.getDouble("longitude");
                Log.d("Posisjon",getlat+""+getLongtude);
                LatLng nyHus = new LatLng(getlat, getLongtude);
                mMap.addMarker(new MarkerOptions().position(nyHus));*/
            //mMap.addMarker(new MarkerOptions().position(marker));
        }
        /*VisInfoFragment visInfoFragment = new VisInfoFragment();
        visInfoFragment.show(getSupportFragmentManager().beginTransaction(),"Info fragment");*/
    }

    public void visRegFragment(String innAdresse, double selectedLat, double selectedLng){
        System.out.print("Vis reg fragment");
        VisRegistrerFragment visRegFragment = new VisRegistrerFragment(innAdresse,selectedLat,selectedLng);
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
                            //debugging
                            /*System.out.println("Jsonobject:-----------------------------"+jsonobject);
                            String name = jsonobject.getString("name");*/
                            //String adresse = jsonobject.getString("adresse");
                           /* double latitude = jsonobject.getDouble("latitude");
                            double longitude = jsonobject.getDouble("longitude");*/
                            //husMarkers.add(new LatLng(latitude, longitude));
                            husMarkers.add(jsonobject);
                            //displayMarker();
                            /*System.out.println(adresse);
                            System.out.println(longitude);
                            System.out.println(latitude);*/
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
            //visEndreFragment();
            //visRegFragment();
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