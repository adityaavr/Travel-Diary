package sp.com;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;


import java.util.ArrayList;
import java.util.List;


public class ViewDiaryFragment extends Fragment {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter adapter;
    private DiaryHelper diaryHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diaryHelper = new DiaryHelper(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_diary, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Fetch diary entries from the database
        List<DiaryEntry> diaryEntries = fetchDiaryEntries();

        adapter = new DiaryEntryAdapter(diaryEntries);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<DiaryEntry> fetchDiaryEntries() {
        List<DiaryEntry> entries = new ArrayList<>();
        Cursor cursor = diaryHelper.getAll();

        if (cursor != null) {
            int titleIndex = cursor.getColumnIndex("diaryTitle");
            int imageIndex = cursor.getColumnIndex("diaryImage");
            int descIndex = cursor.getColumnIndex("diaryDesc");
            int latIndex = cursor.getColumnIndex("lat");
            int lonIndex = cursor.getColumnIndex("lon");

            while (cursor.moveToNext()) {
                // Check if the columns exist in the cursor
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


    // Inner class for the RecyclerView adapter
    private class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.DiaryEntryViewHolder> {

        private final List<DiaryEntry> diaryEntries;

        DiaryEntryAdapter(List<DiaryEntry> diaryEntries) {
            this.diaryEntries = diaryEntries;
        }

        @NonNull
        @Override
        public DiaryEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_diary_entry, parent, false);
            return new DiaryEntryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DiaryEntryViewHolder holder, int position) {
            DiaryEntry entry = diaryEntries.get(position);
            holder.textViewDiaryTitle.setText(entry.getTitle());
            holder.textViewDiaryDescription.setText(entry.getDescription());

            // Load image into imageViewDiaryPhoto using Glide
            Glide.with(holder.itemView)
                    .load(entry.getImagePath())
                    .into(holder.imageViewDiaryPhoto);
        }

        @Override
        public int getItemCount() {
            return diaryEntries.size();
        }

        class DiaryEntryViewHolder extends RecyclerView.ViewHolder {
            ImageView imageViewDiaryPhoto;
            TextView textViewDiaryTitle, textViewDiaryDescription;

            DiaryEntryViewHolder(View itemView) {
                super(itemView);
                imageViewDiaryPhoto = itemView.findViewById(R.id.imageViewDiaryPhoto);
                textViewDiaryTitle = itemView.findViewById(R.id.textViewDiaryTitle);
                textViewDiaryDescription = itemView.findViewById(R.id.textViewDiaryDescription);
            }
        }
    }
}



