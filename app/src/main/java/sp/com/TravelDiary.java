package sp.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TravelDiary extends AppCompatActivity {

    private BottomNavigationView navView;
    private NewEntryFragment newEntryFragment;
    private ViewDiaryFragment viewDiaryFragment;
    private MapFragment mapViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        navView = findViewById(R.id.bottom_navigation);
        navView.setOnItemSelectedListener(menuSelected);

        // Initialize the fragments
        newEntryFragment = new NewEntryFragment();
        viewDiaryFragment = new ViewDiaryFragment();
        mapViewFragment = new MapFragment();

        // Load the default fragment when the app starts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newEntryFragment).commit();
    }

    private final BottomNavigationView.OnItemSelectedListener menuSelected = new BottomNavigationView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem item) {
            Fragment selectedFragment = null;

            int itemId = item.getItemId();
            if (itemId == R.id.nav_new_entry) {
                selectedFragment = newEntryFragment;
            } else if (itemId == R.id.nav_view_diary) {
                selectedFragment = viewDiaryFragment;
            } else if (itemId == R.id.nav_map_view) {
                selectedFragment = mapViewFragment;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }

            return true;
        }
    };
}


