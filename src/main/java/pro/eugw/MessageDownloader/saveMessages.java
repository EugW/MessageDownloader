package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import pro.eugw.MessageDownloader.Attachments.Audio;
import pro.eugw.MessageDownloader.Attachments.Doc;
import pro.eugw.MessageDownloader.Attachments.Photo;
import pro.eugw.MessageDownloader.Attachments.Video;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static pro.eugw.MessageDownloader.Miscellaneous.getConfig;
import static pro.eugw.MessageDownloader.Miscellaneous.log;

class saveMessages {

    static void json(String path, String token, String id, String type) throws Exception {
        JsonArray arr;
        File fol = new File(path);
        File local = new File(path, "local");
        if (!fol.exists())
            if (fol.mkdirs())
                log().debug("Created " + fol);
        if (!local.exists()) {
            if (local.createNewFile())
                log().debug("Created " + local);
            Integer count = new getResponse("messages.getHistory", "access_token=" + token + "&" + type + "_id=" + id).get().get(0).getAsInt();
            JsonArray result = new JsonArray();
            for (Integer i = 0; i <= count; i = i + 200) {
                JsonArray response = new getResponse("messages.getHistory", "access_token=" + token + "&" + type + "_id=" + id + "&count=200&offset=" + i).get();
                response.remove(0);
                for (JsonElement element : response) {
                    result.add(element);
                }
            }
            arr = result;
        } else {
            JsonArray localArray = new JsonParser().parse(new BufferedReader(new FileReader(local)).readLine()).getAsJsonObject().get("local").getAsJsonArray();
            JsonArray result = new JsonArray();
            Integer start_id = localArray.get(0).getAsJsonObject().get("mid").getAsInt();
            JsonArray response;
            JsonArray temp1 = new JsonArray();
            ArrayList<Integer> remote_mid = new ArrayList<>();
            ArrayList<Integer> local_mid = new ArrayList<>();
            Integer i = 0;
            do {
                response = new getResponse("messages.getHistory", "access_token=" + token + "&" + type + "_id=" + id + "&count=200&offset=" + i).get();
                response.remove(0);
                for (JsonElement element : response) {
                    temp1.add(element);
                }
                for (JsonElement element : response) {
                    remote_mid.add(element.getAsJsonObject().get("mid").getAsInt());
                }
                i = i + 200;
            } while (!remote_mid.contains(start_id));
            for (JsonElement element : localArray) {
                local_mid.add(element.getAsJsonObject().get("mid").getAsInt());
            }
            for (JsonElement element : temp1) {
                if (!local_mid.contains(element.getAsJsonObject().get("mid").getAsInt()))
                    result.add(element);
            }
            for (JsonElement element : localArray) {
                result.add(element);
            }
            arr = result;
        }
        JsonObject obj = new JsonObject();
        obj.add("local", arr);
        PrintWriter printWriter = new PrintWriter(local);
        printWriter.println(obj);
        printWriter.flush();
        printWriter.close();
        log().debug("Finished writing to local json");
    }

