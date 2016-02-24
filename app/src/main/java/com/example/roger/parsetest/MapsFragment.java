package com.example.roger.parsetest;

import android.Manifest;
import android.annotation.TargetApi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsFragment extends Fragment implements LocationListener {
    String bestProvider;
    GoogleMap map;
    float zoom = 18;
    private LocationManager locationManager;
    String PlayerId, MapId;
    @Bind(R.id.mapview)
    MapView mapView;
    ArrayList<String> nameList = new ArrayList<>();
    Firebase ref, base;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        ButterKnife.bind(this, v);
        mapView.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        PlayerId = bundle.getString("PLAYER_ID");
        base = new Firebase("http://brilliant-heat-8278.firebaseio.com/");
        InitMap();


        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);

        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        bestProvider = locationManager.getBestProvider(criteria, true);
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
        locationManager.requestLocationUpdates(bestProvider, 2000, 20, this);
        super.onResume();
    }

    public void onPause() {
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
        locationManager.removeUpdates(this);
        super.onPause();
        mapView.onPause();
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

    public void InitMap() {
        base.child("userData/" + PlayerId + "/CurrentMap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapId = dataSnapshot.getValue(String.class);
                initPlayerPosition();
                InitMarker();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void InitMarker() {
        ref = base.child("Maps/location/" + MapId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        double latitude, longitude;
                        String name;
                        latitude = i.child("latitude").getValue(double.class);
                        longitude = i.child("longitude").getValue(double.class);
                        name = i.child("name").getValue(String.class);
                        Integer num = i.child("number").getValue(int.class);
                        nameList.add(name);
                        LatLng latLng = new LatLng(latitude, longitude);
                        addingMarker(name, latLng, num);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkMatchPoint(final LatLng latLng) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        double latitude, longitude;
                        String name, price;
                        latitude = i.child("latitude").getValue(double.class);
                        longitude = i.child("longitude").getValue(double.class);
                        name = i.child("name").getValue(String.class);
                        price = i.child("price").getValue(String.class);
                        if (latitude > latLng.latitude - 0.00025 && latitude < latLng.latitude + 0.00025
                                && longitude > latLng.longitude - 0.00025 && longitude < latLng.longitude + 0.00025) {
                            if (i.child("owner").getValue(String.class).equals("none")) {
                                onCreateBuyBuildingDialog(name, price);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    int PlayerPosition;
    public void initPlayerPosition() {
        ref.child("player/" + PlayerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayerPosition = dataSnapshot.getValue(int.class);
                System.out.println(PlayerPosition);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addingMarker(String title, LatLng latLng, int num) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title(String.valueOf(num))
                .snippet(title)
                .draggable(false)
                .visible(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_house));
        map.addMarker(markerOptions);
        if (PlayerPosition == num)
        {
            LatLng playerLatLng=new LatLng(latLng.latitude+0.0002,latLng.longitude);
            MarkerOptions markerOptionPlayer = new MarkerOptions();
            markerOptionPlayer.position(playerLatLng)
                    .draggable(false)
                    .visible(true)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_house_player));
            map.addMarker(markerOptionPlayer);
        }
    }

    int buf = 0;

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onLocationChanged(Location location) {
        buf++;
        LatLng Point = new LatLng(location.getLatitude(), location.getLongitude());

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
        if (buf > 1)
            checkMatchPoint(Point);
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


    int Bank, Price, Cash;

    public void onCreateBuyBuildingDialog(final String pathName, String matchPrice) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_buybuilding, null);

        getBankMoney();
        getPrice(pathName);
        getCashMoney();
        ImageButton positive = (ImageButton) dialogView.findViewById(R.id.positive_button);
        ImageButton negative = (ImageButton) dialogView.findViewById(R.id.negative_button);
        TextView p = (TextView) dialogView.findViewById(R.id.buildingPrice);
        TextView n = (TextView) dialogView.findViewById(R.id.buildingName);
        n.setText(pathName);
        p.setText(matchPrice);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Firebase cash = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId + "/cash");
                Firebase bank = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId + "/bank");

                dialog.dismiss();
                if (Cash - Price >= 0) {
                    cash.setValue(Cash - Price);
                    ref.child(pathName + "/owner").setValue(PlayerId);
                } else if (Bank - Price >= 0) {
                    bank.setValue(Bank - Price);
                    ref.child(pathName + "/owner").setValue(PlayerId);
                } else {
                    Toast.makeText(MapsFragment.this.getContext(), "You need more gold", Toast.LENGTH_LONG).show();
                }
            }
        });
        negative.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public int getPrice(String pathName) {
        ref.child(pathName + "/price").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Price = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return Price;
    }

    public int getCashMoney() {
        Firebase cash = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId + "/cash");
        cash.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Cash = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return Cash;
    }

    public int getBankMoney() {
        Firebase bank = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId + "/bank");
        bank.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Bank = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return Bank;
    }


}