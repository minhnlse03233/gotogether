package com.capstoneproject.gotogether.presenter.quicksearch;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Nguyen Luc on 10/4/2016.
 */
public interface IQuickSearchPresenter {
    void showProgressBar();
    void dissProgressBar();
    void loadTrip(LatLng currentLocation);
}
