package com.capstoneproject.gotogether.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstoneproject.gotogether.CircleTransform;
import com.capstoneproject.gotogether.R;
import com.capstoneproject.gotogether.ShowInfoTripFragment;
import com.capstoneproject.gotogether.model.Trip;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by MinhNL on 10/13/2016.
 */
public class TripAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater layoutInflater;
    private ArrayList<Trip> currentTripsMap;
//    ITripInforView iTripInforView;

    public TripAdapter(Context context, ArrayList<Trip> currentTripsMap) {
        this.context = context;
        this.currentTripsMap = currentTripsMap;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

//    public TripAdapter(ITripInforView iTripInforView){
//        this.iTripInforView = iTripInforView;
//    }

    @Override
    public int getCount() {
        return currentTripsMap.size();
    }

    @Override
    public Object getItem(int position) {
        return currentTripsMap.get(position);
    }

    @Override
    public long getItemId(int position) {
        return currentTripsMap.get(position).getTripId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LatLng latLngStart;
        LatLng latLngEnd;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_trip, null);
            viewHolder = new ViewHolder();
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.view_trip = (ImageView) convertView.findViewById(R.id.view_trip);
            viewHolder.title = (TextView) convertView.findViewById(R.id.title);
            viewHolder.start = (TextView) convertView.findViewById(R.id.start);
            viewHolder.end = (TextView) convertView.findViewById(R.id.end);
            viewHolder.date_start = (TextView) convertView.findViewById(R.id.date_start);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Trip trip = currentTripsMap.get(position);
        latLngStart = new LatLng(trip.getStart_lat(), trip.getStart_lng());
        latLngEnd = new LatLng(trip.getEnd_lat(), trip.getEnd_lng());
        viewHolder.title.setText(trip.getTitle());
        viewHolder.start.setText("ĐIỂM ĐI: ");
        viewHolder.end.setText("ĐIỂM ĐẾN: ");
//        viewHolder.start.setText("Nơi đi: " + formatAddress(getNameOfProvince(latLngStart)));
//        viewHolder.end.setText("Nơi đến: " + formatAddress(getNameOfProvince(latLngEnd)));
        viewHolder.date_start.setText("NGÀY ĐI: " + formatDate(trip.getDate_start()));
//        viewHolder.avatar.setImageBitmap(yourSelectedImage);
        Picasso.with(context).load("https://graph.facebook.com/v2.7/" + trip.getUserId() + "/picture?height=120&type=small").transform(new CircleTransform()).resize(48, 48).into(viewHolder.avatar);
        viewHolder.view_trip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = (Activity) context;
                android.app.FragmentManager fragmentManager = activity.getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ShowInfoTripFragment showInfoTripFragment = new ShowInfoTripFragment();
                fragmentTransaction.replace(R.id.quick_search_fragment, showInfoTripFragment);
                fragmentTransaction.commit();
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView avatar, view_trip;
        TextView title, start, end, date_start;
    }

    private String getNameOfProvince(LatLng latLng){
        android.location.Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
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

    public String formatDate(String date_start){
        String [] s = date_start.split("-");
        String newDate = s[2] + "-" + s[1] + "-"  + s[0];
        return newDate;
    }

    public String formatAddress(String finalAddress){
        if(finalAddress.length() > 17){
            String newFinalAddress = finalAddress.substring(0, 17) + "...";
            return newFinalAddress;
        }else{
            return finalAddress;
        }
    }

}
