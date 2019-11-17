import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

class LogicalDumper {
    private final SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HHmmss");
    private File originRoot;
    private File destRoot;
    private File mostRecentBackupRoot;

    LogicalDumper(File orig, File dest){
        setOriginRoot(orig);
        setDestRoot(dest);
    }

    /*public File getOriginRoot() { return originRoot; }*/

    /*public File getDestRoot() { return destRoot; }*/

    public void setOriginRoot(File originRoot) { this.originRoot = originRoot; }

    public void setDestRoot(File destRoot) { this.destRoot = destRoot; }

    void backup(){
        Path destDirectory = Paths.get(destRoot +"/"+originRoot.getName()+ " " + sdf.format(destRoot.lastModified()));

        if(backupExists()){
            mostRecentBackupRoot = getRecentBackup();
            previousBackupDump(originRoot, destDirectory, mostRecentBackupRoot.toPath());
        }
        else{
            initialDump(originRoot, destDirectory);
        }
    }

    private boolean backupExists(){
        File[] destDirectories = destRoot.listFiles();

        for (File f: destDirectories) {
            if (f.getName().contains(originRoot.getName())){
                return true;
            }
        }
        return false;
    }

    private File getRecentBackup(){
        File newest = originRoot;
        String partialName = originRoot.getName();

        for(File f: destRoot.listFiles()){
            if (f.getName().contains(partialName)){
                if (f.lastModified() > newest.lastModified()){
                    newest = f;
                }
            }
        }
        return newest;
    }

    private void previousBackupDump(File source, Path target, Path recentBackupSource) {
        //if the recentBackupSource file exists
        //if the source file is newer than the recentBackupSource file, copy source file to target
        //else dont copy it
        //if the recentBackupSource file doesnt exist
        //if the source file is newer than the recentBackupSource root, copy source file to target
        //only call this method on files

        if(recentBackupSource.toFile().exists()){
            if (source.lastModified() > recentBackupSource.toFile().lastModified()){
                System.out.println("File in previous backup copied again: " + source);
                try {
                    createPath(source.toPath(), target);
                    Files.copy(source.toPath(), target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("File in previous backup not copied: " + source);
            }
        }
        else{
            System.out.println(source.lastModified() +" + "+ mostRecentBackupRoot.lastModified());
            if (source.lastModified() > mostRecentBackupRoot.lastModified()){
                System.out.println("File not in previous backup copied again: " + source);
                try {
                    Files.copy(source.toPath(), target);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                System.out.println("File not in previous backup not copied: " + source);
            }
        }

        //If the file is a directory call this function on all of it's subfiles.
        if(source.isDirectory()) {
            for (File f : source.listFiles()) {
                previousBackupDump(f, Paths.get(target+"/"+f.getName()), Paths.get(recentBackupSource +"/"+f.getName()));
            }
        }

    }

    private void initialDump(File source, Path target){
        try {
            System.out.println("Initial Dump: " + source);
            Files.copy(source.toPath(), target);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //If the file is a directory call this function on all of it's subfiles.
        if(source.isDirectory()) {
            for (File f : source.listFiles()) {
                initialDump(f, Paths.get(target +"/"+f.getName()));
            }
        }
    }

    /*Ensures that the parent folder and path exists for target
    by recursively copying over the parent folders until the path exists.*/
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
                Files.copy(sourceParent, targetParent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
