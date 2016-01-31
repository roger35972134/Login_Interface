package com.example.roger.parsetest;

import android.Manifest;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsFragment extends Fragment implements LocationListener {
    String bestProvider;
    GoogleMap map;
    int positionCount = 0;
    float zoom = 17;
    private LocationManager locationManager;
    @Bind(R.id.mapview)
    MapView mapView;
    EditText edtPrice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        ButterKnife.bind(this, v);
        // Gets the MapView from the XML layout and creates it
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (onCreateDalog() != -1) {
                    updateData(positionCount, latLng);
                    addMarker(String.valueOf(positionCount), latLng);
                    //Toast.makeText(MapsFragment.this.getContext(), latLng.latitude + " , " + latLng.longitude, Toast.LENGTH_LONG);
                    positionCount++;
                }
            }
        });

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());
        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(bestProvider, 5000, 1, this);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public void addMarker(String title, LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title(title)
                .draggable(false)
                .visible(true);
        map.addMarker(markerOptions);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {
        LatLng Point = new LatLng(location.getLatitude(), location.getLongitude());
        /*SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        String date = sDateFormat.format(new java.util.Date());*/

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(Point, zoom));
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);
        //addMarker(String.valueOf(positionCount),Point);


        //positionCount++;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    int price = -1;

    public int onCreateDalog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.layout_dialog, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        edtPrice = (EditText) dialog.findViewById(R.id.edtPrice);
        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String p = edtPrice.getText().toString();
                        price = Integer.parseInt(p);
                        //price=100;
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        price = -1;
                    }
                })
                .create().show();
        return price;
    }

    public void updateData(int pathNumber, LatLng latLng) {
        ParseObject mapData = new ParseObject("Map");
        mapData.put("pathNumber", pathNumber);
        mapData.put("latitude", latLng.latitude);
        mapData.put("longitude", latLng.longitude);
        mapData.put("price", price);
        mapData.saveInBackground();
    }
}