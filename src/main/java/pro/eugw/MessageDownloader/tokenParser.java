package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

import static pro.eugw.MessageDownloader.Miscellaneous.log;

class tokenParser {
    
    private File file = null;
    
    tokenParser(String file) {
        this.file = new File(file);
    }
    
    ArrayList<String> parse() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        File config = this.file;
        if (!config.exists())
            if (config.createNewFile())
                log().info("CONFIG CREATED");
        FileInputStream fis = new FileInputStream(config);
        Properties properties = new Properties();
        properties.load(fis);
        Scanner scanner = new Scanner(System.in);
        if (!properties.containsKey("token.cnt")) {
            log().info("ENTER TOKENS COUNT (INTEGER): ");
            Integer cnt = scanner.nextInt();
            properties.setProperty("token.cnt", String.valueOf(cnt));
        }
        if (Integer.valueOf(properties.getProperty("token.cnt")) <= 0) {
            log().error("TOKENS COUNT <= 0");
            System.exit(0);
        }
        for (Integer i = 1; i <= Integer.valueOf(properties.getProperty("token.cnt")); i++){
            if (!properties.containsKey("token" + i)) {
                log().info("ENTER TOKEN " + i + ": ");
                String tkn = scanner.next();
                if (tkn.length() < 85 || tkn.length() > 85) {
                    log().error("TOKEN IS TOO LONG OR TOO SHORT. TRY AGAIN");
                    System.exit(0);
                }
                properties.setProperty("token" + i, tkn);
            }
            list.add(properties.getProperty("token" + i));
        }
        FileWriter fileWriter = new FileWriter(config);
        properties.store(fileWriter, "TOKENS");
        return list;
    }

}
