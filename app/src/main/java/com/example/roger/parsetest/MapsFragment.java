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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsFragment extends Fragment implements LocationListener {
    String bestProvider;
    GoogleMap map;
    float zoom = 18;
    private LocationManager locationManager;
    EditText edtPrice, edtName;
    String setBuildName, PlayerId;
    @Bind(R.id.mapview)
    MapView mapView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.maps_fragment, container, false);
        ButterKnife.bind(this, v);
        mapView.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        PlayerId = bundle.getString("PLAYER_ID");
        InitMarker();

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

    public void InitMarker() {
        ParseQuery query = ParseQuery.getQuery("Map");
        /*try {
            List<ParseObject> objects = query.find();
            if (!objects.isEmpty()) {
                for (int i = 0; i < objects.size(); i++) {
                    ParseObject parseObject = objects.get(i);
                    LatLng latLng = new LatLng(parseObject.getNumber("latitude").doubleValue()
                            , parseObject.getNumber("longitude").doubleValue());
                    addingMarker(parseObject.getString("name"), latLng);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && !objects.isEmpty()) {
                    for (int i = 0; i < objects.size(); i++) {
                        ParseObject parseObject = objects.get(i);
                        LatLng latLng = new LatLng(parseObject.getNumber("latitude").doubleValue()
                                , parseObject.getNumber("longitude").doubleValue());
                        addingMarker(parseObject.getString("name"), latLng);
                    }
                }
            }
        });
    }

    public void checkMatchPoint(LatLng latLng) {
        ParseQuery query = ParseQuery.getQuery("Map");
        query.whereGreaterThanOrEqualTo("latitude", latLng.latitude - 0.00025)
                .whereLessThanOrEqualTo("latitude", latLng.latitude + 0.00025)
                .whereGreaterThanOrEqualTo("longitude", latLng.longitude - 0.00025)
                .whereLessThanOrEqualTo("longitude", latLng.longitude + 0.00025);
        try {
            List<ParseObject> objects = query.find();
            if (!objects.isEmpty()) {
                ParseObject object = objects.get(0);
                String pathName = object.getString("name");
                String matchPrice=object.getNumber("price").toString();
                if (object.getString("owner") == null) {
                        onCreateBuyBuildingDialog(pathName,matchPrice);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void addingMarker(String title, LatLng latLng) {
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

    int buildPrice = -1;
    boolean nameDuplicate;

    public void checkDuplicate(final String name) throws ParseException {
        nameDuplicate = false;
        ParseQuery query = ParseQuery.getQuery("Map");
        query.whereEqualTo("name", name);
        if (!query.find().isEmpty())
            nameDuplicate = true;
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
                    Toast.makeText(MapsFragment.this.getContext(), "Item can't be empty", Toast.LENGTH_LONG);
                } else if (nameDuplicate) {
                    edtName.setText("");
                    Toast.makeText(MapsFragment.this.getContext(), "Name had been used", Toast.LENGTH_LONG).show();
                } else {
                    buildPrice = Integer.parseInt(edtPrice.getText().toString());
                    setBuildName = edtName.getText().toString();
                    addingMarker(setBuildName, latLng);
                    alertDialog.dismiss();
                    updateData(latLng);
                }

                try {
                    checkDuplicate(edtName.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
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

    int Bank, Price;

    public void onCreateBuyBuildingDialog(final String pathName,String matchPrice){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_dialog_buybuilding, null);

        getBankMoney();
        getPrice(pathName);
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
                dialog.dismiss();

                ParseQuery query = ParseQuery.getQuery("UserData");
                query.whereEqualTo("Id", PlayerId);
                try {
                    List<ParseObject> objects = query.find();
                    if (!objects.isEmpty()) {
                        ParseObject object = objects.get(0);
                        object.put("bank", Bank - Price);
                        object.save();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                query = ParseQuery.getQuery("Map");
                query.whereEqualTo("name", pathName);
                try {
                    List<ParseObject> objects = query.find();
                    ParseObject object = objects.get(0);
                    object.put("owner", PlayerId);
                    object.save();
                } catch (ParseException e) {
                    e.printStackTrace();
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
        ParseQuery query = ParseQuery.getQuery("Map");
        query.whereEqualTo("name", pathName);
        try {
            List<ParseObject> objects = query.find();
            ParseObject object = objects.get(0);
            Price = object.getInt("price");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Price;
    }

    public int getBankMoney() {
        ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", PlayerId);
        try {
            List<ParseObject> objects = query.find();
            if (!objects.isEmpty()) {
                ParseObject object = objects.get(0);
                Bank = object.getInt("bank");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return Bank;
    }

    public void updateData(LatLng latLng) {
        ParseObject mapData = new ParseObject("Map");
        mapData.put("latitude", latLng.latitude);
        mapData.put("longitude", latLng.longitude);
        mapData.put("name", setBuildName);
        mapData.put("price", buildPrice);
        mapData.saveInBackground();
    }
}