package com.example.roger.parsetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

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
    @Bind(R.id.edtpassword)
    EditText edt_password;

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
                if (e == null&& objects.size()!=0) {
                    Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_LONG).show();
                    //Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    //startActivity(intent);
                } else if(e==null) {
                    //Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
                    Toast.makeText(LoginActivity.this, "Account error", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Server error",Toast.LENGTH_LONG).show();
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
    }

    public void initParse() {
        try {
            Parse.initialize(this, "BAMBLehMHOD8NeRcVAaRMbRVgFEnfAuAmJm4QKpR", "uS2MUeHZHzya4rGTC3UlG0TI0gSEClxuKbnHFC60");
            ParseInstallation.getCurrentInstallation().saveInBackground();
        } catch (Exception e) {
            //do nothing
        }

    }

}
