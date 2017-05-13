package com.mad.maintenancemanager;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class SignedInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mEmailTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in);


        //Firebase Stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        Toast.makeText(SignedInActivity.this, user.getEmail() + " is signed in", Toast.LENGTH_SHORT).show();
        mEmailTv = (TextView) findViewById(R.id.signed_in_email_tv);
        mEmailTv.setText(user.getDisplayName() + " is Signed in");


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(Constants.FIREBASE, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(Constants.FIREBASE, "onAuthStateChanged:signed_out");
                    Toast.makeText(SignedInActivity.this, "No user signed in", Toast.LENGTH_SHORT).show();

                }

            }
        };


        Button signoutBtn = (Button) findViewById(R.id.signed_in_sign_out);
        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(SignedInActivity.this, LoginActivity.class));
                finish();
            }
        });


    }

    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, SignedInActivity.class);
        return in;
    }
}
