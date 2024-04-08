package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

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
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 14 : 19;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + StringUtils.leftPad(String.valueOf(cleanses), 5);
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
