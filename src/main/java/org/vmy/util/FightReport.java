package org.vmy.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class FightReport implements Serializable {
    private String zone;
    private String duration;
    private String commander;
    private String squadSummary;
    private String friendliesSummary;

    private String enemySummary;
    private String damage;
    private String cleanses;
    private String strips;
    private String ccs;

    private String dbooners;
    private String url;
    private String endTime;
    private HashMap<String, List<Object>> dmgMap = new HashMap<>();

    public static FightReport readReportFile() throws Exception {
        FightReport myReport = null;
        File reportFile = new File(org.vmy.Parameters.getInstance().homeDir + File.separator + "fightreport.bin");

        if (!reportFile.exists())
            throw new Exception("Fight Report object file not found: " + reportFile.getAbsolutePath());

        FileInputStream frf = null;
        ObjectInputStream o = null;
        try {
            frf = new FileInputStream(reportFile);
            o = new ObjectInputStream(frf);
            // Write objects to file
            myReport = (FightReport) o.readObject();
        } finally {
            if (o!=null)
                o.close();
            if (frf!=null)
                frf.close();
        }
        return myReport;
    }

    public HashMap<String, List<Object>> getDmgMap() {
        return dmgMap;
    }

    public void setDmgMap(HashMap<String, List<Object>> dmgMap) {
        this.dmgMap = dmgMap;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCommander() {
        return commander;
    }

    public void setCommander(String commander) {
        this.commander = commander;
    }

    public String getSquadSummary() {
        return squadSummary;
    }

    public void setSquadSummary(String squadSummary) {
        this.squadSummary = squadSummary;
    }

    public String getFriendliesSummary() { return friendliesSummary; }

    public void setFriendliesSummary(String friendliesSummary) { this.friendliesSummary = friendliesSummary; }

    public String getEnemySummary() {
        return enemySummary;
    }

    public void setEnemySummary(String enemySummary) {
        this.enemySummary = enemySummary;
    }

    public String getDamage() {
        return damage;
    }

    public void setDamage(String damage) {
        this.damage = damage;
    }

    public String getCleanses() {
        return cleanses;
    }

    public void setCleanses(String cleanses) {
        this.cleanses = cleanses;
    }

    public String getStrips() {
        return strips;
    }

    public void setStrips(String strips) { this.strips = strips; }

    public String getDbooners() { return dbooners; }

    public void setDbooners(String dbooners) { this.dbooners = dbooners; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getCcs() {
        return ccs;
    }

    public void setCcs(String ccs) {
        this.ccs = ccs;
    }

}
