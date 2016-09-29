package com.capstoneproject.gotogether.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstoneproject.gotogether.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Nguyen Luc on 9/28/2016.
 */
public class LoginHelper extends Activity implements ILoginUser{
    ILoginResult iLoginResult;
    private static Application instance;
    private Context mContext;

    public LoginHelper(){}

    public LoginHelper(Context mContext){this.mContext = mContext;}

    public static Context getContext() {
        return instance.getApplicationContext();
    }

    public LoginHelper(ILoginResult iLoginResult){
        this.iLoginResult = iLoginResult;
    }



    @Override
    public void loginSuccess(){
    //    getData("1122");
    }

    @Override
    public void loginError() {
        iLoginResult.loginError();
    }

    private void getData(String id) {

        String url = "http://fugotogether.com/sql/checkUserLogin.php?id=" + id;

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                showJSON(response);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        String id = "";
        String fullname = "";
        String gender = "";
        String phonenumber = "";
//        String users = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("users");

            for (int i = 0; i < result.length(); i++){
                JSONObject collegeData = result.getJSONObject(i);
                id = collegeData.getString("userId");
                fullname = collegeData.getString("fullname");
                gender = collegeData.getString("gender");
                phonenumber = collegeData.getString("phonenumber");
//                users += "Id:\t"+id+"\nFullname:\t" +fullname+ "\nGender:\t"+ gender + "\nPhonenumber:\t"+ phonenumber + "\n\n";
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(id.equals("")){
            //Tài khoản chưa đăng ký
        }else {
            //Tài khoản đã đăng ký
        }
    }

}
