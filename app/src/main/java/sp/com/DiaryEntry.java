package sp.com;

public class DiaryEntry {
    private String title;
    private String imagePath;
    private String description;

    public DiaryEntry(String title, String imagePath, String description) {
        this.title = title;
        this.imagePath = imagePath;
        this.description = description;
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
}
