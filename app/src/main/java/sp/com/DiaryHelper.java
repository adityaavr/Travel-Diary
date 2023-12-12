package sp.com;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DiaryHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "travelDiary.db";
    private static final int SCHEMA_VERSION = 2;

    public DiaryHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE diaries_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "diaryTitle TEXT, diaryImage TEXT, diaryDesc TEXT, " +
                "lat REAL, lon REAL, timestamp INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Will not be called until SCHEMA_VERSION increases
        // Here we can upgrade the database e.g. add more tables
    }

    /* Read all records from diaries_table */
    public Cursor getAll() {
        return (getReadableDatabase().rawQuery(
                "SELECT _id, diaryTitle, diaryImage, diaryDesc," +
                        "lat, lon, timestamp FROM diaries_table ORDER BY timestamp DESC", null));
    }

    public Cursor getById(String id) {
        String[] args = {id};

        return (getReadableDatabase().rawQuery(
                "SELECT _id, diaryTitle, diaryImage, diaryDesc, " +
                        "lat, lon, timestamp FROM diaries_table WHERE _ID = ?", args));
    }

    public void insert(String diaryTitle, String diaryImage, String diaryDesc,
                       double lat, double lon) {
        ContentValues cv = new ContentValues();

        cv.put("diaryTitle", diaryTitle);
        cv.put("diaryImage", diaryImage);
        cv.put("diaryDesc", diaryDesc);
        cv.put("lat", lat);
        cv.put("lon", lon);
        cv.put("timestamp", System.currentTimeMillis()); // Store current timestamp

        getWritableDatabase().insert("diaries_table", "diaryTitle", cv);
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

    public String getID(Cursor c) { return (c.getString(0)); }
    public String getDiaryTitle(Cursor c) { return (c.getString(1)); }
    public String getDiaryImage(Cursor c) { return (c.getString(2)); }
    public String getDiaryDesc(Cursor c) { return (c.getString(3)); }
    public double getLatitude(Cursor c) { return (c.getDouble(4)); }
    public double getLongitude(Cursor c) { return (c.getDouble(5)); }
    public long getTimestamp(Cursor c) { return (c.getLong(6)); }
}


