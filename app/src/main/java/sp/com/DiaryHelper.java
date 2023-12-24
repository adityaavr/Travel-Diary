package sp.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaryHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "travelDiary.db";
    private static final int SCHEMA_VERSION = 4; // Increase the version for the new schema

    public DiaryHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the diaries_table with appropriate columns
        db.execSQL("CREATE TABLE diaries_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "diaryTitle TEXT, diaryImage TEXT, diaryDesc TEXT, " +
                "lat REAL, lon REAL, timestamp INTEGER, diaryDate TEXT);"); // Add diaryDate column
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle the upgrade by dropping the existing table and creating a new one
        db.execSQL("DROP TABLE IF EXISTS diaries_table");
        onCreate(db);
    }

    /* Read all records from diaries_table */
    public Cursor getAll() {
        return (getReadableDatabase().rawQuery(
                "SELECT _id, diaryTitle, diaryImage, diaryDesc, diaryDate," +
                        "lat, lon, timestamp FROM diaries_table ORDER BY timestamp DESC", null));
    }

    public Cursor getByDate(String date) {
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {"_id", "diaryTitle", "diaryImage", "diaryDesc", "lat", "lon", "timestamp"};

        // Assuming your date is stored as a string in the format 'yyyy-MM-dd'
        String selection = "strftime('%Y-%m-%d', timestamp/1000, 'unixepoch') = ?";
        String[] selectionArgs = {date};

        return db.query("diaries_table", columns, selection, selectionArgs, null, null, "timestamp DESC");
    }


    public void insert(String diaryTitle, String diaryImage, String diaryDesc,
                       double lat, double lon) {
        ContentValues cv = new ContentValues();

        cv.put("diaryTitle", diaryTitle);
        cv.put("diaryImage", diaryImage);
        cv.put("diaryDesc", diaryDesc);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("timestamp", System.currentTimeMillis());
        cv.put("diaryDate", getCurrentDate()); // Add diaryDate

        getWritableDatabase().insert("diaries_table", "diaryTitle", cv);
    }

    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void update(String id, String diaryTitle, String diaryImage, String diaryDesc,
                       double lat, double lon) {
        ContentValues cv = new ContentValues();
        String[] args = {id};

        cv.put("diaryTitle", diaryTitle);
        cv.put("diaryImage", diaryImage);
        cv.put("diaryDesc", diaryDesc);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("timestamp", System.currentTimeMillis()); // Update timestamp

        getWritableDatabase().update("diaries_table", cv, " _ID = ?", args);
    }
}

