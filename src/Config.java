import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

final class Config
{
    private static final String filePath = "./parameters.cfg";
    private static Properties configFile;

    //instance variable allows for only one instance of this class.
    private static Config instance = null;

    private Config()
    {
        configFile = new Properties();

        try {
            FileInputStream fis = new FileInputStream(filePath);
            configFile.load(new InputStreamReader(fis));
            System.out.println("Loaded parameters.cfg...");
        } catch (IOException e) {
            System.out.println("ERROR: parameters.cfg was not found, ending program.");
            System.exit(0);
            //e.printStackTrace();
        }
    }
    public static String getProperty(String key)
    {
        //ensures that only one instance of this class can exist.
        if (instance==null){
            instance = new Config();
        }
        return configFile.getProperty(key);
    }
}