package pro.eugw.MessageParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws Exception {
        while (true) {
            get();
            SaveNewLocal();
            post_file();
            chat();
            System.out.println("Messages refreshed");
            Thread.sleep(60000);
        }
    }

    private static void get() throws Exception {
        URL url = new URL("https://api.vk.com/method/messages.getHistory" +
                "?access_token=53d220974cbf0e2b13c876a09fde50404320cbd0fd1e718c3db0572e2ca586d9f2118ed9a4c036b13ac37" +
                "&user_id=169338954" +
                "&count=200");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        BufferedReader rd;
        rd = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        PrintWriter printWriter = new PrintWriter(f());
        printWriter.println(rd.readLine());
        printWriter.flush();
        printWriter.close();
    }

    private static JSONArray response_parse() throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(f()));
        String json = bufferedReader.readLine();
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

    private static JSONArray file_parse() throws Exception {
        File local = new File("local");
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
        Integer i = 0;
        while (i < local.length()) {
            mids.add(local.getJSONObject(i).get("mid").toString());
            i++;
        }
        i = 0;
        while (i < response.length()) {
            Integer i2 = 0;
            while (i2 < mids.size()) {
                if (mids.get(i2).equals(String.valueOf(response.getJSONObject(i).getInt("mid")))) {
                    response.remove(i);
                }
                i2++;
            }
            i++;
        }
        return response;
    }

    private static File f() throws Exception {
        File file = new File("response");
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private static void post_file() {
        File file = new File("response");
        if (file.exists()) {
            file.delete();
        }
    }

    private static void SaveNewLocal() throws Exception {
        JSONArray arr = diff(response_parse(), file_parse());
        File local = new File("local");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(local));
        String json = bufferedReader.readLine();
        JSONObject object = new JSONObject(json);
        JSONArray array = object.getJSONArray("local");
        Integer i = 0;
        while (i < arr.length()) {
            array.put(arr.getJSONObject(i));
            i++;
        }
        object.put("local", array);
        PrintWriter printWriter = new PrintWriter(local);
        printWriter.println(object);
        printWriter.flush();
        printWriter.close();
    }

    private static void chat() throws Exception {
        File local = new File("local");
        BufferedReader br = new BufferedReader(new FileReader(local));
        String jst = br.readLine();
        JSONObject obj = new JSONObject(jst);
        JSONArray array = obj.getJSONArray("local");
        File messages = new File("messages");
        if (messages.exists()) {
            messages.delete();
        }
        if (!messages.exists()) {
            messages.createNewFile();
        }
        PrintWriter pw = new PrintWriter(messages);
        Integer i = 0;
        while (i < array.length()) {
            boolean fwd_messages = false;
            if (array.getJSONObject(i).has("fwd_messages")) {
                fwd_messages = true;
                File dir = new File("additions");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File fl = new File("additions" + File.separator + array.getJSONObject(i).get("mid"));
                if (!fl.exists()) {
                    fl.createNewFile();
                }
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.getJSONObject(i).get("fwd_messages"));
                printWriter.flush();
                printWriter.close();
            }
            boolean attachment = false;
            if (array.getJSONObject(i).has("attachment")) {
                attachment = true;
                File dir = new File("additions");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File fl = new File("additions" + File.pathSeparator + array.getJSONObject(i).get("mid"));
                if (!fl.exists()) {
                    fl.createNewFile();
                }
                PrintWriter printWriter = new PrintWriter(fl);
                printWriter.println(array.getJSONObject(i).get("attachment"));
                printWriter.flush();
                printWriter.close();
            }
            Integer uid = (Integer) array.getJSONObject(i).get("uid");
            if (uid.equals(169338954)) pw.println("Eva Pobedimova");
            if (uid.equals(203310182)) pw.println("Anuar Dalagunov");
            if (fwd_messages) pw.println("fwd_messages: true: additions id: " + array.getJSONObject(i).get("mid"));
            if (attachment) pw.println("attachment: true: additions id: " + array.getJSONObject(i).get("mid"));
            pw.println(array.getJSONObject(i).get("body"));
            pw.println();
            pw.flush();
            i++;
        }
        pw.close();
    }
}