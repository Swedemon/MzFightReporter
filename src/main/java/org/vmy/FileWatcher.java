package org.vmy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FileWatcher {
    private HashMap<String,File> fileMap = new HashMap<>();
    private HashMap<String,File> changeMap = new HashMap<>();

    public void run() throws Exception {
        Parameters p = Parameters.getInstance();

        if (new File(p.homeDir + File.separator + p.gw2EIExe).exists())
            System.out.println("Detected GuildWars2EliteInsights Application.");
        else
            System.out.println("Failure to detect GuildWars2EliteInsights application at: " + p.homeDir + File.separator + p.gw2EIExe);

        File folder = new File(p.customLogFolder);
        File defaultFolder = new File(p.defaultLogFolder);

        //loop to await folder detection
        System.out.println("Parent folder(s) configured to monitor ArcDps log files:");
        if (p.customLogFolder!=null && p.customLogFolder.length()>0)
            System.out.println("   > " + folder.getAbsolutePath());
        System.out.println("   > " + defaultFolder.getAbsolutePath());
        while (true) {
            if (folder.exists() || defaultFolder.exists()) {
                System.out.println("OK");
                break;
            }
            Thread.sleep(10000L);
            System.out.print(".");
        }

        List<File> listOfFiles = listLogFiles();
        listOfFiles.forEach(f -> fileMap.put(f.getAbsolutePath(),f));

        System.out.println("Monitoring ArcDps log files.");

        //continuous file monitor loop
        while (true) {

           //short pause
            Thread.sleep(5000L);

            //update map of all files
            listOfFiles = listLogFiles();

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
                        String confFolder = p.homeDir + "\\GW2EI\\Settings\\";
                        String parseConfig = confFolder + "wvwupload.conf";

                        //if large file then directly upload to dps reports without wvw stats
                        if (f.length() > p.maxWvwUpload*1024*1024) {
                            String uploadConfig = confFolder + "uploadwithoutwvw.conf";
                            ProcessBuilder pb = new ProcessBuilder(p.homeDir + File.separator + p.gw2EIExe, "-c", uploadConfig, fullFilePath);
                            pb.inheritIO();
                            Process p0 = pb.start();
                            p0.waitFor(120, TimeUnit.SECONDS);
                            p0.destroy();
                            p0.waitFor();
                            System.out.println("GW2EI Upload Status (0=success): " + p0.exitValue());
                            parseConfig = confFolder + "wvwnoupload.conf";
                        }

                        //parse json
                        ProcessBuilder pb = new ProcessBuilder(p.homeDir + File.separator + p.gw2EIExe, "-c", parseConfig, fullFilePath);
                        pb.inheritIO();
                        Process p1 = pb.start();
                        p1.waitFor();
                        System.out.println("GW2EI Parse Status (0=success): " + p1.exitValue());

                        File logFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+".log");
                        File jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_wvw_kill.json");
                        if (jsonFile.exists()) {

                            //call parsebot
                            System.out.println("Generating FightReport...");
                            ProcessBuilder pb2 = new ProcessBuilder("java", "-jar", p.jarName, "ParseBot", jsonFile.getAbsolutePath(), logFile.getAbsolutePath(), p.homeDir);
                            pb2.inheritIO();
                            pb2.directory(new File(p.homeDir));
                            Process p2 = pb2.start();
                            p2.waitFor(120, TimeUnit.SECONDS);
                            p2.destroy();
                            p2.waitFor();
                            System.out.println("FightReport Status (0=success): " + p2.exitValue());

                            if (p2.exitValue() == 0) {

                                /*
                                //call graphbot
                                System.out.println("Generating Graph...");
                                ProcessBuilder pb3 = new ProcessBuilder("java", "-jar", p.jarName, "GraphBot", p.homeDir);
                                pb3.inheritIO();
                                pb3.directory(new File(p.homeDir));
                                Process p3 = pb3.start();
                                p3.waitFor(120, TimeUnit.SECONDS);
                                p3.destroy();
                                p3.waitFor();
                                System.out.println("Graphing Status (0=success): " + p3.exitValue());

                                if (p3.exitValue() == 0) {
                                */

                                    //call discordbot
                                    FightReport report = FightReport.readReportFile();
                                    if (report==null) {
                                        System.out.println("ERROR: FightReport file not available.");
                                    } else {
                                        DiscordBot dBot = org.vmy.DiscordBot.getSingletonInstance();
                                        dBot.sendWebhookMessage(report);
                                    }
                                /*}*/
                            }
                            try { jsonFile.delete(); if (logFile.exists()) logFile.delete(); } catch (Exception e) {}
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

    private List<File> listLogFiles() throws IOException {
        List<File> list = new ArrayList<>();

        //custom folder
        String customFolder = Parameters.getInstance().customLogFolder;
        if (customFolder!=null && customFolder.length()>0) {
            File folder = new File(Parameters.getInstance().customLogFolder);
            list = !folder.exists() ? new ArrayList<>() :
                    Files.find(Paths.get(folder.getAbsolutePath()),
                            Integer.MAX_VALUE,
                            (filePath, fileAttr) -> fileAttr.isRegularFile())
                            .filter(f -> f.toFile().getName().endsWith(".zevtc") || f.toFile().getName().endsWith(".evtc"))
                            .map(p -> p.toFile())
                            .collect(Collectors.toList());
        }

        //default folder
        File defaultFolder = new File(Parameters.getInstance().defaultLogFolder);
        List<File> list2 = !defaultFolder.exists() ? new ArrayList<>() :
            Files.find(Paths.get(defaultFolder.getAbsolutePath()),
                Integer.MAX_VALUE,
                (filePath, fileAttr) -> fileAttr.isRegularFile())
                .filter(f -> f.toFile().getName().endsWith(".zevtc") || f.toFile().getName().endsWith(".evtc"))
                .map(p -> p.toFile())
                .collect(Collectors.toList());
        list.addAll(list2);
        return list;
    }

    private File locateNewFile(List<File> listOfFiles) {
        for (File f : listOfFiles) {
            if (!fileMap.containsKey(f.getAbsolutePath())) {
                System.out.println("\nNew file detected: " + f.getName());
                return f;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Parameters p = Parameters.getInstance();

        System.out.println("\n*** MzFightReporter ***\n");
        System.out.println("Note:\n   You should see a dot printed below every few seconds indicating ArcDps log polling.\n"
                +"   You can change settings in the config properties file at the install location.\n");

        if (args.length>1)
            p.homeDir = args[1];

        System.out.println("homeDir="+p.homeDir);
        System.out.println("defaultLogFolder="+p.defaultLogFolder);
        System.out.println("customLogFolder="+p.customLogFolder);
        System.out.println("discordThumbnail="+p.discordThumbnail);
        System.out.println("discordWebhook=("+new String(p.discordWebhook).length()+" characters)");
        System.out.println("jarName="+p.jarName);
        System.out.println("maxWvwUpload="+p.maxWvwUpload);
        System.out.println("graphPlayerLimit="+p.graphPlayerLimit);
        System.out.println();

        if (p.discordWebhook==null | p.discordWebhook.length()==0) {
            System.out.println("ERROR: Discord webhook is missing.  Review README.txt for install instructions.");
            System.exit(1);
        }

        new FileWatcher().run();
    }
}
