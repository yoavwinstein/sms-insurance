package ensurance.yoav.smsensurance;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class DriveAwareService extends Service {
    public DriveAwareService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria locationCriteria = new Criteria();
        // We need to determine the speed of the user, in order to determine whether it is driving or not.
        locationCriteria.setSpeedRequired(true);
        String bestProvider = locationManager.getBestProvider(locationCriteria, false);
        Log.d("DriveAware", "TROLOLOL");
        LocationListener listener = new LocationListener() {
            long lastTimeDrove = System.currentTimeMillis();

            @Override
            public void onLocationChanged(Location location) {
                Log.d("DriveAware", "Location Update");
                // Speed in meters / seconds converted to km/h.
                float speed = location.getSpeed() * METERS_SECONDS_TO_KMH;
                if (speed > MINIMUM_DRIVING_SPEED_KMH) {
                    lastTimeDrove = System.currentTimeMillis();
                }

                if (lastTimeDrove > 15000) {
                    Log.d("DriveAware", "We are proz!");
                }
                Toast.makeText(DriveAwareService.this, "Update!@!@bg", Toast.LENGTH_SHORT);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {
                lastTimeDrove = System.currentTimeMillis();
                Toast.makeText(DriveAwareService.this, "Enabled!", Toast.LENGTH_SHORT);
            }

            @Override
            public void onProviderDisabled(String provider) {
                Toast.makeText(DriveAwareService.this, "Disabled!", Toast.LENGTH_SHORT);
            }
        };

        if (PackageManager.PERMISSION_GRANTED ==
                getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName())) {
            locationManager.requestLocationUpdates(bestProvider, 0, 0.0f, listener);
        } else {
            Toast.makeText(this, R.string.permission_location, Toast.LENGTH_SHORT);
        }

        return Service.START_NOT_STICKY;
    }

    private final IBinder mBinder = new Binder();
    private final float METERS_SECONDS_TO_KMH = 3.6f;
    private final float MINIMUM_DRIVING_SPEED_KMH = 15.0f;
}
