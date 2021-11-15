package com.example.isiahlibor.mymapp;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sqlitelib.DataBaseHelper;
import com.sqlitelib.SQLite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ArrayAdapter AdapterCoordinates;
    public int cptr;
    public DataBaseHelper dbhelper = new DataBaseHelper(this, "MapCoordinates", 2);
    public Double lat;
    List<String> listCoordinates;
    public Double lng;
    public String location;
    ListView lstviewCoordinateList;
    private GoogleMap mMap;
    public Integer[] valueId;
    public Double[] valueLatitude;
    public String[] valueLocation;
    public Double[] valueLongitude;
    private String TAG;
    public String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.listview);
            this.lstviewCoordinateList = (ListView) findViewById(R.id.lstview);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            reloadPointList();
            lstviewCoordinateListItemSelectedChangedListener();
            mapFragment.getMapAsync(this);
    }

    private void lstviewCoordinateListItemSelectedChangedListener() {
        this.lstviewCoordinateList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                MapsActivity.this.cptr = position;
                MapsActivity.this.msrkNewPoints();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    private void msrkNewPoints() {
        this.lat = this.valueLatitude[this.cptr];
        this.lng = this.valueLongitude[this.cptr];
        this.location = this.valueLocation[this.cptr];
        this.mMap.clear();
        LatLng sydney = new LatLng(this.lat.doubleValue(), this.lng.doubleValue());
        this.mMap.addMarker(new MarkerOptions().position(sydney).title(this.location));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @SuppressLint("ResourceType")
    public void reloadPointList() {
        SQLiteDatabase dbCoordinates = this.dbhelper.getWritableDatabase();
        Cursor cCordinates = dbCoordinates.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='tblcoordinates'", null);
        cCordinates.moveToNext();
        if (cCordinates.getCount() == 0) {
            SQLite.FITCreateTable("MapCoordinates", this, "tblcoordinates", "id INTEGER PRIMARY KEY AUTOINCREMENT, latitude DOUBLE,longitude DOUBLE,location VARCHAR(100)");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (-37.81361100000001,144.96305600000005,'Melbourne, Australia')");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (38.9071923,-77.03687070000001,'Washington DC, USA')");
            dbCoordinates.execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (59.32932349999999,18.068580800000063,'Stockholm, Sweden')");
            return;
        }
        cCordinates = dbCoordinates.rawQuery("SELECT id, latitude, longitude,location FROM tblcoordinates order by id desc", null);
        String[] valueCoordinates = new String[cCordinates.getCount()];
        Integer[] ValueCurrentID = new Integer[cCordinates.getCount()];
        Double[] valueCurrentLatitude = new Double[cCordinates.getCount()];
        Double[] valueCurrentLongitude = new Double[cCordinates.getCount()];
        String[] valueCurrentLocation = new String[cCordinates.getCount()];
        int ctrl = 0;
        while (cCordinates.moveToNext()) {
            valueCoordinates[ctrl] = ((BuildConfig.FLAVOR + "Latitude : " + cCordinates.getDouble(cCordinates.getColumnIndex("latitude"))) + System.lineSeparator() + "Longitude : " + cCordinates.getDouble(cCordinates.getColumnIndex("longitude"))) + System.lineSeparator() + "Address : " + cCordinates.getString(cCordinates.getColumnIndex("location"));
            ValueCurrentID[ctrl] = Integer.valueOf(cCordinates.getInt(cCordinates.getColumnIndex("id")));
            ctrl++;
        }
        this.valueId = (Integer[]) Arrays.copyOf(ValueCurrentID, cCordinates.getCount());
        this.valueLatitude = (Double[]) Arrays.copyOf(valueCurrentLatitude, cCordinates.getCount());
        this.valueLongitude = (Double[]) Arrays.copyOf(valueCurrentLongitude, cCordinates.getCount());
        this.valueLocation = (String[]) Arrays.copyOf(valueCurrentLocation, cCordinates.getCount());
        this.listCoordinates = new ArrayList();
        for (Object add : valueCoordinates) {
            this.listCoordinates.add((String) add);
        }
        this.AdapterCoordinates = new ArrayAdapter(this, 17367043, valueCoordinates);
        try {
            this.lstviewCoordinateList.setAdapter(this.AdapterCoordinates);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        LatLng sydney = new LatLng(-34.0d, 151.0d);
        this.mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        this.mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        this.mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                MapsActivity.this.mMap.clear();
                LatLng newmarker = new LatLng(point.latitude, point.longitude);
                MapsActivity.this.mMap.addMarker(new MarkerOptions().position(newmarker).title("New Point").snippet("4 E. 28TH Street From $15 /per night").rotation(-15.0f).icon(BitmapDescriptorFactory.defaultMarker(30.0f)));
                MapsActivity.this.lat = Double.valueOf(point.latitude);
                MapsActivity.this.lng = Double.valueOf(point.longitude);
                MapsActivity.this.addCoordinates();
                MapsActivity.this.mMap.moveCamera(CameraUpdateFactory.newLatLng(newmarker));
                MapsActivity.this.mMap.animateCamera(CameraUpdateFactory.zoomTo(13.0f), 1000, null);
            }
        });
    }

    @SuppressLint("WrongConstant")
    private void addCoordinates() {
        try {

            try{
                Geocoder geo = new Geocoder(MapsActivity.this.getApplicationContext(), Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(this.lat, this.lng, 1);
                if (addresses.isEmpty()) {
                    Toast.makeText(this,"Waiting for Location",Toast.LENGTH_LONG).show();
                }
                else {
                    if (addresses.size() > 0) {
                        address = addresses.get(0).getLocality() +", " + addresses.get(0).getCountryName();
                        Toast.makeText(this,""+address,Toast.LENGTH_LONG).show();
                        this.dbhelper.getWritableDatabase().execSQL("INSERT INTO tblcoordinates (latitude,longitude, location) VALUES (" + this.lat + "," + this.lng + "," + address);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage().toString(), 1).show();
        }
        reloadPointList();
    }

}
