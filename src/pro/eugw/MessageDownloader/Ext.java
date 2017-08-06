package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

class Ext {
    static ArrayList<ArrayList<String>> getConversations(String token) throws Exception {
        JsonArray arr = get("messages.getDialogs", "access_token=" + token + "&count=200");
        arr.remove(0);
        ArrayList<ArrayList<String>> ar = new ArrayList<>();
        ArrayList<String> list0 = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        for (Integer i = 0; i < arr.size(); i++) {
            if (!arr.get(i).getAsJsonObject().has("chat_id") && arr.get(i).getAsJsonObject().get("uid").getAsInt() > 0)
                list0.add(arr.get(i).getAsJsonObject().get("uid").getAsInt()
                        + "@"
                        + getUsernameById(arr.get(i).getAsJsonObject().get("uid").getAsInt()));
            if (arr.get(i).getAsJsonObject().has("chat_id"))
                list1.add(arr.get(i).getAsJsonObject().get("chat_id").getAsInt()
                        + "@"
                        + arr.get(i).getAsJsonObject().get("title").getAsString());
        }
        ar.add(list0);
        ar.add(list1);
        return ar;
    }

    static String getUsernameById(Integer uid) throws Exception {
        String id = uid.toString();
        File fl = new File("names");
        if (!fl.exists())
            fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(id) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            JsonObject object = get("users.get", "user_id=" + id).get(0).getAsJsonObject();
            properties.put(id, object.get("first_name").getAsString() + " " + object.get("last_name").getAsString());
            properties.store(fos, "ROFL");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(id);
    }

    static String getUsernameByToken(String token) throws Exception {
        File fl = new File("names");
        if (!fl.exists())
            fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(token) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            JsonObject object = get("users.get", "access_token=" + token).get(0).getAsJsonObject();
            properties.put(token, object.get("first_name").getAsString() + " " + object.get("last_name").getAsString());
            properties.store(fos, "ROFL");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(token);
    }

    static JsonArray get(String method, String arguments) throws Exception {
        while (true) {
            URL url = new URL("https://api.vk.com/method/" + method + "?" + arguments);
            System.out.println(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            JsonObject object = new JsonParser().parse(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine()).getAsJsonObject();
            if (object.has("response")) {
                if (object.get("response").isJsonArray()) {
                    return object.getAsJsonArray("response");
                }
                if (object.get("response").isJsonObject()) {
                    JsonArray jsonArray = new JsonArray();
                    jsonArray.add(object.getAsJsonObject("response"));
                    return jsonArray;
                }
            }
        }
    }
}