
package org.vmy;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.vmy.util.FightReport;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class FileWatcher {
    private HashMap<String,File> fileMap = new HashMap<>();
    private HashMap<String,File> changeMap = new HashMap<>();

    public void run() throws Exception {
        Parameters p = Parameters.getInstance();

        CheckUpdater.syncBatFile();

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

        System.out.println("\nActive Discord Webhook: " + p.getCurrentDiscordWebhookLabel());

        File folder = new File(p.customLogFolder);
        File defaultFolder = new File(p.defaultLogFolder);

        //loop to await folder detection
        System.out.println("\r\nMonitoring new ArcDps log files in the folders defined in the Settings.");
        while (true) {
            if (folder.exists() || defaultFolder.exists()) {
                break;
            }
            Thread.sleep(10000L);
            System.out.print(".");
        }

        List<File> listOfFiles = listLogFiles();
        listOfFiles.forEach(f -> fileMap.put(f.getAbsolutePath(),f));

        //continuous file monitor loop
        int dotCount = 0;
        while (true) {
            dotCount++;

            MainFrame.statusLabel.setText("Status: Monitoring ArcDps logs");

            //short pause
            Thread.sleep(2000L);

            //update map of all files
            listOfFiles = listLogFiles();

            //find any new file
            File f = locateNewFile(listOfFiles);

            //monitor file for completion then process
            if (f != null) {
                dotCount = 0;
                MainFrame.statusLabel.setText("Status: Processing " + f.getName());
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

                        int fileMegabytes = (int) f.length() / 1048576;
                        int sizeFactor = 1 + ((int) f.length() / 5000000);
                        int eiWaitTime = sizeFactor * 60 + 60;
                        int uploadWaitTime = 150;
                        int parseWaitTime = sizeFactor * 60 + 60;

                        //parse json
                        long startTime = System.currentTimeMillis();
                        System.out.println("Invoking GW2EI...");
                        MainFrame.statusLabel.setText("Status: Invoking GW2EI");
                        ProcessBuilder pb1 = new ProcessBuilder("cmd", "/c", "start", "/b", "/low", "/affinity", "1",
                                "/wait", "." + p.gw2EIExe, "-c", parseConfig, fullFilePath);
                        pb1.directory(new File(p.homeDir));
                        Process p1 = pb1.start();
                        boolean finished = p1.waitFor(eiWaitTime, TimeUnit.SECONDS);
                        if (finished) {
                            System.out.println("GW2EI Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p1.exitValue());
                        } else {
                            System.out.println("GW2EI Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): 1");
                        }

                        File jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_wvw_kill.json");
                        if (!jsonFile.exists())
                            jsonFile = new File(fullFilePath.substring(0,fullFilePath.lastIndexOf('.'))+"_detailed_gh_kill.json");
                        if (jsonFile.exists()) {

                            //call parsebot
                            startTime = System.currentTimeMillis();
                            System.out.println("Generating FightReport...");
                            MainFrame.statusLabel.setText("Status: Generating FightReport");
                            System.setOut(new PrintStream(MainFrame.reportStream));
                            System.out.println("------------------------------------------------------------------------------------------------");
                            ProcessBuilder pb2 = new ProcessBuilder("cmd", "/c", "start", "/b", "/low", "/affinity", "1",
                                    "/wait", "java", "-Xms512m", "-Xmx" + p.maxParseMemory + "m", "-jar", p.jarName, "ParseBot",
                                    jsonFile.getAbsolutePath(), p.homeDir, "");
                            pb2.directory(new File(p.homeDir));
                            Process p2 = pb2.start();
                            handleIO(p2);
                            p2.waitFor(parseWaitTime, TimeUnit.SECONDS);
                            System.setOut(new PrintStream(MainFrame.consoleStream));
                            System.out.println("FightReport Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p2.exitValue());
                            MainFrame.statusLabel.setText("Status: Parsed " + f.getName());

                            //delete json file
                            try { jsonFile.delete(); } catch (Exception ignored) {}

                            //read report file
                            FightReport report = FightReport.readReportFile();
                            System.out.println(report.getOverview());

                            //check fight minimums
                            if (report.getTotalSeconds() < p.minFightDuration) {
                                System.out.println("SKIPPING:  Fight duration is below minimum defined in the Settings.");
                                break;
                            }
                            if (report.getTotalDowns() < p.minFightDowns) {
                                System.out.println("SKIPPING:  Total downs is below the minimum defined in the Settings.");
                                break;
                            }
                            if (report.getTotalDmg() < p.minFightTotalDmg) {
                                System.out.println("SKIPPING:  Total damage is below the minimum defined in the Settings.");
                                break;
                            }

                            //call twitchbot
                            if (!StringUtils.isEmpty(p.twitchBotToken) && !StringUtils.isEmpty(p.twitchChannelName)) {
                                if (p.enableTwitchBot) {
                                    sendTwitchMsg(report);
                                    MainFrame.statusLabel.setText("Status: Twitch sent " + f.getName());
                                }
                            }

                            //conditionally upload (before)
                            report.setUrl(!p.largeUploadsAfterParse || fileMegabytes < p.largeUploadMegabytes ? processUpload(p, fullFilePath, fileMegabytes, uploadWaitTime) : "");

                            //call discordbot
                            boolean discordOkay = true;
                            if (!p.getCurrentDiscordWebhooks().isEmpty() && p.enableDiscordBot) {
                                discordOkay = sendDiscordMsg(report);
                                MainFrame.statusLabel.setText("Status: Discord sent " + f.getName());
                            }

                            //conditionally upload (after)
                            if (p.largeUploadsAfterParse && fileMegabytes >= p.largeUploadMegabytes) {
                                String uploadUrl = processUpload(p, fullFilePath, fileMegabytes, uploadWaitTime);

                                //call discordbot on report URL
                                if (!StringUtils.isEmpty(uploadUrl) && !p.getCurrentDiscordWebhooks().isEmpty()) {
                                    System.setOut(new PrintStream(MainFrame.reportStream));
                                    System.out.println("Report URL = " + uploadUrl);
                                    System.setOut(new PrintStream(MainFrame.consoleStream));
                                    if (discordOkay && p.enableDiscordBot) {
                                        discordOkay = sendDiscordUrlMsg(uploadUrl);
                                        MainFrame.statusLabel.setText("Status: Twitch sent " + f.getName());
                                    }
                                }
                            }

                            //call graphbot
                            if (p2.exitValue() == 0) {
                                if (p.graphPlayerLimit > 0 && p.showDamageGraph) {
                                    startTime = System.currentTimeMillis();
                                    System.out.println("Generating Graph...");
                                    MainFrame.statusLabel.setText("Status: Generating Graph");
                                    ProcessBuilder pb3 = new ProcessBuilder("cmd", "/c", "start", "/b", "/low", "/affinity", "1",
                                            "/wait", "java", "-Xms256m", "-jar", p.jarName, "GraphBot", p.homeDir);
                                    pb3.directory(new File(p.homeDir));
                                    Process p3 = pb3.start();
                                    handleIO(p3);
                                    p3.waitFor(parseWaitTime, TimeUnit.SECONDS);
                                    System.out.println("Graphing Status [" + ((int) ((System.currentTimeMillis() - startTime) / 1000)) + "s] (0=success): " + p3.exitValue());

                                    //call discordbot on graph
                                    if (p3.exitValue() == 0 && !p.getCurrentDiscordWebhooks().isEmpty()) {
                                        if (discordOkay && p.enableDiscordBot)
                                            sendDiscordGraphMsg();
                                    }
                                }
                            }

                            //finished
                            MainFrame.statusLabel.setText("Status: Finished " + f.getName());
                        }
                        break; //exit loop
                    } else { //else keep looping until file is no longer being modified
                        lastModified = f.lastModified();
                        System.out.print(">");
                    }
                }
            }

            System.out.print(".");
            if (dotCount % 5 == 0)
                MainFrame.statusLabel.setText("Status: Monitoring ArcDps logs");
            if (dotCount > 0 && dotCount % 150 == 0)
                System.out.println();
        }
    }

    @NotNull
    private static String processUpload(Parameters p, String fullFilePath, int fileMegabytes, int uploadWaitTime) throws IOException, InterruptedException {

        if (!p.enableReportUpload) {
            return "";
        }

        if (fileMegabytes >= p.maxUploadMegabytes) {
            System.out.println("Skipping upload. File size exceeds max defined in the Settings (" + p.maxUploadMegabytes + "MB).");
            return "";
        }

        String uploadUrl = "";
        long startTime;
        for (int k = 0; k < 2; k++) {
            startTime = System.currentTimeMillis();
            String uploadRespText = "";
            try {
                System.out.println("Invoking Upload...");
                MainFrame.statusLabel.setText("Status: Invoking Upload");
                ProcessBuilder pb0 = new ProcessBuilder("cmd", "/c", "start", "/b", "/low", "/affinity", "1",
                        ".\\curl\\bin\\curl.exe",
                        "--max-time", Integer.toString(uploadWaitTime),
                        "--request", "POST", p.activeUploadPostUrl,
                        "-H", "\"Transfer-Encoding: chunked\"",
                        "-H", "\"Connection: keep-alive\"",
                        "-H", "\"Accept-Encoding: identity\"",
                        "-H", "\"Cookie: userToken=" + p.uploadToken + "\"",
                        //"-H", "\"Content-Length: " + f.length()+"\"",
                        //"--limit-rate", "5M",
                        "--form", "json=1", "--form", "detailedwvw=true",
                        "--form", "\"file=@" + fullFilePath + "\"")
                        .redirectError(new File("uploadStats.txt"));
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
                break; //break on success

            //handle failure
            int seconds = ((int) ((System.currentTimeMillis() - startTime) / 1000));
            //if quick failure then change URL
            if (seconds < 10) {
                p.activeUploadPostUrl = p.activeUploadPostUrl.equals(p.uploadPostUrl) ? p.uploadPostAltUrl : p.uploadPostUrl;
                System.out.println("Changing URL to " + p.activeUploadPostUrl + "...");
            }
            //if long failure
            else if (k == 0 && seconds > 60) {
                if (fileMegabytes < 20) {
                    System.out.println("Giving the poor report server a 60s break...");
                    Thread.sleep(60000L);
                } else {
                    System.out.println("Giving the poor report server a 120s break...");
                    Thread.sleep(120000L);
                }
            }
        }
        return uploadUrl;
    }

    private static boolean sendDiscordMsg(FightReport report) {
        MainFrame.statusLabel.setText("Status: Sending to Discord");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable task = () -> {
            DiscordBot dBot = DiscordBot.getSingletonInstance();
            dBot.sendMainMessage(report);
        };
        Future<?> future = executor.submit(task);
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    private static boolean sendDiscordUrlMsg(String uploadUrl) {
        MainFrame.statusLabel.setText("Status: Sending Report URL to Discord");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable task = () -> {
            DiscordBot dBot = DiscordBot.getSingletonInstance();
            dBot.sendReportUrlMessage(uploadUrl);
        };
        Future<?> future = executor.submit(task);
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    private static boolean sendDiscordGraphMsg() {
        MainFrame.statusLabel.setText("Status: Sending Report Graph to Discord");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable task = () -> {
            DiscordBot dBot = DiscordBot.getSingletonInstance();
            dBot.sendGraphMessage();
        };
        Future<?> future = executor.submit(task);
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Failed to send Discord Graph message: " + e.getMessage());
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    private static boolean sendTwitchMsg(FightReport report) {
        MainFrame.statusLabel.setText("Status: Sending to Twitch");
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable task = () -> {
            TwitchBot tBot = TwitchBot.getSingletonInstance();
            try {
                tBot.sendMessage(report.getOverview());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        Future<?> future = executor.submit(task);
        try {
            future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Failed to send Twitch message: " + e.getMessage());
            e.printStackTrace(System.out);
            return false;
        }
        return true;
    }

    private void handleIO(Process p) throws IOException {
        InputStream is = p.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null)
        {
            System.out.println(line);
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
            e.printStackTrace(System.out);
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
                System.out.println("\nNew file detected: " + f.getName() + " " + new DecimalFormat("#,###").format(f.length()) + " bytes");
                return f;
            }
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        Parameters p = Parameters.getInstance();

        MainFrame.start();

        System.setOut(new PrintStream(MainFrame.consoleStream));

        System.out.println("Note:  You will see a dot printed below every few seconds indicating ArcDps log polling.\n");

        p.homeDir = System.getProperty("user.dir");

        if (p.getCurrentDiscordWebhooks().isEmpty()) {
            System.out.println("*** WARNING ***: Discord webhook is not yet defined in the Settings!\r\n");
        } else if (!p.enableDiscordBot) {
            System.out.println("*** WARNING ***: Discord messaging is set to disabled in the Settings!\r\n");
        }

        new FileWatcher().run();
    }
}
