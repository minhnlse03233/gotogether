package com.capstoneproject.gotogether;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstoneproject.gotogether.model.Trip;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PostTripFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RelativeLayout postAdvanced, layoutGMap, layoutTextStart, layoutTextEnd;
    ImageView ivNext, ivBack, ivCalendar, ivTimePicker;
    int gMapHeight;
    int listViewHeight;
    GoogleMap googleMap;
    MapView mapView;
    Button btnPost;
    TextView  tvStart, tvEnd,dateStart;
    Trip postTrip;
    boolean focusStart, focusEnd;
    int PLACE_PICKER_REQUEST = 1;
    LatLng latLngStart, latLngEnd;
    Marker markerStart, markerEnd;
    List<LatLng> points;
    Polyline polyline;
    EditText etTitle, etDescription, etSlot, etPrice;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostTripFragment() {
        // Required empty public constructor
    }

    public static PostTripFragment newInstance(String param1, String param2) {
        PostTripFragment fragment = new PostTripFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_post_trip, container, false);
        ivNext = (ImageView) rootView.findViewById(R.id.next);
        ivNext.setOnClickListener(this);
        ivBack = (ImageView) rootView.findViewById(R.id.back);
        ivBack.setOnClickListener(this);
        ivBack.setVisibility(View.INVISIBLE);
        postAdvanced = (RelativeLayout) rootView.findViewById(R.id.post_advanced);
        postAdvanced.setVisibility(View.INVISIBLE);
        layoutGMap = (RelativeLayout) rootView.findViewById(R.id.gMap);

//        btnDatePicker = (Button) rootView.findViewById(R.id.date_picker);

        btnPost = (Button) rootView.findViewById(R.id.post);
        btnPost.setOnClickListener(this);
