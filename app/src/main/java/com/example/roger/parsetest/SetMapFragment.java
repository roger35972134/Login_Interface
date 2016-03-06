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
import android.widget.SeekBar;
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

public class SetMapFragment extends Fragment implements LocationListener {
    String bestProvider;
    GoogleMap map;
    float zoom = 18;
    int count = 0, PlayerPosition;
    private LocationManager locationManager;
    EditText edtPrice, edtName, edtMapName;
    String setBuildName, PlayerId, MapId;
    @Bind(R.id.mapview)
    MapView mapView;
    ArrayList<String> nameList = new ArrayList<>(),
                      playerList = new ArrayList<>();
    int[] house={R.drawable.maps_house_player1,R.drawable.maps_house_player2,R.drawable.maps_house_player3,
            R.drawable.maps_house_player4,R.drawable.maps_house_player5,R.drawable.maps_house_player6
            ,R.drawable.maps_house_player7,R.drawable.maps_house_player8,R.drawable.map_house};
    int[] playerIcon = {R.drawable.map_player1, R.drawable.map_player2, R.drawable.map_player3,
            R.drawable.map_player4, R.drawable.map_player5, R.drawable.map_player6
            , R.drawable.map_player7, R.drawable.map_player8};
    int trapIcon[] = {R.drawable.rocket81, R.drawable.bombs, R.drawable.dice,
            R.drawable.stop, R.drawable.change, R.drawable.change_direction};

    Firebase ref, mapRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        ButterKnife.bind(this, v);
        mapView.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        PlayerId = bundle.getString("PLAYER_ID");


        onCreateMapName();
        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/Maps/");



        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                onCreateSetPriceDialog(latLng);
            }
        });

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
        locationManager.requestLocationUpdates(bestProvider, 5000, 20, this);

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

    public void initPlayerList(){
        ref.child(MapId+"/player").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        String player=i.getKey();
                        playerList.add(player);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void InitMarker() {
        mapRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        double latitude, longitude;
                        int houseNo = 8,trap,playerNo=8;
                        String name,owner;
                        owner=i.child("owner").getValue(String.class);
                        latitude = i.child("latitude").getValue(double.class);
                        longitude = i.child("longitude").getValue(double.class);
                        name = i.child("name").getValue(String.class);
                        int num = i.child("number").getValue(int.class);
                        trap=i.child("trapType").getValue(int.class);
                        nameList.add(name);
                        for(int j=0;j<playerList.size();j++)
                        {
                            if(owner.equals(playerList.get(j)))
                                houseNo=j;
                            if(PlayerId.equals(playerList.get(j)))
                                playerNo=j;
                        }
                        LatLng latLng = new LatLng(latitude, longitude);
                        addingMarker(name, latLng, num,houseNo,trap,playerNo);
                    }
                    count = nameList.size();
                    ref.child(MapId + "/player/" + PlayerId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                ref.child(MapId + "/player/" + PlayerId + "/position").setValue((int) (Math.random() * count) + 1);
                                ref.child(MapId + "/player/" + PlayerId + "/direction").setValue((int) (Math.random() * 2));
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


    }

    public void initPlayerPosition() {
        ref.child(MapId + "/player/" + PlayerId + "/position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                PlayerPosition = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void addingMarker(String title, LatLng latLng, int num,int houseNo,int trap,int playerNo) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng)
                .title(String.valueOf(num))
                .snippet(title)
                .draggable(false)
                .visible(true)
                .icon(BitmapDescriptorFactory.fromResource(house[houseNo]));

        map.addMarker(markerOptions);
        if (num == PlayerPosition) {
            LatLng playerLatLng = new LatLng(latLng.latitude + 0.0002, latLng.longitude);
            MarkerOptions markerOptionPlayer = new MarkerOptions();
            markerOptionPlayer.position(playerLatLng)
                    .draggable(false)
                    .visible(true)
                    .icon(BitmapDescriptorFactory.fromResource(playerIcon[playerNo]));
            map.addMarker(markerOptionPlayer);
        }
        if(trap!=6)
        {
            LatLng trapLatLng = new LatLng(latLng.latitude - 0.0002, latLng.longitude);
            MarkerOptions trapMarker = new MarkerOptions();
            trapMarker.position(trapLatLng)
                    .title(String.valueOf(num))
                    .snippet(title)
                    .draggable(false)
                    .visible(true)
                    .icon(BitmapDescriptorFactory.fromResource(trapIcon[trap]));
        }
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
        locationManager.removeUpdates(this);
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

    int buildPrice = -1;
    boolean nameDuplicate;

    public boolean checkDuplicate(String name) {
        nameDuplicate = false;
        for (int i = 0; i < nameList.size(); i++) {
            if (nameList.get(i).equals(name))
                nameDuplicate = true;
        }
        return nameDuplicate;
    }

    public void onCreateSetPriceDialog(final LatLng latLng) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.layout_dialog_setprice, null);
        ImageButton checkbtn = (ImageButton) dialog.findViewById(R.id.setprice_positive_button);
        ImageButton cancelbtn = (ImageButton) dialog.findViewById(R.id.setprice_negative_button);
        edtPrice = (EditText) dialog.findViewById(R.id.edtPrice);
        edtName = (EditText) dialog.findViewById(R.id.edtName);


        builder.setView(dialog);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edtPrice.getText().toString().equals("") || edtName.getText().toString().equals("")) {
                    Toast.makeText(SetMapFragment.this.getContext(), "Item can't be empty", Toast.LENGTH_LONG);
                } else if (checkDuplicate(edtName.getText().toString())) {
                    edtName.setText("");
                    Toast.makeText(SetMapFragment.this.getContext(), "Name had been used", Toast.LENGTH_LONG).show();
                } else {
                    buildPrice = Integer.parseInt(edtPrice.getText().toString());
                    setBuildName = edtName.getText().toString();
                    count++;
                    addingMarker(setBuildName, latLng, count,8,6,0);
                    nameList.add(setBuildName);
                    alertDialog.dismiss();
                    updateData(latLng, count);
                }
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPrice = -1;
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void onCreateMapName() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.layout_dialog_map_name, null);
        ImageButton checkbtn = (ImageButton) dialog.findViewById(R.id.mapName_positive_button);
        ImageButton cancelbtn = (ImageButton) dialog.findViewById(R.id.mapName_negative_button);
        edtMapName = (EditText) dialog.findViewById(R.id.edtMapName);


        builder.setView(dialog);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                MapId = edtMapName.getText().toString();
                mapRef = ref.child(MapId + "/location");
                Firebase currentMap = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId);
                currentMap.child("CurrentMap").setValue(MapId);

                initPlayerList();
                initPlayerPosition();
                InitMarker();
            }
        });
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    public void updateData(LatLng latLng, int num) {
        Firebase build = mapRef.child(setBuildName);
        build.child("latitude").setValue(latLng.latitude);
        build.child("longitude").setValue(latLng.longitude);
        build.child("name").setValue(setBuildName);
        build.child("number").setValue(num);
        build.child("price").setValue(buildPrice);
        build.child("level").setValue(1);
        build.child("owner").setValue("none");
        build.child("trapType").setValue(6);
    }
}