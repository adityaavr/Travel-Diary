package sp.com;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import sp.com.databinding.FragmentMapViewBinding;

public class MapViewFragment extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FragmentMapViewBinding binding;
    private double lat;
    private double lon;
    private String diaryTitle;
    private boolean isRecording;
    private Button btnRecordSession;
    private GPSTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.diary_map);
        mapFragment.getMapAsync(this);

        gpsTracker = new GPSTracker(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker at the initial position (Sydney) and move the camera
        LatLng initialPosition = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(initialPosition).title("Marker at Initial Position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(initialPosition));

        // Assuming you have a button with the id btnRecordSession in your layout
        btnRecordSession = findViewById(R.id.btnRecordSession);
        btnRecordSession.setOnClickListener(v -> onRecordSessionClick());

        // Update map as the location changes
        gpsTracker.startLocationUpdates();

        gpsTracker.setLocationUpdateListener(new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                // Update map with the new location
                LatLng currentLocation = new LatLng(latitude, longitude);
                mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker at Current Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
            }
        });
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


