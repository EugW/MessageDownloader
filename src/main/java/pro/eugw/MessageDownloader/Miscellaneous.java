package pro.eugw.MessageDownloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

class Miscellaneous {

    static Properties getConfig() {
        File file = new File("iCon");
        if (!file.exists()) {
            Properties properties = new Properties();
            try {
                if (file.createNewFile())
                    log().debug("CREATED " + file);
                properties.load(ClassLoader.getSystemResourceAsStream("icon.properties"));
            } catch (IOException e) {
                log().error("CANNOT CREATE iCon");
            }
            Properties lProp = new Properties();
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file);
                lProp.load(fileInputStream);
                lProp.setProperty("autoDownload", properties.getProperty("autoDownload"));
                lProp.setProperty("getMaxTries", properties.getProperty("getMaxTries"));
                FileWriter fileWriter = new FileWriter(file);
                lProp.store(fileWriter, "iCon");
            } catch (FileNotFoundException e) {
                log().error("CANNOT FIND iCon");
            } catch (IOException e) {
                log().error("CANNOT READ iCon");
            }
        }
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            log().error("CANNOT FIND iCon");
        } catch (IOException e) {
            log().error("CANNOT READ iCon");
        }
        return properties;
    }

    static Logger log() {
        return LogManager.getLogger();
    }

}
