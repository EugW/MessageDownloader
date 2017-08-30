package pro.eugw.MessageDownloader.Attachments;

import com.google.gson.JsonObject;

public class Video {

    private Integer vid;
    private Integer owner_id;
    private String title;
    private String description;
    private Integer duration;
    private String image;
    private String image_big;
    private String image_small;
    private Integer views;
    private Integer date;

    public Video(JsonObject object) {
        vid = object.get("vid").getAsInt();
        owner_id = object.get("owner_id").getAsInt();
        title = object.get("title").getAsString();
        description = object.get("description").getAsString();
        duration = object.get("duration").getAsInt();
        image = object.get("image").getAsString();
        image_big = object.get("image_big").getAsString();
        image_small = object.get("image_small").getAsString();
        views = object.get("views").getAsInt();
        date = object.get("date").getAsInt();
    }

    public Integer getVid() {
        return vid;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getImage() {
        return image;
    }

    public String getImage_big() {
        return image_big;
    }

    public String getImage_small() {
        return image_small;
    }

    public Integer getViews() {
        return views;
    }

    public Integer getDate() {
        return date;
    }

}
