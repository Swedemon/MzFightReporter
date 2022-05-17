package org.vmy.util;

public class Group implements Comparable<Group> {
    private String name;
    private String number;
    private int kills=0;
    private int deaths=0;

    public Group(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public int compareTo(Group c) {
        if (kills/(deaths==0?1:deaths) == c.kills/(c.deaths==0?1:c.deaths))
            return 0;
        else if (kills/(deaths==0?1:deaths) > c.kills/(c.deaths==0?1:c.deaths))
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", "Team " + name).trim())
                + String.format("%,4d",kills) + " "
                + String.format("%,4d",deaths);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
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
