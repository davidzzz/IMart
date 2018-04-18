package com.imart.shop.util;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.imart.shop.R;


public class GPSTracker implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final Context mContext;
    public GoogleApiClient mGoogleApiClient;
    public LocationRequest mLocationRequest;
    public LocationSettingsRequest mLocationSettingsRequest;
    public Location mCurrentLocation;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    private boolean canGetLocation = false;
    private double latitude;
    private double longitude;

    public GPSTracker(Context context) {
        this.mContext = context;
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        enableGPS();
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void enableGPS() {
        try {
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled || isNetworkEnabled) {
                canGetLocation = true;
            }/* else {
                mContext.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }*/
        } catch (Exception e) {
            Toast.makeText(mContext, "Terjadi kesalahan saat mengaktifkan GPS", Toast.LENGTH_LONG).show();
        }
    }

    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    public boolean isConnected(){
        return mGoogleApiClient.isConnected();
    }

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (canGetLocation() && mCurrentLocation == null) {
            try {
                mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                getLatitude();
                getLongitude();
            } catch (SecurityException e) {
                Toast.makeText(mContext, "Lokasi tidak terdeteksi", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        if (canGetLocation() && mCurrentLocation != null) {
            getLatitude();
            getLongitude();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Toast.makeText(mContext, "Koneksi gagal", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Toast.makeText(mContext, "Menyambungkan kembali koneksi", Toast.LENGTH_LONG).show();
        mGoogleApiClient.connect();
    }

    public double getLatitude(){
        if (canGetLocation() && mCurrentLocation != null) {
            latitude = mCurrentLocation.getLatitude();
        }

        return latitude;
    }

    public double getLongitude() {
        if (canGetLocation() && mCurrentLocation != null) {
            longitude = mCurrentLocation.getLongitude();
        }

        return longitude;
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     * */
    public boolean canGetLocation() {
        enableGPS();
        return this.canGetLocation;
    }


    /**
     * Function to show settings alert dialog.
     * On pressing the Settings button it will launch Settings Options.
     * */
    public void showSettingsAlert(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        //alertDialog.setCancelable(false);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        alertDialog.setView(dialogView);

        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.txtTitleAlert);
        dialogTitle.setText("GPS is settings");
        Button setting = (Button) dialogView.findViewById(R.id.btnsetting);
        final AlertDialog dialog = alertDialog.create();
        setting .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                dialog.cancel();
                mContext.startActivity(intent);

            }
        });
        dialog.show();
    }
}
