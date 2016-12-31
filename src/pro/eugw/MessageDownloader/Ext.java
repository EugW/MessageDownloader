package pro.eugw.MessageDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static pro.eugw.MessageDownloader.OldExt.SaveNewLocal;
import static pro.eugw.MessageDownloader.OldExt.chat;

class Ext {
    static JSONArray getChat(String token) throws Exception {
        JSONObject obj = new JSONObject(get("https://api.vk.com/method/messages.getDialogs" + "?access_token=" + token + "&count=200"));
        JSONArray arr = obj.getJSONArray("response");
        arr.remove(0);
        Integer i = 0;
        while (i < arr.length()) {
            if (arr.getJSONObject(i).has("admin_id")) arr.remove(i);
            i++;
        }
        File chats = new File(getUserNameByToken(token) + "@" + token, "chats");
        if (!chats.exists()) chats.mkdirs();
        File clv = new File(chats + File.separator + "count");
        if (!clv.exists()) clv.createNewFile();
        PrintWriter pw = new PrintWriter(clv);
        i = 0;
        while (i < arr.length()) {
            File fol = new File(chats, arr.getJSONObject(i).get("uid") + "@" + getUserNameById((Integer) arr.getJSONObject(i).get("uid")));
            if (!fol.exists()) fol.mkdir();
            pw.print(arr.getJSONObject(i).get("uid") + "@" + getUserNameById((Integer) arr.getJSONObject(i).get("uid")) + ":");
            i++;
        }
        pw.flush();
        pw.close();
        return arr;
    }

    static String getUserNameById(Integer id) throws Exception {
        JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?user_ids=" + id));
        JSONArray er = or.getJSONArray("response");
        return er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
    }

    private static String getUserNameByToken(String token) throws Exception {
        JSONObject or = new JSONObject(get("https://api.vk.com/method/users.get?access_token=" + token));
        JSONArray er = or.getJSONArray("response");
        return er.getJSONObject(0).getString("first_name") + er.getJSONObject(0).getString("last_name");
    }

    private static String get(String kurl) throws Exception {
        URL url = new URL(kurl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        BufferedReader rd;
        rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        return rd.readLine();
    }

    static void SaveMsg(String token) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(new File(getUserNameByToken(token) + "@" + token + File.separator + "chats", "count")));
        String readed = br.readLine();
        Integer count = readed.split(":").length;
        Integer i = 0;
        while (i < count) {
            String pr = getUserNameByToken(token) + "@" + token + File.separator + "chats" + File.separator + readed.split(":")[i];
            SaveNewLocal(pr, token, readed.split(":")[i]);
            chat(pr);
            Thread.sleep(500);
            i++;
        }

    }
}