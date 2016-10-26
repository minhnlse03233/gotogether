package com.capstoneproject.gotogether.model.quicksearch;

import com.capstoneproject.gotogether.model.Trip;

import java.util.ArrayList;

/**
 * Created by MinhNL on 10/5/2016.
 */
public interface IQuickSearchResult {
    void returnTrip(ArrayList<Trip> currentTrips);
    void returnNoTripAvai();
}
