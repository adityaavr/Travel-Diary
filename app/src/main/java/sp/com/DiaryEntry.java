package sp.com;

public class DiaryEntry {
    private String title;
    private String imagePath;
    private String description;
    private double latitude;
    private double longitude;

    public DiaryEntry(String title, String imagePath, String description, double latitude, double longitude) {
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
