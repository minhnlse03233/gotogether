package com.capstoneproject.gotogether.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public interface ILoginUser {
    public void loginSuccess(String id);
    public void loginError();
}
