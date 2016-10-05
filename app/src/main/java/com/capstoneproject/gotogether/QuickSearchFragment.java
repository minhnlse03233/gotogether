package com.capstoneproject.gotogether;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
<<<<<<< HEAD
import android.widget.Button;
=======
import android.widget.FrameLayout;
>>>>>>> bd5da0bd47b5efb2288c66b2da50cae6dba0c4f1
import android.widget.Toast;

import com.capstoneproject.gotogether.presenter.quicksearch.IQuickSearchPresenter;
import com.capstoneproject.gotogether.presenter.quicksearch.QuickSearchPresenter;
import com.capstoneproject.gotogether.view.quicksearch.IQuickSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class QuickSearchFragment extends Fragment implements IQuickSearchView {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GoogleMap googleMap;
    MapView mapView;
    private ProgressDialog myProgress;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    Button quickSearch;

    IQuickSearchPresenter iQuickSearchPresenter;


    public QuickSearchFragment() {}

    public static QuickSearchFragment newInstance(String param1, String param2) {
        QuickSearchFragment fragment = new QuickSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        // Tạo Progress Bar
        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("Đang tải bản đồ ...");
        myProgress.setMessage("Xin vui lòng đợi");
        myProgress.setCancelable(true);

        iQuickSearchPresenter = new QuickSearchPresenter(this);
        iQuickSearchPresenter.showProgressBar();


    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getView().setVisibility(View.GONE);
                    return true;
                }
                return false;
            }
        } );
        //super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_quick_search, container, false);


        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                // For showing a move to my location button
                googleMap.setMyLocationEnabled(true);
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setTrafficEnabled(true);
                onMyMapReady(mMap);
            }
        });
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
//        getView().setFocusableInTouchMode(true);
//        getView().requestFocus();
//        getView().setOnKeyListener( new View.OnKeyListener()
//        {
//            @Override
//            public boolean onKey( View v, int keyCode, KeyEvent event )
//            {
//                if( keyCode == KeyEvent.KEYCODE_BACK )
//                {
//                    getView().setVisibility(View.GONE);
//                    return true;
//                }
//                return false;
//            }
//        });
        super.onResume();
    }

    private void onMyMapReady(GoogleMap mMap) {
        // Lấy đối tượng Google Map ra:
        googleMap = mMap;
        // Thiết lập sự kiện đã tải Map thành công
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
//                myProgress.dismiss();
                askPermissionsAndShowMyLocation();
            }
        });
    }


    private void askPermissionsAndShowMyLocation() {
        // Với API >= 23, bạn phải hỏi người dùng cho phép xem vị trí của họ.
        if (Build.VERSION.SDK_INT >= 23) {
            int accessCoarsePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission
                    = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
            if (accessCoarsePermission != PackageManager.PERMISSION_GRANTED
                    || accessFinePermission != PackageManager.PERMISSION_GRANTED) {
                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION};
                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);
                return;
            }
        }
        // Hiển thị vị trí hiện thời trên bản đồ.
        iQuickSearchPresenter.dissProgressBar();
        getMyCurrentLocal();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    iQuickSearchPresenter.dissProgressBar();
                    getMyCurrentLocal();
                } else {
                    Toast.makeText(getContext(), "Tính năng truy cập GPS đã bị từ chối", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void getMyCurrentLocal(){

        Location myLocation  = googleMap.getMyLocation();

        if(myLocation != null){
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(dLatitude, dLongitude);

            MarkerOptions option = new MarkerOptions();
            option.title("Vị trí của bạn");

            String currentNameOfLocal = getNameOfProvince(latLng);
            option.snippet(currentNameOfLocal);
            option.position(latLng);
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

            Marker currentMarker = googleMap.addMarker(option);
            currentMarker.showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String getNameOfProvince (LatLng latLng){
        android.location.Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        String fnialAddress = "";

        try {

            List<Address> addressList;
            addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1);

            String address = addressList.get(0).getAddressLine(0);
            String city = addressList.get(0).getLocality();
            String state = addressList.get(0).getAdminArea();
            String district = addressList.get(0).getSubAdminArea();
            String country = addressList.get(0).getCountryName();
            String postalCode = addressList.get(0).getPostalCode();
            String knownName = addressList.get(0).getFeatureName();
            if(address == null){
                fnialAddress = district + country;
            }
            fnialAddress = address + " " + district + " " + city + " " + state;
        } catch (IOException e) {}
        catch (NullPointerException e) {}
        if(fnialAddress != null)
            return fnialAddress;
        else
            return "Cannot receive name of location";
    }

    @Override
    public void showProgressBar() {
        myProgress.show();
    }

    @Override
    public void dissProgressBar() {
        myProgress.dismiss();
    }
}
