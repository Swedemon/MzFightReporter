package org.vmy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Parameters {

    public String homeDir = "";
    public String gw2EIExe = "\\GW2EI\\GuildWars2EliteInsights.exe";
    public String defaultLogFolder =
            System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\";
    public String customLogFolder = "";
    public String discordThumbnail = "https://i.imgur.com/KKddNgl.png";
    public String discordWebhook = "";
    public String jarName = "";
    public int maxWvwUpload = 6;
    public int graphPlayerLimit = 20;

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
            gw2EIExe = homeDir + "\\GW2EI\\GuildWars2EliteInsights.exe";
            String lg = prop.getProperty("customLogFolder");
            customLogFolder = prop.getProperty("customLogFolder");
            discordThumbnail = prop.getProperty("discordThumbnail",discordThumbnail);
            discordWebhook = prop.getProperty("discordWebhook",discordWebhook);
            jarName = prop.getProperty("jarName",jarName);
            maxWvwUpload = Integer.parseInt(prop.getProperty("maxWvwUpload", maxWvwUpload+""));
            graphPlayerLimit = Integer.parseInt(prop.getProperty("graphPlayerLimit", graphPlayerLimit+""));
        } catch (Exception e) {
            System.out.println("Warning: Unable to read config.properties.  Using default values.");
        } finally { if (file != null) { try { file.close(); } catch (IOException e) {}}}
    }
}
