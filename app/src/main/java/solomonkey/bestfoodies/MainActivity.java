package solomonkey.bestfoodies;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static ActionBar staticActionBar;
    static Context staticContext;
    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    static LinearLayout containerLayout, homeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        staticActionBar = getSupportActionBar();
        staticContext = MainActivity.this;

        containerLayout = (LinearLayout) findViewById(R.id.main_container);
        homeLayout = (LinearLayout) findViewById(R.id.main_home);
        fragmentManager = getSupportFragmentManager();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id){
            case R.id.nav_home:
                clearBackstack();
                getSupportActionBar().setTitle("Home");
                homeLayout.setVisibility(View.VISIBLE);
                containerLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_categories:
                getSupportActionBar().setTitle("Categories");
                homeLayout.setVisibility(View.INVISIBLE);
                changeBackstack(false,new Fragment_Categories(), "categories");
                containerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_developers:
                getSupportActionBar().setTitle("Developers");
                homeLayout.setVisibility(View.INVISIBLE);
                changeBackstack(false,new Fragment_Developers(), "developers");
                containerLayout.setVisibility(View.VISIBLE);
                break;
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void changeBackstack(boolean addToBackStack, Fragment fragment,String name){
        try{
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_container,fragment);
            if(addToBackStack){
                fragmentTransaction.addToBackStack(name);
            }
            fragmentTransaction.commit();
        }catch (Exception ee){
            Log.wtf("addToBackStack","ERROR: "+ee.getMessage());
        }
    }

    protected static void clearBackstack(){
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
        containerLayout.setVisibility(View.INVISIBLE);
        homeLayout.setVisibility(View.VISIBLE);
    }



}
