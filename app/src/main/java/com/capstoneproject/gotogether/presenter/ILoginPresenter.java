package com.capstoneproject.gotogether.presenter;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public interface ILoginPresenter {

    public void onSuccess(String id);
    public void onCancel();
    public void onError();
}
