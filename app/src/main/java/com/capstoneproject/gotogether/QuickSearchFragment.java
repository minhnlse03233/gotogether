package com.capstoneproject.gotogether;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstoneproject.gotogether.model.Trip;
import com.capstoneproject.gotogether.presenter.quicksearch.IQuickSearchPresenter;
import com.capstoneproject.gotogether.presenter.quicksearch.QuickSearchPresenter;
import com.capstoneproject.gotogether.view.quicksearch.IQuickSearchView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class QuickSearchFragment extends Fragment implements IQuickSearchView, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GoogleMap googleMap;
    MapView mapView;
    private ProgressDialog myProgress;
    public static final int REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;
    private List<Polyline> polylinePaths = new ArrayList<>();
    Trip currentTrip;
    ArrayList<Trip> currentTripsMap = new ArrayList<>();
    List<LatLng> points;
    LatLng latLngStart;
    LatLng latLngEnd;
    MarkerOptions option;
    Marker currentMarker;
    double distance;
    Polyline polyline;

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

//        googleMap.setOnMarkerClickListener(this);
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
//        super.onResume();
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
//        } );
//        currentLocation = getCurrentLatLng();
//        iQuickSearchPresenter.loadTrip(currentLocation);
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
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

        //currentLocation = getCurrentLatLng();
//        iQuickSearchPresenter.loadTrip(currentLocation);
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
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
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnInfoWindowClickListener(this);

        if(myLocation != null){
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            LatLng latLng = new LatLng(dLatitude, dLongitude);

            iQuickSearchPresenter.loadTrip(latLng);

//            option = new MarkerOptions();
//            option.title("Vị trí của bạn");

            //String currentNameOfLocal = getNameOfProvince(latLng);
            //option.snippet(currentNameOfLocal);
//            option.position(latLng);
//            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));

//            currentMarker = googleMap.addMarker(option);
//            currentMarker.showInfoWindow();

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String getNameOfProvince(LatLng latLng){
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
            if(address == null || address.equals("Unnamed Road"))
                address = "";
            if(city == null)
                city = "";
            if(state == null)
                state = "";
            if(district == null)
                district = "";

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

    @Override
    public void returnTrip(ArrayList<Trip> currentTrips) {

//        Toast.makeText(getContext(), "AA: " + currentTrips.size(), Toast.LENGTH_LONG).show();
        for (int i = 0; i < currentTrips.size(); i++){
            currentTripsMap = currentTrips;
        }
        getJSONPolyline(currentTripsMap);
    }

    public void getJSONPolyline(ArrayList<Trip> currentTripsMap){
        String url = "";
        for (int i = 0; i < currentTripsMap.size(); i++){
            currentTrip = new  Trip(currentTripsMap.get(i).getTripId(), currentTripsMap.get(i).getUserId(), currentTripsMap.get(i).getTitle(),
                    currentTripsMap.get(i).getDescription(), currentTripsMap.get(i).getDate_start(), currentTripsMap.get(i).getSlot(),
                    currentTripsMap.get(i).getStart_lat(), currentTripsMap.get(i).getEnd_lat(), currentTripsMap.get(i).getStart_lng(),
                    currentTripsMap.get(i).getEnd_lng(), currentTripsMap.get(i).getListLatLng(), currentTripsMap.get(i).getStatus(), currentTripsMap.get(i).getDistance());

            latLngStart = new LatLng(currentTrip.getStart_lat(), currentTrip.getStart_lng());
            latLngEnd = new LatLng(currentTrip.getEnd_lat(), currentTrip.getEnd_lng());

            distance = currentTripsMap.get(i).getDistance();
            distance = (double) Math.round(distance * 100) / 100;

//            drawPolyline(latLngStart, latLngEnd);

//            option = new MarkerOptions();
//            option.title(distance + " km");
//            option.snippet(getNameOfProvince(latLngEnd));
//            option.position(new LatLng(latLngStart.latitude, latLngStart.longitude));
//            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
//            currentMarker = googleMap.addMarker(option);
//            currentMarker.showInfoWindow();

            if(i == 0){
                googleMap.addMarker(new MarkerOptions()
                        .title("Click để xem thông tin chi tiết")
                        .snippet(distance + " km")
                        .position(new LatLng(latLngStart.latitude, latLngStart.longitude))
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            }

            googleMap.addMarker(new MarkerOptions()
                    .title("Click để xem thông tin chi tiết")
                    .snippet(distance + " km")
                    .position(new LatLng(latLngStart.latitude, latLngStart.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));



        }
    }

    public void drawPolyline(LatLng latLngStart, LatLng latLngEnd){

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + latLngStart.latitude + "," + latLngStart.longitude + "&destination=" + latLngEnd.latitude + "," + latLngEnd.longitude;
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonRoutes = jsonObject.getJSONArray("routes");

                    for (int i = 0; i < jsonRoutes.length(); i++){
                        JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                        JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
                        points = PolyUtil.decode(overview_polylineJson.getString("points"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                polylinePaths = new ArrayList<>();
                PolylineOptions polylineOptions = new PolylineOptions().
                        geodesic(true).
                        color(Color.RED).
                        width(8);
                for (int i = 0; i < points.size(); i++)
                    polylineOptions.add(points.get(i));
//                polylinePaths.add(googleMap.addPolyline(polylineOptions));
                polyline = googleMap.addPolyline(polylineOptions);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

//        if (marker.equals(currentMarker))
//        {
//            Toast.makeText(getContext(), marker.getSnippet(),Toast.LENGTH_LONG).show();
//        }
        if(polyline != null)
            polyline.remove();

        LatLng latLngStart;
        LatLng latLngEnd;
        for (int i = 0; i < currentTripsMap.size(); i++){
            if(currentTripsMap.get(i).getStart_lat() == marker.getPosition().latitude &&
                    currentTripsMap.get(i).getStart_lng() == marker.getPosition().longitude){
                latLngStart = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                latLngEnd = new LatLng(currentTripsMap.get(i).getEnd_lat(), currentTripsMap.get(i).getEnd_lng());
                drawPolyline(latLngStart, latLngEnd);
                break;
            }
        }

        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(polyline != null)
            polyline.remove();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

        ShowInfoTripFragment showInfoTripFragment = new ShowInfoTripFragment();
//        this.getFragmentManager().beginTransaction()
//                .replace(R.id.quick_search_fragment, showInfoTripFragment)
//                .addToBackStack(null)
//                .commit();
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.quick_search_fragment, showInfoTripFragment).commit();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.quick_search_fragment, showInfoTripFragment).commit();
        FrameLayout frameLayout;
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.quick_search_fragment);
        frameLayout.setVisibility(View.INVISIBLE);
//        Toast.makeText(getContext(), "Mở ra thông tin chi tiết",Toast.LENGTH_LONG).show();
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.quick_search_fragment, showInfoTripFragment).commit();
    }
}
