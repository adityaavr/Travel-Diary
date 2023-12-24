package sp.com;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class TravelDiary extends AppCompatActivity {

    private BottomNavigationView navView;
    private NewEntryFragment newEntryFragment;
    private ViewDiaryFragment viewDiaryFragment;
    private MapFragment mapViewFragment;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;


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

        // Set a toolbar to replace the ActionBar.
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Drawer and back button to close the drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // Pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // Enable the home button in the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(navSelected);

        // Load the default fragment when the app starts
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newEntryFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle
        // If it returns true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        Intent intent;
        if (item.getItemId() == R.id.about) {
            intent = new Intent(TravelDiary.this, About.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
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

    private final NavigationView.OnNavigationItemSelectedListener navSelected = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            Intent intent;

            int id = item.getItemId();
            if (id == R.id.diary_entries) {
                selectedFragment = viewDiaryFragment;
                navView.setSelectedItemId(R.id.nav_view_diary);
            } else if (id == R.id.new_entry) {
                selectedFragment = newEntryFragment;
                navView.setSelectedItemId(R.id.nav_new_entry);
            } else if (id == R.id.map_view) {
                selectedFragment = mapViewFragment;
                navView.setSelectedItemId(R.id.nav_map_view);
            } else if (id == R.id.nav_drawer_about) {
                intent = new Intent(TravelDiary.this, About.class);
                startActivity(intent);
            } else if (id == R.id.exit) {
                finish();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    };
}


