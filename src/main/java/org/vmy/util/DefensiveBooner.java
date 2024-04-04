package org.vmy.util;

public class DefensiveBooner implements Comparable<DefensiveBooner> {
    private String name;
    private String profession;
    private String group;
    private int stability=0;
    private int aegis=0;
    private int protection=0;
    private int resistance=0;
    private int resolution=0;
    private int alacrity=0;
    private int quickness=0;
    private int defensiveRating=0;

    public DefensiveBooner(String group) {
        this.group = group;
    }

    public DefensiveBooner(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public DefensiveBooner(String name, String profession, String group, int stability, int aegis, int protection, int resistance, int resolution, int alacrity, int quickness) {
        this.name = name;
        this.profession = profession;
        this.group = group;
        this.stability = stability;
        this.aegis = aegis;
        this.protection = protection;
        this.resistance = resistance;
        this.resolution = resolution;
        this.alacrity = alacrity;
        this.quickness = quickness;
        computeRating();
    }

    public void computeRating() {
        defensiveRating = 5*stability + 3*aegis + 2*resistance + 2*protection + 2*alacrity + 2*quickness + resolution;
    }

    public int compareTo(DefensiveBooner d) {
        if (group.equals(d.group))
            return 0;
        else if (Integer.parseInt(group) < Integer.parseInt(d.group))
            return -1;
        else
            return 1;
    }

    public String toString() {
        if (name == null) {
            return String.format("%3s", group)
                    + String.format("%7s", defensiveRating);
        } else {
            return String.format("%-25s",
                    String.format("%.18s", name).trim() + " (" + profession.substring(0, 4) + ")")
                    + String.format("%7s", defensiveRating);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public int getStability() {
        return stability;
    }

    public void setStability(int stability) {
        this.stability = stability;
    }

    public int getAegis() {
        return aegis;
    }

    public void setAegis(int aegis) {
        this.aegis = aegis;
    }

    public int getProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public int getResistance() {
        return resistance;
    }

    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }

    public int getDefensiveRating() {
        return defensiveRating;
    }

    public void setDefensiveRating(int defensiveRating) {
        this.defensiveRating = defensiveRating;
    }

    public int getAlacrity() {
        return alacrity;
    }

    public void setAlacrity(int alacrity) {
        this.alacrity = alacrity;
    }

    public int getQuickness() {
        return quickness;
    }

    public void setQuickness(int quickness) {
        this.quickness = quickness;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
