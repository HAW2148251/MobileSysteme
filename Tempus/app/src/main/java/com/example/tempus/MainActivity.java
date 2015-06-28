package com.example.tempus;

import android.location.Location;
import android.location.LocationManager;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;
import android.content.Context;
import android.widget.TextView;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationListener;
import java.util.Locale;
import java.util.List;

public class MainActivity extends ActionBarActivity
{

    double LatitudeA = 0.0, LatitudeB = 0.0, LongitudeA = 0.0, LongitudeB = 0.0;
    boolean running = false;
    Context mContext = this;
    float completeDist = 0;
    float completeDistance = 0;
    float[] results = new float[3];
    Button button;
    float MaxSpeed = 0;
    Chronometer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //###############################
        // Code from: http://stackoverflow.com/questions/14475109/remove-android-app-title-bar
        android.support.v7.app.ActionBar
        AB=getSupportActionBar();
        AB.hide();
        //###############################

        initializeLocationManager();

        //Chronometer
        timer = (Chronometer) findViewById(R.id.chronometer);

        //Der Start/Stop Button
        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener()
        {


            @Override
            public void onClick(View arg0)
            {
                // TODO Auto-generated method stub
                // Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_LONG).show();
                if(running)
                {
                    running = false;

                    timer.stop();
                    button.setText("Start");
                }
                else
                {
                    reset();
                    running = true;
                    button.setText("Beenden");

                    timer.setBase(SystemClock.elapsedRealtime());
                    timer.start();
                }
            }
        });
    }

    private void reset()
    {

        GPS gps = new GPS(mContext);
        gps.getLocation();

        LatitudeA = gps.getLatitude();
        LongitudeA = gps.getLongitude();

        MaxSpeed = 0;
        completeDistance = 0;
        results[0] = 0;

        ((TextView)findViewById(R.id.textView4)).setText(String.format("0 m/s"));
        ((TextView)findViewById(R.id.textView3)).setText(String.format("0 m/s"));
        ((TextView)findViewById(R.id.textView)).setText(String.format("0 m"));

        gps.closeGPS();
    }

    private void displayLocation()
    {
        GPS gps = new GPS(mContext);
        gps.getLocation();

        ((TextView)findViewById(R.id.textView2)).setText(String.format("Adresse unbekannt."));

        try
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(LatitudeA, LongitudeA, 1);

            Address address = addresses.get(0);
            String addressText = String.format( address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "",address.getLocality());

            // Toast.makeText(getApplicationContext(), addressText, Toast.LENGTH_LONG).show();
            ((TextView)findViewById(R.id.textView2)).setText(String.format(addressText));

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        gps.closeGPS();
    }

    private void initializeLocationManager()
    {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                if(running){

                    displayLocation();
                    GPS gps = new GPS(mContext);
                    gps.getLocation();

                    //  Toast.makeText(getApplicationContext(), "Accuracy: " + location.getAccuracy(), Toast.LENGTH_LONG).show();

                    LatitudeB = gps.getLatitude();
                    LongitudeB = gps.getLongitude();

                    Location.distanceBetween(LongitudeA, LatitudeA,LongitudeB, LatitudeB,results);

                        LatitudeA = LatitudeB;
                        LongitudeA = LongitudeB;

                        completeDistance += results[0];

                        //###############################
                        // Code from: http://stackoverflow.com/questions/13295140/android-calculations-with-time-elapsed-in-the-chronometer
                        long timeElapsed = SystemClock.elapsedRealtime() - timer.getBase();
                        int hours = (int) (timeElapsed / 3600000);
                        int minutes = (int) (timeElapsed - hours * 3600000) / 60000;
                        float seconds = (int) (timeElapsed - hours * 3600000 - minutes * 60000) / 1000;
                        //###############################

                        ((TextView)findViewById(R.id.textView4)).setText(String.format(completeDistance/seconds + " m/s"));

                        if((completeDistance/seconds) > MaxSpeed && (completeDistance/seconds) < 1000000) //Die 1000000 vermeidet einen Wert wie "Infinity"
                        {
                            MaxSpeed = completeDistance/seconds;
                        }

                    ((TextView)findViewById(R.id.textView3)).setText(String.format(MaxSpeed + " m/s"));

                    gps.closeGPS();
                    ((TextView)findViewById(R.id.textView)).setText(String.format(completeDistance + " m"));
                }
            }
            
            public void onStatusChanged(String provider, int status, Bundle extras) {}
            public void onProviderEnabled(String provider) {}
            public void onProviderDisabled(String provider) {}

        };

        //Toast.makeText(getApplicationContext(), "Lat: " + location.getLatitude() + " Long: " + location.getLongitude() , Toast.LENGTH_LONG).show();

        // Register the listener with the Location Manager to receive location updates
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }else
        {
            if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}
