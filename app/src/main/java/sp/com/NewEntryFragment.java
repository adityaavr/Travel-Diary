package sp.com;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewEntryFragment extends Fragment {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private ImageView imageViewPhoto;
    private Uri photoUri;
    private String currentPhotoPath;
    private DiaryHelper diaryHelper;
    private GPSTracker gpsTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diaryHelper = new DiaryHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_entry, container, false);

        EditText editTextTitle = view.findViewById(R.id.editTextEntryTitle);
        EditText editTextDescription = view.findViewById(R.id.editTextEntryDescription);
        Button buttonTakePhoto = view.findViewById(R.id.buttonTakePhoto);
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        Button buttonSaveEntry = view.findViewById(R.id.buttonSaveEntry);

        // initialisation for gpsTracker
        gpsTracker = new GPSTracker(requireContext(), new GPSTracker.LocationUpdateListener() {
            @Override
            public void onLocationUpdate(double latitude, double longitude) {
                // You can handle location updates here if needed
            }
        });

        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        buttonSaveEntry.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            String description = editTextDescription.getText().toString();

            // Get the current location from the GPS tracker
            double currentLat = gpsTracker.getLatitude();
            double currentLon = gpsTracker.getLongitude();

            diaryHelper.insert(title, currentPhotoPath, description, currentLat, currentLon);

            editTextTitle.setText("");
            editTextDescription.setText("");
            imageViewPhoto.setVisibility(View.GONE);
            currentPhotoPath = null; // Reset the current photo path after saving
        });


        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Handle the error
            }
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(getContext(),
                        "sp.com.fileprovider", // Adjust with your package name
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            imageViewPhoto.setImageURI(photoUri);
            imageViewPhoto.setVisibility(View.VISIBLE);
        }
    }
}


