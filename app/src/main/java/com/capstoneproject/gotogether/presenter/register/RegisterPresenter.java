package com.capstoneproject.gotogether.presenter.register;

import com.capstoneproject.gotogether.model.User;
import com.capstoneproject.gotogether.model.register.IRegisterResult;
import com.capstoneproject.gotogether.model.register.IRegisterUser;
import com.capstoneproject.gotogether.model.register.RegisterHelper;
import com.capstoneproject.gotogether.view.register.IRegisterView;
import com.capstoneproject.gotogether.view.register.IRegisterViewFrag;

/**
 * Created by MinhNL on 10/1/2016.
 */
public class RegisterPresenter implements IRegisterPresenter, IRegisterResult {

    IRegisterView iRegisterView;
    IRegisterUser iRegisterUser;
    IRegisterViewFrag iRegisterViewFrag;

    public RegisterPresenter(IRegisterView iRegisterView){
        this.iRegisterView = iRegisterView;
        iRegisterUser = new RegisterHelper(this);
    }

    public RegisterPresenter(IRegisterViewFrag iRegisterViewFrag){
        this.iRegisterViewFrag = iRegisterViewFrag;
        iRegisterUser = new RegisterHelper(this);
    }


    @Override
    public void sendUserFromLogin(User userInfo) {
        iRegisterView.receiveUserFromLogin(userInfo);
//        iRegisterUser.passUserFromLogin(userInfo);
//        iRegisterViewFrag.receiveUserFromLoginFrag(userInfo);
    }

    @Override
    public void actionFromFrag() {
//        iRegisterViewFrag.receivedAction();
    }

    @Override
    public void passUserToFrag(User userInfo) {
//        iRegisterViewFrag.receiveUserFromLoginFrag(userInfo);
    }
}
