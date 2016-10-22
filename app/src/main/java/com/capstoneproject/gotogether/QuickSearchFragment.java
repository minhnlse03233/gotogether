package com.capstoneproject.gotogether;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstoneproject.gotogether.adapter.PlacesAutoCompleteAdapter;
import com.capstoneproject.gotogether.adapter.RecycleItemClickListener;
import com.capstoneproject.gotogether.adapter.TripAdapter;
import com.capstoneproject.gotogether.model.Trip;
import com.capstoneproject.gotogether.presenter.quicksearch.IQuickSearchPresenter;
import com.capstoneproject.gotogether.presenter.quicksearch.QuickSearchPresenter;
import com.capstoneproject.gotogether.view.quicksearch.IQuickSearchView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
        GoogleMap.OnMapClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMyLocationChangeListener,
        View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private GoogleMap googleMap;
    MapView mapView;
    RelativeLayout layoutListView, layoutGMap, rowTrip;
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
    Button btnListTrip;
    ListView listView;
    EditText mAutocompleteView;
    ImageView btnDelete;
//    protected GoogleApiClient mGoogleApiClient;
//    PlacesAutoCompleteAdapter mAutoCompleteAdapter;
//    RecyclerView mRecyclerView;
//    LinearLayoutManager mLinearLayoutManager;
//    LatLngBounds myBound = new LatLngBounds(
//            new LatLng(-0, 0), new LatLng(0, 0));
    int PLACE_PICKER_REQUEST = 1;
    TextView tvEnd;
    RelativeLayout searchTextView;
    LatLng endLocation;
    IQuickSearchPresenter iQuickSearchPresenter;
//    MarkerOptions endMarker;
    Marker endMarker;


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
        myProgress.setTitle("Đang tải bản đồ...");
        myProgress.setMessage("Xin vui lòng đợi");
        myProgress.setCancelable(false);

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

//        mAutocompleteView = (EditText)rootView.findViewById(R.id.autocomplete_places);
//        btnDelete = (ImageView)rootView.findViewById(R.id.btn_delete);
//        btnDelete.setOnClickListener(this);
        tvEnd = (TextView) rootView.findViewById(R.id.text_view_end);
        searchTextView = (RelativeLayout) rootView.findViewById(R.id.search_text_view);
        searchTextView.setOnClickListener(this);

//        searchTextView.setPressed(true);
//        searchTextView.setHovered(true);
//        searchTextView.setFocusable(true);

//        buildGoogleAPIClient();
//        mAutoCompleteAdapter =  new PlacesAutoCompleteAdapter(this, R.layout.row_tip,
//                mGoogleApiClient, myBound  , null);
//
//        mRecyclerView=(RecyclerView)rootView.findViewById(R.id.recyclerView);
//        mLinearLayoutManager=new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView.setAdapter(mAutoCompleteAdapter);

//        mAutocompleteView.setOnClickListener(this);

//        mAutocompleteView.addTextChangedListener(new TextWatcher(){
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
//                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                }else if(!mGoogleApiClient.isConnected()){
//                    Toast.makeText(getActivity().getApplicationContext(), "Không thể kết nối Google API",Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        mRecyclerView.addOnItemTouchListener(
//                new RecycleItemClickListener(this, new RecycleItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
//                        final String placeId = String.valueOf(item.placeId);
//                        Log.i("TAG", "Autocomplete item selected: " + item.description);
//
//                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
//                                .getPlaceById(mGoogleApiClient, placeId);
//                        placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
//                            @Override
//                            public void onResult(PlaceBuffer places) {
//                                if(places.getCount() == 1){
//                                    //Do the things here on Click.....
//                                    Toast.makeText(getActivity().getApplicationContext(),String.valueOf(places.get(0).getLatLng()),Toast.LENGTH_SHORT).show();
//                                    //Toast.makeText(getActivity().getApplicationContext(),String.valueOf(places.get(0).getAddress()),Toast.LENGTH_SHORT).show();
//                                    mAutocompleteView.setText("");
//                                    mAutocompleteView.setText(String.valueOf(places.get(0).getAddress()));
//                                }else {
//                                    Toast.makeText(getActivity().getApplicationContext(),"Đã xảy ra lỗi, xin tắt ứng dụng rồi mở lại",Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                        Log.i("TAG", "Clicked: " + item.description);
//                        Log.i("TAG", "Called getPlaceById to get Place details for " + item.placeId);
//                    }
//                })
//        );

        btnListTrip = (Button) rootView.findViewById(R.id.btn_list_trip);
        btnListTrip.setOnClickListener(this);
        btnListTrip.setText("DANH SÁCH CHUYẾN ĐI");

        layoutListView = (RelativeLayout) rootView.findViewById(R.id.list_trip);
        layoutGMap = (RelativeLayout) rootView.findViewById(R.id.gMap);
        listView = (ListView) rootView.findViewById(R.id.listView);


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
                View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                // position on right bottom
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                rlp.setMargins(0, 700, 22, 0);

                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.setTrafficEnabled(true);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(14.058324, 108.277199))             // Sets the center of the map to location user
                        .zoom(5)                   // Sets the zoom
