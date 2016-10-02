package com.capstoneproject.gotogether.presenter.register;

import com.capstoneproject.gotogether.model.User;

/**
 * Created by MinhNL on 10/1/2016.
 */
public interface IRegisterPresenter {
    void sendUserFromLogin(User userInfo);
    void actionFromFrag();
}
