package com.hackingbuzz.googlemaplollipop3;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager manager;
    LocationListener locationListener;
    Location lastKnownLocation;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // why arent we taking just 2nd condition is because if we dont get any result or our array is empty it will throw an error

                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0 ,locationListener);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);



    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



        manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));


                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());  // for getting place address n put in address class cotaining in list

                try {
                    List<Address> listAddress = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                    if(listAddress !=null && listAddress.size()>0) {   // we must check first condition othewise if its null and we are checking size it will show error





                        String address = " ";

                        if(listAddress.get(0).getSubThoroughfare() !=null) {
                            address += listAddress.get(0).getSubThoroughfare() + " ";
                        }
                        if(listAddress.get(0).getThoroughfare() !=null) {
                            address += listAddress.get(0).getThoroughfare()+ ", ";
                        }
                        if(listAddress.get(0).getLocality() !=null) {
                            address += listAddress.get(0).getLocality()+ ", ";
                        }
                        if(listAddress.get(0).getPostalCode() !=null) {
                            address += listAddress.get(0).getPostalCode()+ ", ";
                        }
                        if(listAddress.get(0).getCountryName() !=null) {
                            address += listAddress.get(0).getCountryName()+ ", ";
                        }

                        Toast.makeText(MapsActivity.this, address, Toast.LENGTH_SHORT).show();




                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

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





        if(Build.VERSION.SDK_INT < 23) {

            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0 , locationListener);

        } else {  // else --> inside else if

          if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
              ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);  // not initilzing the string array so dont use = sign\

          }  else {
              manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 , 0 , locationListener);

              // we need user location when app loads so we can do that by getting user last known location..its the location where u left using google map..

                try {
                    lastKnownLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

              if(lastKnownLocation != null) {

                  LatLng userLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                  mMap.clear();
                  mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                  mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
              } else {
                  Toast.makeText(MapsActivity.this, "Location is null so..dont know what to do", Toast.LENGTH_SHORT);
              }
          }


        }


    }

    // sometime this program return null pointer exception due to ..lastknownlocation return null..i dont know why it gets null,,,

}
