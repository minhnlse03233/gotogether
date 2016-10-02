package com.capstoneproject.gotogether.view.register;

import com.capstoneproject.gotogether.model.User;

/**
 * Created by MinhNL on 10/2/2016.
 */
public interface IRegisterViewFrag {
    void receiveUserFromLoginFrag(User userInfo);
    void receivedAction();
}
