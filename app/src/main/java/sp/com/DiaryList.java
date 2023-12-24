package sp.com;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class DiaryList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryEntryAdapter adapter;
    private String selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diary_list);

        selectedDate = getIntent().getStringExtra("selectedDate");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<DiaryEntry> diaryEntries = fetchDiaryEntriesForDate(selectedDate);

        adapter = new DiaryEntryAdapter(diaryEntries, entry -> openMapFragment(entry));
        recyclerView.setAdapter(adapter);
    }

    private List<DiaryEntry> fetchDiaryEntriesForDate(String date) {
        List<DiaryEntry> entries = new ArrayList<>();
        DiaryHelper diaryHelper = new DiaryHelper(this);
        Cursor cursor = diaryHelper.getByDate(date);

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
                        long timestamp = cursor.getLong(dateIndex);

                        DiaryEntry entry = new DiaryEntry(title, imagePath, description, latitude, longitude, timestamp);
                        entries.add(entry);
                    }
                }
            } finally {
                cursor.close();
            }
        }

        return entries;
    }

    public class DiaryEntryAdapter extends RecyclerView.Adapter<DiaryEntryAdapter.DiaryEntryViewHolder> {

        private final List<DiaryEntry> diaryEntries;
        private final OnItemClickListener clickListener;

        DiaryEntryAdapter(List<DiaryEntry> diaryEntries, OnItemClickListener clickListener) {
            this.diaryEntries = diaryEntries;
            this.clickListener = clickListener;
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

            holder.itemView.setOnClickListener(v -> clickListener.onItemClick(entry));
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

    private interface OnItemClickListener {
        void onItemClick(DiaryEntry entry);
    }

    private void openMapFragment(DiaryEntry entry) {
        MapFragment mapFragment = new MapFragment();
        mapFragment.setSelectedEntry(entry);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit();
    }
}

