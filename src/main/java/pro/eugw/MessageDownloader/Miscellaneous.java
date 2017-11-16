package pro.eugw.MessageDownloader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;

class Miscellaneous {

    static Properties getConfig() {
        File fProp = new File("md.properties");
        if (!fProp.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("md.properties")));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fProp)));
                String rd;
                while ((rd = reader.readLine()) != null) {
                    writer.write(rd);
                    writer.newLine();
                }
                reader.close();
                writer.flush();
                writer.close();
            } catch (Exception e) {
                log().error("Error while initializing configuration");
                e.printStackTrace();
                System.exit(0);
            }
        }
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(fProp));
        } catch (IOException e) {
            log().error("Cannot read " + fProp);
            e.printStackTrace();
            System.exit(0);
        }
        return properties;
    }

    static Logger log() {
        return LogManager.getLogger();
    }

}
