
package org.vmy;

import org.apache.commons.io.FileUtils;
import org.vmy.util.FightReport;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FileWatcher {
    private HashMap<String,File> fileMap = new HashMap<>();
    private HashMap<String,File> changeMap = new HashMap<>();

    public void run() throws Exception {
        Parameters p = Parameters.getInstance();

        if (new File(p.homeDir + File.separator + p.gw2EIExe).exists()) {
            System.out.println("Detected GuildWars2EliteInsights application.");
        } else {
            updateZPackage(p);
            if (new File(p.homeDir + File.separator + p.gw2EIExe).exists()) {
                System.out.println("Detected GuildWars2EliteInsights application.");
            } else {
                System.out.println("!!! Failure to detect GuildWars2EliteInsights application at: " + p.homeDir + File.separator + p.gw2EIExe);
                return;
            }
        }

        if (new File(p.homeDir + File.separator + p.curlExe).exists()) {
            System.out.println("Detected cURL executable.");
        } else {
            updateZPackage(p);
            if (new File(p.homeDir + File.separator + p.curlExe).exists()) {
                System.out.println("Detected cURL executable.");
            } else {
                System.out.println("!!! Failure to detect cURL executable  at: " + p.homeDir + File.separator + p.curlExe);
                return;
            }
        }

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
            Thread.sleep(2000L);

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
                    Thread.sleep(500L);
                    if (!f.exists()) {
                        System.out.println("File was removed.");
                        break; //exit loop
                    } else if (lastModified == f.lastModified()) {
                        String confFolder = p.homeDir + p.gw2EISettings;
                        String parseConfig = confFolder + "wvwnoupload.conf";

                        int sizeFactor = 1 + ((int) f.length() / 5000000);
                        int eiWaitTime = sizeFactor * 60 + 60;
                        int uploadWaitTime = sizeFactor * 60 + 60;
                        int parseWaitTime = sizeFactor * 60 + 60;
                        //System.out.println(sizeFactor +","+ eiWaitTime +","+uploadWaitTime +","+parseWaitTime);

                        //parse json
                        long startTime = System.currentTimeMillis();
                        System.out.println("Invoking GW2EI...");
                        ProcessBuilder pb1 = new ProcessBuilder("cmd", "/c", "start", "/b", "/belownormal",
                                "/wait", "." + p.gw2EIExe, "-c", parseConfig, fullFilePath);
                        pb1.directory(new File(p.homeDir));
                        pb1.inheritIO();
                        Process p1 = pb1.start();
                        boolean finished = p1.waitFor(eiWaitTime, TimeUnit.SECONDS);
                        if (finished) {
                            System.out.println("GW2EI Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p1.exitValue());
                        } else {
                            System.out.println("GW2EI Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): 1");
                        }

                        File logFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+".log");
                        File jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_wvw_kill.json");
                        if (jsonFile.exists()) {

                            //upload
                            String uploadUrl = "";
                            for (int k=0; k<5; k++) {
                                String uploadPostUrl = "https://dps.report/uploadContent";
                                startTime = System.currentTimeMillis();
                                String uploadRespText = new String();
                                try {
                                    System.out.println("Invoking Upload...");
                                    ProcessBuilder pb0 = new ProcessBuilder("cmd", "/c", "start", "/b", "/belownormal",
                                            ".\\curl\\bin\\curl.exe", "--max-time", uploadWaitTime + "", "--request", "POST", uploadPostUrl,
                                            "-H", "\"Transfer-Encoding: chunked\"",
                                            "-H", "\"Connection: keep-alive\"",
                                            "-H", "\"Accept-Encoding: identity\"",
                                            "-H", "\"Cookie: userToken=0dhhn6op61qb6m7ete5tr2aus0mho374\"",
                                            //"-H", "\"Content-Length: " + f.length()+"\"",
                                            //"--limit-rate", "5M",
                                            "--form", "json=1", "--form", "detailedwvw=true",
                                            "--form", "\"file=@" + fullFilePath + "\"" )
                                            .redirectError(new File("uploadStats.txt"));
                                    //System.out.println(String.join(" ", pb0.command()));
                                    pb0.directory(new File(p.homeDir));
                                    Process p0 = pb0.start();
                                    try (BufferedReader reader =
                                                 new BufferedReader(new InputStreamReader(p0.getInputStream()))) {
                                        StringBuilder builder = new StringBuilder();
                                        String line = null;
                                        while ((line = reader.readLine()) != null) {
                                            builder.append(line);
                                            builder.append("\r\n");
                                        }
                                        uploadRespText = builder.toString();
                                    }
                                    int baseIndex = uploadRespText.indexOf("permalink");
                                    if (baseIndex < 0) {
                                        FileUtils.writeStringToFile(new File("uploadLog.txt"), uploadRespText, "UTF-8");
                                        System.out.println("Upload Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): 1");
                                    } else {
                                        int startIndex = uploadRespText.indexOf("http", baseIndex);
                                        int endIndex = uploadRespText.indexOf("\"", startIndex);
                                        uploadUrl = uploadRespText.substring(startIndex, endIndex).replace("\\", "");
                                        System.out.println("Upload Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p0.exitValue());
                                        System.out.println("URL = " + uploadUrl);
                                    }
                                } catch (Exception e) {
                                    System.out.println("Upload failed: " + e.getMessage());
                                    FileUtils.writeStringToFile(new File("uploadLog.txt"), uploadRespText, "UTF-8");
                                }
                                if (uploadUrl.length() > 0)
                                    break;
                            }

                            //call parsebot
                            startTime = System.currentTimeMillis();
                            System.out.println("Generating FightReport...");
                            ProcessBuilder pb2 = new ProcessBuilder("cmd", "/c", "start", "/b", "/belownormal",
                                    "/wait", "java", "-Xmx" + p.maxParseMemory + "M", "-jar", p.jarName, "ParseBot", jsonFile.getAbsolutePath(),
                                    logFile.getAbsolutePath(), p.homeDir, uploadUrl);
                            pb2.inheritIO();
                            pb2.directory(new File(p.homeDir));
                            Process p2 = pb2.start();
                            p2.waitFor(parseWaitTime, TimeUnit.SECONDS);
                            System.out.println("FightReport Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p2.exitValue());

                            if (p2.exitValue() == 0) {

                                //call graphbot
                                if (p.graphPlayerLimit > 0) {
                                    startTime = System.currentTimeMillis();
                                    System.out.println("Generating Graph...");
                                    ProcessBuilder pb3 = new ProcessBuilder("cmd", "/c", "start", "/b",
                                            "/belownormal", "/wait", "java", "-jar", p.jarName, "GraphBot", p.homeDir);
                                    pb3.inheritIO();
                                    pb3.directory(new File(p.homeDir));
                                    Process p3 = pb3.start();
                                    p3.waitFor(parseWaitTime, TimeUnit.SECONDS);
                                    System.out.println("Graphing Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p3.exitValue());
                                }

                                //call discordbot and twitchbot
                                FightReport report = FightReport.readReportFile();
                                if (report==null) {
                                    System.out.println("ERROR: FightReport file not available.");
                                } else {
                                    DiscordBot dBot = DiscordBot.getSingletonInstance();
                                    dBot.sendWebhookMessage(report);
                                    TwitchBot tBot = TwitchBot.getSingletonInstance();
                                    tBot.sendMessage(report.getOverview());
                                }
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

    private void updateZPackage(Parameters p) {
        System.out.println("Installing latest packages...");

        String jsonTxt = CheckUpdater.getGithubReleaseJson(p);
        if (jsonTxt == null) return;

        String zpackUrl = CheckUpdater.getGithubZPackUrl(jsonTxt);
        if (zpackUrl == null) return;

        if (!CheckUpdater.downloadZPackZip(zpackUrl))
            return;

        try {
            CheckUpdater.unzipFolder(Paths.get("zpack.zip"), Paths.get(p.homeDir));
        } catch (IOException e) {
            e.printStackTrace();
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
        System.out.print("showDamageGraph="+p.showDamageGraph);
        System.out.print(" showDamage="+p.showDamage);
        System.out.print(" showCleanses="+p.showCleanses);
        System.out.print(" showStrips="+p.showStrips);
        System.out.print(" showSpikeDmg="+p.showSpikeDmg);
        System.out.print(" showDefensiveBoons="+p.showDefensiveBoons);
        System.out.print(" showCCs="+p.showCCs);
        System.out.println(" showQuickReport="+p.showQuickReport);
        System.out.println("discordThumbnail="+p.discordThumbnail);
        System.out.println("discordWebhook=("+new String(p.discordWebhook).length()+" characters)");
        System.out.println("twitchChannelName="+p.twitchChannelName);
        System.out.println("twitchBotToken=("+new String(p.twitchBotToken).length()+" characters)");
        System.out.println("jarName="+p.jarName);
        System.out.println("maxParseMemory="+p.maxParseMemory);
        System.out.println("graphPlayerLimit="+p.graphPlayerLimit);
        System.out.println();

        if (p.discordWebhook==null || p.discordWebhook.length()==0) {
            System.out.println("ERROR: Discord webhook is missing.  Review README.txt for install instructions.");
            System.exit(1);
        }

        new FileWatcher().run();
    }
}
