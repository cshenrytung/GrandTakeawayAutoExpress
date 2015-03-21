package com.share.gta.helper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.share.gta.R;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.SystemService;

import java.util.List;

/**
 * Created by chiyungtung on 22/3/15.
 */

@EBean(scope = EBean.Scope.Singleton)
public class LocationHelper implements LocationListener{

    protected LocationListener locationListener;

    private String lon = "-90.286781";
    private String lat = "38.53463";

    @SystemService
    LocationManager locationManager;

    @AfterInject
    protected void init() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }



    @Override
    public void onLocationChanged(Location location) {

        lat  = location.getLatitude()+"";
        lon = location.getLongitude()+"";
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public String getLat() {
        return lat;
    }

    public String getLon() {
        return lon;
    }
}
