package com.mad.maintenancemanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mad.maintenancemanager.model.User;
import com.mad.maintenancemanager.useractivites.GroupFragment;
import com.mad.maintenancemanager.useractivites.GroupTasks;
import com.mad.maintenancemanager.useractivites.MyTasks;
import com.squareup.picasso.Picasso;

/**
 * Activity that is the basis for a user that is signed in and has a navigation drawer
 * to navigate between fragments
 */
public class SignedInUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView mEmailTv;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TextView mNameTV;
    private ImageView mUserImage;
    protected NavigationView navigationView;
    protected FirebaseUser mUser;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in_user);
        //My Stuff
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        //Firebase Stuff
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        //Populate navigation drawer with user data
        mEmailTv = (TextView) navigationView.getHeaderView(0).
                findViewById(R.id.signed_in_user_email);
        mEmailTv.setText(mUser.getEmail());

        mNameTV = (TextView) navigationView.getHeaderView(0).
                findViewById(R.id.signed_in_username);
        mNameTV.setText(mUser.getDisplayName());

        mUserImage = (ImageView) navigationView.getHeaderView(0).
                findViewById(R.id.signed_in_user_image);
        Picasso.with(SignedInUserActivity.this).load(mUser.getPhotoUrl()).
                into(mUserImage);
        checkUserData(mUser.getUid(), mUser.getDisplayName());

        mAuthListener = new FirebaseAuth.AuthStateListener()

        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(Constants.FIREBASE, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(Constants.FIREBASE, "onAuthStateChanged:signed_out");

                }
            }
        };

    }


    /**
     * Checks if the user already has data on the server if not, sets up the base user on the server
     *
     * @param currentUserID The currently signed in users ID
     * @param displayName   The Currently signed in users Display Name
     */
    private void checkUserData(final String currentUserID, final String displayName) {
        final DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference(Constants.USERS).child(currentUserID);
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    dataRef.setValue(new User(displayName, null, false));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Creates an intent that includes the extra data from login in the intent
     *
     * @param context     Application Context
     * @param idpResponse Response from login services
     * @return The intent that includes the IDP data
     */
    public static Intent createIntent(Context context, IdpResponse idpResponse) {
        Intent in = IdpResponse.getIntent(idpResponse);
        in.setClass(context, SignedInUserActivity.class);
        return in;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signed_in_user, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = new Fragment();
        if (!item.isChecked()) {
            if (id == R.id.nav_my_tasks) {
                fragment = new MyTasks();
            } else if (id == R.id.nav_group_tasks) {
                fragment = new GroupTasks();
            } else if (id == R.id.nav_completed_tasks) {

            } else if (id == R.id.nav_group) {
                fragment = new GroupFragment();
            } else if (id == R.id.nav_account_settings) {

            } else if (id == R.id.nav_sign_out) {
                mAuth.signOut();
                startActivity(new Intent(SignedInUserActivity.this, LoginActivity.class));
                finish();
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, fragment).commit();

            // update selected item and title, then close the drawer
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
            mDrawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

}
