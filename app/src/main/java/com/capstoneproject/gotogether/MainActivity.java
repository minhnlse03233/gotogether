package com.capstoneproject.gotogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    String id,name,email;
    ProfilePictureView profilePictureView;
    TextView txtName, txtEmail, txtTest;
    Button btnQuickSearch, btnSearch, btnPost;
    boolean fragmentIsShow = false;
    int PLACE_PICKER_REQUEST = 1;
//    MapAdapter mapAdapter = MapAdapter.getInstance();
    GoogleMap googleMap;
    MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        btnQuickSearch = (Button) findViewById(R.id.btn_quick_search);
        btnQuickSearch.setOnClickListener(this);

        btnSearch = (Button) findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(this);

        btnPost = (Button) findViewById(R.id.btn_post);
        btnPost.setOnClickListener(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//
//        mapAdapter.setGoogleMap(googleMap);
//        mapAdapter.setMapView(mapView);

//        mapAdapter = new MapAdapter(googleMap, mapView);
//        mapAdapter.createMap();

//        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        //to save
//        SharedPreferences.Editor prefsEditor = mPrefs.edit();
//        Gson gson = new Gson();
//        String json = gson.toJson(mapAdapter);
//        prefsEditor.putString("MyMapAdapter", json);
//        prefsEditor.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // nhận intent
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("profile");

        View headerView = navigationView.getHeaderView(0);
        txtName = (TextView) headerView.findViewById(R.id.txtName);
        txtEmail = (TextView) headerView.findViewById(R.id.txtEmail);

        // get data từ intent (Login activity)
        id = bundle.getString("id");
        name = bundle.getString("name");
        email = bundle.getString("email");

        ImageView profilePic = (ImageView) headerView.findViewById(R.id.imageView);
        // tranform từ image facebook sang imageView. Hiển thị ảnh hình tròn lên navigation
        Picasso.with(this).load("https://graph.facebook.com/v2.7/" + id + "/picture?height=120&type=small").transform(new CircleTransform()).resize(120, 120).into(profilePic);
        txtName.setText(name);
        txtEmail.setText(email);

    }

    private void goLoginScreen() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }
    public void logout() {
        LoginManager.getInstance().logOut();
        goLoginScreen();
        finish();
    }

    public void onResume() {

        super.onResume();
//        Toast.makeText(this, "Chào mừng quay lại ứng dụng", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        RelativeLayout relativeLayout;
        relativeLayout = (RelativeLayout) findViewById(R.id.quick_search_fragment);
        if((relativeLayout == null && fragmentIsShow == false) || (relativeLayout != null && fragmentIsShow == false))
            super.onBackPressed();
        else {
            relativeLayout.setVisibility(View.INVISIBLE);
            btnQuickSearch.setVisibility(View.VISIBLE);
            btnSearch.setVisibility(View.VISIBLE);
            btnPost.setVisibility(View.VISIBLE);
            fragmentIsShow = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id ==R.id.logout) {
            logout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        int wiget = v.getId();
        switch (wiget) {
            case R.id.btn_quick_search:
                RelativeLayout relativeLayout;
                relativeLayout = (RelativeLayout) findViewById(R.id.quick_search_fragment);
                if(relativeLayout == null){
                    QuickSearchFragment quickSearchFragment = new QuickSearchFragment();
                    FragmentManager fragmentManager =  getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_activity, quickSearchFragment).commit();
                }
                else{
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                btnQuickSearch.setVisibility(View.INVISIBLE);
                btnSearch.setVisibility(View.INVISIBLE);
                btnPost.setVisibility(View.INVISIBLE);
                fragmentIsShow = true;
                break;
            case R.id.btn_search:
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = intentBuilder.build(getApplicationContext());
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
//        if(requestCode == PLACE_PICKER_REQUEST){
//            if(resultCode == RESULT_OK){
//                Place place = PlacePicker.getPlace(data, this);
//                Toast.makeText(this, "Vi tri: " + place.getName(),Toast.LENGTH_LONG).show();
////                String address = String.format("Place")
//            }
//        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
