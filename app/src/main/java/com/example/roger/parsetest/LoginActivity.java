package com.example.roger.parsetest;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    String Id, password;
    @Bind(R.id.edtId)
    EditText edt_Id;
    @Bind(R.id.login_title)
    TextView login_title;
    @Bind(R.id.edtpassword)
    EditText edt_password;
    TextView toastText;
    Firebase ref;

    @OnClick(R.id.signup)
    void onSignUpClick() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login)
    void onClick() {
        Id = edt_Id.getText().toString();
        password = edt_password.getText().toString();
        Firebase depthref = ref.child("userData/" + Id);
        depthref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.child("Id").getValue().equals(Id)
                        && dataSnapshot.child("password").getValue().equals(password)) {
                    setToast("Login Success");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("PLAYER_ID",Id);
                    startActivity(intent);
                }
                else
                {
                    setToast("Wrong Id or password");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        edt_Id.setText("");
        edt_password.setText("");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Typeface font = Typeface.createFromAsset(getAssets(), "Bigfish.ttf");
        login_title.setTypeface(font);
        Firebase.setAndroidContext(this);
        ref = new Firebase("http://brilliant-heat-8278.firebaseio.com/");
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
