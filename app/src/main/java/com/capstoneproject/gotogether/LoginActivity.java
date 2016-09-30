package com.capstoneproject.gotogether;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.capstoneproject.gotogether.presenter.ILoginPresenter;
import com.capstoneproject.gotogether.presenter.LoginPresenter;
import com.capstoneproject.gotogether.view.ILoginView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ILoginView {
    LoginButton btnLogin;
    CallbackManager callbackManager;
    ILoginPresenter iLoginPresenter;
    TextView textView;
    String name, emaill;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        textView = (TextView) findViewById(R.id.textView);
        // gọi call back facebook
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) findViewById(R.id.login_button);

        // set đọc quyền đc lấy thông tin email, profile...
        btnLogin.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        iLoginPresenter = new LoginPresenter(this);



        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String id = object.getString("id");

                                    name = object.getString("name");
                                    emaill = object.getString("email");

                                    iLoginPresenter.onSuccess(id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                iLoginPresenter.onCancel();
            }

            @Override
            public void onError(FacebookException error) {
                iLoginPresenter.onError();
            }
        });

    }




    @Override
    public void loginChangeIten(String id) {
        Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
        getData(id);
    }

    @Override
    public void loginCancel() {

    }

    @Override
    public void loginError() {
        Toast.makeText(getApplicationContext(),"Đăng nhập không thành công. Vui lòng thử lại!",Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response){
        String id = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray result = jsonObject.getJSONArray("users");

            for (int i = 0; i < result.length(); i++){
                JSONObject collegeData = result.getJSONObject(i);
                id = collegeData.getString("userId");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(id.equals("")){
            //Tài khoản chưa đăng ký

            //truyền dữ liệu sang fragment
            Bundle bundle = new Bundle();
            bundle.putString("name", name );
            bundle.putString("email", emaill);
            AddInfoFragment addInfoFragment = new AddInfoFragment();
            FragmentManager fragmentManager =  getSupportFragmentManager();
            addInfoFragment.setArguments(bundle);

            // call fragment
            fragmentManager.beginTransaction().replace(R.id.login_activity, addInfoFragment).commit();
            Toast.makeText(getApplicationContext(),"Đăng nhập lần đầu, vui lòng điền thông tin cá nhân để sử dụng dịch vụ.",Toast.LENGTH_LONG).show();


        }else {
           //Tài khoản đã đăng ký
            // intent sang activity main
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }

}
