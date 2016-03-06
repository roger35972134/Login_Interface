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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StoreFragment extends Fragment {
    int[] house = {R.drawable.maps_house_player1, R.drawable.maps_house_player2, R.drawable.maps_house_player3,
            R.drawable.maps_house_player4, R.drawable.maps_house_player5, R.drawable.maps_house_player6
            , R.drawable.maps_house_player7, R.drawable.maps_house_player8, R.drawable.map_house};
    @Bind(R.id.store)
    TextView store;

    //itemId : 0 Rocket, 1 Bomb, 2 Dice, 3 stop, 4 change position, 5 change direction
    @OnClick(R.id.store_rocket)
    void Rocket() {
        onCreateTargetDialog(0);
    }

    @OnClick(R.id.store_bombs)
    void Bomb() {
        onCreateTargetDialog(1);
    }

    @OnClick(R.id.store_dice)
    void Dice() {
        onCreateTargetDialog(2);
    }

    @OnClick(R.id.store_stop)
    void Stop() {
        onCreateTargetDialog(3);
    }

    @OnClick(R.id.store_change_position)
    void ChangePosition() {
        onCreateTargetDialog(4);
    }

    @OnClick(R.id.store_change_direction)
    void ChangeDirection() {
        onCreateTargetDialog(5);
    }

    String TargetChoose, PositionChoose, PlayerId, MapId;
    int playerPosition, playerDirection, dicePoint;
    String[] diceList = {"1", "2", "3", "4", "5", "6"};
    ArrayList<String> nameList = new ArrayList<>(),
            positionList = new ArrayList<>();
    Firebase base;

    ArrayAdapter<String> targetList;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.store_fragment, container, false);
        ButterKnife.bind(this, v);
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Bigfish.ttf");
        store.setTypeface(font);

        Bundle bundle = getArguments();
        PlayerId = bundle.getString("PLAYER_ID");

        base = new Firebase("http://brilliant-heat-8278.firebaseio.com/");


        initMap();

        return v;
    }

    public void initPosition() {
        base.child("Maps/" + MapId + "/location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String location = dataSnapshot1.getKey();
                        positionList.add(location);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void initTarget() {

        base.child("Maps/" + MapId + "/player").addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        String name = dataSnapshot1.getKey();
                        nameList.add(name);
                        System.out.println(name);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void initMap() {
        base.child("userData/" + PlayerId + "/CurrentMap").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapId = dataSnapshot.getValue(String.class);
                initPosition();
                initTarget();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void getPlayerPositionDirection() {
        base.child("Maps/" + MapId + "/player/" + PlayerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                playerPosition = dataSnapshot.child("position").getValue(int.class);
                playerDirection = dataSnapshot.child("direction").getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onCreateTargetDialog(final int itemId) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_choose_target, null);

        ImageButton positive = (ImageButton) dialogView.findViewById(R.id.target_positive_button);
        ImageButton negative = (ImageButton) dialogView.findViewById(R.id.target_negative_button);
        final ImageView targetPhoto = (ImageView) dialogView.findViewById(R.id.target_photo);

        Spinner spinner = (Spinner) dialogView.findViewById(R.id.target_spinner);

        if (itemId == 0 || itemId == 4) {
            targetList = new ArrayAdapter<String>(StoreFragment.this.getContext(), android.R.layout.simple_spinner_item, nameList);
            spinner.setAdapter(targetList);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    TargetChoose = nameList.get(position);
                    System.out.println(TargetChoose);
                    Firebase target = new Firebase("http://brilliant-heat-8278.firebaseio.com/userData/"
                            + TargetChoose + "/photo");

                    target.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                byte[] base = Base64.decode(dataSnapshot.getValue().toString(), Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(base, 0, base.length);
                                targetPhoto.setImageBitmap(bitmap);
                            } else {
                                targetPhoto.setImageResource(R.drawable.profile);
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
        } else if (itemId == 1 || itemId == 3) {
            targetList = new ArrayAdapter<String>(StoreFragment.this.getContext(), android.R.layout.simple_spinner_item, positionList);
            spinner.setAdapter(targetList);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, final int position, long arg3) {
                    PositionChoose = positionList.get(position);

                    base.child("Maps/" + MapId + "/location/" + PositionChoose + "/owner").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String owner = dataSnapshot.getValue(String.class);
                            int positionOwner = 8;
                            for (int i = 0; i < nameList.size(); i++) {
                                if (owner.equals(nameList.get(i))) {
                                    positionOwner = i;
                                }
                            }
                            targetPhoto.setImageResource(house[positionOwner]);
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
        } else if (itemId == 2) {
            targetList = new ArrayAdapter<String>(StoreFragment.this.getContext(), android.R.layout.simple_spinner_item, diceList);
            spinner.setAdapter(targetList);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
                    dicePoint = position + 1;
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        } else {
            //itemId==5
            getPlayerPositionDirection();
            if (playerDirection == 0)
                playerDirection = 1;
            else
                playerDirection = 0;
            base.child("Maps/" + MapId + "/player/" + PlayerId + "/direction").setValue(playerDirection);
        }


        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                switch (itemId) {
                    case 0:
                        base.child("userData/" + TargetChoose + "/delayRound").setValue(5);
                        break;
                    case 1:
                        base.child("Maps/" + MapId + "/location/" + PositionChoose + "/trapType").setValue(1);
                        break;
                    case 2:
                        int mapCount = positionList.size();
                        getPlayerPositionDirection();
                        if (playerDirection == 0) {
                            playerPosition = (playerPosition + dicePoint) % mapCount;
                        } else if (playerDirection == 1) {
                            playerPosition -= dicePoint + 1;
                            while (playerPosition <= 0) {
                                playerPosition += mapCount;
                            }
                        }
                        base.child("Maps/" + MapId + "/player/" + PlayerId + "/position").setValue(playerPosition);
                        //rounds over
                        break;
                    case 3:
                        base.child("Maps/" + MapId + "/location/" + PositionChoose + "/trapType").setValue(3);
                        break;
                    case 4:
                        changePosition();
                        break;
                    case 5:
                        break;
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

    int targetPosition;

    public void changePosition() {

        getPlayerPositionDirection();
        base.child("Maps/" + MapId + "/player/" + TargetChoose + "/position").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                targetPosition = dataSnapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        int buf = targetPosition;
        targetPosition = playerPosition;
        playerPosition = buf;
        base.child("Maps/" + MapId + "/player/" + PlayerId + "/position").setValue(playerDirection);
        base.child("Maps/" + MapId + "/player/" + TargetChoose + "/position").setValue(targetPosition);
    }
}
