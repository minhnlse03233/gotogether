package com.capstoneproject.gotogether.presenter.quicksearch;

import com.capstoneproject.gotogether.view.quicksearch.IQuickSearchView;

/**
 * Created by Nguyen Luc on 10/4/2016.
 */
public class QuickSearchPresenter implements IQuickSearchPresenter{
    IQuickSearchView iQuickSearchView;

    QuickSearchPresenter(){}
    public QuickSearchPresenter(IQuickSearchView iQuickSearchView){
        this.iQuickSearchView = iQuickSearchView;
    }


    @Override
    public void showProgressBar() {
        iQuickSearchView.showProgressBar();
    }

    @Override
    public void dissProgressBar() {
        iQuickSearchView.dissProgressBar();
    }
}
