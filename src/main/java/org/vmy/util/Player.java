package org.vmy.util;

import java.util.Objects;

public class Player implements Comparable<Player> {
    private String name;
    private String group;
    private String profession;
    private int kills=0;
    private int deaths=0;
    private int downsOut=0;
    private int downsIn=0;

    public Player(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public int compareTo(Player c) {
        double killFactor = 1;
        if ((kills*killFactor+downsOut) == (c.kills*killFactor+c.downsOut))
            return Objects.compare(kills, c.kills, Integer::compareTo);
        else if ((kills*killFactor+downsOut) > (c.kills*killFactor+c.downsOut))
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%,4d",downsOut) + String.format("     %,3d",kills);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getDownsOut() {
        return downsOut;
    }

    public void setDownsOut(int downsOut) {
        this.downsOut = downsOut;
    }

    public int getDownsIn() {
        return downsIn;
    }

    public void setDownsIn(int downsIn) {
        this.downsIn = downsIn;
    }
}
