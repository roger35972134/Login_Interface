package com.example.roger.parsetest;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoreFragment extends Fragment {
    @Bind(R.id.store)
    TextView store;
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.store_fragment,container,false);
        ButterKnife.bind(this, v);

        Typeface font=Typeface.createFromAsset(getActivity().getAssets(),"Bigfish.ttf");
        store.setTypeface(font);

        return v;
    }
}
