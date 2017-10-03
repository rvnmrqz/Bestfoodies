package solomonkey.bestfoodies;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    static NavigationView navigationView;
    static ActionBar staticActionBar;
    static Context staticContext;
    static Activity staticActivity;
    static FragmentManager fragmentManager;
    static FragmentTransaction fragmentTransaction;
    Toolbar toolbar;
    static LinearLayout containerLayout;
    public static  MenuItem searchItem;
    static SearchView searchView;

    public static boolean homeIsShown=false;
    boolean searching=false;
    boolean backFromUser=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_side_navigation);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        staticActionBar = getSupportActionBar();
        staticContext = MainActivity.this;
        staticActivity = MainActivity.this;
        containerLayout = (LinearLayout) findViewById(R.id.main_container);
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
                changeBackstack(false,new Fragment_home(),"Home");
                break;
            case R.id.nav_categories:
                getSupportActionBar().setTitle("Categories");
                navigationView.getMenu().getItem(1).setChecked(true);
                changeBackstack(false,new Fragment_Categories(), "Categories");
                break;
            case R.id.nav_developers:
                navigationView.getMenu().getItem(2).setChecked(true);
                getSupportActionBar().setTitle("Developers");
                changeBackstack(false,new Fragment_Developers(), "Developers");
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void changeBackstack(boolean addToBackStack, Fragment fragment,String name){
        try{
            if (!searchView.isIconified() && !name.equalsIgnoreCase("Search")) {
                searchView.setIconified(true);
            }
            fragmentTransaction = fragmentManager.beginTransaction();
            if(addToBackStack){
                Log.wtf("changeBackstack","Adding to backstack");
                fragmentTransaction.replace(R.id.main_container,fragment);
                fragmentTransaction.addToBackStack(name);
            }else{
                Log.wtf("changeBackstack","Replacing backstack");
                fragmentTransaction.replace(R.id.main_container,fragment);
            }
            fragmentTransaction.commit();
            Log.wtf("changeBackstack","Backstack count: "+fragmentManager.getBackStackEntryCount());
        }catch (Exception ee){
            Log.wtf("addToBackStack","ERROR: "+ee.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        Log.wtf("onBackPressed","backstack count is  = "+fragmentManager.getBackStackEntryCount());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            Log.wtf("onBackPressed","Drawer is opened");
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!searchView.isIconified()) {
                Log.wtf("onBackPressed","search is not iconfied");
                backFromUser=true;
                super.onBackPressed();
                searchView.setIconified(true);
            } else {
                Log.wtf("onBackPressed","search is iconfied");
                Log.wtf("onBackPressed","Count= "+fragmentManager.getBackStackEntryCount()+" Home is shown: "+homeIsShown);

                if(fragmentManager.getBackStackEntryCount()==0 && homeIsShown){
                    finish();
                }
                else if(fragmentManager.getBackStackEntryCount()==0 && !homeIsShown){
                    navigationView.getMenu().getItem(0).setChecked(true);
                    changeBackstack(false,new Fragment_home(),"Home");
                }
                else{
                    super.onBackPressed();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        changeBackstack(false,new Fragment_home(),"Home");
        searchLayoutFunction();
        return true;
    }

    //SEARCH LAYOUT
    protected void searchLayoutFunction(){

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.wtf("searchView","Search is clicked");
                searching=true;
                changeBackstack(true,new Fragment_Search(),"Search");
            }
        });

        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchView.getQuery().toString().length()==0){
                    onBackPressed();
                }else{
                    searchView.setQuery("",false);
                    searchView.clearFocus();
                    Fragment_Search.updateDisplayInterface(true,false,"Search Something");
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.wtf("searchView","onQueryTextSubmit");
                if(Fragment_Search.staticContext!=null){
                    Fragment_Search.doSearch(query);
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });

    }

    protected static void clearBackstack(){
        for(int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }

}