//                      .bearing(90)                // Sets the orientation of the camera to east
//                       .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                iQuickSearchPresenter.dissProgressBar();
                //onMyMapReady(mMap);
            }
        });

//        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//            @Override
//            public void onMapLoaded() {
//                iQuickSearchPresenter.dissProgressBar();
//            }
//        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void onMyMapReady(GoogleMap mMap) {
        googleMap = mMap;
        // Thiết lập sự kiện đã tải Map thành công
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
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
//        iQuickSearchPresenter.dissProgressBar();
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

            iQuickSearchPresenter.loadTrip(latLng, endLocation);

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
                    .zoom(12)                   // Sets the zoom
//                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String getNameOfProvince(LatLng latLng){
        android.location.Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        String finalAddress = "";

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

            finalAddress = address + " " + district + " " + city + " " + state;
        } catch (IOException e) {}
        catch (NullPointerException e) {}
        if(finalAddress != null)
            return finalAddress;
        else
            return "Chưa biết địa chỉ hiện tại của bạn";
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
    public void returnTrip(final ArrayList<Trip> currentTrips) {
        if(currentTrips.size() > 0){
            currentTripsMap = currentTrips;
            getJSONPolyline(currentTripsMap);
            listView.setAdapter(new TripAdapter(getContext(), currentTripsMap));
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LatLng latLngStart = new LatLng(currentTripsMap.get(position).getStart_lat(), currentTripsMap.get(position).getStart_lng());;
                    LatLng latLngEnd = new LatLng(currentTripsMap.get(position).getEnd_lat(), currentTripsMap.get(position).getEnd_lng());;
                    if(polyline != null)
                        polyline.remove();
                    drawPolyline(latLngStart, latLngEnd);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLngStart)             // Sets the center of the map to location user
                            .zoom(14)                   // Sets the zoom
//                        .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(45)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            });
        }
        else{
            new AlertDialog.Builder(getContext()).setTitle("Thông báo").setMessage("Không có chuyến đi nào tới " + getNameOfProvince(endLocation)).setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
        }
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
        if(endMarker != null){
            endMarker.remove();
        }
//        endMarker.title("")
//                .snippet("")
//                .position(new LatLng(latLngEnd.latitude, latLngEnd.longitude))
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        endMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latLngEnd.latitude, latLngEnd.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

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
                        color(Color.BLUE).
                        width(8);
                for (int i = 0; i < points.size(); i++)
                    polylineOptions.add(points.get(i));
