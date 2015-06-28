package com.example.tempus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

public class GPS extends Service implements LocationListener
{
    Context mContext;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location location;
    double latitude;
    double longitude;
    LocationManager mLocationManager;

    public GPS(Context context)
    {
        this.mContext = context;
        mLocationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

    }

    public Location getLocation()
    {
        try
        {
            isGPSEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (isGPSEnabled)
            {
                if (mLocationManager != null)
                {
                   location = mLocationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        return location;
                    }
                }
            }

            isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnabled)
            {
                if (mLocationManager != null)
                {
                    location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null)
                    {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        return location;
                    }
                }
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public double getLatitude()
    {
        if (location != null)
        {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    public double getLongitude()
    {
        if (location != null)
        {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    public void closeGPS()
    {
        if (mLocationManager != null)
        {
            mLocationManager.removeUpdates(GPS.this);
        }
    }

    @Override
    public void onLocationChanged(Location location){}

    @Override
    public void onProviderDisabled(String provider){}

    @Override
    public void onProviderEnabled(String provider){}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){}

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }
}
