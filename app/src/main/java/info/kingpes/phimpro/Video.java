package info.kingpes.phimpro;

/**
 * Created by Chau Huynh on 5/21/2019.
 */

public class Video {
    private String videoId;
    private String title;
    private String description;

    public Video(String videoId, String title, String description) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
