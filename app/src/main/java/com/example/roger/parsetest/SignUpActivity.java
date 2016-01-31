package com.example.roger.parsetest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    TextView toastText;
    //custom toast



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
            setToast("You should fill all the slots");
            flag = false;
        }
        ParseQuery query = ParseQuery.getQuery("UserData");
        query.whereEqualTo("Id", Id);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> objects, ParseException e) {
                if (objects.size() != 0) {
                    setToast("ID have been used by another user");
                    flag = false;
                } else if (e != null) {
                    setToast("Server error");
                }
            }
        });
    }


    @SuppressLint("SetTextI18n")
    public void updateData(String ID, String PW, String Email) {
        ParseObject userdata = new ParseObject("UserData");
        userdata.put("Id", ID);
        userdata.put("password", PW);
        userdata.put("email", Email);
        userdata.saveInBackground();
        setToast("Sign up success");

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

