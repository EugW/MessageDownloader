package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

class tokenParser {
    
    private File file = null;
    
    tokenParser(String path) {
        this.file = new File(path);
    }
    
    ArrayList<String> parse() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        File config = this.file;
        if (!config.exists())
            if (config.createNewFile())
                System.out.println("CONFIG CREATED");
        FileInputStream fis = new FileInputStream(config);
        Properties properties = new Properties();
        properties.load(fis);
        Scanner scanner = new Scanner(System.in);
        if (!properties.containsKey("token.cnt")) {
            System.out.print("ENTER TOKENS COUNT (INTEGER): ");
            Integer cnt = scanner.nextInt();
            properties.setProperty("token.cnt", String.valueOf(cnt));
        }
        if (Integer.valueOf(properties.getProperty("token.cnt")) <= 0) {
            System.out.println("TOKENS COUNT <= 0");
            System.exit(0);
        }
        for (Integer i = 1; i <= Integer.valueOf(properties.getProperty("token.cnt")); i++){
            if (!properties.containsKey("token" + i)) {
                System.out.print("ENTER TOKEN " + i + ": ");
                String tkn = scanner.next();
                if (tkn.length() < 85 || tkn.length() > 85) {
                    System.out.println("TOKEN IS TOO LONG OR TOO SHORT. TRY AGAIN");
                    System.exit(0);
                }
                properties.setProperty("token" + i, tkn);
            }
            list.add(properties.getProperty("token" + i));
        }
        FileWriter fileWriter = new FileWriter(config);
        properties.store(fileWriter, "TOKENS");
        //test
        return list;
    }

}
