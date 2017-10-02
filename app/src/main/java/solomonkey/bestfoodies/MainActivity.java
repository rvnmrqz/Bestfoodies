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
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    static NavigationView navigationView;
    static ActionBar staticActionBar;
    static Context staticContext;
    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    static LinearLayout containerLayout, homeLayout;
    public static  MenuItem searchItem;
    static SearchView searchView;

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

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onBackPressed() {
        Log.wtf("onBackPressed","backstack count is  = "+fragmentManager.getBackStackEntryCount());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!searchView.isIconified()) {
                searchView.setIconified(true);
                super.onBackPressed();
            } else {
                if(fragmentManager.getBackStackEntryCount() == 0 && !homeLayout.isShown()){
                    clearBackstack();
                    navigationView.getMenu().getItem(0).setChecked(true);
                    getSupportActionBar().setTitle("Home");
                }else {
                    super.onBackPressed();
                }
            }
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
                navigationView.getMenu().getItem(0).setChecked(true);
                homeLayout.setVisibility(View.VISIBLE);
                containerLayout.setVisibility(View.INVISIBLE);
                break;
            case R.id.nav_categories:
                getSupportActionBar().setTitle("Categories");
                navigationView.getMenu().getItem(1).setChecked(true);
                homeLayout.setVisibility(View.INVISIBLE);
                changeBackstack(false,new Fragment_Categories(), "Categories");
                containerLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.nav_developers:
                navigationView.getMenu().getItem(2).setChecked(true);
                getSupportActionBar().setTitle("Developers");
                homeLayout.setVisibility(View.INVISIBLE);
                changeBackstack(false,new Fragment_Developers(), "Developers");
                containerLayout.setVisibility(View.VISIBLE);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void changeBackstack(boolean addToBackStack, Fragment fragment,String name){
        try{
            homeLayout.setVisibility(View.INVISIBLE);
            containerLayout.setVisibility(View.VISIBLE);

            if (!searchView.isIconified() && !name.equalsIgnoreCase("Search")) {
                searchView.setIconified(true);
            }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("searchView","Search is clicked");
                changeBackstack(true,new Fragment_Search(),"Search");
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.wtf("searchView","onQueryTextSubmit");

                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.wtf("searchView","onQueryTextChange");
                return false;
            }
        });

        return true;
    }
}
