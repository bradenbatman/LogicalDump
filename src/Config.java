import java.io.IOException;
import java.util.Properties;

public final class Config
{
    private static final String fileName = "parameters.cfg";
    private static Properties configFile;

    //instance variable makes this class a Singleton, only one can exist.
    private static Config instance = null;

    private Config()
    {
        configFile = new Properties();

        try {
            //System.out.println(getClass() +" + "+ getClass().getClassLoader() +" + "+ getClass().getClassLoader().getResourceAsStream(fileName));
            configFile.load(getClass().getClassLoader().getResourceAsStream(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getProperty(String key)
    {
        if (instance == null){
            instance = new Config();
        }

        return configFile.getProperty(key);
    }
}