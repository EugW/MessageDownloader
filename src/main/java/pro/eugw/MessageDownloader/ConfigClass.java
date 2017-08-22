package pro.eugw.MessageDownloader;

import java.util.Properties;

class ConfigClass {

    static Properties getConfig() {
        Properties properties = new Properties();
        try {
            properties.load(ClassLoader.getSystemResourceAsStream("icon.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return properties;
    }

}
