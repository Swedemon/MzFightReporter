package org.vmy;

import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.List;

public class Parameters {

    public static final String appVersion = "4.3.7";

    public String repoUrl = "https://api.github.com/repos/Swedemon/MzFightReporter/releases/latest";
    public String homeDir = "";
    public String curlExe = "\\curl\\bin\\curl.exe";
    public String gw2EIDir = "\\GW2EI-2024-06-19";
    public String gw2EIExe = gw2EIDir + "\\GuildWars2EliteInsights.exe";
    public String gw2EISettings = gw2EIDir + "\\Settings\\";
    public String defaultLogFolder = System.getenv("USERPROFILE") + "\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\";
    public String customLogFolder = "";
    public String discordThumbnail = "https://i.imgur.com/KKddNgl.png";
    public int activeDiscordWebhook = 1; //1=1 2=2 3=3 4=1,2 5=1,3 6=2,3 7=1,2,3
    public String discordWebhook = "";
    public String discordWebhook2 = "";
    public String discordWebhook3 = "";
    public String discordWebhookLabel = "Default";
    public String discordWebhookLabel2 = "Secondary";
    public String discordWebhookLabel3 = "Tertiary";
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
    public int maxUploadMegabytes = 15;
    public int largeUploadMegabytes = 8;
    public boolean enableReportUpload = true;
    public boolean enableDiscordBot = true;
    public boolean enableTwitchBot = true;
    public boolean enableDiscordMobileMode = false;
    public boolean showSquadSummary = true;
    public boolean showEnemySummary = true;
    public boolean showDamage = true;
    public boolean showSpikeDmg = true;
    public boolean showCleanses = true;
    public boolean showStrips = true;
    public boolean showDefensiveBoons = true;
    public boolean showOffensiveBoons = true;
    public boolean showHeals = true;
    public boolean showDownsKills = true;
    public boolean showCCs = true;
    public boolean showTopEnemySkills = true;
    public boolean showEnemyBreakdown = true;
    public boolean showQuickReport = true;
    public boolean showDamageGraph = true;
    public boolean largeUploadsAfterParse = false;
    public boolean minimizeToTray = false;
    public boolean closeToTray = false;
    public boolean startMinimized = false;

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
            defaultLogFolder = props.getProperty("defaultLogFolder", defaultLogFolder);
            customLogFolder = props.getProperty("customLogFolder");
            discordThumbnail = props.getProperty("discordThumbnail",discordThumbnail);
            activeDiscordWebhook = Integer.parseInt(props.getProperty("activeDiscordWebhook",activeDiscordWebhook+""));
            discordWebhook = props.getProperty("discordWebhook",discordWebhook);
            discordWebhook2 = props.getProperty("discordWebhook2",discordWebhook2);
            discordWebhook3 = props.getProperty("discordWebhook3",discordWebhook3);
            discordWebhookLabel = props.getProperty("discordWebhookLabel",discordWebhookLabel);
            discordWebhookLabel2 = props.getProperty("discordWebhookLabel2",discordWebhookLabel2);
            discordWebhookLabel3 = props.getProperty("discordWebhookLabel3",discordWebhookLabel3);
            twitchChannelName = props.getProperty("twitchChannelName",twitchChannelName);
            twitchBotToken = props.getProperty("twitchBotToken",twitchBotToken);
            maxParseMemory = Integer.parseInt(props.getProperty("maxParseMemory", maxParseMemory+""));
            maxUploadMegabytes = Integer.parseInt(props.getProperty("maxUploadMegabytes", maxUploadMegabytes +""));
            graphPlayerLimit = Integer.parseInt(props.getProperty("graphPlayerLimit", graphPlayerLimit+""));
            enableReportUpload = Boolean.valueOf(props.getProperty("enableReportUpload", "true"));
            enableDiscordBot = Boolean.valueOf(props.getProperty("enableDiscordBot", "true"));
            enableTwitchBot = Boolean.valueOf(props.getProperty("enableTwitchBot", "true"));
            enableDiscordMobileMode = Boolean.valueOf(props.getProperty("enableDiscordMobileMode", "false"));
            showSquadSummary = Boolean.valueOf(props.getProperty("showSquadSummary", "true"));
            showEnemySummary = Boolean.valueOf(props.getProperty("showEnemySummary", "true"));
            showDamage = Boolean.valueOf(props.getProperty("showDamage", "true"));
            showSpikeDmg = Boolean.valueOf(props.getProperty("showSpikeDmg", "true"));
            showCleanses = Boolean.valueOf(props.getProperty("showCleanses", "true"));
            showStrips = Boolean.valueOf(props.getProperty("showStrips", "true"));
            showDefensiveBoons = Boolean.valueOf(props.getProperty("showDefensiveBoons", "true"));
            showOffensiveBoons = Boolean.valueOf(props.getProperty("showOffensiveBoons", "true"));
            showDownsKills = Boolean.valueOf(props.getProperty("showDownsKills", "true"));
            showCCs = Boolean.valueOf(props.getProperty("showCCs", "true"));
            showHeals = Boolean.valueOf(props.getProperty("showHeals", "true"));
            showTopEnemySkills = Boolean.valueOf(props.getProperty("showTopEnemySkills", "true"));
            showEnemyBreakdown = Boolean.valueOf(props.getProperty("showEnemyBreakdown", "true"));
            showQuickReport = Boolean.valueOf(props.getProperty("showQuickReport", "true"));
            showDamageGraph = Boolean.valueOf(props.getProperty("showDamageGraph", "true"));
            largeUploadsAfterParse = Boolean.valueOf(props.getProperty("largeUploadsAfterParse", "true"));
            minimizeToTray = Boolean.valueOf(props.getProperty("minimizeToTray", "false"));
            closeToTray = Boolean.valueOf(props.getProperty("closeToTray", "false"));
            startMinimized = Boolean.valueOf(props.getProperty("startMinimized", "false"));
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
            if (o instanceof JCheckBox)
                continue; //ignore checkbox validation
            if (o instanceof JComboBox)
                continue; //ignore combobox validation
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
                    else if (!text.toLowerCase().startsWith("http"))
                        errorContent += "- Discord Webhook #1 is not a valid webhook URL.\r\n";
                    break;
                case "discordWebhook2":
                    if (StringUtils.isEmpty(text))
                        break;
                    else if (!text.toLowerCase().startsWith("http"))
                        errorContent += "- Discord Webhook #2 is not a valid webhook URL.\r\n";
                    break;
                case "discordWebhook3":
                    if (StringUtils.isEmpty(text))
                        break;
                    else if (!text.toLowerCase().startsWith("http"))
                        errorContent += "- Discord Webhook #3 is not a valid webhook URL.\r\n";
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
                            if (val < 1024 || val > 20480)
                                errorContent += "- Max Parse Memory (MB) must be from 1024 to 20480.\r\n";
                        } catch (Exception e) {
                            errorContent += "- Max Parse Memory (MB) must be from 1024 to 20480.\r\n";
                        }
                    }
                    break;
                case "maxUploadMegabytes":
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
                case "twitchBotToken":
                    break;
                case "twitchChannelName":
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
                if (o instanceof JCheckBox) {
                    System.out.println(key + " = " + ((JCheckBox) o).isSelected());
                    props.setProperty(key, String.valueOf(((JCheckBox) o).isSelected()));
                }
                else if (o instanceof JComboBox) {
                    System.out.println(key + " = " + (((JComboBox) o).getSelectedIndex()) + 1);
                    int currIndex = ((JComboBox)o).getSelectedIndex() + 1;
                    props.setProperty("activeDiscordWebhook", currIndex+"");
                }
                else if (o instanceof JTextField) {
                    String text = ((JTextField)o).getText();
                    if (!key.startsWith("discordWebhookLabel") && text.length() > 0
                            && (key.startsWith("discordWebhook") || key.endsWith("LogFolder") || key.equals("twitchBotToken")))
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

        try {
            if (!getCurrentDiscordWebhooks().isEmpty())
                DiscordBot.getSingletonInstance().buildSessions(); //in case webhook changed
        } catch (Exception e) {
            System.out.println("Warning: Unable to establish Discord webhook connection.");
        }
    }

    public void resetSettings(HashMap<String, Component> settingsMap) {
        Parameters p = Parameters.getInstance();

        for (String key : settingsMap.keySet()) {
            Object o = settingsMap.get(key);
            //System.out.println(key + ", " + text);
            if (o instanceof JCheckBox) {
                JCheckBox checkbox = (JCheckBox) o;
                switch (key) {
                    case "enableReportUpload":
                        checkbox.setSelected(p.enableReportUpload);
                        break;
                    case "enableDiscordBot":
                        checkbox.setSelected(p.enableDiscordBot);
                        break;
                    case "enableTwitchBot":
                        checkbox.setSelected(p.enableTwitchBot);
                        break;
                    case "enableDiscordMobileMode":
                        checkbox.setSelected(p.enableDiscordMobileMode);
                        break;
                    case "showSquadSummary":
                        checkbox.setSelected(p.showSquadSummary);
                        break;
                    case "showEnemySummary":
                        checkbox.setSelected(p.showEnemySummary);
                        break;
                    case "showDamage":
                        checkbox.setSelected(p.showDamage);
                        break;
                    case "showSpikeDmg":
                        checkbox.setSelected(p.showSpikeDmg);
                        break;
                    case "showCleanses":
                        checkbox.setSelected(p.showCleanses);
                        break;
                    case "showStrips":
                        checkbox.setSelected(p.showStrips);
                        break;
                    case "showDefensiveBoons":
                        checkbox.setSelected(p.showDefensiveBoons);
                        break;
                    case "showOffensiveBoons":
                        checkbox.setSelected(p.showOffensiveBoons);
                        break;
                    case "showDownsKills":
                        checkbox.setSelected(p.showDownsKills);
                        break;
                    case "showCCs":
                        checkbox.setSelected(p.showCCs);
                        break;
                    case "showHeals":
                        checkbox.setSelected(p.showHeals);
                        break;
                    case "showTopEnemySkills":
                        checkbox.setSelected(p.showTopEnemySkills);
                        break;
                    case "showEnemyBreakdown":
                        checkbox.setSelected(p.showEnemyBreakdown);
                        break;
                    case "showQuickReport":
                        checkbox.setSelected(p.showQuickReport);
                        break;
                    case "showDamageGraph":
                        checkbox.setSelected(p.showDamageGraph);
                        break;
                    case "largeUploadsAfterParse":
                        checkbox.setSelected(p.largeUploadsAfterParse);
                        break;
                    case "minimizeToTray":
                        checkbox.setSelected(p.minimizeToTray);
                        break;
                    case "closeToTray":
                        checkbox.setSelected(p.closeToTray);
                        break;
                    case "startMinimized":
                        checkbox.setSelected(p.startMinimized);
                        break;
                    default:
                }
            } else if (o instanceof JComboBox) {
                JComboBox jComboBox = (JComboBox) o;
                jComboBox.setSelectedIndex(p.activeDiscordWebhook - 1);
            } else {
                JTextField jTextField = (JTextField) o;
                switch (key) {
                    case "defaultLogFolder": jTextField.setText(p.defaultLogFolder); jTextField.setCaretPosition(0); break;
                    case "customLogFolder": jTextField.setText(p.customLogFolder); jTextField.setCaretPosition(0); break;
                    case "discordThumbnail": jTextField.setText(p.discordThumbnail); jTextField.setCaretPosition(0); break;
                    case "discordWebhook": jTextField.setText(p.discordWebhook); jTextField.setCaretPosition(0); break;
                    case "discordWebhook2": jTextField.setText(p.discordWebhook2); jTextField.setCaretPosition(0); break;
                    case "discordWebhook3": jTextField.setText(p.discordWebhook3); jTextField.setCaretPosition(0); break;
                    case "discordWebhookLabel":  jTextField.setText(p.discordWebhookLabel); jTextField.setCaretPosition(0); break;
                    case "discordWebhookLabel2": jTextField.setText(p.discordWebhookLabel2); jTextField.setCaretPosition(0); break;
                    case "discordWebhookLabel3": jTextField.setText(p.discordWebhookLabel3); jTextField.setCaretPosition(0); break;
                    case "graphPlayerLimit": jTextField.setText(String.valueOf(p.graphPlayerLimit)); jTextField.setCaretPosition(0); break;
                    case "maxParseMemory": jTextField.setText(String.valueOf(p.maxParseMemory)); jTextField.setCaretPosition(0); break;
                    case "twitchBotToken": jTextField.setText(p.twitchBotToken); jTextField.setCaretPosition(0); break;
                    case "twitchChannelName": jTextField.setText(p.twitchChannelName); jTextField.setCaretPosition(0); break;
                    case "maxUploadMegabytes": jTextField.setText(String.valueOf(p.maxUploadMegabytes)); jTextField.setCaretPosition(0); break;
                    default:
                }
            }
        }
        System.out.println("\nSettings reset.");
        MainFrame.statusLabel.setText("Status: Settings reset");
    }

    public List<String> getCurrentDiscordWebhooks() {
        if (activeDiscordWebhook == 2)
            return Arrays.asList(discordWebhook2);
        if (activeDiscordWebhook == 3)
            return Arrays.asList(discordWebhook3);
        if (activeDiscordWebhook == 4)
            return Arrays.asList(discordWebhook, discordWebhook2);
        if (activeDiscordWebhook == 5)
            return Arrays.asList(discordWebhook, discordWebhook3);
        if (activeDiscordWebhook == 6)
            return Arrays.asList(discordWebhook2, discordWebhook3);
        if (activeDiscordWebhook == 7)
            return Arrays.asList(discordWebhook, discordWebhook2, discordWebhook3);
        if (activeDiscordWebhook == 8 || StringUtils.isEmpty(discordWebhook))
            return new ArrayList<>();
        return Arrays.asList(discordWebhook);
    }

    public String getCurrentDiscordWebhookLabel() {
        if (activeDiscordWebhook == 2)
            return discordWebhookLabel2;
        if (activeDiscordWebhook == 3)
            return discordWebhookLabel3;
        if (activeDiscordWebhook == 4)
            return discordWebhookLabel + " and " + discordWebhookLabel2;
        if (activeDiscordWebhook == 5)
            return discordWebhookLabel + " and " + discordWebhookLabel3;
        if (activeDiscordWebhook == 6)
            return discordWebhookLabel2 + " and " + discordWebhookLabel3;
        if (activeDiscordWebhook == 7)
            return discordWebhookLabel + " and " + discordWebhookLabel2 +" and "+ discordWebhookLabel3;
        if (activeDiscordWebhook == 8 || StringUtils.isEmpty(discordWebhook))
            return "Not defined.";
        return discordWebhookLabel;
    }
}
