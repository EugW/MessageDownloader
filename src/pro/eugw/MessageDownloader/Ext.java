package pro.eugw.MessageDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

class Ext {
    static ArrayList<ArrayList<String>> getConversations(String token) throws Exception {
        JSONObject obj = new JSONObject(get("https://api.vk.com/method/messages.getDialogs?access_token=" + token + "&count=200"));
        JSONArray arr = obj.getJSONArray("response");
        arr.remove(0);
        ArrayList<ArrayList<String>> ar = new ArrayList<>();
        ArrayList<String> list0 = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        for (Integer i = 0; i < arr.length(); i++){
            if (!arr.getJSONObject(i).has("chat_id"))
                list0.add(arr.getJSONObject(i).get("uid")
                        + "@"
                        + getUsernameById(arr.getJSONObject(i).get("uid").toString(), "name")
                        + getUsernameById(arr.getJSONObject(i).get("uid").toString(), "surname"));
            if (arr.getJSONObject(i).has("chat_id"))
                list1.add(arr.getJSONObject(i).get("chat_id")
                        + "@"
                        + arr.getJSONObject(i).get("title"));
        }
        ar.add(list0);
        ar.add(list1);
        return ar;
    }

    static String getUsernameById(String id, String x) throws Exception {
        switch (x){
            case "name":{
                File fl = new File("names.cache");
                if (!fl.exists())
                    fl.createNewFile();
                FileInputStream fis = new FileInputStream(fl);
                Properties properties = new Properties();
                properties.load(fis);
                if (properties.getProperty(id) == null) {
                    JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?user_ids=" + id));
                    JSONArray er = or.getJSONArray("response");
                    String kek = er.getJSONObject(0).getString("first_name");
                    FileOutputStream fos = new FileOutputStream(fl);
                    properties.put(id, kek);
                    properties.store(fos, "no comments");
                    fos.flush();
                    fos.close();
                }
                return properties.getProperty(id);
            }
            case "surname":{
                File fl = new File("surnames.cache");
                if (!fl.exists())
                    fl.createNewFile();
                FileInputStream fis = new FileInputStream(fl);
                Properties properties = new Properties();
                properties.load(fis);
                if (properties.getProperty(id) == null) {
                    JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?user_ids=" + id));
                    JSONArray er = or.getJSONArray("response");
                    String kek = er.getJSONObject(0).getString("last_name");
                    FileOutputStream fos = new FileOutputStream(fl);
                    properties.put(id, kek);
                    properties.store(fos, "no comments");
                    fos.flush();
                    fos.close();
                }
                return properties.getProperty(id);
            }
        }
        return null;
    }

    static String getUsernameByToken(String token, String x) throws Exception {
        switch (x){
            case "name":{
                File fl = new File("names.cache");
                if (!fl.exists())
                    fl.createNewFile();
                FileInputStream fis = new FileInputStream(fl);
                Properties properties = new Properties();
                properties.load(fis);
                if (properties.getProperty(token) == null) {
                    JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?access_token=" + token));
                    JSONArray er = or.getJSONArray("response");
                    String kek = er.getJSONObject(0).getString("first_name");
                    FileOutputStream fos = new FileOutputStream(fl);
                    properties.put(token, kek);
                    properties.store(fos, "no comments");
                    fos.flush();
                    fos.close();
                }
                return properties.getProperty(token);
            }
            case "surname":{
                File fl = new File("surnames.cache");
                if (!fl.exists())
                    fl.createNewFile();
                FileInputStream fis = new FileInputStream(fl);
                Properties properties = new Properties();
                properties.load(fis);
                if (properties.getProperty(token) == null) {
                    JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?access_token=" + token));
                    JSONArray er = or.getJSONArray("response");
                    String kek = er.getJSONObject(0).getString("last_name");
                    FileOutputStream fos = new FileOutputStream(fl);
                    properties.put(token, kek);
                    properties.store(fos, "no comments");
                    fos.flush();
                    fos.close();
                }
                return properties.getProperty(token);
            }
        }

        return null;
    }

    static String getDateByTime(String date) throws Exception {
        File fl = new File("dates.cache");
        if (!fl.exists())
            fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(date) == null) {
            URL url = new URL("http://www.convert-unix-time.com/api?timestamp=" + date + "&timezone=ekaterinburg");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader rd;
            rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String read = rd.readLine();
            JSONObject or = new JSONObject(read);
            String kek = or.getString("localDate");
            FileOutputStream fos = new FileOutputStream(fl);
            properties.put(date, kek);
            properties.store(fos, "no comments");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(date);
    }

    static String get(String kurl) throws Exception {
        while (true) {
            URL url = new URL(kurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            BufferedReader rd;
            rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String read = rd.readLine();
            JSONObject obj = new JSONObject(read);
            if (obj.has("response"))
                return read;
        }
    }
}