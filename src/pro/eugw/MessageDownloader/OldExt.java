package pro.eugw.MessageDownloader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;

import static pro.eugw.MessageDownloader.Ext.*;

class OldExt {
    private static JSONArray response_parse(String token, String id, String type) throws Exception {
        String json = get("https://api.vk.com/method/messages.getHistory?access_token=" + token + "&" + type + "_id=" + id + "&count=200");
        JSONObject obj = new JSONObject(json);
        JSONArray arr = obj.getJSONArray("response");
        arr.remove(0);
        JSONArray arr2 = new JSONArray();
        Integer i = arr.length() - 1;
        while (i >= 0) {
            arr2.put(arr.getJSONObject(i));
            i--;
        }
        return arr2;
    }

    private static JSONArray file_parse(String path) throws Exception {
        File fol = new File(path);
        if (!fol.exists())
            fol.mkdirs();
        File local = new File(path, "local");
        if (!local.exists()) {
            local.createNewFile();
            JSONObject obj = new JSONObject();
            JSONArray arr = new JSONArray();
            obj.put("local", arr);
            PrintWriter printWriter = new PrintWriter(local);
            printWriter.println(obj);
            printWriter.flush();
            printWriter.close();
        }
        BufferedReader bufferedReader = new BufferedReader(new FileReader(local));
        JSONObject obj = new JSONObject(bufferedReader.readLine());
        return obj.getJSONArray("local");
    }

    private static JSONArray diff(JSONArray response, JSONArray local) {
        ArrayList<String> mids = new ArrayList<>();
        for (Integer i = 0; i < local.length(); i++)
            mids.add(local.getJSONObject(i).get("mid").toString());
        for (Integer i = 0; i < response.length(); i++)
            for (String mid : mids)
                if (mid.equals(String.valueOf(response.getJSONObject(i).getInt("mid"))))
                    response.remove(i);
        return response;
    }

    static void SaveNewLocal(String path, String token, String id, String type) throws Exception {
        JSONArray arr = diff(response_parse(token, id, type), file_parse(path));
        File local = new File(path, "local");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(local));
        String json = bufferedReader.readLine();
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("local");
        for (Integer i = 0; i < arr.length(); i++)
            array.put(arr.getJSONObject(i));
        object.put("local", array);
        PrintWriter printWriter = new PrintWriter(local);
        printWriter.println(object);
        printWriter.flush();
        printWriter.close();
    }

    static void chat(String path) throws Exception {
        File local = new File(path, "local");
        BufferedReader br = new BufferedReader(new FileReader(local));
        String jst = br.readLine();
        JSONObject obj = new JSONObject(jst);
        JSONArray array = obj.getJSONArray("local");
        File messages = new File(path, "messages");
        if (messages.exists())
            messages.delete();
        if (!messages.exists())
            messages.createNewFile();
        PrintWriter pw = new PrintWriter(messages);
        for (Integer i = 0; i < array.length(); i++){
            boolean fwd_messages = false;
            if (array.getJSONObject(i).has("fwd_messages")) {
                fwd_messages = true;
                File dir = new File(path, "additions");
                if (!dir.exists()) dir.mkdirs();
                File fl = new File(path, "additions" + File.separator + array.getJSONObject(i).get("mid"));
                if (!fl.exists()) fl.createNewFile();
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.getJSONObject(i).get("fwd_messages"));
                printWriter.flush();
                printWriter.close();
            }
            boolean attachment = false;
            if (array.getJSONObject(i).has("attachment")) {
                attachment = true;
                File dir = new File(path, "additions");
                if (!dir.exists()) dir.mkdirs();
                File fl = new File(path, "additions" + File.separator + array.getJSONObject(i).get("mid"));
                if (!fl.exists()) fl.createNewFile();
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.getJSONObject(i).get("attachment"));
                printWriter.flush();
                printWriter.close();
            }
            Integer uid = (Integer) array.getJSONObject(i).get("uid");
            Integer date = (Integer) array.getJSONObject(i).get("date");
            pw.println(getUsernameById(uid.toString(), "name") + " " + getUsernameById(uid.toString(), "surname") + " " + getDateByTime(date.toString()));
            if (fwd_messages)
                pw.println("fwd_messages: true: additions id: " + array.getJSONObject(i).get("mid"));
            if (attachment)
                pw.println("attachment: true: additions id: " + array.getJSONObject(i).get("mid"));
            pw.println(array.getJSONObject(i).get("body"));
            pw.println();
        }
        pw.flush();
        pw.close();
    }
}