package com.example.roger.parsetest;


import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.parse.ParseQuery;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DiceFragment extends Fragment {

    int dice_pic[] = {R.drawable.dice_one, R.drawable.dice_two, R.drawable.dice_three,
            R.drawable.dice_four, R.drawable.dice_five, R.drawable.dice_six};
    int current=1;
    @Bind(R.id.rollIt)
    TextView rollIt;
    @Bind(R.id.dice)
    ImageButton dice;

    @OnClick(R.id.dice)
    void onClick() {
        dice.setImageResource(dice_pic[current]);

    }

    public int random() {
        int points = (int) (Math.random() * 6);
        return points;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dice, container, false);
        ButterKnife.bind(this, v);

        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "Bigfish.ttf");
        rollIt.setTypeface(font);
        current=random();
        return v;
    }

}
