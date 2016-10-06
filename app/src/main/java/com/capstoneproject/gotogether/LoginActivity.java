package com.capstoneproject.gotogether;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements ILoginView, IRegisterView, View.OnClickListener {
    LoginButton btnLogin;
    Button btnPhoneLogin;
    CallbackManager callbackManager;
    ILoginPresenter iLoginPresenter;
    IRegisterPresenter iRegisterPresenter;
    TextView textView;
    String id,name, emaill, gender;
    int genderInt;
    BigInteger userId;
    Profile profile;
    public static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 100;

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
        btnPhoneLogin = (Button) findViewById(R.id.phonenumber_login);
        btnPhoneLogin.setOnClickListener(this);

        textView = (TextView) findViewById(R.id.textView);
        callbackManager = CallbackManager.Factory.create();
        btnLogin = (LoginButton) findViewById(R.id.login_button);
        btnLogin.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        iLoginPresenter = new LoginPresenter(this);
        iRegisterPresenter = new RegisterPresenter(this);
        //textView.setText(AccessToken.getCurrentAccessToken().toString());

        if (AccessToken.getCurrentAccessToken() == null) {
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
                                        btnLogin.setVisibility(View.INVISIBLE);
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
        }
        else {
            profile = Profile.getCurrentProfile();
            iLoginPresenter.onSuccess(profile.getId());
            btnLogin.setVisibility(View.INVISIBLE);
            btnPhoneLogin.setVisibility(View.INVISIBLE);
        }
    }




    @Override
    public void loginChangeInten(String id) {

    }

    @Override
    public void loginCancel() {
        Toast.makeText(getApplicationContext(),"Đăng nhập không thành công vì bị hủy. Vui lòng thử lại!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void loginError() {
        Toast.makeText(getApplicationContext(),"Đăng nhập không thành công. Vui lòng kiểm tra lại kết nối!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void statusUser(String status) {
        if (status.equals("NoActive")) {
            profile = Profile.getCurrentProfile();
            btnLogin.setVisibility(View.INVISIBLE);
            userId = new BigInteger(profile.getId());

            if(gender != null){
                if(gender.equals("male"))
                    genderInt = 1;
                else
                    genderInt = 0;
            }else
                genderInt = 1;

            if(emaill == null)
                emaill = "";

            if(name == null)
                name = profile.getName();

            User userInfo = new User(userId, name, genderInt, emaill);
            iRegisterPresenter.sendUserFromLogin(userInfo);
        } else if (status.equals("Active")) {
            Profile profile = Profile.getCurrentProfile();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", profile.getId());
            bundle.putString("name", profile.getName());
            bundle.putString("email", emaill);
            intent.putExtra("profile", bundle);
            startActivity(intent);
            finish();
        } else {
//            LoginManager.getInstance().logOut();
//            Toast.makeText(getApplicationContext(), "Id của bạn là: " + id, Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        finish();
    }

    public void bundleToFrag(User userInfo){
        Bundle bundle = new Bundle();
        bundle.putString("name", userInfo.getFullname() );
        bundle.putString("email", userInfo.getEmail());
        bundle.putString("id", userInfo.getUserId().toString());
        bundle.putString("gender", userInfo.getGender() + "");

        AddInfoFragment addInfoFragment = new AddInfoFragment();
        FragmentManager fragmentManager =  getSupportFragmentManager();
        addInfoFragment.setArguments(bundle);

        fragmentManager.beginTransaction().replace(R.id.login_activity, addInfoFragment).commit();
    }

    @Override
    public void onClick(View v) {
        int wiget = v.getId();
        switch (wiget) {
            case R.id.phonenumber_login:
                askPermissionsAndGetPhoneNumber();
                break;
        }
    }

    private void askPermissionsAndGetPhoneNumber() {
        if (Build.VERSION.SDK_INT >= 23) {
            int accessReadPhoneState
                    = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
            if (accessReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                // Các quyền cần người dùng cho phép.
                String[] permissions = new String[]{Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_PHONE_STATE};
                // Hiển thị một Dialog hỏi người dùng cho phép các quyền trên.
                ActivityCompat.requestPermissions(this, permissions,
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
                return;
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentPhoneNumber();
                } else {
                    Toast.makeText(this, "Tính năng lấy số điện thoại đã bị từ chối", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void getCurrentPhoneNumber(){
        TelephonyManager tMgr = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
//        Toast.makeText(this, "Xin chào " + tMgr.getLine1Number(), Toast.LENGTH_LONG).show();
        //mPhoneNumber = tMgr.getLine1Number();
    }
}
