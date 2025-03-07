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
    private int durationMS;
    private int totalSeconds;
    private int totalDowns;
    private int totalDmg;
    private String commander;
    private String squadSummary;
    private String enemySummary;
    private String damage;
    private String cleanses;
    private String strips;
    private String ccs;
    private String defense;
    private String enemySkillDmg;
    private String enemyBreakdown;
    private String overview;
    private String dbooners;
    private String obooners;
    private String bursters;
    private String downsKills;
    private String healers;
    private String url;
    private String endTime;
    private String arcVersion;
    private String eiVersion;
    private String recordedBy;
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

    public int getDurationMS() {
        return durationMS;
    }

    public void setDurationMS(int durationMS) {
        this.durationMS = durationMS;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public void setTotalSeconds(int totalSeconds) {
        this.totalSeconds = totalSeconds;
    }

    public int getTotalDowns() {
        return totalDowns;
    }

    public void setTotalDowns(int totalDowns) {
        this.totalDowns = totalDowns;
    }

    public int getTotalDmg() {
        return totalDmg;
    }

    public void setTotalDmg(int totalDmg) {
        this.totalDmg = totalDmg;
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

    public String getObooners() {
        return obooners;
    }

    public void setObooners(String obooners) {
        this.obooners = obooners;
    }

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

    public String getDefense() {
        return defense;
    }

    public void setDefense(String defense) {
        this.defense = defense;
    }

    public String getEnemySkillDmg() {
        return enemySkillDmg;
    }

    public void setEnemySkillDmg(String enemySkillDmg) {
        this.enemySkillDmg = enemySkillDmg;
    }

    public String getEnemyBreakdown() {
        return enemyBreakdown;
    }

    public void setEnemyBreakdown(String enemyBreakdown) {
        this.enemyBreakdown = enemyBreakdown;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getBursters() {
        return bursters;
    }

    public void setBursters(String bursters) {
        this.bursters = bursters;
    }

    public String getDownsKills() {
        return downsKills;
    }

    public void setDownsKills(String downsKills) {
        this.downsKills = downsKills;
    }

    public String getHealers() {
        return healers;
    }

    public void setHealers(String healers) {
        this.healers = healers;
    }

    public String getArcVersion() {
        return arcVersion;
    }

    public void setArcVersion(String arcVersion) {
        this.arcVersion = arcVersion;
    }

    public String getEiVersion() {
        return eiVersion;
    }

    public void setEiVersion(String eiVersion) {
        this.eiVersion = eiVersion;
    }

    public String getRecordedBy() {
        return recordedBy;
    }

    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }
}
