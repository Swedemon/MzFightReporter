package org.vmy;

public class Cleanser implements Comparable<Cleanser> {
    private String name;
    private String profession;
    private int cleanses;

    public Cleanser(String name, String profession, int cleanses) {
        this.name = name;
        this.profession = profession;
        this.cleanses = cleanses;
    }

    public int compareTo(Cleanser c) {
        if (cleanses==c.cleanses)
            return 0;
        else if (cleanses>c.cleanses)
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,3) + ")")
                + String.format("%,7d",cleanses);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public int getCleanses() { return cleanses; }

    public void setCleanses(int cleanses) {
        this.cleanses = cleanses;
    }
}
