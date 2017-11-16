package pro.eugw.MessageDownloader;

import java.io.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import static pro.eugw.MessageDownloader.Miscellaneous.log;

class tokenParser {
    
    private File file = null;
    
    tokenParser(String file) {
        this.file = new File(file);
    }
    
    ArrayList<String> parse() {
        ArrayList<String> list = new ArrayList<>();
        File config = this.file;
        try {
            if (!config.exists())
                if (config.createNewFile())
                    log().info("Config created");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties properties = new Properties();
        try {
            FileInputStream fis = new FileInputStream(config);
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        if (!properties.containsKey("token.cnt")) {
            log().info("Enter tokes count (digit): ");
            Integer cnt = scanner.nextInt();
            properties.setProperty("token.cnt", String.valueOf(cnt));
        }
        if (Integer.valueOf(properties.getProperty("token.cnt")) <= 0) {
            log().error("Tokens count <= 0");
            System.exit(0);
        }
        for (Integer i = 1; i <= Integer.valueOf(properties.getProperty("token.cnt")); i++){
            if (!properties.containsKey("token" + i)) {
                log().info("Enter token " + i + ": ");
                String tkn = scanner.next();
                if (tkn.length() != 85) {
                    log().error("Token is too short or too long. Try again");
                    System.exit(0);
                }
                properties.setProperty("token" + i, tkn);
            }
            list.add(properties.getProperty("token" + i));
        }
        try {
            FileWriter fileWriter = new FileWriter(config);
            properties.store(fileWriter, "TOKENS");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

}
