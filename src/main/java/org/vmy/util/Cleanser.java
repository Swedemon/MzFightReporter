package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

public class Cleanser implements Comparable<Cleanser> {
    private String name;
    private String profession;
    private int totalCleanses;
    private int outgoingCleanses;

    public Cleanser(String name, String profession, int totalCleanses, int outgoingCleanses) {
        this.name = name;
        this.profession = profession;
        this.totalCleanses = totalCleanses;
        this.outgoingCleanses = outgoingCleanses;
    }

    public int compareTo(Cleanser c) {
        if (totalCleanses==c.totalCleanses)
            return 0;
        else if (totalCleanses>c.totalCleanses)
            return -1;
        else
            return 1;
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 10 : 15;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + StringUtils.leftPad(String.valueOf(totalCleanses), 5)
                + StringUtils.leftPad(String.valueOf(outgoingCleanses), 5);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public int getTotalCleanses() { return totalCleanses; }

    public void setTotalCleanses(int cleanses) {
        this.totalCleanses = cleanses;
    }

    public int getOutgoingCleanses() { return outgoingCleanses; }

    public void setOutgoingCleanses(int cleanses) {
        this.outgoingCleanses = cleanses;
    }
}
