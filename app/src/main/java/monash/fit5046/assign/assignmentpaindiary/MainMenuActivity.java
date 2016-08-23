package monash.fit5046.assign.assignmentpaindiary;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;

import monash.fit5046.assign.assignmentpaindiary.Fragments.MainMenuFragment;
import monash.fit5046.assign.assignmentpaindiary.Fragments.HospitalLocFragment;
import monash.fit5046.assign.assignmentpaindiary.Fragments.RecordCreateFragment;
import monash.fit5046.assign.assignmentpaindiary.Fragments.RecordModifyFragment;
import monash.fit5046.assign.assignmentpaindiary.Fragments.ReportLocationFragment;
import monash.fit5046.assign.assignmentpaindiary.Fragments.ReportWeatherFragment;
import monash.fit5046.assign.assignmentpaindiary.entities.Registration;

public class MainMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static Registration loginUser;
    public static String loginUserId = "";

    MainMenuFragment mainMenuFragment = new MainMenuFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Menu");

        // Get login user
        Intent intent = getIntent();
        loginUser = (Registration) intent.getSerializableExtra("login_user");
        loginUserId = loginUser.getPatientid().toString();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.mm_fragment, mainMenuFragment).commit();

        /* Remove floating mail icon
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/

        // When clicking Back button, switch back to main menu
        if (getFragmentManager().getBackStackEntryCount() > 0 ) {
            getFragmentManager().beginTransaction().replace(R.id.mm_fragment, new MainMenuFragment()).commit();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment nextFragment = null;

        if (id == R.id.nav_mainMenu) {
            nextFragment = new MainMenuFragment();
            getSupportActionBar().setTitle("Main Menu");
        } else if (id == R.id.nav_recordCreate) {
            nextFragment = new RecordCreateFragment();
            getSupportActionBar().setTitle("Create Record");
        } else if (id == R.id.nav_recordModify) {
            nextFragment = new RecordModifyFragment();
            getSupportActionBar().setTitle("Modify Record");
        } else if (id == R.id.nav_reportWeather) {
            nextFragment = new ReportWeatherFragment();
            getSupportActionBar().setTitle("Pain-Weather Report");
        } else if (id == R.id.nav_reportLocation) {
            nextFragment = new ReportLocationFragment();
            getSupportActionBar().setTitle("Pain-Location Report");
        } else if (id == R.id.nav_map) {
            nextFragment = new HospitalLocFragment();
            getSupportActionBar().setTitle("Nearby Hospital");
        }

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.mm_fragment, nextFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
