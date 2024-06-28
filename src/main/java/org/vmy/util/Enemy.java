package org.vmy.util;

public class Enemy implements Comparable<Enemy> {
    private String name;
    private String profession;
    private String team;
    private int damage;
    private int dps;
    private int downs;
    private int deaths;
    private boolean hasSquadActivity = false;

    public Enemy(String name, String profession, String team, int damage, int dps, int downs, int deaths, boolean hasSquadActivity) {
        this.name = name;
        this.profession = profession;
        this.team = team;
        this.damage = damage;
        this.dps = dps;
        this.downs = downs;
        this.deaths = deaths;
        this.hasSquadActivity = hasSquadActivity;
    }

    public int compareTo(Enemy d) {
        if (damage==d.damage)
            return 0;
        else if (damage>d.damage)
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-23s",
                String.format("%.16s", team + " " + name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%7s",withSuffix(damage,damage < 1000000 ? 0 : damage >= 10000000 ? 1 : 2)) + " "
                + String.format("%5s",withSuffix(dps,1));
    }

    public static String withSuffix(long count, int decimals) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%."+decimals+"f%c",
                count / Math.pow(1000, exp),
                "kmbtqQ".charAt(exp-1));
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getDps() {
        return dps;
    }

    public void setDps(int dps) {
        this.dps = dps;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public boolean isHasSquadActivity() {
        return hasSquadActivity;
    }

    public void setHasSquadActivity(boolean hasSquadActivity) {
        this.hasSquadActivity = hasSquadActivity;
    }
}
