package com.capstoneproject.gotogether;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.capstoneproject.gotogether.model.User;
import com.capstoneproject.gotogether.presenter.login.ILoginPresenter;
import com.capstoneproject.gotogether.presenter.login.LoginPresenter;
import com.capstoneproject.gotogether.presenter.register.IRegisterPresenter;
import com.capstoneproject.gotogether.presenter.register.RegisterPresenter;
import com.capstoneproject.gotogether.view.login.ILoginView;
import com.capstoneproject.gotogether.view.register.IRegisterView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ILoginView, IRegisterView {
    LoginButton btnLogin;
    CallbackManager callbackManager;
    ILoginPresenter iLoginPresenter;
    IRegisterPresenter iRegisterPresenter;
    TextView textView;
    String id,name, emaill, gender;
    int genderInt;
    BigInteger userId;
    boolean isRegister = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                if(AccessToken.getCurrentAccessToken() == null){
                    System.out.println("not logged in yet");

                } else {
                    System.out.println("Logged in");

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
        iRegisterPresenter = new RegisterPresenter(this);


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
        //Toast.makeText(getApplicationContext(),"Đăng nhập thành công",Toast.LENGTH_SHORT).show();
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
            btnLogin.setVisibility(View.INVISIBLE);
            userId = new BigInteger(id);
            if(gender.equals("male"))
                genderInt = 1;
            else
                genderInt = 0;

            if(emaill == null)
                emaill = "";
            User userInfo = new User(userId, name, genderInt, emaill);
            iRegisterPresenter.sendUserFromLogin(userInfo);
            isRegister = true;

        } else if (status.equals("Active")) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", id);
            bundle.putString("name", name);
            bundle.putString("email", emaill);
            intent.putExtra("profile", bundle);
            startActivity(intent);
            finish();
            //Toast.makeText(getApplicationContext(), "Ac cmn tive", Toast.LENGTH_LONG).show();

        } else {
            LoginManager.getInstance().logOut();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void receiveUserFromLogin(User userInfo) {
//        Toast.makeText(getApplicationContext(), "Id của bạn là: " + userInfo.getUserId(), Toast.LENGTH_LONG).show();
        bundleToFrag(userInfo);
    }

    public void bundleToFrag(User userInfo){
        Bundle bundle = new Bundle();
        bundle.putString("name", userInfo.getFullname() );
        bundle.putString("email", userInfo.getEmail());
        bundle.putString("id",userInfo.getUserId().toString());
        bundle.putString("gender",userInfo.getGender() + "");

        AddInfoFragment addInfoFragment = new AddInfoFragment();
        FragmentManager fragmentManager =  getSupportFragmentManager();
        addInfoFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.login_activity, addInfoFragment).commit();
    }
}
