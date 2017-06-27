package com.game.android.mahfuzcse11.online_tic_tac_toe;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {


    EditText etInviteEmal, etMyEmail;
    Button buLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInviteEmal = (EditText) findViewById(R.id.etInviteEmal);
        etMyEmail = (EditText) findViewById(R.id.etMyEmail);
        buLogin = (Button) findViewById(R.id.buLogin);
    }

    public void BuInvite(View view) {

        Log.d("Invite", etInviteEmal.getText().toString());
    }

    public void BuAccept(View view) {

        Log.d("Accept", etInviteEmal.getText().toString());
    }

    public void BuLogin(View view) {
        Log.d("Log in", etMyEmail.getText().toString());
    }

    public void BuClick(View view) {
    }
}
