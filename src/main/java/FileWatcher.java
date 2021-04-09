import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class FileWatcher {
    private HashMap<String,File> fileMap = new HashMap<>();
    private HashMap<String,File> changeMap = new HashMap<>();

    public void run() throws Exception {
        File folder = new File(Parameters.logFolder);
        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".zevtc") || name.endsWith(".evtc"));
        Arrays.stream(listOfFiles).forEach(f -> fileMap.put(f.getAbsolutePath(),f));

        System.out.println("Monitoring ArcDps output files at: " + Parameters.logFolder);

        //continuous file monitor loop
        while (true) {

            //short pause
            Thread.sleep(5000L);

            //update map of all files
            listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".zevtc") || name.endsWith(".evtc"));

            //find any new file
            File f = locateNewFile(listOfFiles);

            //monitor file for completion then process
            if (f != null) {
                String fullFilePath = f.getAbsolutePath();
                fileMap.put(fullFilePath,f);
                long lastModified = f.lastModified();
                for (int i=0;i<200;i++) { //max retries
                    Thread.sleep(10000L);
                    if (!f.exists()) {
                        System.out.println("File was removed.");
                        break; //exit loop
                    } else if (lastModified == f.lastModified()) {
                        System.out.println("Generating WvW JSON");
                        ProcessBuilder pb = new ProcessBuilder(Parameters.Gw2EIExe, fullFilePath);
                        Process p = pb.start();
                        p.waitFor();
                        System.out.println("Status (0=success): " + p.exitValue());
                        File jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_wvw_kill.json");
                        if (jsonFile.exists()) {
                            System.out.println(jsonFile.getName());
                            FightReport report = new ParseBot().processWvwJsonLog(jsonFile);
                            if (report.getZone()!=null) {
                                DiscordBot bot = DiscordBot.getSingletonInstance();
                                bot.sendMessage(Parameters.discordChannel, report);
                            }
                            try { jsonFile.delete(); } catch (Exception e) {}
                        }
                        break; //exit loop
                    } else { //else keep looping until file is no longer being modified
                        lastModified = f.lastModified();
                        System.out.print(">");
                    }
                }
            }

            System.out.print(".");
        }
    }

    private File locateNewFile(File[] listOfFiles) {
        for (File f : listOfFiles) {
            if (!fileMap.containsKey(f.getAbsolutePath())) {
                System.out.println("\nNew file detected: " + f.getName());
                return f;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n*** MzFightReporter Version 1.0-BETA ***\n");
        System.out.println("Note: You should see a dot printed below every few seconds indicating ArcDps log polling.\n");
        new FileWatcher().run();
    }
}
