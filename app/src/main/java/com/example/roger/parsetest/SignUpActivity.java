package com.example.roger.parsetest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;


import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends Activity {

    @Bind(R.id.signID)
    EditText signId;
    @Bind(R.id.signPW)
    EditText signPw;
    @Bind(R.id.signEmail)
    EditText signEmail;
    @Bind(R.id.signUp_title)
    TextView signUp_title;
    TextView toastText;
    //custom toast
    Firebase ref;
    String id, password, email;
    ValueEventListener checkup;

    @OnClick(R.id.imgCheck)
    void onClick() {
        id = signId.getText().toString();
        password = signPw.getText().toString();
        email = signEmail.getText().toString();
        Check(id, password, email);
    }

    @OnClick(R.id.returnpage)
    void onReturnClick() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "Bigfish.ttf");
        signUp_title.setTypeface(font);


        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/");
    }

    int count = 0;

    public void Check(final String Id, String Password, String Email) {
        final boolean[] flag = {true, false};

        if (Id.isEmpty() || Password.isEmpty() || Email.isEmpty()) {
            setToast("You should fill all the slots");
        }

        ref.child("userData").addValueEventListener(checkup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println(count++);
                if (!flag[1]) {

                    for (DataSnapshot i : dataSnapshot.getChildren()) {
                        System.out.println(i.child("Id"));
                        if (i.child("Id").getValue().toString().equals(Id)) {
                            setToast("ID have been used by another user");
                            flag[0] = false;
                        }
                    }
                    if (flag[0]) {
                        updateData(id, password, email);
                        ref.child("userData").removeEventListener(checkup);
                        flag[1] = true;
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        startActivity(intent);

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    public void updateData(String ID, String PW, String Email) {
        Firebase depth = ref.child("userData/"+ID);

        depth.child("Id").setValue(ID);
        depth.child("password").setValue(PW);
        depth.child("email").setValue(Email);
        depth.child("bank").setValue(2000);
        depth.child("cash").setValue(1000);
        depth.child("point").setValue(200);


        setToast("Sign up success");
    }

    public void setToast(String message) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.custom_toast));
        toastText = (TextView) layout.findViewById(R.id.toastText);
        toastText.setText(message);
        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }
}

