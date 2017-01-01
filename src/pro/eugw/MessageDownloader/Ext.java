package pro.eugw.MessageDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class Ext {
    static ArrayList<String> getDialogs(String token) throws Exception {
        JSONObject obj = new JSONObject(get("https://api.vk.com/method/messages.getDialogs?access_token=" + token + "&count=200"));
        JSONArray arr = obj.getJSONArray("response");
        arr.remove(0);
        Integer i = 0;
        ArrayList<String> list = new ArrayList<>();
        while (i < arr.length()) {
            if (!arr.getJSONObject(i).has("chat_id")) {
                list.add(arr.getJSONObject(i).get("uid") + "@" + getUserNameById((Integer) arr.getJSONObject(i).get("uid")));
            }
            i++;
        }
        return list;
    }

    static ArrayList<String> getChats(String token) throws Exception {
        JSONObject obj = new JSONObject(get("https://api.vk.com/method/messages.getDialogs?access_token=" + token + "&count=200"));
        JSONArray arr = obj.getJSONArray("response");
        arr.remove(0);
        Integer i = 0;
        ArrayList<String> list = new ArrayList<>();
        while (i < arr.length()) {
            if (arr.getJSONObject(i).has("chat_id")) {
                list.add(arr.getJSONObject(i).get("chat_id") + "@" + arr.getJSONObject(i).get("title").toString().replace("/", ";") );
            }
            i++;
        }
        return list;
    }

    static String getUserNameById(Integer id) throws Exception {
        JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?user_ids=" + id));
        JSONArray er = or.getJSONArray("response");
        return er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
    }

    static String getUserNameByToken(String token) throws Exception {
        JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?access_token=" + token));
        JSONArray er = or.getJSONArray("response");
        return er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
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