package com.capstoneproject.gotogether.model.login;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
 * Created by Nguyen Luc on 9/28/2016.
 */
public class LoginHelper extends AsyncTask<String, Void, String> implements ILoginUser{
    ILoginResult iLoginResult;
    String result;

    public LoginHelper(ILoginResult iLoginResult){
        this.iLoginResult = iLoginResult;
    }

    public LoginHelper(){}

    @Override
    protected String doInBackground(String... params) {
        String id = params[0];

        try {
            URL url = new URL(LoginConfig.DATA_URL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data = URLEncoder.encode("id","UTF-8")+"="+URLEncoder.encode(id,"UTF-8");
            bufferedWriter.write(post_data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
            String result="";
            StringBuilder stringBuilder = new StringBuilder();
            String line="";
            while((line = bufferedReader.readLine())!= null) {
                result += line;
                stringBuilder.append(line + "\n");
            }
            bufferedReader.close();
            inputStream.close();
            httpURLConnection.disconnect();
//            iLoginResult.loginStatus("Nguoi dung chua dang cmn nhap");
            return result;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onPostExecute(String result) {
//        iLoginResult.loginStatus("Result: " + result);
        String id = "";
        try {
            if(!result.equals("")){
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray(LoginConfig.JSON_ARRAY);
                if(jsonArray.length() == 0)
                    iLoginResult.loginStatus("NoActive");
                else
                    iLoginResult.loginStatus("Active");
            }

//            JSONObject collegeData = jsonArray.getJSONObject(0);
//            id = collegeData.getString(LoginConfig.KEY_ID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void setResult(String result){this.result = result;}
    public String getResult(){return result;}

    @Override
    public void loginSuccess(String id){
        this.execute(id);
    }

    public void receiveStatus(String status){
        iLoginResult.loginStatus(status);
    }

    @Override
    public void loginError() {
        iLoginResult.loginError();
    }

}