//                polylinePaths.add(googleMap.addPolyline(polylineOptions));
                polyline = googleMap.addPolyline(polylineOptions);
                int numberMid = Math.round(points.size() / 2);
                LatLng pointMid = new LatLng(points.get(numberMid).latitude, points.get(numberMid).longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(pointMid)             // Sets the center of the map to location user
                        .zoom(10)                   // Sets the zoom
                        //                    .bearing(90)                // Sets the orientation of the camera to east
                        .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
        if(endMarker != null){
            endMarker.remove();
        }
//        if(endMarker == null){
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
//            CameraPosition cameraPosition = new CameraPosition.Builder()
//                    .target(marker.getPosition())             // Sets the center of the map to location user
//                    .zoom(12)                   // Sets the zoom
//    //                    .bearing(90)                // Sets the orientation of the camera to east
//                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
//                    .build();                   // Creates a CameraPosition from the builder
//            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(polyline != null)
            polyline.remove();
        if(endMarker != null){
            endMarker.remove();
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        ShowInfoTripFragment showInfoTripFragment = new ShowInfoTripFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.quick_search_fragment, showInfoTripFragment).commit();
//        Toast.makeText(getContext(), "Mở ra thông tin chi tiết",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMyLocationChange(Location location) {
        LatLng myChange = new LatLng(location.getLatitude(), location.getLongitude());
        Toast.makeText(getContext(), "Vi tri thay doi: " + myChange.toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View v) {
        int wiget = v.getId();
        switch (wiget){
            case R.id.btn_list_trip:
//                layoutListView.setVisibility(View.VISIBLE);
//                layoutGMap.setVisibility(View.INVISIBLE);

//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutGMap.getLayoutParams();
//                Toast.makeText(getContext(), "Vi tri thay doi: " + layoutListView.getHeight(),Toast.LENGTH_LONG).show();
//                params.height = layoutGMap.getHeight() / 2;
//                params.width = layoutGMap.getWidth();
                resizeFragment();
                changeButtonText();
                break;
//            case R.id.btn_delete:
//                mAutocompleteView.setText("");
//                break;
            case R.id.search_text_view:
                iQuickSearchPresenter.showProgressBar();
                callPlacePicker();
                break;
        }
    }

    public void callPlacePicker(){
//        iQuickSearchPresenter.showProgressBar();
//        myProgress = new ProgressDialog(getContext());
//        myProgress.setTitle("Đang tải ...");
//        myProgress.setMessage("Xin vui lòng đợi");
//        myProgress.setCancelable(false);
//        myProgress.show();
        PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
        Intent intent;
        try {
            intent = intentBuilder.build(getContext());
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        iQuickSearchPresenter.dissProgressBar();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
//        Toast.makeText(getContext(), "ALOOLLAALOLCOALSO",Toast.LENGTH_LONG).show();
        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                Place place = PlacePicker.getPlace(data, getContext());
//                Toast.makeText(getContext(), "Vi tri: " + place.getName(),Toast.LENGTH_LONG).show();
//                tvEnd.setText(getNameOfProvince(place.getLatLng()));
                tvEnd.setText(place.getAddress());
                endLocation = place.getLatLng();
                googleMap.clear();
                askPermissionsAndShowMyLocation();
            }
        }
    }

    public void changeButtonText(){
        if(btnListTrip.getText().toString().equals("DANH SÁCH CHUYẾN ĐI")){
            btnListTrip.setText("ĐÓNG DANH SÁCH");
        }
        else{
            btnListTrip.setText("DANH SÁCH CHUYẾN ĐI");
        }
    }

    public void resizeFragment(){
        RelativeLayout.LayoutParams paramsGMap = (RelativeLayout.LayoutParams) layoutGMap.getLayoutParams();
        RelativeLayout.LayoutParams paramsListView = (RelativeLayout.LayoutParams) layoutListView.getLayoutParams();
//        View childView = listView.getAdapter().getView(1, null, listView);
//        childView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY));
//        Toast.makeText(getContext(), "Do dai: " + listView.getPaddingTop() + listView.getPaddingBottom(),Toast.LENGTH_LONG).show();

        if(btnListTrip.getText().toString().equals("DANH SÁCH CHUYẾN ĐI")){
//            if(currentTripsMap.size() == 1){
//                paramsListView.height = 180;
//                paramsGMap.height = layoutGMap.getHeight() - 90;
//            }
//            else if(currentTripsMap.size() == 2){
//                paramsListView.height = 270;
//                paramsGMap.height = layoutGMap.getHeight() - 180;
//            }
//            else if(currentTripsMap.size() == 3){
//                paramsListView.height = 360;
//                paramsGMap.height = layoutGMap.getHeight() - 270;
//            }else{
                paramsListView.height = layoutGMap.getHeight() / 2;
                paramsGMap.height = layoutGMap.getHeight() / 2;
//            }
            paramsGMap.width = layoutGMap.getWidth();
            paramsListView.width = layoutGMap.getWidth();
        }
        else{
//            if(currentTripsMap.size() == 1){
//                paramsGMap.height = layoutGMap.getHeight() + 180;
//            }
//            else if(currentTripsMap.size() == 2){
//                paramsGMap.height = layoutGMap.getHeight() + 270;
//            }
//            else if(currentTripsMap.size() == 3){
//                paramsGMap.height = layoutGMap.getHeight() + 360;
//            }
//            else{
                paramsGMap.height = layoutGMap.getHeight() * 2;
//            }
            paramsListView.height = 0;
            paramsListView.width = 0;
            paramsGMap.width = layoutGMap.getWidth();
        }
    }


//    protected synchronized void buildGoogleAPIClient(){
//        mGoogleApiClient = new GoogleApiClient
//                .Builder(getContext())
//                .addApi(Places.GEO_DATA_API)
//                .addApi(Places.PLACE_DETECTION_API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//        mGoogleApiClient.connect();
//    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
