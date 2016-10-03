package com.capstoneproject.gotogether;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.capstoneproject.gotogether.model.User;
import com.capstoneproject.gotogether.presenter.register.IRegisterPresenter;
import com.capstoneproject.gotogether.presenter.register.RegisterPresenter;
import com.capstoneproject.gotogether.view.register.IRegisterViewFrag;

import java.math.BigInteger;


public class AddInfoFragment extends Fragment implements View.OnClickListener, IRegisterViewFrag{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    EditText txtName,txtPhone, txtEmail, txtAddress;
    String name, mPhoneNumber, email,id, gender;
    Button btnSave, btnIgnore;
    IRegisterPresenter iRegisterPresenter;
    User userInfo;
    String finalGender;

    public AddInfoFragment() {}

    public static AddInfoFragment newInstance(String param1, String param2) {
        AddInfoFragment fragment = new AddInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_info, container, false);

        iRegisterPresenter = new RegisterPresenter(this);
        // lấy giá trị từ login activity
        name = this.getArguments().getString("name");
        email = this.getArguments().getString("email");
        id = this.getArguments().getString("id");
        gender = this.getArguments().getString("gender");


        if (gender.equals("male") ) {
            finalGender = "1";
        } else {
            finalGender = "0";
        }
        // Inflate the layout for this fragment
        iRegisterPresenter = new RegisterPresenter(this);

        txtName = (EditText) v.findViewById(R.id.editName);
        txtEmail = (EditText) v.findViewById(R.id.editEmail);
        txtPhone = (EditText) v.findViewById(R.id.editPhone);
        btnSave = (Button) v.findViewById(R.id.btnSave);
        //btnIgnore = (Button) v.findViewById(R.id.btnIgnore);
        txtAddress = (EditText) v.findViewById(R.id.editAddress);
        btnSave.setOnClickListener(this);
//        btnIgnore.setOnClickListener(this);
        try {
            TelephonyManager tMgr = (TelephonyManager)getContext().getSystemService(Context.TELEPHONY_SERVICE);
            mPhoneNumber = tMgr.getLine1Number();
        } catch (Exception e){

        }


        return v;

    }

    @Override
    public void onResume() {
        super.onResume();
        txtName.setText(name);
        txtPhone.setText(mPhoneNumber);
        txtEmail.setText(email);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        int wiget = v.getId();
        switch (wiget) {
            case R.id.btnSave:
                try{
                    String phonenumber = txtPhone.getText().toString();
                    String address = txtAddress.getText().toString();
                    if(name.equals("") ) {
                        Toast.makeText(getContext(), "Hãy nhập Tên của bạn", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(phonenumber.equals("") ) {
                        Toast.makeText(getContext(), "Hãy nhập SĐT", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if (email == null) {
                        email = "";
                    }
                    String type = "register";
                    userInfo = new User(new BigInteger(id), name, Integer.parseInt(finalGender), email, address, phonenumber);
//                    BackgroundWorker backgroundWorker = new BackgroundWorker(getContext());
//                    backgroundWorker.execute(type,id, name, finalGender,email, address, phonenumber);
                    iRegisterPresenter.registerNewUser(userInfo);
                    bundleToMain(id, name, email);
                } catch ( Exception ex) {}

//                goToMain();

                break;
//            case R.id.btnIgnore:
//                String phonenumber1 = txtPhone.getText().toString();
//                String address1 = txtAddress.getText().toString();
//                if (email == null) {
//                    email = "";
//                }
//                String type1 = "register";
//                 BackgroundWorker backgroundWorker1 = new BackgroundWorker(getContext());
//                 backgroundWorker1.execute(type1,id, name, finalGender,email, address1, phonenumber1 );
//                bundleToMain(id, name, email);
//                break;
//            default:
        }
    }

    public void goToMain () {
        Intent intent = new Intent(getContext(), MainActivity.class);
        startActivity(intent);
    }

    public void bundleToMain(String id, String name, String email){
        Intent intent = new Intent(getContext(), MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("id", id);
        bundle.putString("name", name);
        bundle.putString("email", email);
        intent.putExtra("profile", bundle);
        startActivity(intent);

    }

    @Override
    public void receiveUserFromLoginFrag(User userInfo) {
//        Toast.makeText(getContext(), "OK bo nhan dc roi nhe", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void receivedAction() {
//        Toast.makeText(getContext(), "OK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void noticeRegister(String result) {
//        Toast.makeText(getContext(), "OK nhé đăng ký thành công rồi đấy", Toast.LENGTH_SHORT).show();
    }
}
