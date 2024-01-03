package org.vmy;

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Properties;

public class Parameters {

    public static final String appVersion = "4.0.3";

    public String repoUrl = "https://api.github.com/repos/Swedemon/MzFightReporter/releases/latest";
    public String homeDir = "";
    public String curlExe = "\\curl\\bin\\curl.exe";
    public String gw2EIDir = "\\GW2EI-12-21-23";
    public String gw2EIExe = gw2EIDir + "\\GuildWars2EliteInsights.exe";
    public String gw2EISettings = gw2EIDir + "\\Settings\\";
    public String defaultLogFolder = System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\";
    public String customLogFolder = "";
    public String discordThumbnail = "https://i.imgur.com/KKddNgl.png";
    public String discordWebhook = "";
    public String twitchChannelName = "";
    public String twitchBotToken = "";
    public String jarName = "MzApp-Latest.jar";
    public String uploadPostUrl = "https://dps.report/uploadContent";
    public String uploadPostAltUrl = "https://b.dps.report/uploadContent";
    public String activeUploadPostUrl = uploadPostUrl;
    public String uploadToken = "mzfightreporter"+ System.currentTimeMillis();
    public Properties props = new Properties();
    public int maxParseMemory = 4096;
    public int graphPlayerLimit = 20;
    public int uploadLimitMegabytes = 15;
    public boolean enableReportUpload = false;
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
        String path = "config.properties";
        try (FileInputStream file = new FileInputStream(path)) {
            props.load(file);

            //set properties
            gw2EIExe = gw2EIDir + "\\GuildWars2EliteInsights.exe";
            curlExe = homeDir + "\\curl\\bin\\curl.exe";
            customLogFolder = props.getProperty("customLogFolder");
            discordThumbnail = props.getProperty("discordThumbnail",discordThumbnail);
            discordWebhook = props.getProperty("discordWebhook",discordWebhook);
            twitchChannelName = props.getProperty("twitchChannelName",twitchChannelName);
            twitchBotToken = props.getProperty("twitchBotToken",twitchBotToken);
            jarName = props.getProperty("jarName",jarName);
            maxParseMemory = Integer.parseInt(props.getProperty("maxParseMemory", maxParseMemory+""));
            graphPlayerLimit = Integer.parseInt(props.getProperty("graphPlayerLimit", graphPlayerLimit+""));
            uploadLimitMegabytes = Integer.parseInt(props.getProperty("uploadLimitMegabytes", uploadLimitMegabytes+""));
            enableReportUpload = Boolean.valueOf(props.getProperty("enableReportUpload", "true"));
            showSquadSummary = Boolean.valueOf(props.getProperty("showSquadSummary", "true"));
            showEnemySummary = Boolean.valueOf(props.getProperty("showEnemySummary", "true"));
            showDamage = Boolean.valueOf(props.getProperty("showDamage", "true"));
            showSpikeDmg = Boolean.valueOf(props.getProperty("showSpikeDmg", "true"));
            showCleanses = Boolean.valueOf(props.getProperty("showCleanses", "true"));
            showStrips = Boolean.valueOf(props.getProperty("showStrips", "true"));
            showDefensiveBoons = Boolean.valueOf(props.getProperty("showDefensiveBoons", "true"));
            showCCs = Boolean.valueOf(props.getProperty("showCCs", "true"));
            showHeals = Boolean.valueOf(props.getProperty("showHeals", "true"));
            showEnemyBreakdown = Boolean.valueOf(props.getProperty("showEnemyBreakdown", "true"));
            showQuickReport = Boolean.valueOf(props.getProperty("showQuickReport", "true"));
            showDamageGraph = Boolean.valueOf(props.getProperty("showDamageGraph", "true"));
        } catch (Exception e) {
            System.out.println("Warning: Unable to read config.properties.  Using default values.");
        }
    }

    public String validateSettings(HashMap<String, Component> settingsMap) {
        String errorContent = "";
        System.out.println();
        for (String key : settingsMap.keySet()) {
            Object o = settingsMap.get(key);
            //System.out.println(key + ", " + text);
            if (o instanceof Checkbox)
                continue; //ignore checkbox validation
            String text = ((JTextField) o).getText();
            switch (key) {
                case "customLogFolder":
                    if (StringUtils.isEmpty(text)) {
                        ((JTextField) o).setText("C:\\Arc\\");
                        break;
                    } else {
                        if (text.length() < 4)
                            errorContent += "- Not a valid Custom Log Folder.\r\n";
                    }
                    break;
                case "discordThumbnail":
                    if (StringUtils.isEmpty(text))
                        break;
                    else if (!text.toLowerCase().startsWith("http"))
                        errorContent += "- Discord Thumbnail is not a image image URL (png/jpg/jpeg/gif).\r\n";
                    else if (!text.toLowerCase().endsWith("png")
                            && !text.toLowerCase().endsWith("jpg")
                            && !text.toLowerCase().endsWith("gif")
                            && !text.toLowerCase().endsWith("jpeg"))
                        errorContent += "- Discord Thumbnail is not a valid image URL (png/jpg/jpeg/gif).\r\n";
                    break;
                case "discordWebhook":
                    if (StringUtils.isEmpty(text))
                        break;
                    else if (!text.toLowerCase().startsWith("https://") || !text.toLowerCase().contains("discord.com")
                            || text.length() < 40)
                        errorContent += "- Discord Webhook is not a valid webhook URL.\r\n";
                    break;
                case "graphPlayerLimit":
                    if (StringUtils.isEmpty(text)) {
                        ((JTextField) o).setText("20");
                    } else {
                        try {
                            int val = Integer.parseInt(text);
                            if (val < 0 || val > 50)
                                errorContent += "- Graph Player Limit must be a number from 0 to 50.\r\n";
                        } catch (Exception e) {
                            errorContent += "- Graph Player Limit must be a number from 0 to 50.\r\n";
                        }
                    }
                    break;
                case "maxParseMemory":
                    if (StringUtils.isEmpty(text)) {
                        ((JTextField) o).setText("4096");
                    } else {
                        try {
                            int val = Integer.parseInt(text);
                            if (val < 512 || val > 20480)
                                errorContent += "- Max Parse Memory (MB) must be from 512 to 20480.\r\n";
                        } catch (Exception e) {
                            errorContent += "- Max Parse Memory (MB) must be from 512 to 20480.\r\n";
                        }
                    }
                    break;
                case "twitchBotToken":
                case "twitchChannelName":
                    break;
                case "uploadLimitMegabytes":
                    if (StringUtils.isEmpty(text)) {
                        ((JTextField) o).setText("15");
                    } else {
                        try {
                            int val = Integer.parseInt(text);
                            if (val < 0 || val > 99)
                                errorContent += "- Upload Limit (MB) must be a number from 0 to 99.\r\n";
                        } catch (Exception e) {
                            errorContent += "- Upload Limit (MB) must be a number from 0 to 99.\r\n";
                        }
                    }
                    break;
                default:
            }
        }
        return errorContent;
    }

    public void saveSettings(HashMap<String, Component> settingsMap) {
        String path = "config.properties";
        try (FileOutputStream fos = new FileOutputStream(path)) {
            System.out.println();
            for (String key : settingsMap.keySet()) {
                Object o = settingsMap.get(key);
                if (o instanceof Checkbox) {
                    System.out.println(key + " = " + ((Checkbox) o).getState());
                    props.setProperty(key, String.valueOf(((Checkbox) o).getState()));
                }
                else if (o instanceof JTextField) {
                    String text = ((JTextField)o).getText();
                    if ((key.equals("discordWebhook") || key.equals("twitchBotToken")) && text.length() > 0)
                        System.out.println(key + " = (" + text.length() + " characters)");
                    else
                        System.out.println(key + " = " + text);
                    props.setProperty(key, text);
                }
            }
            props.store(fos, "Properties");
            System.out.println("\nSettings saved.");
            MainFrame.statusLabel.setText("Status: Settings saved");
            loadResources();
        } catch (Exception e) {
            System.out.println("Warning: Unable to save config.properties.");
        }
    }

    public void resetSettings(HashMap<String, Component> settingsMap) {
        Parameters p = Parameters.getInstance();

        for (String key : settingsMap.keySet()) {
            Object o = settingsMap.get(key);
            //System.out.println(key + ", " + text);
            if (o instanceof Checkbox) {
                Checkbox checkbox = (Checkbox) o;
                switch (key) {
                    case "enableReportUpload": checkbox.setState(p.enableReportUpload); break;
                    case "showSquadSummary": checkbox.setState(p.showSquadSummary); break;
                    case "showEnemySummary": checkbox.setState(p.showEnemySummary); break;
                    case "showDamage": checkbox.setState(p.showDamage); break;
                    case "showSpikeDmg": checkbox.setState(p.showSpikeDmg); break;
                    case "showCleanses": checkbox.setState(p.showCleanses); break;
                    case "showStrips": checkbox.setState(p.showStrips); break;
                    case "showDefensiveBoons": checkbox.setState(p.showDefensiveBoons); break;
                    case "showCCs": checkbox.setState(p.showCCs); break;
                    case "showHeals": checkbox.setState(p.showHeals); break;
                    case "showEnemyBreakdown": checkbox.setState(p.showEnemyBreakdown); break;
                    case "showQuickReport": checkbox.setState(p.showQuickReport); break;
                    case "showDamageGraph": checkbox.setState(p.showDamageGraph); break;
                    default:
                }
            } else {
                JTextField jTextField = (JTextField) o;
                switch (key) {
                    case "customLogFolder": jTextField.setText(p.customLogFolder); jTextField.setCaretPosition(0); break;
                    case "discordThumbnail": jTextField.setText(p.discordThumbnail); jTextField.setCaretPosition(0); break;
                    case "discordWebhook": jTextField.setText(p.discordWebhook); jTextField.setCaretPosition(0); break;
                    case "graphPlayerLimit": jTextField.setText(String.valueOf(p.graphPlayerLimit)); jTextField.setCaretPosition(0); break;
                    case "maxParseMemory": jTextField.setText(String.valueOf(p.maxParseMemory)); jTextField.setCaretPosition(0); break;
                    case "twitchBotToken": jTextField.setText(p.twitchBotToken); jTextField.setCaretPosition(0); break;
                    case "twitchChannelName": jTextField.setText(p.twitchChannelName); jTextField.setCaretPosition(0); break;
                    case "uploadLimitMegabytes": jTextField.setText(String.valueOf(p.uploadLimitMegabytes)); jTextField.setCaretPosition(0); break;
                    default:
                }
            }
        }
        System.out.println("\nSettings reset.");
        MainFrame.statusLabel.setText("Status: Settings reset");
    }
}
