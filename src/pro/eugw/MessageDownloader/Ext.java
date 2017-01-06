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
        Integer i = 0;
        ArrayList<ArrayList<String>> ar = new ArrayList<>();
        ArrayList<String> list0 = new ArrayList<>();
        ArrayList<String> list1 = new ArrayList<>();
        while (i < arr.length()) {
            if (!arr.getJSONObject(i).has("chat_id")) {
                list0.add(arr.getJSONObject(i).get("uid") + "@" + getUserNameById((Integer) arr.getJSONObject(i).get("uid")));
            }
            if (arr.getJSONObject(i).has("chat_id")) {
                list1.add(arr.getJSONObject(i).get("chat_id") + "@" + arr.getJSONObject(i).get("title"));
            }
            i++;
        }
        ar.add(list0);
        ar.add(list1);
        return ar;
    }

    static String getUserNameById(Integer id) throws Exception {
        File fl = new File("names.cache");
        if (!fl.exists()) fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(id.toString()) != null) {} else {
            JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?user_ids=" + id));
            JSONArray er = or.getJSONArray("response");
            String kek = er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
            FileOutputStream fos = new FileOutputStream(fl);
            properties.put(id.toString(), kek);
            properties.store(fos, "no comments");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(id.toString());
    }

    static String getUserNameByToken(String token) throws Exception {
        File fl = new File("names.cache");
        if (!fl.exists()) fl.createNewFile();
        FileInputStream fis = new FileInputStream(fl);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.getProperty(token) != null) {} else {
            JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?access_token=" + token));
            JSONArray er = or.getJSONArray("response");
            String kek = er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
            FileOutputStream fos = new FileOutputStream(fl);
            properties.put(token, kek);
            properties.store(fos, "no comments");
            fos.flush();
            fos.close();
        }
        return properties.getProperty(token);
    }

    private static String get(String kurl) throws Exception {
        Thread.sleep(250);
        URL url = new URL(kurl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        BufferedReader rd;
        rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        return rd.readLine();
    }
}