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
        Integer i2 = 1;
        System.out.println("TOKENS= " + Integer.valueOf(properties.getProperty("token.cnt")));
        while (i2 <= Integer.valueOf(properties.getProperty("token.cnt"))) {
            list.add(properties.getProperty("token" + i2));
            System.out.println("TOKEN" + i2 + "= " + properties.getProperty("token" + i2));
            i2++;
        }
        Integer i = 0;
        while (i < list.size()) {
            ArrayList<ArrayList<String>> arr = getConversations(list.get(i));
            ArrayList<String> ld = arr.get(0);
            Integer cd = ld.size();
            Integer i3 = 0;
            while (i3 < cd) {
                String pr = getUserNameByToken(list.get(i)) + "@" + list.get(i) + File.separator + "dialogs" + File.separator + ld.get(i3);
                SaveNewLocal(pr, list.get(i), ld.get(i3).split("@")[0], "user");
                chat(pr);
                i3++;
            }
            ArrayList<String> lc = arr.get(1);
            Integer cc = lc.size();
            i3 = 0;
            while (i3 < cc){
                String pr = getUserNameByToken(list.get(i)) + "@" + list.get(i) + File.separator + "chats" + File.separator + lc.get(i3);
                SaveNewLocal(pr, list.get(i), lc.get(i3).split("@")[0], "chat");
                chat(pr);
                i3++;
            }
            System.out.println("NEW TOKEN");
            i++;
        }
        System.out.println("ONE TICK FINISHED");
    }
}