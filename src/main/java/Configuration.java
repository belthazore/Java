import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//Not used, need help
public class Configuration {

    private static Properties prop = new Properties();
    private static InputStream input;


    static {
        try{
            input = new FileInputStream("//opt//tomcat//webapps//project//db.conf");
            // load a properties file
            prop.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String get(String name) {
        return prop.getProperty(name);
    }
}