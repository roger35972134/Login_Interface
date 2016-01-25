package com.example.roger.parsetest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nispok.snackbar.SnackbarManager;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SignUpActivity extends Activity {
    boolean flag = true;
    @Bind(R.id.signID)
    EditText signId;
    @Bind(R.id.signPW)
    EditText signPw;
    @Bind(R.id.signEmail)
    EditText signEmail;

    @OnClick(R.id.imgCheck)
    void onClick() {
        final String id = signId.getText().toString();
        final String password = signPw.getText().toString();
        final String email = signEmail.getText().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (flag) {
                    updateData(id, password, email);
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });
        Check(id, password, email);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.run();
    }

    @OnClick(R.id.returnpage)
    void onReturnClick(View v) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }

    public void Check(String Id, String Password, String Email) {
        flag = true;
        if (Id.isEmpty() && Password.isEmpty() && Email.isEmpty()) {
            Toast.makeText(this, "You should fill all the slots", Toast.LENGTH_LONG).show();
            flag = false;
        }
        ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", Id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    Toast.makeText(SignUpActivity.this, "ID have been used by another user", Toast.LENGTH_LONG).show();
                    flag = false;
                } else if (e != null) {
                    Toast.makeText(SignUpActivity.this, "Server error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void updateData(String ID, String PW, String Email) {
        ParseObject userdata = new ParseObject("UserData");
        userdata.put("Id", ID);
        userdata.put("password", PW);
        userdata.put("email", Email);
        userdata.saveInBackground();
        Toast.makeText(SignUpActivity.this, "Sign up success", Toast.LENGTH_LONG).show();

    }
}

