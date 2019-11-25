import java.io.File;

public class Main {
    public static void main(String[] args) {
        System.out.println("Beginning execution of logical dump system...");

        //Root directory of the origin and destination locations
        File originRoot = null;
        File destRoot = null;

        try {
            originRoot = new File(Config.getProperty("o"));
            destRoot = new File(Config.getProperty("d"));
        } catch (NullPointerException e) {
            System.out.println("ERROR: Config file was not set up properly, ending program.");
            System.exit(0);
        }

        LogicalDumper d = new LogicalDumper(originRoot, destRoot);

        System.out.println("Starting backup...");
        d.dump();

    }

}


