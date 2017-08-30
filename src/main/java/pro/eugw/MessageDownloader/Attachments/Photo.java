package pro.eugw.MessageDownloader.Attachments;

import com.google.gson.JsonObject;

public class Photo {

    private Integer pid;
    private Integer owner_id;
    private String src;
    private String src_big;

    public Photo(JsonObject object) {
        pid = object.get("pid").getAsInt();
        owner_id = object.get("owner_id").getAsInt();
        src = object.get("src").getAsString();
        src_big = object.get("src_big").getAsString();
    }

    public Integer getPid() {
        return pid;
    }

    public Integer getOwner_id() {
        return owner_id;
    }

    public String getSrc() {
        return src;
    }

    public String getSrc_big() {
        return src_big;
    }

}
