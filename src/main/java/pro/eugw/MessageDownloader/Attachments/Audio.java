package pro.eugw.MessageDownloader.Attachments;

import com.google.gson.JsonObject;

public class Audio {

    private Integer aid;
    private Integer owner_id;
    private String performer;
    private String title;
    private Integer duration;
    private String url;

    public Audio(JsonObject object) {
        aid = object.get("aid").getAsInt();
        owner_id = object.get("owner_id").getAsInt();
        performer = object.get("performer").getAsString();
        title = object.get("title").getAsString();
        duration = object.get("duration").getAsInt();
        url = object.get("url").getAsString();
    }

    public Integer getAid() {
        return aid;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public String getPerformer() {
        return performer;
    }

    public String getTitle() {
        return title;
    }

    public Integer getDuration() {
        return duration;
    }

    public String getUrl() {
        return url;
    }

}
