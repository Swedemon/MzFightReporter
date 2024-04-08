package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

public class Stripper implements Comparable<Stripper> {
    private String name;
    private String profession;
    private int strips;

    public Stripper(String name, String profession, int strips) {
        this.name = name;
        this.profession = profession;
        this.strips = strips;
    }

    public int compareTo(Stripper s) {
        if (strips==s.strips)
            return 0;
        else if (strips>s.strips)
            return -1;
        else
            return 1;
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 14 : 19;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + StringUtils.leftPad(strips+"", 5);
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public int getStrips() { return strips; }

    public void setStrips(int strips) { this.strips = strips; }
}
