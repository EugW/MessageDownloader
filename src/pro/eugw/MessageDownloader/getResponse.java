package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

class getResponse {

    private String method = "";
    private String arguments = "";

    getResponse(String method, String arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    JsonArray get() throws Exception {
        while (true) {
            URL url = new URL("https://api.vk.com/method/" + this.method + "?" + this.arguments + "&lang=en");
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

    String getNameById() throws Exception {
        String id = this.method;
        File fl = new File("names");
        if (!fl.exists())
            fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(id) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            JsonObject object = getForName("user_id=" + id).get(0).getAsJsonObject();
            properties.put(id, object.get("first_name").getAsString() + " " + object.get("last_name").getAsString());
            properties.store(fos, "ROFL");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(id);
    }

    String getNameByToken() throws Exception {
        String token = this.method;
        File fl = new File("names");
        if (!fl.exists())
            fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(token) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            JsonObject object = getForName("access_token=" + token).get(0).getAsJsonObject();
            properties.put(token, object.get("first_name").getAsString() + " " + object.get("last_name").getAsString());
            properties.store(fos, "ROFL");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(token);
    }

    private JsonArray getForName(String arguments) throws Exception {
        while (true) {
            URL url = new URL("https://api.vk.com/method/users.get?" + arguments + "&lang=en");
            System.out.println(url);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            JsonObject object = new JsonParser().parse(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine()).getAsJsonObject();
            if (object.has("response")) {
                return object.getAsJsonArray("response");
            }
        }
    }

}