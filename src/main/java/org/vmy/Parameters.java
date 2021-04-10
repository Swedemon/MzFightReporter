package org.vmy;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Parameters {

    public String homeDir = "C:\\Arc\\MzFightReporter\\";
    public String Gw2EIExe = homeDir + "\\GW2EI\\GuildWars2EliteInsights.exe";
    public String logFolder =
            "C:\\Arc\\arcdps.cbtlogs\\WvW\\Elite Apes\\";
            //System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\1\\";
    public String thumbnail = "https://i.imgur.com/KKddNgl.png";
    public String token = "ODI4ODU4MzM3NDYyMDU5MDE4.YGvsew.SPuwZeO_rlZg4jpPCUWrxxGyWh4";
    public String discordChannel = "fight-reports";
    public String jarName = "MzFightReporter-1.1-BETA.jar ";
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
            homeDir = prop.getProperty("homeDir",homeDir);
            Gw2EIExe = homeDir + "\\GW2EI\\GuildWars2EliteInsights.exe";
            String lg = prop.getProperty("logFolder");
            logFolder =
                    lg==null || lg.length()==0
                    ? logFolder
                    : lg.indexOf(':') > 0
                        ? lg
                        : System.getenv("USERPROFILE") + lg;
            thumbnail = prop.getProperty("thumbnail",thumbnail);
            token = prop.getProperty("token",token);
            discordChannel = prop.getProperty("discordChannel",discordChannel);
            jarName = prop.getProperty("jarName",jarName);
            graphPlayerLimit = Integer.parseInt(prop.getProperty("graphPlayerLimit", graphPlayerLimit+""));
        } catch (Exception e) {
            System.out.println("Warning: Unable to read config.properties.  Using default values.");
        } finally { if (file != null) { try { file.close(); } catch (IOException e) {}}}
    }
}
