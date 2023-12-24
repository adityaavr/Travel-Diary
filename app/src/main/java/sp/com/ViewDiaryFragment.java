package sp.com;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// ViewDiaryFragment.java
public class ViewDiaryFragment extends Fragment {

    private CalendarView calendarView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_diary, container, false);

        // Initialize the CalendarView
        calendarView = view.findViewById(R.id.calendarView);
        initCalendar();

        return view;
    }

    private void initCalendar() {
        // Set a listener for date selection
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                // Handle date selection
                handleDateSelection(year, month, day);
            }
        });

        Calendar currentDate = Calendar.getInstance();
        calendarView.setDate(currentDate.getTimeInMillis(), true, true);
    }

    private void handleDateSelection(int year, int month, int day) {
        // Format the selected date
        String selectedDate = formatDate(year, month, day);

        // Create an intent to start the DiaryList activity
        Intent intent = new Intent(requireContext(), DiaryList.class);

        // Pass the selected date to the DiaryList activity
        intent.putExtra("selectedDate", selectedDate);

        // Start the DiaryList activity
        startActivity(intent);

        // Notify the user about the selected date
        Toast.makeText(requireContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
    }

    private String formatDate(int year, int month, int day) {
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.set(year, month, day);
        Date date = selectedDate.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }
}
