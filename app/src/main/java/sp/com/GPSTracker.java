package sp.com;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class GPSTracker extends Service {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private final Context mContext;
    private FusedLocationProviderClient fusedLocationClient;
    private Location location;
    private double latitude;
    private double longitude;
    private LocationCallback locationCallback;
    private LocationUpdateListener locationUpdateListener;

    public GPSTracker(Context context, LocationUpdateListener listener) {
        this.mContext = context;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        getLocation(listener);
    }

    public void getLocation(LocationUpdateListener listener) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted yet, handle it as needed
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location loc) {
                        if (loc != null) {
                            location = loc;
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            // Notify the listener
                            if (listener != null) {
                                listener.onLocationUpdate(latitude, longitude);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure, such as displaying an error message or taking appropriate action
                        Log.e("GPSTracker", "Error getting location", e);
                    }
                });
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request Permissions
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
            return;
        }

        // Create a location request with desired parameters
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(100); // Update interval in milliseconds
        locationRequest.setFastestInterval(50); // Fastest update interval in milliseconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Create a location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    // Update location values
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Notify the listener
                    if (locationUpdateListener != null) {
                        locationUpdateListener.onLocationUpdate(latitude, longitude);
                    }
                }
            }
        };

        // Request location updates
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    public void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            // Remove location updates
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public void setLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(double latitude, double longitude);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}




