package com.capstoneproject.gotogether.presenter;

import com.capstoneproject.gotogether.model.ILoginResult;
import com.capstoneproject.gotogether.model.ILoginUser;
import com.capstoneproject.gotogether.view.ILoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public class LoginPresenter implements ILoginPresenter, ILoginResult {
    ILoginUser iLoginUser;
    ILoginView iLoginView;

    @Override
    public void onSuccess(JSONObject object) throws JSONException {
        iLoginUser.loginSuccess(object);
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
        iLoginView.loginChangeIten();
    }

    @Override
    public void loginCancel() {

    }

    @Override
    public void loginError() {
        iLoginView.loginError();
    }
}
