package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

import static pro.eugw.MessageDownloader.Ext.SaveMsg;
import static pro.eugw.MessageDownloader.Ext.getChat;

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
                SaveMsg(list.get(i));
                System.out.println("NEW TOKEN");
                i++;
            }
            System.out.println("ONE TICK FINISHED");
            Thread.sleep(3600000);
        }
    }
}