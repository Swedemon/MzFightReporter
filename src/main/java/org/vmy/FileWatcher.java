package org.vmy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

public class FileWatcher {
    private HashMap<String,File> fileMap = new HashMap<>();
    private HashMap<String,File> changeMap = new HashMap<>();

    public void run() throws Exception {

        if (new File(Parameters.getInstance().homeDir).exists())
            System.out.println("Detected MzFightReporter Home.");
        else
            System.out.println("Failure to detect GuildWars2EliteInsights application at: " + Parameters.getInstance().Gw2EIExe);
        if (new File(Parameters.getInstance().Gw2EIExe).exists())
            System.out.println("Detected GuildWars2EliteInsights Application.");
        else
            System.out.println("Failure to detect GuildWars2EliteInsights application at: " + Parameters.getInstance().Gw2EIExe);

        File folder = new File(Parameters.getInstance().logFolder);

        //loop to await folder detection
        System.out.println("Awaiting presence of ArcDps log folder: " + Parameters.getInstance().logFolder);
        while (true) {
            if (folder.exists()) {
                System.out.println("OK");
                break;
            }
            Thread.sleep(10000L);
            System.out.print(".");
        }

        File[] listOfFiles = folder.listFiles((dir, name) -> name.endsWith(".zevtc") || name.endsWith(".evtc"));
        Arrays.stream(listOfFiles).forEach(f -> fileMap.put(f.getAbsolutePath(),f));

        System.out.println("Monitoring ArcDps output files.");

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
                    Thread.sleep(5000L);
                    if (!f.exists()) {
                        System.out.println("File was removed.");
                        break; //exit loop
                    } else if (lastModified == f.lastModified()) {
                        System.out.println("Invoking GW2EI...");
                        ProcessBuilder pb = new ProcessBuilder(Parameters.getInstance().Gw2EIExe, fullFilePath);
                        pb.inheritIO();
                        Process p = pb.start();
                        p.waitFor();
                        System.out.println("GW2EI Status (0=success): " + p.exitValue());
                        File jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_wvw_kill.json");
                        if (jsonFile.exists()) {

                            //call parsebot
                            System.out.println("Generating FightReport...");
                            ProcessBuilder pb2 = new ProcessBuilder("java", "-jar", Parameters.getInstance().jarName, "ParseBot", jsonFile.getAbsolutePath());
                            pb2.inheritIO();
                            pb2.directory(new File(Parameters.getInstance().homeDir));
                            Process p2 = pb2.start();
                            p2.waitFor();
                            System.out.println("FightReport Status (0=success): " + p2.exitValue());

                            //call graphbot
                            if (p2.exitValue() == 0) {
                                System.out.println("Generating Graph...");
                                ProcessBuilder pb3 = new ProcessBuilder("java", "-jar", Parameters.getInstance().jarName, "GraphBot");
                                pb3.inheritIO();
                                pb3.directory(new File(Parameters.getInstance().homeDir));
                                Process p3 = pb3.start();
                                p3.waitFor();
                                System.out.println("Graphing Status (0=success): " + p3.exitValue());

                                //call discordbot
                                if (p3.exitValue() == 0) {

                                    FightReport report = FightReport.readReportFile();
                                    if (report==null) {
                                        System.out.println("ERROR: FightReport file not available.");
                                    } else {
                                        DiscordBot dBot = org.vmy.DiscordBot.getSingletonInstance();
                                        dBot.sendMessage(Parameters.getInstance().discordChannel, report);
                                    }
                                }
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
        System.out.println("Note:\n   You should see a dot printed below every few seconds indicating ArcDps log polling.\n"
                +"   You can change settings in the config properties file in the same directory then restart.\n");

        System.out.println("homeDir="+Parameters.getInstance().homeDir);
        System.out.println("Gw2EIExe="+Parameters.getInstance().Gw2EIExe);
        System.out.println("logFolder="+Parameters.getInstance().logFolder);
        System.out.println("thumbnail="+Parameters.getInstance().thumbnail);
        System.out.println("discordChannel="+Parameters.getInstance().discordChannel);
        System.out.println("jarName="+Parameters.getInstance().jarName);
        System.out.println("graphPlayerLimit="+Parameters.getInstance().graphPlayerLimit);
        System.out.println();

        new FileWatcher().run();
    }
}
