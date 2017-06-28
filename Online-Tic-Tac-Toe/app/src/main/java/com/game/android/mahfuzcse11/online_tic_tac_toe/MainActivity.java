package com.game.android.mahfuzcse11.online_tic_tac_toe;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {


    EditText etInviteEmal, etMyEmail;
    Button buLogin;


    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    String myEmail;
    String uid;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInviteEmal = (EditText) findViewById(R.id.etInviteEmal);
        etMyEmail = (EditText) findViewById(R.id.etMyEmail);
        buLogin = (Button) findViewById(R.id.buLogin);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            public static final String TAG = "Log in";

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    uid = user.getUid();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myEmail = user.getEmail();
                    buLogin.setEnabled(false);
                    etMyEmail.setText(myEmail);

                    myRef.child("Users").child(beforeAt(myEmail)).child("Request").setValue(user.getUid());

                    incomingRequest();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


    }


    void butonColor() {
        etInviteEmal.setBackgroundColor(Color.RED);
    }

    String beforeAt(String email) {

        String[] split = email.split("@");
        return split[0];
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    void userLogin(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    public static final String TAG = "Register";

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Failed ", Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }


    public void BuInvite(View view) {

        Log.d("Invite", etInviteEmal.getText().toString());

        myRef.child("Users").child(beforeAt(etInviteEmal.getText().toString())).child("Request").push().setValue(myEmail);
        startGame(beforeAt(etInviteEmal.getText().toString()) + ":" + beforeAt(myEmail));

        mySample = "X";
    }


    void incomingRequest() {

        myRef.child("Users").child(beforeAt(myEmail)).child("Request").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {

                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                    if (map != null) {

                        String value;

                        for (String k : map.keySet()) {

                            value = (String) map.get(k);
                            etInviteEmal.setText(value);
                            butonColor();
                            myRef.child("Users").child(beforeAt(myEmail)).child("Request").setValue(uid);

                            break;
                        }
                    }

                } catch (Exception e) {


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });

    }


    public void BuAccept(View view) {

        Log.d("Accept", etInviteEmal.getText().toString());
        myRef.child("Users").child(beforeAt(etInviteEmal.getText().toString())).child("Request").push().setValue(myEmail);
        startGame(beforeAt(myEmail) + ":" + etInviteEmal.getText().toString());
        mySample = "O";
    }


    String playerGameId = "";
    String mySample = "X";

    void startGame(String s) {
        playerGameId = s;
        myRef.child("Players").child(s).removeValue();


        myRef.child("Players").child(s).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                try {
                    Player1.clear();
                    Player2.clear();
                    ActivePlayer = 2;

                    String firsyPlayer = beforeAt(myEmail);
                    HashMap<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

                    if (map != null) {

                        String value;

                        for (String k : map.keySet()) {

                            value = (String) map.get(k);
                            if (!value.equals(beforeAt(myEmail))) {
                                ActivePlayer = mySample == "X" ? 1 : 2;
                            } else {

                                ActivePlayer = mySample == "X" ? 2 : 1;
                            }

                            firsyPlayer = value;

                            String[] split = k.split(":");
                            AutoPlay(Integer.parseInt(split[1]));
                        }
                    }

                } catch (Exception e) {


                }

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });


    }


    public void BuLogin(View view) {
        Log.d("Log in", etMyEmail.getText().toString());

        userLogin(etMyEmail.getText().toString(), "treehouse");

    }


    public void BuClick(View view) {

        if (playerGameId.length() <= 0) {
            return;
        }

        Button buSelected = (Button) view;
        int CellID = 0;
        switch ((buSelected.getId())) {

            case R.id.bu1:
                CellID = 1;
                break;

            case R.id.bu2:
                CellID = 2;
                break;

            case R.id.bu3:
                CellID = 3;
                break;

            case R.id.bu4:
                CellID = 4;
                break;

            case R.id.bu5:
                CellID = 5;
                break;

            case R.id.bu6:
                CellID = 6;
                break;

            case R.id.bu7:
                CellID = 7;
                break;

            case R.id.bu8:
                CellID = 8;
                break;

            case R.id.bu9:
                CellID = 9;
                break;
        }

        myRef.child("Players").child(playerGameId).child("CellID" + CellID).child(beforeAt(myEmail));

    }

    int ActivePlayer = 1; // 1- for first , 2 for second
    ArrayList<Integer> Player1 = new ArrayList<Integer>();// hold player 1 data
    ArrayList<Integer> Player2 = new ArrayList<Integer>();// hold player 2 data

    void PlayGame(int CellID, Button buSelected) {

        Log.d("Player:", String.valueOf(CellID));

        if (ActivePlayer == 1) {
            buSelected.setText("X");
            buSelected.setBackgroundColor(Color.GREEN);
            Player1.add(CellID);


        } else if (ActivePlayer == 2) {
            buSelected.setText("O");
            buSelected.setBackgroundColor(Color.BLUE);
            Player2.add(CellID);


        }

        buSelected.setEnabled(false);
        CheckWiner();
    }

    void CheckWiner() {
        int Winer = -1;
        //row 1
        if (Player1.contains(1) && Player1.contains(2) && Player1.contains(3)) {
            Winer = 1;
        }
        if (Player2.contains(1) && Player2.contains(2) && Player2.contains(3)) {
            Winer = 2;
        }

        //row 2
        if (Player1.contains(4) && Player1.contains(5) && Player1.contains(6)) {
            Winer = 1;
        }
        if (Player2.contains(4) && Player2.contains(5) && Player2.contains(6)) {
            Winer = 2;
        }

        //row 3
        if (Player1.contains(7) && Player1.contains(8) && Player1.contains(9)) {
            Winer = 1;
        }
        if (Player2.contains(7) && Player2.contains(8) && Player2.contains(9)) {
            Winer = 2;
        }


        //col 1
        if (Player1.contains(1) && Player1.contains(4) && Player1.contains(7)) {
            Winer = 1;
        }
        if (Player2.contains(1) && Player2.contains(4) && Player2.contains(7)) {
            Winer = 2;
        }

        //col 2
        if (Player1.contains(2) && Player1.contains(5) && Player1.contains(8)) {
            Winer = 1;
        }
        if (Player2.contains(2) && Player2.contains(5) && Player2.contains(8)) {
            Winer = 2;
        }


        //col 3
        if (Player1.contains(3) && Player1.contains(6) && Player1.contains(9)) {
            Winer = 1;
        }
        if (Player2.contains(3) && Player2.contains(6) && Player2.contains(9)) {
            Winer = 2;
        }


        if (Winer != -1) {
            // We have winer

            if (Winer == 1) {
                Toast.makeText(this, "Player 1 is winner", Toast.LENGTH_LONG).show();
            }

            if (Winer == 2) {
                Toast.makeText(this, "Player 2 is winner", Toast.LENGTH_LONG).show();
            }

        }

    }

    void AutoPlay(int CellID) {

        Button buSelected;
        switch (CellID) {

            case 1:
                buSelected = (Button) findViewById(R.id.bu1);
                break;

            case 2:
                buSelected = (Button) findViewById(R.id.bu2);
                break;

            case 3:
                buSelected = (Button) findViewById(R.id.bu3);
                break;

            case 4:
                buSelected = (Button) findViewById(R.id.bu4);
                break;

            case 5:
                buSelected = (Button) findViewById(R.id.bu5);
                break;

            case 6:
                buSelected = (Button) findViewById(R.id.bu6);
                break;

            case 7:
                buSelected = (Button) findViewById(R.id.bu7);
                break;

            case 8:
                buSelected = (Button) findViewById(R.id.bu8);
                break;

            case 9:
                buSelected = (Button) findViewById(R.id.bu9);
                break;
            default:
                buSelected = (Button) findViewById(R.id.bu1);
                break;

        }
        PlayGame(CellID, buSelected);
    }

}


