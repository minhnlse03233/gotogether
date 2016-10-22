package com.capstoneproject.gotogether.model.quicksearch;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MinhNL on 10/5/2016.
 */
public interface IQuickSearchUser {
    void loadTrip(LatLng currentLocation, LatLng endLocation);
}
