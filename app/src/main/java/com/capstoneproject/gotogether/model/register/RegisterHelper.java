package com.capstoneproject.gotogether.model.register;

import android.os.AsyncTask;

import com.capstoneproject.gotogether.model.User;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by MinhNL on 10/2/2016.
 */
public class RegisterHelper extends AsyncTask<String,Void,String> implements IRegisterUser {
    IRegisterResult iRegisterResult;


    public RegisterHelper (IRegisterResult iRegisterResult){
        this.iRegisterResult = iRegisterResult;
    }

    @Override
    public void passUserFromLogin(User userInfo) {
//        iRegisterResult.passUserToFrag(userInfo);
    }

    @Override
    public void requestRegister(User userInfo) {

        String type = "register";
        String userId = userInfo.getUserId().toString();
        String fullname =  userInfo.getFullname();
        String gender = userInfo.getGender() + "";
        String email = userInfo.getEmail();
        String address = userInfo.getAddress();
        String phonenumber = userInfo.getPhonenumber();
        this.execute(type, userId, fullname, gender, email, address, phonenumber);

    }

    @Override
    protected String doInBackground(String... params) {
        String type = params[0];
        String insertURL = "http://fugotogether.com/sql/insertUser.php";
        if(type.equals("register")) {
            try {
                // lấy dữ liệu cần insert
                String id = params[1];
                String name = params[2];
                String finalGender = params[3];
                String email = params[4];
                String address = params[5];
                String phonenumber = params[6];

                //insert
                URL url = new URL(insertURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();

                // post data vào data base thông qua key và value
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                String post_data = URLEncoder.encode("userId","UTF-8")+"="+URLEncoder.encode(id,"UTF-8")+"&"
                        + URLEncoder.encode("fullname","UTF-8")+"="+URLEncoder.encode(name,"UTF-8")+"&"
                        + URLEncoder.encode("gender","UTF-8")+"="+URLEncoder.encode(finalGender,"UTF-8")+"&"
                        + URLEncoder.encode("email","UTF-8")+"="+URLEncoder.encode(email,"UTF-8")+"&"
                        + URLEncoder.encode("address","UTF-8")+"="+URLEncoder.encode(address,"UTF-8")+"&"
                        +URLEncoder.encode("phonenumber","UTF-8")+"="+URLEncoder.encode(phonenumber,"UTF-8");
                bufferedWriter.write(post_data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
                String result="";
                String line="";
                while((line = bufferedReader.readLine())!= null) {
                    result += line;
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {
        iRegisterResult.registerComplete(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
