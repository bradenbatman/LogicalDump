import java.io.File;

public class Main {
    public static void main(String[] args) {

    File originRoot = new File(Config.getProperty("o"));
    File destRoot = new File(Config.getProperty("d"));

    LogicalDumper d = new LogicalDumper(originRoot, destRoot);

    d.dump();

    }

}


