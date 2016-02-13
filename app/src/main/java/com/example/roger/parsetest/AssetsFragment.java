package com.example.roger.parsetest;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


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
        ParseQuery query = ParseQuery.getQuery("UserData");
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
        }
        /*query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    ParseObject hit = objects.get(0);
                    if (hit.getNumber("bank") != null) {
                        assetsBank.setText(hit.getNumber("bank").toString());
                        assetsPoint.setText(hit.getNumber("point").toString());
                        assetsCash.setText(hit.getNumber("cash").toString());
                    }
                } else {

                }
            }
        });*/
    }
}
