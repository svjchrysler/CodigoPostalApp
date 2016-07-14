package com.codigopostal.svjchrysler.codigopostal.Activities;

import android.location.LocationListener;
import android.os.Bundle;

/**
 * Created by salguero on 13-07-16.
 */
public class Location implements LocationListener {
    RegisterUbicationActivity registerUbicationActivity;

    public RegisterUbicationActivity getRegisterUbicationActivity() {
        return registerUbicationActivity;
    }

    public void setRegisterUbicationActivity(RegisterUbicationActivity registerUbicationActivity) {
        this.registerUbicationActivity = registerUbicationActivity;
    }

    @Override
    public void onLocationChanged(android.location.Location loc) {
        loc.getLatitude();
        loc.getLongitude();
        String Text = "Mi ubicacion actual es: " + "\n Lat = "
                + loc.getLatitude() + "\n Long = " + loc.getLongitude();

        registerUbicationActivity.setLocation(loc);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        registerUbicationActivity.tvCoordenada.setText("GPS Activado");
    }

    @Override
    public void onProviderDisabled(String provider) {
        registerUbicationActivity.tvCoordenada.setText("GPS Desactivado");
    }
}
