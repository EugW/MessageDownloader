package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import static pro.eugw.MessageDownloader.ConfigClass.getConfig;

class saveMessages {

    static void json(String path, String token, String id, String type) throws Exception {
        JsonArray arr;
        File fol = new File(path);
        File local = new File(path, "local");
        if (!fol.exists())
            if (fol.mkdirs())
                System.out.println("CREATED " + fol);
        if (!local.exists()) {
            if (local.createNewFile())
                System.out.println("CREATED " + local);
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
                System.out.println("CREATED " + messages);
        PrintWriter pw = new PrintWriter(messages);
        for (Integer i = 0; i < array.size(); i++) {
            boolean fwd_messages = false;
            if (array.get(i).getAsJsonObject().has("fwd_messages"))
                fwd_messages = true;
            boolean attachments = false;
            if (array.get(i).getAsJsonObject().has("attachments")) {
                attachments = true;
            }
            String uid = array.get(i).getAsJsonObject().get("uid").getAsString();
            Integer date = array.get(i).getAsJsonObject().get("date").getAsInt();
            String name = new getResponse(uid, null).getNameById();
            pw.println(name + " " + new Date(date * 1000L));
            if (fwd_messages) {
                pw.println("fwd_messages:{");
                for (JsonElement element : array.get(i).getAsJsonObject().get("fwd_messages").getAsJsonArray()) {
                    pw.println(" " + new getResponse(element.getAsJsonObject().get("uid").getAsString(), null).getNameById() + " " + new Date(element.getAsJsonObject().get("date").getAsInt() * 1000L));
                    if (!element.getAsJsonObject().get("body").getAsString().isEmpty())
                        pw.println(" " + element.getAsJsonObject().get("body").getAsString());
                    if (element.getAsJsonObject().has("fwd_messages"))
                        pw.println(" RECURSIVE FORWARDED MESSAGES ARE NOT SUPPORTED YET");
                }
                pw.println("}");
            }
            if (attachments) {
                pw.println("attachments:{");
                for (JsonElement element : array.get(i).getAsJsonObject().get("attachments").getAsJsonArray()) {
                    String type = element.getAsJsonObject().get("type").getAsString();
                    pw.println(" TYPE: " + type.toUpperCase());
                    switch (type) {
                        case "doc":
                            URL udoc = new URL(element.getAsJsonObject().get("doc").getAsJsonObject().get("url").getAsString());
                            pw.println(" LINK: " + udoc);
                            File fdoc = new File(path + File.separator + "downloaded", element.getAsJsonObject().get("doc").getAsJsonObject().get("title").getAsString());
                            if (getConfig().get("auto-download") == "true") {
                                FileUtils.copyURLToFile(udoc, fdoc);
                                pw.println(" LOCAL LINK: " + fdoc);
                            }
                            pw.println(" TITLE: " + element.getAsJsonObject().get("doc").getAsJsonObject().get("title").getAsString());
                            break;
                        case "photo":
                            URL uph = new URL(element.getAsJsonObject().get("photo").getAsJsonObject().get("src").getAsString());
                            pw.println(" LINK: " + uph);
                            File fph = new File(path + File.separator + "downloaded", element.getAsJsonObject().get("photo").getAsJsonObject().get("src").getAsString().split("/")[6]);
                            if (getConfig().get("auto-download") == "true") {
                                FileUtils.copyURLToFile(uph, fph);
                                pw.println(" LOCAL LINK: " + fph);
                            }
                            pw.println(" TITLE: " + element.getAsJsonObject().get("photo").getAsJsonObject().get("src").getAsString().split("/")[6]);
                            break;
                        default:
                            pw.println(" UNSUPPORTED TYPE OF ATTACHMENT");
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
    }

}
