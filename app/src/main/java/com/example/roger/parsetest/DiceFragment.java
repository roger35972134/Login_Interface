package com.example.roger.parsetest;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.games.Player;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiceFragment extends Fragment {

    int dice_pic[] = {R.drawable.dice_one, R.drawable.dice_two, R.drawable.dice_three,
            R.drawable.dice_four, R.drawable.dice_five, R.drawable.dice_six};
    int current = 1, count = 0, direction = 1, playerPosition, mapCount;
    String PlayerId, MapId;
    Firebase ref;
    @Bind(R.id.rollIt)
    TextView rollIt;
    @Bind(R.id.dice)
    ImageView dice;

    @OnClick(R.id.dice)
    void onClick() {
        count = 0;
        handleChange();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);
        ButterKnife.bind(this, v);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Bigfish.ttf");
        rollIt.setTypeface(font);


        Bundle bundle = getArguments();
        PlayerId = bundle.getString("PLAYER_ID");
        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/");

        getMap();


        return v;
    }

    public int random() {
        int points = (int) (Math.random() * 6);
        current = points;
        return points;
    }

    public void handleChange() {
        Handler hand = new Handler();

        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                change();

            }


            private void change() {
                // TODO Auto-generated method stub
                count++;
                int index = random();

                dice.setImageResource(dice_pic[index]);
                if (count < 15) {
                    handleChange();
                }
                if (count == 15) {
                    if (direction == 0) {
                        playerPosition = (playerPosition + current) % mapCount;
                    } else if (direction == 1) {
                        playerPosition -= current;
                        while (playerPosition <= 0) {
                            playerPosition += mapCount;
                        }
                    }
                    ref.child("Maps/" + MapId + "/player/" + PlayerId + "/position").setValue(playerPosition);
                }
            }
        }, 200);

    }

    public void getMap() {
        ref.child("userData/" + PlayerId + "/CurrentMap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    MapId = dataSnapshot.getValue(String.class);
                    getPlayerPosition();
                    getMapCount();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void getMapCount() {
        ref.child("Maps/" + MapId + "/location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mapCount = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getPlayerPosition() {
        ref.child("Maps/" + MapId + "/player/" + PlayerId + "/position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                playerPosition = dataSnapshot.getValue(int.class);

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
