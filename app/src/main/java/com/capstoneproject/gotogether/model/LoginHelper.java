package com.capstoneproject.gotogether.model;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public class LoginHelper implements ILoginUser{
    ILoginResult iLoginResult;

    public LoginHelper(ILoginResult iLoginResult){
        this.iLoginResult = iLoginResult;
    }

    @Override
    public void loginSuccess(){
        
    }

    @Override
    public void loginError() {
        iLoginResult.loginError();
    }
}
