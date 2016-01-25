package com.example.roger.parsetest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.parse.ParseObject;

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
    @OnClick(R.id.imgCheck) void onClick(){
        String id=signId.getText().toString();
        String password=signPw.getText().toString();
        String email=signEmail.getText().toString();
        if(!id.isEmpty()&&!password.isEmpty()&&!email.isEmpty())
        {
            updateData(id, password, email);
            Toast.makeText(this,"Sign up success",Toast.LENGTH_LONG);
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(this,"You should fill all the slots",Toast.LENGTH_LONG).show();
        }
    }
    @OnClick(R.id.returnpage) void onReturnClick(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);
    }
    public void updateData(String ID, String PW,String Email) {
        ParseObject userdata = new ParseObject("UserData");
        userdata.put("Id", ID);
        userdata.put("password", PW);
        userdata.put("email",Email);
        userdata.saveInBackground();
    }
}

