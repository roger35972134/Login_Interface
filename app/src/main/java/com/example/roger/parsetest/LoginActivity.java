package com.example.roger.parsetest;

import android.app.Notification;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mrengineer13.snackbar.SnackBar;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.ParseException;
import java.util.List;

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
    @OnClick(R.id.signup)
    void onSignUpClick() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login)
    void onClick() {
        Id = edt_Id.getText().toString();
        password = edt_password.getText().toString();
        ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", Id).whereEqualTo("password", password);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, com.parse.ParseException e) {
                if (e == null && objects.size() != 0) {
                    setToast("Login success");
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("PLAYER_ID",Id);
                    startActivity(intent);
                } else if (e == null) {
                    //Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    setToast("Account error");
                } else {
                    setToast("Server error");
                }
            }
        });
        edt_Id.setText("");
        edt_password.setText("");
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initParse();
        ButterKnife.bind(this);
        Typeface font=Typeface.createFromAsset(getAssets(),"Bigfish.ttf");
        login_title.setTypeface(font);
    }

    public void initParse() {
        try {
            Parse.initialize(this, "BAMBLehMHOD8NeRcVAaRMbRVgFEnfAuAmJm4QKpR", "uS2MUeHZHzya4rGTC3UlG0TI0gSEClxuKbnHFC60");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e) {
            //do nothing
        }

    }
    public void setToast(String message)
    {
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.layout_toast,(ViewGroup)findViewById(R.id.custom_toast));
        toastText=(TextView)layout.findViewById(R.id.toastText);
        toastText.setText(message);
        Toast toast=new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

}
