package sp.com;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.Toast;

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
    private static final int REQUEST_IMAGE_PICK = 2;
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
        Button buttonChooseImage = view.findViewById(R.id.buttonChooseImage);
        imageViewPhoto = view.findViewById(R.id.imageViewPhoto);
        Button buttonSaveEntry = view.findViewById(R.id.buttonSaveEntry);

        // initialization for gpsTracker
        gpsTracker = new GPSTracker(requireContext(), (latitude, longitude) -> {
            // You can handle location updates here if needed
        });

        buttonTakePhoto.setOnClickListener(v -> dispatchTakePictureIntent());
        buttonChooseImage.setOnClickListener(v -> openGallery());
        buttonSaveEntry.setOnClickListener(v -> saveEntry(editTextTitle, editTextDescription));

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
                        "sp.com.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_IMAGE_PICK);
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

    private void saveEntry(EditText editTextTitle, EditText editTextDescription) {
        if (currentPhotoPath == null) {
            Toast.makeText(requireContext(), "Please capture or choose an image before saving an entry", Toast.LENGTH_LONG).show();
        } else {
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
        }
    }

    // Setting the Image file path for further use
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                imageViewPhoto.setImageURI(photoUri);
                imageViewPhoto.setVisibility(View.VISIBLE);
            } else if (requestCode == REQUEST_IMAGE_PICK && data != null) {
                Uri selectedImage = data.getData();
                String imagePath = getRealPathFromURI(selectedImage);
                imageViewPhoto.setImageURI(selectedImage);
                imageViewPhoto.setVisibility(View.VISIBLE);
                currentPhotoPath = imagePath;
            }
        }
    }

    // getting real file path for chosen image
    private String getRealPathFromURI(Uri contentUri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(contentUri, projection, null, null, null);
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }
}





