import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Objects;

class LogicalDumper {
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HHmmss");
    private File originRoot;
    private File destRoot;
    private File mostRecentBackupRoot;

    //This boolean is to track whether any files were copied.
    private boolean wasCopied = false;

    //constructor sets originRoot and destRoot variables
    LogicalDumper(File orig, File dest){
        setOriginRoot(orig);
        setDestRoot(dest);
    }

    //setter for originRoot
    private void setOriginRoot(File originRoot) { this.originRoot = originRoot; }

    //setter for destRoot
    private void setDestRoot(File destRoot) { this.destRoot = destRoot; }

    //Function performs an initial, full, dump if there are no existing backups.
    //If there are existing backups, run an incremental backup.
    void dump(){
        //Path for the backup to go in. (original file name + current date and time)
        Path destDirectory = Paths.get(destRoot +"/"+originRoot.getName()+ " " + sdf.format(destRoot.lastModified()));

        //if there is an existing backup, set mostRecentBackupRoot to the most recently modified backup.
        //process an incremental dump
        if(backupExists()){
            System.out.println("Processing incremental dump...");
            mostRecentBackupRoot = getRecentBackup();
            incrementalDump(originRoot, destDirectory);
        }
        //else this is the the first dump so process an initial dump.
        else{
            System.out.println("Processing initial dump...");
            initialDump(originRoot, destDirectory);
        }

        System.out.println("Backup complete...");
        if (wasCopied){
            System.out.println("Files backed up to: "+ destDirectory);
        }
        else{
            System.out.println("No files were backed up.");
        }

    }

    //For each file in the destRoot, return true if they have the name of the originRoot (they are a backup), else return false.
    private boolean backupExists(){
        for (File f: Objects.requireNonNull(destRoot.listFiles())) {
            if (f.getName().contains(originRoot.getName())){
                return true;
            }
        }
        return false;
    }

    //For each file in the destRoot that is a backup, compare last modified time and return the most recent backup.
    private File getRecentBackup(){
        File newest = originRoot;
        for(File f: Objects.requireNonNull(destRoot.listFiles())){
            if (f.getName().contains(originRoot.getName())){
                if (f.lastModified() > newest.lastModified()){
                    newest = f;
                }
            }
        }
        return newest;
    }

    //this recursive function is run on every file in the source folder and if the file had been modified since the last backup, the file is backed up.
    private void incrementalDump(File source, Path target) {
        //If the source file has been modified since the last backup, make sure the path to it exists and copy it over.
        if(source.lastModified() > mostRecentBackupRoot.lastModified()){
            try {
                copy(source.toPath(), target);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //If the file is a directory, call this function on it's children files.
        if(source.isDirectory()) {
            for (File f : Objects.requireNonNull(source.listFiles())) {
                incrementalDump(f, Paths.get(target+"/"+f.getName()));
            }
        }

    }

    //This recursive function is called on every file in the source directory and copies over the file to the proper destination directory location.
    private void initialDump(File source, Path target){
        try {
            copy(source.toPath(), target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //If the file is a directory, call this function on it's children files.
        if(source.isDirectory()) {
            for (File f : Objects.requireNonNull(source.listFiles())) {
                initialDump(f, Paths.get(target +"/"+f.getName()));
            }
        }
    }

    private void copy(Path source, Path target) throws IOException {
        //Ensures that the path to this file exists, important for the incremental dump
        createPath(source, target);

        Files.copy(source, target);
        System.out.println("Backed up: " + source);
        wasCopied = true;
    }

    private void createPath(Path source, Path target){
        Path sourceParent = source.getParent();
        Path targetParent = target.getParent();

        //If the target's parent directory does not exist
        if (!targetParent.toFile().exists()){
            //try to create the parent directory for that file
            createPath(sourceParent,targetParent);
            //execution will proceed to this point once it is certain that the file we're about to copy has an existing path.
            try {
                //copy the sourceParent to targetParent
                copy(sourceParent, targetParent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
