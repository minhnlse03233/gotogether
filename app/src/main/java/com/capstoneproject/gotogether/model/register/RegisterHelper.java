package com.capstoneproject.gotogether.model.register;

import com.capstoneproject.gotogether.model.User;

/**
 * Created by MinhNL on 10/2/2016.
 */
public class RegisterHelper implements IRegisterUser {
    IRegisterResult iRegisterResult;


    public RegisterHelper (IRegisterResult iRegisterResult){
        this.iRegisterResult = iRegisterResult;
    }

    @Override
    public void passUserFromLogin(User userInfo) {
        iRegisterResult.passUserToFrag(userInfo);
    }
}
