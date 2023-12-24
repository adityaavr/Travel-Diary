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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE = 101;

    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    private DiaryEntry selectedEntry;

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

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            moveToCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }

        gpsTracker.startLocationUpdates();

        gpsTracker.setLocationUpdateListener(new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                mMap.clear();

                if (selectedEntry != null) {
                    LatLng entryLocation = new LatLng(selectedEntry.getLatitude(), selectedEntry.getLongitude());

                    Bitmap bitmap = BitmapFactory.decodeFile(selectedEntry.getImagePath());
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(entryLocation)
                            .title(selectedEntry.getTitle())
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(entryLocation, 15f));
                } else {
                    List<DiaryEntry> diaryEntries = fetchDiaryEntries();
                    List<LatLng> entryLocations = new ArrayList<>();

                    for (DiaryEntry entry : diaryEntries) {
                        LatLng entryLocation = new LatLng(entry.getLatitude(), entry.getLongitude());
                        entryLocations.add(entryLocation);

                        Bitmap bitmap = BitmapFactory.decodeFile(entry.getImagePath());
                        Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                        mMap.addMarker(new MarkerOptions()
                                .position(entryLocation)
                                .title(entry.getTitle())
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                    }

                    LatLng currentLocation = new LatLng(latitude, longitude);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
                }
            }
        });
    }


    private void moveToCurrentLocation() {
        gpsTracker.getLocation(new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

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
            int dateIndex = cursor.getColumnIndex("timestamp");

            while (cursor.moveToNext()) {
                if (titleIndex != -1 && imageIndex != -1 && descIndex != -1 && latIndex != -1 && lonIndex != -1 && dateIndex != -1) {
                    String title = cursor.getString(titleIndex);
                    String imagePath = cursor.getString(imageIndex);
                    String description = cursor.getString(descIndex);
                    double latitude = cursor.getDouble(latIndex);
                    double longitude = cursor.getDouble(lonIndex);
                    long date = cursor.getLong(dateIndex);

                    entries.add(new DiaryEntry(title, imagePath, description, latitude, longitude, date));
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

    private List<DiaryEntry> fetchDiaryEntriesForDate(long timestamp) {
        List<DiaryEntry> entries = new ArrayList<>();
        DiaryHelper diaryHelper = new DiaryHelper(requireContext());

        // Convert the timestamp to the "yyyy-MM-dd" format
        String dateString = convertTimestampToString(timestamp);

        // Use the dateString parameter in the getByDate method
        Cursor cursor = diaryHelper.getByDate(dateString);

        if (cursor != null) {
            try {
                int titleIndex = cursor.getColumnIndex("diaryTitle");
                int imageIndex = cursor.getColumnIndex("diaryImage");
                int descIndex = cursor.getColumnIndex("diaryDesc");
                int latIndex = cursor.getColumnIndex("lat");
                int lonIndex = cursor.getColumnIndex("lon");
                int dateIndex = cursor.getColumnIndex("timestamp");

                while (cursor.moveToNext()) {
                    if (titleIndex != -1 && imageIndex != -1 && descIndex != -1 && latIndex != -1 && lonIndex != -1 && dateIndex != -1) {
                        String title = cursor.getString(titleIndex);
                        String imagePath = cursor.getString(imageIndex);
                        String description = cursor.getString(descIndex);
                        double latitude = cursor.getDouble(latIndex);
                        double longitude = cursor.getDouble(lonIndex);
                        long entryTimestamp = cursor.getLong(dateIndex);

                        // Ensure 'entry' variable is unique
                        DiaryEntry entry = new DiaryEntry(title, imagePath, description, latitude, longitude, entryTimestamp);
                        entries.add(entry);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return entries;
    }

    // Method to convert a timestamp to a string date in "yyyy-MM-dd" format
    private String convertTimestampToString(long timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }

    public void setSelectedEntry(DiaryEntry entry) {
        this.selectedEntry = entry;

        if (mMap != null) {
            mMap.clear();

            if (selectedEntry != null) {
                LatLng entryLocation = new LatLng(selectedEntry.getLatitude(), selectedEntry.getLongitude());

                Bitmap bitmap = BitmapFactory.decodeFile(selectedEntry.getImagePath());
                Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                mMap.addMarker(new MarkerOptions()
                        .position(entryLocation)
                        .title(selectedEntry.getTitle())
                        .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));

                // Move the camera to the selected location
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(entryLocation, 15f));
            }

            // Fetch all diary entries for the selected date
            List<DiaryEntry> diaryEntries = fetchDiaryEntriesForDate(selectedEntry.getDate());

            if (!diaryEntries.isEmpty()) {
                // Display all entries and draw a polyline connecting them
                List<LatLng> entryLocations = new ArrayList<>();

                for (DiaryEntry diaryEntry : diaryEntries) {
                    LatLng entryLocation = new LatLng(diaryEntry.getLatitude(), diaryEntry.getLongitude());
                    entryLocations.add(entryLocation);

                    Bitmap bitmap = BitmapFactory.decodeFile(diaryEntry.getImagePath());
                    Bitmap smallMarker = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
                    mMap.addMarker(new MarkerOptions()
                            .position(entryLocation)
                            .title(diaryEntry.getTitle())
                            .icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
                }
            }

        }
    }

}










