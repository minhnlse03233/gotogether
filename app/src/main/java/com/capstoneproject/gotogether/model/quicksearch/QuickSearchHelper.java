package com.capstoneproject.gotogether.model.quicksearch;

import android.os.AsyncTask;

import com.capstoneproject.gotogether.model.Trip;
import com.google.android.gms.maps.model.LatLng;

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
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by MinhNL on 10/5/2016.
 */
public class QuickSearchHelper extends AsyncTask<String,Void,String> implements IQuickSearchUser {
    IQuickSearchResult iQuickSearchResult;

    public QuickSearchHelper(){}

    public QuickSearchHelper(IQuickSearchResult iQuickSearchResult){
        this.iQuickSearchResult = iQuickSearchResult;
    }


    @Override
    public void loadTrip(LatLng currentLocation, LatLng endLocation) {
        String startLat = currentLocation.latitude + "";
        String startLng = currentLocation.longitude + "";
        String endLat = endLocation.latitude + "";
        String endLng = endLocation.longitude + "";
//        this.execute(startLat, startLng, endLat, endLng);
        QuickSearchHelper myTask = null;
        myTask = new QuickSearchHelper(iQuickSearchResult);
        myTask.execute(startLat, startLng, endLat, endLng);
    }

    @Override
    protected String doInBackground(String... params) {
        String startLat = params[0];
        String startLng = params[1];
        String endLat = params[2];
        String endLng = params[3];
        String insertURL = "http://fugotogether.com/sql/quickSearch.php";
        try {
            URL url = new URL(insertURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
            String post_data  =  URLEncoder.encode("startLat","UTF-8")+"="+URLEncoder.encode(startLat,"UTF-8")+"&"
                                +URLEncoder.encode("startLng","UTF-8")+"="+URLEncoder.encode(startLng,"UTF-8")+"&"
                                +URLEncoder.encode("endLat","UTF-8")+"="+URLEncoder.encode(endLat,"UTF-8")+"&"
                                +URLEncoder.encode("endLng","UTF-8")+"="+URLEncoder.encode(endLng,"UTF-8");
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

        return null;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected void onPostExecute(String result) {
        Trip currentTrip;
        int tripId;
        BigInteger userId;
        String title;
        String description;
        String date_start;
        int slot;
        double start_lat;
        double end_lat;
        double start_lng;
        double end_lng;
        String listLatLng;
        boolean status = true;
        double distance;
        ArrayList<Trip> currentTrips = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("trips");

            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject trip = jsonArray.getJSONObject(i);
                tripId = Integer.parseInt(trip.getString("tripId"));
                userId = new BigInteger(trip.getString("userId"));
                title = trip.getString("title");
                description = trip.getString("description");
                date_start = trip.getString("date_start");
                slot = Integer.parseInt(trip.getString("slot"));
                start_lat = Double.parseDouble(trip.getString("start_lat"));
                end_lat = Double.parseDouble(trip.getString("end_lat"));
                start_lng = Double.parseDouble(trip.getString("start_lng"));
                end_lng = Double.parseDouble(trip.getString("end_lng"));
                listLatLng = trip.getString("listLatLng");
                if(trip.getString("status").equals("1")){
                    status = true;
                }
                distance = Double.parseDouble(trip.getString("distancetostart"));
                currentTrip = new Trip(tripId, userId, title, description, date_start, slot, start_lat, end_lat, start_lng, end_lng, listLatLng, status, distance);
                currentTrips.add(currentTrip);
            }
            iQuickSearchResult.returnTrip(currentTrips);
//            this.cancel(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //iQuickSearchResult.returnTrip(result);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        // Make sure we clean up if the task is killed
        this.cancel(true);
    }

}
