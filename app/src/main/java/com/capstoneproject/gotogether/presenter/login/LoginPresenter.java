package com.capstoneproject.gotogether.presenter.login;

import com.capstoneproject.gotogether.model.login.ILoginResult;
import com.capstoneproject.gotogether.model.login.ILoginUser;
import com.capstoneproject.gotogether.model.login.LoginHelper;
import com.capstoneproject.gotogether.view.login.ILoginView;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public class LoginPresenter implements ILoginPresenter, ILoginResult {
    ILoginUser iLoginUser;
    ILoginView iLoginView;

    public LoginPresenter(ILoginView iLoginView){
        this.iLoginView = iLoginView;
        iLoginUser = new LoginHelper(this);
    }

    @Override
    public void onSuccess(String id){
        iLoginView.loginChangeInten(id);
        iLoginUser.loginSuccess(id);
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onError() {
        iLoginUser.loginError();
    }

    @Override
    public void loginSuccess() {

    }

    @Override
    public void loginCancel() {

    }

    @Override
    public void loginError() {
        iLoginView.loginError();
    }

    @Override
    public void loginStatus(String status) {
        iLoginView.statusUser(status);
    }
}
