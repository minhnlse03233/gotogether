package com.capstoneproject.gotogether;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class AddInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText txtName,txtPhone, txtEmail, txtAddress;
    String myValue,mPhoneNumber, email;

    //private OnFragmentInteractionListener mListener;

    public AddInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        // lấy giá trị từ login activity
        myValue = this.getArguments().getString("name");
        email = this.getArguments().getString("email");
        // Inflate the layout for this fragment
        txtName = (EditText) v.findViewById(R.id.editName);
        txtEmail = (EditText) v.findViewById(R.id.editEmail);
        txtPhone = (EditText) v.findViewById(R.id.editPhone);

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
        txtName.setText(myValue);
        txtPhone.setText(mPhoneNumber);
        txtEmail.setText(email);
    }

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }



}
