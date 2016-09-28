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


    @Override
    public void loginSuccess(JSONObject object) throws JSONException {
        String id = object.getString("id");
        Log.i("Thong tin: ", object.getString("id").toString());
//        LoginResult loginResult = null;
//        GraphRequest request = GraphRequest.newMeRequest(
//                loginResult.getAccessToken(),
//                new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        Log.v("LoginActivity", response.toString());
//
//                        // Application code
//                        try {
//
//                            String id = object.getString("id");
//                            String email = object.getString("email");
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//        Bundle parameters = new Bundle();
//        parameters.putString("fields", "id,name,email,gender,birthday");
//        request.setParameters(parameters);
//        request.executeAsync();
        iLoginResult.loginSuccess();
    }

    @Override
    public void loginError() {
        iLoginResult.loginError();
    }
}
