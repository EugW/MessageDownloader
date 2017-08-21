package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;

import java.io.File;
import java.util.ArrayList;

public class Main {
    
    public static void main(String[] args) throws Exception {
        System.out.println("STARTING");
        ArrayList<String> arrayList = new tokenParser("config").parse();
        System.out.println("TOKENS= " + arrayList.size());
        for (String aList : arrayList) {
            System.out.println("NEW TOKEN= " + aList);
            JsonArray arr = new getResponse("messages.getDialogs", "access_token=" + aList + "&count=200").get();
            arr.remove(0);
            ArrayList<String> list0 = new ArrayList<>();
            ArrayList<String> list1 = new ArrayList<>();
            for (Integer i = 0; i < arr.size(); i++) {
                if (!arr.get(i).getAsJsonObject().has("chat_id") && arr.get(i).getAsJsonObject().get("uid").getAsInt() > 0)
                    list0.add(arr.get(i).getAsJsonObject().get("uid").getAsInt()
                            + "@"
                            + new getResponse(arr.get(i).getAsJsonObject().get("uid").getAsString(), null).getNameById());
                if (arr.get(i).getAsJsonObject().has("chat_id"))
                    list1.add(arr.get(i).getAsJsonObject().get("chat_id").getAsInt()
                            + "@"
                            + arr.get(i).getAsJsonObject().get("title").getAsString());
            }
            for (String aLd : list0) {
                String pr = new getResponse(aList, null).getNameByToken() + "@" + aList + File.separator + "dialogs" + File.separator + aLd;
                saveMessages.json(pr, aList, aLd.split("@")[0], "user");
                saveMessages.chat(pr);
            }
            for (String aLc : list1) {
                String pr = new getResponse(aList, null).getNameByToken() + "@" + aList + File.separator + "chats" + File.separator + aLc;
                saveMessages.json(pr, aList, aLc.split("@")[0], "chat");
                saveMessages.chat(pr);
            }
        }
        System.out.println("FINISHED");
    }

}