//        btnDatePicker.setOnClickListener(this);

        ivCalendar = (ImageView) rootView.findViewById(R.id.calendar_date);
        ivCalendar.setOnClickListener(this);

        dateStart = (TextView) rootView.findViewById(R.id.pick_date);
        ivTimePicker = (ImageView) rootView.findViewById(R.id.time_pick);
        ivTimePicker.setOnClickListener(this);


        tvStart = (TextView) rootView.findViewById(R.id.text_view_start);
        tvEnd = (TextView) rootView.findViewById(R.id.text_view_end);

        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etDescription = (EditText) rootView.findViewById(R.id.etDescription);
        etSlot = (EditText) rootView.findViewById(R.id.slot);
        etPrice = (EditText) rootView.findViewById(R.id.price);


        layoutTextStart = (RelativeLayout) rootView.findViewById(R.id.search_text_start);
        layoutTextEnd = (RelativeLayout) rootView.findViewById(R.id.search_text_end);
        layoutTextStart.setOnClickListener(this);
        layoutTextEnd.setOnClickListener(this);

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
                rlp.setMargins(0, 700, 21, 0);

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
            }
        });

        return rootView;

    }


    @Override
    public void onClick(View v) {
        int wiget = v.getId();
        switch (wiget) {
            case R.id.next:
                resizeFragment("next");

                break;
            case R.id.back:
                resizeFragment("back");
                break;
            case R.id.calendar_date:
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(getFragmentManager(), "datePicker");
                break;
            case R.id.time_pick:
                DialogFragment dialogFragment1 = new TimePickerFragment();
                dialogFragment1.show(getFragmentManager(), "timePicker");
                break;
            case R.id.search_text_start:
                focusStart = true;
                focusEnd = false;
                callPlacePicker();
                break;
            case R.id.search_text_end:
                focusStart = false;
                focusEnd = true;
                callPlacePicker();
                break;
            case R.id.post:

                postTrip = new Trip();

                postTrip.setTitle(etTitle.getText().toString());
                postTrip.setDescription(etDescription.getText().toString());
                try {
                    postTrip.setSlot(Integer.parseInt(etSlot.getText().toString()));
                    postTrip.setPrice(Double.parseDouble(etPrice.getText().toString()));
                }catch (Exception ex) {

                }
                postTrip.setDate_start(dateStart.getText().toString());
                try {
                    postTrip.setStart_lat(latLngStart.latitude);
                    postTrip.setStart_lng(latLngStart.longitude);
                    postTrip.setEnd_lat(latLngEnd.latitude);
                    postTrip.setEnd_lat(latLngEnd.longitude);
                }catch (Exception ex){
                    new AlertDialog.Builder(getContext()).setTitle("Thông báo").setMessage("Bạn cần phải chọn điểm đi và điểm đến").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }

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

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                Place place = PlacePicker.getPlace(data, getContext());
                if (focusStart == true && focusEnd == false) {
                    latLngStart = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    tvStart.setText(place.getAddress());
                    if(markerEnd != null){
                        drawPolyline(latLngStart, latLngEnd);
                        if(markerStart != null){
                            markerStart.remove();
                            drawMarker(latLngStart);
                        }else{
                            drawMarker(latLngStart);
                        }
                    }else{
                        if(markerStart != null){
                            markerStart.remove();
                            drawMarker(latLngStart);
                        }else{
                            drawMarker(latLngStart);
                        }
                    }
                } else if (focusStart == false && focusEnd == true) {
                    latLngEnd = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                    tvEnd.setText(place.getAddress());
                    if(markerStart != null){
                        //ve polyline
                        drawPolyline(latLngStart, latLngEnd);
                        if(markerEnd != null){
                            markerEnd.remove();
                            drawMarker(latLngEnd);
                        }else{
                            drawMarker(latLngEnd);
                        }
                    }
                    else{
                        if(markerEnd != null){
                            markerEnd.remove();
                            drawMarker(latLngEnd);
                        }else{
                            drawMarker(latLngEnd);
                        }
                    }
                }
            }
        }
    }

    public void drawMarker(LatLng myLatLng){
        if(focusStart == true && focusEnd == false){
            markerStart = googleMap.addMarker(new MarkerOptions().position(myLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLatLng)             // Sets the center of the map to location user
                    .zoom(10)                   // Sets the zoom
                    //                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        else if(focusStart == false && focusEnd == true){
            markerEnd = googleMap.addMarker(new MarkerOptions().position(myLatLng)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(myLatLng)             // Sets the center of the map to location user
                    .zoom(10)                   // Sets the zoom
                    //                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }

    public void drawPolyline(LatLng latLngStart, LatLng latLngEnd){
        if(polyline != null)
            polyline.remove();
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

    public void resizeFragment(String type){
        RelativeLayout.LayoutParams paramsGMap = (RelativeLayout.LayoutParams) layoutGMap.getLayoutParams();
        RelativeLayout.LayoutParams paramsPostAdvanced = (RelativeLayout.LayoutParams) postAdvanced.getLayoutParams();
        if(type.equals("next")){
//            gMapHeight = layoutGMap.getHeight();
//            listViewHeight = postAdvanced.getHeight();
//            paramsPostAdvanced.height = 1400;
//            paramsGMap.height = 0;
            ivBack.setVisibility(View.VISIBLE);
            ivNext.setVisibility(View.INVISIBLE);
//            Toast.makeText(getContext(), "Chieu cao: " + paramsPostAdvanced.height, Toast.LENGTH_LONG).show();
            layoutGMap.setVisibility(View.INVISIBLE);
            postAdvanced.setVisibility(View.VISIBLE);
            LayoutTransition layoutTransition = new LayoutTransition();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
            }
            postAdvanced.setLayoutTransition(layoutTransition);
        }
        else{
            ivBack.setVisibility(View.INVISIBLE);
            ivNext.setVisibility(View.VISIBLE);
            layoutGMap.setVisibility(View.VISIBLE);
            postAdvanced.setVisibility(View.INVISIBLE);
        }
    }
}
