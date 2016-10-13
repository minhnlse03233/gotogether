package com.capstoneproject.gotogether.presenter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;

import com.capstoneproject.gotogether.LoginActivity;

/**
 * Created by MinhNL on 10/6/2016.
 */

public abstract class ConnectionReceiver extends BroadcastReceiver {
    public ConnectionReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE );
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
        checkConnection(isConnected);
        if (isConnected == false){
//            AlertDialog.Builder alert = new AlertDialog.Builder(context);
//            AlertDialog alertDialog = alert.create();
//            alertDialog.show();
//            new AlertDialog.Builder(this).setTitle("Kết Nối Mạng").setMessage("Vui lòng kiểm tra lại đường truyền mạng").setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    System.exit(0);
//                }
//            }).show();
        }
    }

    protected abstract void checkConnection(boolean isConnect);

}