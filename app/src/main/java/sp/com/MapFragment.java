// MapFragment.java
package sp.com;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private boolean isRecording = false;
    private Button btnRecordSession;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.diary_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Initialize gpsTracker with LocationUpdateListener
        gpsTracker = new GPSTracker(requireContext(), new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                // You can handle location updates here if needed
            }
        });

        btnRecordSession = view.findViewById(R.id.btnRecordSession);
        btnRecordSession.setOnClickListener(v -> onRecordSessionClick());

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Enable the My Location layer
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            // Move camera to the current location if available
            moveToCurrentLocation();
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        // Update map as the location changes
        gpsTracker.startLocationUpdates();

        gpsTracker.setLocationUpdateListener(new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                // Remove existing markers
                mMap.clear();

                // Fetch diary entries from the database
                List<DiaryEntry> diaryEntries = fetchDiaryEntries();

                // Iterate through entries and add markers
                for (DiaryEntry entry : diaryEntries) {
                    LatLng entryLocation = new LatLng(entry.getLatitude(), entry.getLongitude());

                    // Add a custom marker with an image
                    Bitmap bitmap = BitmapFactory.decodeFile(entry.getImagePath());
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(entryLocation)
                            .title(entry.getTitle())
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                }

                // Move camera to the current location
                LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }


    // Method to move the camera to the current location
    private void moveToCurrentLocation() {
        gpsTracker.getLocation(new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

    // Fetch diary entries from the database
    private List<DiaryEntry> fetchDiaryEntries() {
        List<DiaryEntry> entries = new ArrayList<>();
        DiaryHelper diaryHelper = new DiaryHelper(requireContext());

        Cursor cursor = diaryHelper.getAll();

        if (cursor != null) {
            int titleIndex = cursor.getColumnIndex("diaryTitle");
            int imageIndex = cursor.getColumnIndex("diaryImage");
            int descIndex = cursor.getColumnIndex("diaryDesc");
            int latIndex = cursor.getColumnIndex("lat");
            int lonIndex = cursor.getColumnIndex("lon");

            while (cursor.moveToNext()) {
                if (titleIndex != -1 && imageIndex != -1 && descIndex != -1 && latIndex != -1 && lonIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    String imagePath = cursor.getString(imageIndex);
                    String description = cursor.getString(descIndex);
                    double latitude = cursor.getDouble(latIndex);
                    double longitude = cursor.getDouble(lonIndex);

                    entries.add(new DiaryEntry(title, imagePath, description, latitude, longitude));
                }
            }

            cursor.close();
        }

        return entries;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        gpsTracker.stopLocationUpdates();
    }

    public void onRecordSessionClick() {
        isRecording = !isRecording;

        if (isRecording) {
            btnRecordSession.setText("Stop");
            // Request location updates
            gpsTracker.startLocationUpdates();
        } else {
            btnRecordSession.setText("Record");
            // Stop location updates
            gpsTracker.stopLocationUpdates();
        }
    }
}






