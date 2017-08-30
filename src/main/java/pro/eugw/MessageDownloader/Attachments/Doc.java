package pro.eugw.MessageDownloader.Attachments;

import com.google.gson.JsonObject;

public class Doc {

    private Integer did;
    private Integer owner_id;
    private String title;
    private Integer size;
    private String ext;
    private String url;

    public Doc(JsonObject object) {
        did = object.get("did").getAsInt();
        owner_id = object.get("owner_id").getAsInt();
        title = object.get("title").getAsString();
        size = object.get("size").getAsInt();
        ext = object.get("ext").getAsString();
        url = object.get("url").getAsString();
    }

    public Integer getDid() {
        return did;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getSize() {
        return size;
    }

    public String getExt() {
        return ext;
    }

    public String getUrl() {
        return url;
    }

}
