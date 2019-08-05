package com.pratamatechnocraft.smarttempatsampah;

import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.pratamatechnocraft.smarttempatsampah.Fragment.HomeFragment;


import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public Fragment fragment = null;
    int fragmentLast;
    NavigationView navigationView;
    //SessionManager sessionManager;
    int idMenuItem=R.id.nav_home;
    int tmpidMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem( idMenuItem );
        displaySelectedScreen(idMenuItem);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        if (drawer.isDrawerOpen( GravityCompat.START )) {
            drawer.closeDrawer( GravityCompat.START );
            //sessionManager.checkLogin();
        } else {
            navigationView.setCheckedItem( tmpidMenuItem );
            super.onBackPressed();
            //sessionManager.checkLogin();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen( item.getItemId() );
        tmpidMenuItem = idMenuItem;
        idMenuItem=item.getItemId();
        return true;
    }

    private void displaySelectedScreen(int itemId) {
        int id = itemId;
        tmpidMenuItem = idMenuItem;
        idMenuItem=id;
        if (id == R.id.nav_home) {
            fragment = new HomeFragment();
        } else if (id == R.id.nav_logout) {
            //sessionManager.logout();
        }

        if (fragment != null) {
            if (fragmentLast!=id){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }else if(id==R.id.nav_home){
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.screen_area, fragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        fragmentLast = id;

        DrawerLayout drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
        drawer.closeDrawer( GravityCompat.START );
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
