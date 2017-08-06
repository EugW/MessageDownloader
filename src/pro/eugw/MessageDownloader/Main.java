package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import static pro.eugw.MessageDownloader.Ext.getConversations;
import static pro.eugw.MessageDownloader.Ext.getUsernameByToken;
import static pro.eugw.MessageDownloader.OldExt.saveNewLocal;
import static pro.eugw.MessageDownloader.OldExt.chat;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("STARTING");
        File config = new File("config");
        if (!config.exists()) {
            config.createNewFile();
            System.out.println("CONFIG CREATED, ENTER TOKEN");
            System.exit(0);
        } else {
            FileInputStream fis = new FileInputStream(config);
            Properties properties = new Properties();
            properties.load(fis);
            if (properties.isEmpty()) {
                System.out.println("CONFIG CREATED, ENTER TOKEN");
                System.exit(0);
            }
        }
        FileInputStream fis = new FileInputStream(config);
        Properties properties = new Properties();
        properties.load(fis);
        ArrayList<String> list = new ArrayList<>();
        System.out.println("TOKENS= " + Integer.valueOf(properties.getProperty("token.cnt")));
        for (Integer i = 1; i <= Integer.valueOf(properties.getProperty("token.cnt")); i++){
            list.add(properties.getProperty("token" + i));
            System.out.println("TOKEN" + i + "= " + properties.getProperty("token" + i));
        }
        for (String aList : list) {
            System.out.println("NEW TOKEN");
            ArrayList<ArrayList<String>> arr = getConversations(aList);
            ArrayList<String> ld = arr.get(0);
            for (String aLd : ld) {
                String pr = getUsernameByToken(aList) + "@" + aList + File.separator + "dialogs" + File.separator + aLd;
                saveNewLocal(pr, aList, aLd.split("@")[0], "user");
                chat(pr);
            }
            ArrayList<String> lc = arr.get(1);
            for (String aLc : lc) {
                String pr = getUsernameByToken(aList) + "@" + aList + File.separator + "chats" + File.separator + aLc;
                saveNewLocal(pr, aList, aLc.split("@")[0], "chat");
                chat(pr);
            }
        }
        System.out.println("FINISHED");
    }
}