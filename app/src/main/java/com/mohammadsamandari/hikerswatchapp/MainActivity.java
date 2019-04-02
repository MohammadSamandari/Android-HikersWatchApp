package com.mohammadsamandari.hikerswatchapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    //  Defining Location Manager and Location Listener.
    LocationManager locationManager;
    LocationListener locationListener;

    //  Defining The textView for the information to show.
    TextView txt_lat, txt_lon, txt_acc, txt_alt, txt_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //  Findind Views by Id for txts
        txt_lat = findViewById(R.id.txt_LatShow);
        txt_lon = findViewById(R.id.txt_lonShow);
        txt_acc = findViewById(R.id.txt_accShow);
        txt_alt = findViewById(R.id.txt_altShow);
        txt_add = findViewById(R.id.txt_addShow);

        //  Getting system service for the location manager.
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        //  Creating the listener for location changes.
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                //  Extracting the information needed from new location.
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());
                String accuracy = String.valueOf(location.getAccuracy());
                String altitude = String.valueOf(location.getAltitude());

                String address;
                //Getting the address from location.
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    String country = addresses.get(0).getCountryName();
                    //  Spiliting the adress line extracted from location into an array
                    //  so that useful information can be reached.
                    String[] addressLines=String.valueOf(addresses.get(0).getAddressLine(0)).split("،");
                    String addressLine="";
                    //  Looping through the adressline array to extract information.
                    //  escaping the last one because it contains the name of the country.
                    for(int i=0;i<addressLines.length-1;i++){
                        //  Check to see if this item in the array is not empty.
                        if (addressLines[i].length()!=0){
                            //  Adding the info ro the address.
                            addressLine+=addressLines[i]+"\n";
                        }
                    }
                    address = country + "\n" + addressLine;
                } catch (IOException e) {
                    e.printStackTrace();
                    address = "Not Found";
                }

                //  Calling UpdateTextView Method to update the UI.
                updateTextViews(latitude, longitude, accuracy, altitude, address);

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
        };

        //  Checking Our Permission to access the Location.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            //  Requesting Location if there is permission.
            requestLocation();
        }
    }

    private void requestLocation() {
        //  This Method request Location from GPS.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }
    }

    private void updateTextViews(String latitude, String longitude, String accuracy, String altitude, String address) {
        txt_lat.setText(latitude);
        txt_lon.setText(longitude);
        txt_acc.setText(accuracy);
        txt_alt.setText(altitude);
        txt_add.setText(address);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //  Checking The Result Of the Get Permission Dialog. if permission is granted, we are going to
        //  update location, else we are going to inform user we don't have permission.
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            Toast.makeText(this, "We Don't Have Permission To Access Your Location, Please Give us the Permission.", Toast.LENGTH_LONG).show();
        }
    }
}
