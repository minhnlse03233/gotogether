package com.capstoneproject.gotogether.view.quicksearch;

import com.capstoneproject.gotogether.model.Trip;

import java.util.ArrayList;

/**
 * Created by Nguyen Luc on 10/4/2016.
 */
public interface IQuickSearchView {
    void showProgressBar();
    void dissProgressBar();
    void returnTrip(ArrayList<Trip> currentTrips);
}
