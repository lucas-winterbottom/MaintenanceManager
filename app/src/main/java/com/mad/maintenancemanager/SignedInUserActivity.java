package com.mad.maintenancemanager;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mad.maintenancemanager.userActivites.GroupTasks;
import com.mad.maintenancemanager.userActivites.MyTasks;
import com.squareup.picasso.Picasso;

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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

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
