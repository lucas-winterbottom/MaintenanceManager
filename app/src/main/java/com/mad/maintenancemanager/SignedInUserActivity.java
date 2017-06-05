package com.mad.maintenancemanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.mad.maintenancemanager.api.DatabaseHelper;
import com.mad.maintenancemanager.useractivites.CompletedTasksFragment;
import com.mad.maintenancemanager.useractivites.GroupFragment;
import com.mad.maintenancemanager.useractivites.GroupTasks;
import com.mad.maintenancemanager.useractivites.MyTasks;
import com.squareup.picasso.Picasso;

import com.mad.maintenancemanager.useractivites.SplashFragment;

/**
 * Activity that is the basis for a user that is signed in and has a navigation drawer
 * to navigate between fragments
 */
public class SignedInUserActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GroupFragment.INavUnlocker {

    private FirebaseAuth mAuth;
    private TextView mEmailTv;
    private TextView mNameTV;
    private ImageView mUserImage;
    protected NavigationView navigationView;
    protected FirebaseUser mUser;
    private DrawerLayout mDrawer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signed_in_user);

        //Date Stuff
        AndroidThreeTen.init(this);

        //Firebase Stuff
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        //If trade login


        //My Stuff
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(toggle);
        toggle.syncState();

        splashScreen();
        DatabaseHelper.getInstance().setGeneralUserData(new DatabaseHelper.IGroupKeyListener() {
            @Override
            public void onGroupKey(String key) {
                if (key != null) {
                    unlockNavDrawer();
                    onNavigationItemSelected(navigationView.getMenu().getItem(0));
                } else {
                    onNavigationItemSelected(navigationView.getMenu().getItem(3));
                }
            }
        });


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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


    }

    private void splashScreen() {
        Fragment fragment = new SplashFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        ft.replace(R.id.frame_layout, fragment).commit();
        lockNavDrawer();
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.signed_in_user, menu);
        return true;
    }

    private void lockNavDrawer() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    public void unlockNavDrawer() {
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
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
                fragment = new CompletedTasksFragment();
            } else if (id == R.id.nav_group) {
                fragment = new GroupFragment();
            } else if (id == R.id.nav_sign_out) {
                mAuth.signOut();
                DatabaseHelper.getInstance().userLogout();
                startActivity(new Intent(SignedInUserActivity.this, LoginActivity.class));
                finish();
            }
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            ft.replace(R.id.frame_layout, fragment).commit();
            // update selected item and title, then close the drawer
            item.setChecked(true);
            getSupportActionBar().setTitle(item.getTitle());
            mDrawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }


}

