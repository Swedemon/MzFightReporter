package org.vmy.util;

public class OffensiveBooner implements Comparable<OffensiveBooner> {
    private String name;
    private String profession;
    private String group;
    private int might=0;
    private int fury=0;
    private int alacrity=0;
    private int quickness=0;
    private int offensiveRating=0;

    public OffensiveBooner(String group) {
        this.group = group;
    }

    public OffensiveBooner(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public OffensiveBooner(String name, String profession, String group, int might, int fury, int alacrity, int quickness) {
        this.name = name;
        this.profession = profession;
        this.group = group;
        this.might = might;
        this.fury = fury;
        this.alacrity = alacrity;
        this.quickness = quickness;
        computeRating();
    }

    public void computeRating() {
        offensiveRating = 10*might + fury + 2*alacrity + 2*quickness;
    }

    public int compareTo(OffensiveBooner d) {
        if (offensiveRating==d.offensiveRating)
            return 0;
        else if (offensiveRating>d.offensiveRating)
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

}
