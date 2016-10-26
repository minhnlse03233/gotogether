package com.capstoneproject.gotogether.model;

import java.math.BigInteger;

/**
 * Created by MinhNL on 10/5/2016.
 */
public class Trip {
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
    boolean status;
    double distance;
    double price;

    public Trip(){}
    public Trip(int tripId, BigInteger userId, String title, String description, String date_start, int slot, double start_lat, double end_lat, double start_lng, double end_lng, String listLatLng, boolean status, double distance, double price){
        this.tripId = tripId;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.date_start = date_start;
        this.slot = slot;
        this.start_lat = start_lat;
        this.end_lat = end_lat;
        this.start_lng = start_lng;
        this.end_lng = end_lng;
        this.listLatLng = listLatLng;
        this.status = status;
        this.distance = distance;
        this.price = price;
    }

    public void setTripId(int tripId){
        this.tripId = tripId;
    }

    public int getTripId(){
        return tripId;
    }

    public void setUserId(BigInteger userId){
        this.userId = userId;
    }

    public BigInteger getUserId(){
        return userId;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getTitle(){
        return title;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }

    public void setDate_start(String date_start){
        this.date_start = date_start;
    }

    public String getDate_start(){
        return date_start;
    }

    public void setSlot(int slot){
        this.slot = slot;
    }

    public int getSlot(){
        return slot;
    }

    public void setStart_lat(double start_lat){
        this.start_lat = start_lat;
    }

    public double getStart_lat(){
        return start_lat;
    }

    public void setEnd_lat(double end_lat){
        this.end_lat = end_lat;
    }

    public double getEnd_lat(){
        return end_lat;
    }

    public void setStart_lng(double start_lng){
        this.start_lng = start_lng;
    }

    public double getStart_lng(){
        return start_lng;
    }

    public void setEnd_lng(double end_lng){
        this.end_lng = end_lng;
    }

    public double getEnd_lng(){
        return end_lng;
    }

    public void setListLatLng(String listLatLng){
        this.listLatLng = listLatLng;
    }

    public String getListLatLng(){
        return listLatLng;
    }

    public void setStatus(boolean status){
        this.status = status;
    }

    public boolean getStatus(){
        return status;
    }

    public void setDistance(double distance){
        this.distance = distance;
    }

    public double getDistance(){
        return distance;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public double getPrice(){
        return price;
    }
}
