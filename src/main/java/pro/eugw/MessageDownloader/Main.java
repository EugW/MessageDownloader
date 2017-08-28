package pro.eugw.MessageDownloader;

import com.google.gson.JsonArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import static pro.eugw.MessageDownloader.Miscellaneous.getConfig;
import static pro.eugw.MessageDownloader.Miscellaneous.log;

public class Main {
    
    public static void main(String[] args) throws Exception {
        log().info("VERSION: " + getConfig().getProperty("version"));
        log().info("STARTING");
        ArrayList<String> arrayList = new tokenParser("config").parse();
        log().info("TOKENS= " + arrayList.size());
        ArrayList<Thread> workers = new ArrayList<>();
        for (String aList : arrayList) {
            log().info("NEW TOKEN= " + aList);
            JsonArray arr = new getResponse("messages.getDialogs", "access_token=" + aList + "&count=200").get();
            arr.remove(0);
            ArrayList<String> list0 = new ArrayList<>();
            ArrayList<String> list1 = new ArrayList<>();
            for (Integer i = 0; i < arr.size(); i++) {
                if (!arr.get(i).getAsJsonObject().has("chat_id") && arr.get(i).getAsJsonObject().get("uid").getAsInt() > 0)
                    list0.add(arr.get(i).getAsJsonObject().get("uid").getAsInt()
                            + "@"
                            + new getResponse(arr.get(i).getAsJsonObject().get("uid").getAsInt()).getNameById());
                if (arr.get(i).getAsJsonObject().has("chat_id"))
                    list1.add(arr.get(i).getAsJsonObject().get("chat_id").getAsInt()
                            + "@"
                            + arr.get(i).getAsJsonObject().get("title").getAsString());
            }
            final Integer[] badChat = {0};
            for (String aLd : list0) {
                Runnable task = () -> {
                    try {
                        String pr = new getResponse(aList).getNameByToken() + "@" + aList + File.separator + "dialogs" + File.separator + aLd;
                        saveMessages.json(pr, aList, aLd.split("@")[0], "user");
                        saveMessages.chat(pr);
                    } catch (Exception e) {
                        log().error("THIS CHAT CAN NOT BE DOWNLOADED");
                        badChat[0]++;
                    }
                };
                Thread worker = new Thread(task);
                worker.setName(aLd);
                worker.start();
                workers.add(worker);
            }
            for (String aLc : list1) {
                Runnable task = () -> {
                    try {
                        String pr = new getResponse(aList).getNameByToken() + "@" + aList + File.separator + "chats" + File.separator + aLc;
                        saveMessages.json(pr, aList, aLc.split("@")[0], "chat");
                        saveMessages.chat(pr);
                    } catch (Exception e) {
                        log().error("THIS CHAT CAN NOT BE DOWNLOADED");
                        badChat[0]++;
                    }
                };
                Thread worker = new Thread(task);
                worker.setName(aLc);
                worker.start();
                workers.add(worker);
            }
        }
        log().info("FINISHED CREATING THREADS");
        boolean alive;
        String prev = "";
        do {
            Integer non_completed = 0;
            alive = false;
            for (Thread rofl : workers) {
                if (rofl.isAlive()) {
                    non_completed++;
                    log().debug(rofl + "IT IS LIVE");
                    alive = true;
                }
            }
            String now = workers.size() - non_completed + "/" + workers.size();
            if (!Objects.equals(prev, now)) {
                log().info(now);
                prev = now;
            }
            Thread.sleep(1000);
        } while (alive);
        log().info("FINISHED");
    }

}