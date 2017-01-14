package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import static pro.eugw.MessageDownloader.Ext.getConversations;
import static pro.eugw.MessageDownloader.Ext.getUserNameByToken;
import static pro.eugw.MessageDownloader.OldExt.SaveNewLocal;
import static pro.eugw.MessageDownloader.OldExt.chat;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println("STARTING");
        File config = new File("config.properties");
        if (!config.exists()) config.createNewFile();
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
            ArrayList<ArrayList<String>> arr = getConversations(aList);
            ArrayList<String> ld = arr.get(0);
            for (String aLd : ld) {
                String pr = getUserNameByToken(aList) + "@" + aList + File.separator + "dialogs" + File.separator + aLd;
                SaveNewLocal(pr, aList, aLd.split("@")[0], "user");
                chat(pr);
            }
            ArrayList<String> lc = arr.get(1);
            for (String aLc : lc) {
                String pr = getUserNameByToken(aList) + "@" + aList + File.separator + "chats" + File.separator + aLc;
                SaveNewLocal(pr, aList, aLc.split("@")[0], "chat");
                chat(pr);
            }
            System.out.println("NEW TOKEN");
        }
        System.out.println("ONE TICK FINISHED");
    }
}