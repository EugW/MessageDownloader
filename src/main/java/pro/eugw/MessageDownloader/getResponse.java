package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;
import java.util.Properties;

import static pro.eugw.MessageDownloader.Miscellaneous.log;
import static pro.eugw.MessageDownloader.Miscellaneous.getConfig;

class getResponse {

    private String method = "";
    private String arguments = "";

    getResponse(String method, String arguments) {
        this.method = method;
        this.arguments = arguments;
    }

    JsonArray get() {
        while (true) {
            JsonObject object;
            Integer tries = 0;
            Integer maxTries = Integer.valueOf(getConfig().getProperty("getMaxTries"));
            while (true) {
                try {
                    URL url = new URL("https://api.vk.com/method/" + this.method + "?" + this.arguments + "&lang=en");
                    log().debug(url.toString());
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/60.0.3112.101 Safari/537.36");
                    object = new JsonParser().parse(new BufferedReader(new InputStreamReader(urlConnection.getInputStream())).readLine()).getAsJsonObject();
                    break;
                } catch (Exception e) {
                    tries++;
                    if (Objects.equals(tries, maxTries)) {
                        log().error("MAX TRIES REACHED. EXIT");
                    }
                }
            }
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
            if (object.has("error")) {
                if (object.get("error").getAsJsonObject().get("error_code").getAsInt() != 6) {
                    log().error("ERROR CODE: " + object.get("error").getAsJsonObject().get("error_code"));
                    log().error("FIND IT ON THIS PAGE: https://vk.com/dev/errors");
                    System.exit(0);
                }
            }
        }
    }

    String getNameById() throws Exception {
        String id = this.method;
        File fl = new File("names");
        if (!fl.exists())
            if (fl.createNewFile())
                log().debug("CREATED " + fl);
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(id) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            this.method = "users.get";
            this.arguments = "user_id=" + id;
            if (Integer.valueOf(id) < 0) {
                this.arguments = "user_id=" + id.replace("-", "1");
            }
            JsonObject object = get().get(0).getAsJsonObject();
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
            if (fl.createNewFile())
                log().debug("CREATED " + fl);
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(token) == null) {
            FileOutputStream fos = new FileOutputStream(fl);
            this.method = "users.get";
            this.arguments = "access_token=" + token;
            JsonObject object = get().get(0).getAsJsonObject();
            properties.put(token, object.get("first_name").getAsString() + " " + object.get("last_name").getAsString());
            properties.store(fos, "ROFL");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(token);
    }


}