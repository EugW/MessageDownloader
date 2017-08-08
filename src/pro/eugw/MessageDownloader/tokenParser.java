package pro.eugw.MessageDownloader;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Properties;

class tokenParser {
    
    private File file = null;
    
    tokenParser(String path) {
        this.file = new File(path);
    }
    
    ArrayList<String> parse() throws Exception {
        ArrayList<String> list = new ArrayList<>();
        File config = this.file;
        if (!config.exists())
            config.createNewFile();
        FileInputStream fis = new FileInputStream(config);
        Properties properties = new Properties();
        properties.load(fis);
        if (properties.isEmpty())
            error();
        if (!properties.containsKey("token.cnt"))
            error();
        if (properties.getProperty("token.cnt").isEmpty() || Integer.valueOf(properties.getProperty("token.cnt")) <= 0)
            error();
        for (Integer i = 1; i <= Integer.valueOf(properties.getProperty("token.cnt")); i++){
            if (!properties.containsKey("token" + i)) {
               error();
            }
            list.add(properties.getProperty("token" + i));
        }
        return list;
    }
    
    private void error() {
        System.out.println("CONFIG CREATED, ENTER TOKEN");
        System.exit(0);
    }
    
}
