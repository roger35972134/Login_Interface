package com.example.roger.parsetest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class AssetsFragment extends Fragment {
    @Bind(R.id.assetsBank)
    TextView assetsBank;
    @Bind(R.id.assetsPoint)
    TextView assetsPoint;
    @Bind(R.id.assetsCash)
    TextView assetsCash;
    @Bind(R.id.Assets)
    TextView assets;
    Firebase ref;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.assets_fragmet, container, false);
        ButterKnife.bind(this, v);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Bigfish.ttf");
        assets.setTypeface(font);
        assetsBank.setTypeface(font);
        assetsCash.setTypeface(font);
        assetsPoint.setTypeface(font);
        getData();
        return v;
    }

    public void getData() {
        Bundle bundle = getArguments();
        String PlayerId = bundle.getString("PLAYER_ID");
        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/" + PlayerId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                assetsBank.setText(dataSnapshot.child("bank").getValue(String.class));
                assetsPoint.setText(dataSnapshot.child("point").getValue(String.class));
                assetsCash.setText(dataSnapshot.child("cash").getValue(String.class));
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        /*ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", PlayerId);
        try {
            List<ParseObject> objects=query.find();
            if (objects.size() != 0) {
                ParseObject hit = objects.get(0);
                assetsBank.setText(hit.getNumber("bank").toString());
                assetsPoint.setText(hit.getNumber("point").toString());
                assetsCash.setText(hit.getNumber("cash").toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
    }
}
