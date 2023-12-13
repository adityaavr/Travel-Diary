package sp.com;

import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import sp.com.databinding.FragmentMapViewBinding;

public class MapViewFragment extends FragmentActivity {

    private FragmentMapViewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentMapViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the MapFragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MapFragment())
                .commit();
    }
}



