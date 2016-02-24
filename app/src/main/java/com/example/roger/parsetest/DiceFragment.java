package com.example.roger.parsetest;


import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiceFragment extends Fragment {

    int dice_pic[] = {R.drawable.dice_one, R.drawable.dice_two, R.drawable.dice_three,
            R.drawable.dice_four, R.drawable.dice_five, R.drawable.dice_six};
    int current = 1,count=0,direction=1;

    @Bind(R.id.rollIt)
    TextView rollIt;
    @Bind(R.id.dice)
    ImageView dice;

    @OnClick(R.id.dice)
    void onClick() {
        count=0;
        handlechange();
        if(direction==1)
        {
            ////////
        }
        else if(direction==-1){

        }
    }

    public int random() {
        int points = (int) (Math.random() * 6);
        current=points;
        return points;
    }

    public void handlechange() {
        Handler hand = new Handler();

        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub

                // change image here
                change();

            }


            private void change() {
                // TODO Auto-generated method stub

                count++;
                int index = random();

                dice.setImageResource(dice_pic[index]);
                if(count<15)
                {
                    handlechange();
                }
            }
        }, 200);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);
        ButterKnife.bind(this, v);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Bigfish.ttf");
        rollIt.setTypeface(font);

        return v;
    }


}
