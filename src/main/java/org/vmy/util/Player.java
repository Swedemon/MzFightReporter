package org.vmy.util;

public class Player implements Comparable<Player> {
    private String name;
    private String group;
    private String profession;
    private int kills=0;
    private int deaths=0;

    public Player(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public int compareTo(Player c) {
        if (kills/(deaths==0?1:deaths) == c.kills/(c.deaths==0?1:c.deaths))
            return 0;
        else if (kills/(deaths==0?1:deaths) > c.kills/(c.deaths==0?1:c.deaths))
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%,3d",kills) + String.format("%,3d",deaths);
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

}
