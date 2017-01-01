package pro.eugw.MessageDownloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Properties;

import static pro.eugw.MessageDownloader.Ext.getChat;
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
        while (i2 <= Integer.valueOf(properties.getProperty("token.cnt"))) {
            list.add(properties.getProperty("token" + i2));
            i2++;
        }
        while (true) {
            Integer i = 0;
            while (i < list.size()) {
                getChat(list.get(i));
                BufferedReader br = new BufferedReader(new FileReader(new File(getUserNameByToken(list.get(i)) + "@" + list.get(i) + File.separator + "chats", "count")));
                String readed = br.readLine();
                Integer count = readed.split(":").length;
                Integer i3 = 0;
                while (i3 < count) {
                    String pr = getUserNameByToken(list.get(i)) + "@" + list.get(i) + File.separator + "chats" + File.separator + readed.split(":")[i];
                    SaveNewLocal(pr, list.get(i), readed.split(":")[i]);
                    chat(pr);
                    Thread.sleep(500);
                    i3++;
                }
                System.out.println("NEW TOKEN");
                i++;
            }
            System.out.println("ONE TICK FINISHED");
            Thread.sleep(3600000);
        }
    }
}