package org.vmy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Parameters {

    public String repoUrl = "https://api.github.com/repos/Swedemon/MzFightReporter/releases/latest";
    public String homeDir = "";
    public String gw2EIExe = "\\GW2EI-12-3-23\\GuildWars2EliteInsights.exe";
    public String curlExe = "\\curl\\bin\\curl.exe";
    public String gw2EISettings = "\\GW2EI-12-3-23\\Settings\\";
    public String defaultLogFolder =
            System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\";
    public String customLogFolder = "";
    public String discordThumbnail = "https://i.imgur.com/KKddNgl.png";
    public String discordWebhook = "";
    public String twitchChannelName = "";
    public String twitchBotToken = "";
    public String jarName = "";
    public int maxParseMemory = 4096;
    public int graphPlayerLimit = 20;
    public boolean showDamageGraph = true;
    public boolean showDamage = true;
    public boolean showCleanses = true;
    public boolean showStrips = true;
    public boolean showSpikeDmg = true;
    public boolean showDefensiveBoons = true;
    public boolean showCCs = true;
    public boolean showQuickReport = true;
    public boolean showHeals = true;

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
            gw2EIExe = homeDir + "\\GW2EI-12-3-23\\GuildWars2EliteInsights.exe";
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
            showDamageGraph = Boolean.valueOf(prop.getProperty("showDamageGraph", "true"));
            showDamage = Boolean.valueOf(prop.getProperty("showDamage", "true"));
            showCleanses = Boolean.valueOf(prop.getProperty("showCleanses", "true"));
            showStrips = Boolean.valueOf(prop.getProperty("showStrips", "true"));
            showSpikeDmg = Boolean.valueOf(prop.getProperty("showSpikeDmg", "true"));
            showDefensiveBoons = Boolean.valueOf(prop.getProperty("showDefensiveBoons", "true"));
            showCCs = Boolean.valueOf(prop.getProperty("showCCs", "true"));
            showQuickReport = Boolean.valueOf(prop.getProperty("showQuickReport", "true"));
            showHeals = Boolean.valueOf(prop.getProperty("showHeals", "true"));
        } catch (Exception e) {
            System.out.println("Warning: Unable to read config.properties.  Using default values.");
        } finally { if (file != null) { try { file.close(); } catch (IOException e) {}}}
    }
}
