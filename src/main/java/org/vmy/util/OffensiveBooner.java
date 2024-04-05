package org.vmy.util;

public class OffensiveBooner implements Comparable<OffensiveBooner> {
    private String name;
    private String profession;
    private String group;
    private int might=0;
    private int fury=0;
    private int alacrity=0;
    private int quickness=0;
    private int vigor;
    private int offensiveRating=0;
    private int downCn;
    private int downs;
    private int kills;

    public OffensiveBooner(String group) {
        this.group = group;
    }

    public OffensiveBooner(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public void computeRating() {
        offensiveRating = 10*might + fury + 2*alacrity + 2*quickness + (vigor/5);
    }

    public int compareTo(OffensiveBooner d) {
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
                    + String.format("%7s", offensiveRating);
        } else {
            return String.format("%-25s",
                    String.format("%.18s", name).trim() + " (" + profession.substring(0, 4) + ")")
                    + String.format("%7s", offensiveRating);
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

    public int getMight() {
        return might;
    }

    public void setMight(int might) {
        this.might = might;
    }

    public int getFury() {
        return fury;
    }

    public void setFury(int fury) {
        this.fury = fury;
    }

    public int getOffensiveRating() {
        return offensiveRating;
    }

    public void setOffensiveRating(int offensiveRating) {
        this.offensiveRating = offensiveRating;
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

    public int getVigor() {
        return vigor;
    }

    public void setVigor(int vigor) {
        this.vigor = vigor;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getDownCn() {
        return downCn;
    }

    public void setDownCn(int downCn) {
        this.downCn = downCn;
    }

    public int getDowns() {
        return downs;
    }

    public void setDowns(int downs) {
        this.downs = downs;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }
}
