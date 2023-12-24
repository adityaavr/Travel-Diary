package sp.com;

public class DiaryEntry {
    private long id;
    private String title;
    private String imagePath;
    private String description;
    private double latitude;
    private double longitude;
    private long date;

    public DiaryEntry(String title, String imagePath, String description, double latitude, double longitude, long date) {
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getDescription() {
        return description;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public long getDate() {
        return date;
    }
}

