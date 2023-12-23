package org.vmy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Parameters {

    public String repoUrl = "https://api.github.com/repos/Swedemon/MzFightReporter/releases/latest";
    public String homeDir = "";
    public String curlExe = "\\curl\\bin\\curl.exe";
    public String gw2EIDir = "\\GW2EI-12-21-23";
    public String gw2EIExe = gw2EIDir + "\\GuildWars2EliteInsights.exe";
    public String gw2EISettings = gw2EIDir + "\\Settings\\";
    public String defaultLogFolder =
            System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\";
    public String customLogFolder = "";
    public String discordThumbnail = "https://i.imgur.com/KKddNgl.png";
    public String discordWebhook = "";
    public String twitchChannelName = "";
    public String twitchBotToken = "";
    public String jarName = "";
    public String uploadPostUrl = "https://dps.report/uploadContent";
    public String uploadPostAltUrl = "https://b.dps.report/uploadContent";
    public String activeUploadPostUrl = uploadPostUrl;
    public String uploadToken = "mzfightreporter"+ System.currentTimeMillis();
    public int maxParseMemory = 4096;
    public int graphPlayerLimit = 20;
    public boolean showSquadSummary = true;
    public boolean showEnemySummary = true;
    public boolean showDamage = true;
    public boolean showSpikeDmg = true;
    public boolean showCleanses = true;
    public boolean showStrips = true;
    public boolean showDefensiveBoons = true;
    public boolean showHeals = true;
    public boolean showCCs = true;
    public boolean showEnemyBreakdown = true;
    public boolean showQuickReport = true;
    public boolean showDamageGraph = true;

    private static Parameters instance = null;
    public static Parameters getInstance() {
        if (instance == null)
            instance = new Parameters();
        return instance;
    }
    private Parameters() {
        loadResources();
    }

    private void loadResources() {
        FileInputStream file = null;
        try {
            String path = "config.properties";
            Properties prop = new Properties();
            file = new FileInputStream(path);
            prop.load(file);

            //set properties
            gw2EIExe = homeDir + gw2EIDir + "\\GuildWars2EliteInsights.exe";
            curlExe = homeDir + "\\curl\\bin\\curl.exe";
            String lg = prop.getProperty("customLogFolder");
            customLogFolder = prop.getProperty("customLogFolder");
            discordThumbnail = prop.getProperty("discordThumbnail",discordThumbnail);
            discordWebhook = prop.getProperty("discordWebhook",discordWebhook);
            twitchChannelName = prop.getProperty("twitchChannelName",twitchChannelName);
            twitchBotToken = prop.getProperty("twitchBotToken",twitchBotToken);
            jarName = prop.getProperty("jarName",jarName);
            maxParseMemory = Integer.parseInt(prop.getProperty("maxParseMemory", maxParseMemory+""));
            graphPlayerLimit = Integer.parseInt(prop.getProperty("graphPlayerLimit", graphPlayerLimit+""));
            showSquadSummary = Boolean.valueOf(prop.getProperty("showSquadSummary", "true"));
            showEnemySummary = Boolean.valueOf(prop.getProperty("showEnemySummary", "true"));
            showDamage = Boolean.valueOf(prop.getProperty("showDamage", "true"));
            showSpikeDmg = Boolean.valueOf(prop.getProperty("showSpikeDmg", "true"));
            showCleanses = Boolean.valueOf(prop.getProperty("showCleanses", "true"));
            showStrips = Boolean.valueOf(prop.getProperty("showStrips", "true"));
            showDefensiveBoons = Boolean.valueOf(prop.getProperty("showDefensiveBoons", "true"));
            showCCs = Boolean.valueOf(prop.getProperty("showCCs", "true"));
            showHeals = Boolean.valueOf(prop.getProperty("showHeals", "true"));
            showEnemyBreakdown = Boolean.valueOf(prop.getProperty("showEnemyBreakdown", "true"));
            showQuickReport = Boolean.valueOf(prop.getProperty("showQuickReport", "true"));
            showDamageGraph = Boolean.valueOf(prop.getProperty("showDamageGraph", "true"));
        } catch (Exception e) {
            System.out.println("Warning: Unable to read config.properties.  Using default values.");
        } finally { if (file != null) { try { file.close(); } catch (IOException e) {}}}
    }
}
