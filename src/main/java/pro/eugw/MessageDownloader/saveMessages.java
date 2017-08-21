package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

class saveMessages {

    static void json(String path, String token, String id, String type) throws Exception {
        JsonArray arr;
        File fol = new File(path);
        File local = new File(path, "local");
        if (!fol.exists())
            fol.mkdirs();
        if (!local.exists()) {
            local.createNewFile();
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
        if (messages.exists())
            messages.delete();
        if (!messages.exists())
            messages.createNewFile();
        PrintWriter pw = new PrintWriter(messages);
        for (Integer i = 0; i < array.size(); i++) {
            boolean fwd_messages = false;
            if (array.get(i).getAsJsonObject().has("fwd_messages")) {
                fwd_messages = true;
                File dir = new File(path, "additions");
                if (!dir.exists()) dir.mkdirs();
                File fl = new File(path, "additions" + File.separator + array.get(i).getAsJsonObject().get("mid"));
                if (!fl.exists()) fl.createNewFile();
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.get(i).getAsJsonObject().get("fwd_messages"));
                printWriter.flush();
                printWriter.close();
            }
            boolean attachment = false;
            if (array.get(i).getAsJsonObject().has("attachment")) {
                attachment = true;
                File dir = new File(path, "additions");
                if (!dir.exists()) dir.mkdirs();
                File fl = new File(path, "additions" + File.separator + array.get(i).getAsJsonObject().get("mid"));
                if (!fl.exists()) fl.createNewFile();
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.get(i).getAsJsonObject().get("attachment"));
                printWriter.flush();
                printWriter.close();
            }
            String uid = array.get(i).getAsJsonObject().get("uid").getAsString();
            Integer date = array.get(i).getAsJsonObject().get("date").getAsInt();
            String name = new getResponse(uid, null).getNameById();
            pw.println(name + " " + new Date(date * 1000L));
            if (fwd_messages)
                pw.println("fwd_messages: true: additions id: " + array.get(i).getAsJsonObject().get("mid"));
            if (attachment)
                pw.println("attachment: true: additions id: " + array.get(i).getAsJsonObject().get("mid"));
            pw.println(array.get(i).getAsJsonObject().get("body").getAsString());
            pw.println();
        }
        pw.flush();
        pw.close();
    }

}