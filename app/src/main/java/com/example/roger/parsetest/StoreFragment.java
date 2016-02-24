package com.example.roger.parsetest;


import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreFragment extends Fragment {
    @Bind(R.id.store)
    TextView store;
    @Bind(R.id.store_bombs)
    ImageButton bombs;
    @Bind(R.id.store_change_direction)
    ImageButton changeDirection;
    @Bind(R.id.store_change_position)
    ImageButton changePosition;
    @Bind(R.id.store_dice)
    ImageButton dice;
    @Bind(R.id.store_rocket)
    ImageButton rocket;
    @Bind(R.id.store_stop)
    ImageButton stop;
    @OnClick(R.id.store_bombs)
    void Bomb() {

    }
    @OnClick(R.id.store_rocket)
    void Rocket(){
        /*Bundle bundle=new Bundle();
        bundle.putInt("Type", 1);
        ItemUseFragment itemUseFragment=new ItemUseFragment();
        itemUseFragment.setArguments(bundle);
        android.support.v4.app.FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame,itemUseFragment);
        fragmentTransaction.commit();*/

        onCreateTargetDialog();
    }
    String TargetChoose;
    ArrayList<String> nameList = new ArrayList<>();
    ValueEventListener initMarker;
    Firebase ref;
    int trapIcon[] = {R.drawable.rocket81, R.drawable.bombs, R.drawable.dice,
            R.drawable.stop, R.drawable.change, R.drawable.change_direction};
    ArrayAdapter<String> targetList;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View v=inflater.inflate(R.layout.store_fragment, container, false);
        ButterKnife.bind(this, v);
        Typeface font=Typeface.createFromAsset(getActivity().getAssets(),"Bigfish.ttf");
        store.setTypeface(font);

        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/");

        initTarget();

        return v;
    }
    public void initTarget() {

        ref.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String name = dataSnapshot1.child("Id").getValue(String.class);
                        nameList.add(name);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
    public void onCreateTargetDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_choose_target, null);

        ImageButton positive = (ImageButton) dialogView.findViewById(R.id.target_positive_button);
        ImageButton negative = (ImageButton) dialogView.findViewById(R.id.target_negative_button);

        Spinner spinner = (Spinner) dialogView.findViewById(R.id.target_spinner);
        targetList=new ArrayAdapter<String>(StoreFragment.this.getContext(),android.R.layout.simple_spinner_item,nameList);
        spinner.setAdapter(targetList);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                final String name=nameList.get(position);
                TargetChoose=name;
                Firebase target = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/"
                        + TargetChoose+"/photo");

                target.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            byte[] base = Base64.decode(dataSnapshot.getValue().toString(), Base64.DEFAULT);
                            Bitmap bitmap = BitmapFactory.decodeByteArray(base, 0, base.length);
                            ImageView targetPhoto = (ImageView) dialogView.findViewById(R.id.target_photo);
                            targetPhoto.setImageBitmap(bitmap);
                        }


                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

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
}
