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
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
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
    String id,name, emaill, gender;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    System.out.println("not logged in yet");
                    Toast.makeText(getApplicationContext(),"id la:" +id,Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Logged in");
                    Toast.makeText(getApplicationContext(),"bố đăng nhập rồi:" +id,Toast.LENGTH_SHORT).show();
                }
            }
        });
        setContentView(R.layout.activity_login);

        textView = (TextView) findViewById(R.id.textView);
        // gọi call back facebook
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) findViewById(R.id.login_button);

        // set đọc quyền đc lấy thông tin email, profile...
        btnLogin.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

        iLoginPresenter = new LoginPresenter(this);


        //if (AccessToken.getCurrentAccessToken() == null) {

            btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject object, GraphResponse response) {
                                    try {
                                        id = object.getString("id");
                                        gender = object.getString("gender");
                                        name = object.getString("name");

                                        iLoginPresenter.onSuccess(id);
                                        emaill = object.getString("email");

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

//        } else {
//            Intent intent  = new Intent(getApplicationContext(), MainActivity.class);
//            Toast.makeText(getApplicationContext(),"bố đăng nhập rồi:" +id,Toast.LENGTH_SHORT).show();
//            startActivity(intent);
//        }

    }




    @Override
    public void loginChangeInten(String id) {
        Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
        //getData(id);
    }

    @Override
    public void loginCancel() {

    }

    @Override
    public void loginError() {
        Toast.makeText(getApplicationContext(),"Đăng nhập không thành công. Vui lòng thử lại!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void statusUser(String status) {
        if (status.equals("NoActive")) {
            //truyền dữ liệu sang fragment
            Bundle bundle = new Bundle();
            bundle.putString("name", name );
            bundle.putString("email", emaill);
            bundle.putString("id",id);
            bundle.putString("gender",gender);
            AddInfoFragment addInfoFragment = new AddInfoFragment();
            FragmentManager fragmentManager =  getSupportFragmentManager();
            addInfoFragment.setArguments(bundle);

            // call fragment
            fragmentManager.beginTransaction().replace(R.id.login_activity, addInfoFragment).commit();
            Toast.makeText(getApplicationContext(),"Đăng nhập lần đầu, vui lòng điền thông tin cá nhân để sử dụng dịch vụ.",Toast.LENGTH_LONG).show();
        } else if (status.equals("Active")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("name", name);
            bundle.putString("email",emaill);
            intent.putExtra("profile",bundle);
            startActivity(intent);

        } else {
            LoginManager.getInstance().logOut();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }






}
