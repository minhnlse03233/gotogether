package com.capstoneproject.gotogether.presenter.quicksearch;

import com.capstoneproject.gotogether.model.Trip;
import com.capstoneproject.gotogether.model.quicksearch.IQuickSearchResult;
import com.capstoneproject.gotogether.model.quicksearch.IQuickSearchUser;
import com.capstoneproject.gotogether.model.quicksearch.QuickSearchHelper;
import com.capstoneproject.gotogether.view.quicksearch.IQuickSearchView;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Nguyen Luc on 10/4/2016.
 */
public class QuickSearchPresenter implements IQuickSearchPresenter, IQuickSearchResult{
    IQuickSearchView iQuickSearchView;
    IQuickSearchUser iQuickSearchUser;

    QuickSearchPresenter(){}
    public QuickSearchPresenter(IQuickSearchView iQuickSearchView){
        this.iQuickSearchView = iQuickSearchView;
        iQuickSearchUser = new QuickSearchHelper(this);
    }

    public QuickSearchPresenter(IQuickSearchUser iQuickSearchUser){
        this.iQuickSearchUser = iQuickSearchUser;
    }

    @Override
    public void showProgressBar() {
        iQuickSearchView.showProgressBar();
    }

    @Override
    public void dissProgressBar() {
        iQuickSearchView.dissProgressBar();
    }

    @Override
    public void loadTrip(LatLng currentLocation) {
        iQuickSearchUser.loadTrip(currentLocation);
    }

    @Override
    public void returnTrip(ArrayList<Trip> currentTrips) {
        iQuickSearchView.returnTrip(currentTrips);
    }
}