    static void chat(String path) throws Exception {
        File local = new File(path, "local");
        BufferedReader br = new BufferedReader(new FileReader(local));
        String jst = br.readLine();
        JsonObject obj = new JsonParser().parse(jst).getAsJsonObject();
        JsonArray array0 = obj.get("local").getAsJsonArray();
        JsonArray array = new JsonArray();
        for (Integer i = array0.size() - 1; i >= 0; i--) {
            array.add(array0.get(i));
        }
        File messages = new File(path, "messages");
        if (!messages.exists())
            if (messages.createNewFile())
                log().debug("Created " + messages);
        PrintWriter pw = new PrintWriter(messages);
        for (Integer i = 0; i < array.size(); i++) {
            Integer uid = array.get(i).getAsJsonObject().get("uid").getAsInt();
            Integer date = array.get(i).getAsJsonObject().get("date").getAsInt();
            String name = new getResponse(uid).getNameById();
            pw.println(name + " " + new Date(date * 1000L));
            if (array.get(i).getAsJsonObject().has("fwd_messages")) {
                pw.println("fwd_messages:{");
                for (JsonElement element : array.get(i).getAsJsonObject().get("fwd_messages").getAsJsonArray()) {
                    pw.println(" " + new getResponse(element.getAsJsonObject().get("uid").getAsInt()).getNameById() + " " + new Date(element.getAsJsonObject().get("date").getAsInt() * 1000L));
                    if (!element.getAsJsonObject().get("body").getAsString().isEmpty())
                        pw.println(" " + element.getAsJsonObject().get("body").getAsString());
                    if (element.getAsJsonObject().has("fwd_messages"))
                        pw.println(" Recursive forwarded messages are not supported yet");
                }
                pw.println("}");
            }
            if (array.get(i).getAsJsonObject().has("attachments")) {
                pw.println("attachments:{");
                for (JsonElement element : array.get(i).getAsJsonObject().get("attachments").getAsJsonArray()) {
                    String type = element.getAsJsonObject().get("type").getAsString();
                    pw.println(" TYPE: " + type.toUpperCase());
                    switch (type) {
                        case "photo":
                            Photo photo = new Photo(element.getAsJsonObject().get("photo").getAsJsonObject());
                            pw.println(" PHOTO ID: " + photo.getPid());
                            pw.println(" OWNER: " + photo.getOwner_id());
                            pw.println(" PHOTO TITLE: " + photo.getSrc().split("/")[6]);
                            URL url_photo = new URL(photo.getSrc());
                            pw.println(" LINK: " + url_photo);
                            if (Objects.equals(getConfig().getProperty("autoDownload"), "true")) {
                                File file_photo = new File(path + File.separator + "downloaded", photo.getSrc().split("/")[6]);
                                FileUtils.copyURLToFile(url_photo, file_photo);
                                pw.println(" LOCAL LINK: " + file_photo);
                            }
                            break;
                        case "video":
                            Video video = new Video(element.getAsJsonObject().get("video").getAsJsonObject());
                            pw.println(" VIDEO ID: " + video.getVid());
                            pw.println(" VIDEO VIEWS: " + video.getViews());
                            pw.println(" OWNER: " + video.getOwner_id());
                            pw.println(" VIDEO TITLE: " + video.getTitle());
                            if (Objects.equals(getConfig().getProperty("autoDownload"), "true")) {
                                File video_preview = new File(path + File.separator + "downloaded", video.getTitle());
                                FileUtils.copyURLToFile(new URL(video.getImage()), video_preview);
                                pw.println(" VIDEO PREVIEW: " + video_preview);
                            }
                            pw.println(" VIDEO DURATION: " + video.getDuration());
                            pw.println(" VIDEO DESCRIPTION: " + video.getDescription());
                            pw.println(" VIDEO DATE: " + new Date(video.getDate() * 1000L));

                            break;
                        case "audio":
                            Audio audio = new Audio(element.getAsJsonObject().get("audio").getAsJsonObject());
                            pw.println(" AUDIO ID: " + audio.getAid());
                            pw.println(" OWNER: " + audio.getOwner_id());
                            pw.println(" AUDIO TITLE: " + audio.getTitle());
                            URL url_audio = new URL(audio.getUrl());
                            pw.println(" LINK: " + url_audio);
                            if (Objects.equals(getConfig().getProperty("autoDownload"), "true")) {
                                File file_audio = new File(path + File.separator + "downloaded", audio.getTitle());
                                FileUtils.copyURLToFile(url_audio, file_audio);
                                pw.println(" LOCAL LINK: " + file_audio);
                            }
                            pw.println(" AUDIO DURATION: " + audio.getDuration());
                            pw.println(" AUDIO ARTIST: " + audio.getPerformer());
                            break;
                        case "doc":
                            Doc doc = new Doc(element.getAsJsonObject().get("doc").getAsJsonObject());
                            pw.println(" DOC ID: " + doc.getDid());
                            pw.println(" OWNER: " + doc.getOwner_id());
                            pw.println(" DOC TITLE: " + doc.getTitle());
                            URL url_doc = new URL(doc.getUrl());
                            pw.println(" LINK: " + url_doc);
                            if (Objects.equals(getConfig().getProperty("autoDownload"), "true")) {
                                File file_doc = new File(path + File.separator + "downloaded", doc.getTitle());
                                FileUtils.copyURLToFile(url_doc, file_doc);
                                pw.println(" LOCAL LINK: " + file_doc);
                            }
                            pw.println(" DOC SIZE: " + doc.getSize());
                            pw.println(" DOC EXTENSION: " + doc.getExt());
                            break;
                        case "wall":
                            pw.println(" WALL NOT SUPPORTED YET");
                            break;
                    }
                }
                pw.println("}");
            }
            if (!array.get(i).getAsJsonObject().get("body").getAsString().isEmpty())
                pw.println(array.get(i).getAsJsonObject().get("body").getAsString());
            pw.println();
        }
        pw.flush();
        pw.close();
        log().debug("Finished writing to local readable text");
    }

}
