package com.game.android.mahfuzcse11.online_tic_tac_toe;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {


    EditText etInviteEmal, etMyEmail;
    Button buLogin;

    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String myEmail;

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
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    myEmail = user.getEmail();
                    buLogin.setEnabled(false);
                    etMyEmail.setText(myEmail);

                    myRef.child("Users").child(beforeAt(myEmail)).child("Request").setValue(user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


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
    }


    public void BuAccept(View view) {

        Log.d("Accept", etInviteEmal.getText().toString());
    }


    public void BuLogin(View view) {
        Log.d("Log in", etMyEmail.getText().toString());

        userLogin(etMyEmail.getText().toString(), "treehouse");

    }


    public void BuClick(View view) {
    }


}
