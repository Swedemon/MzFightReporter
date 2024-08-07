package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

public class Healer implements Comparable<Healer> {
    private String name;
    private String profession;
    private int healing;
    private int barrier;
    private int downedHealing;
    private int total;

    public Healer(String name, String profession, int healing, int barrier, int downedHealing) {
        this.name = name;
        this.profession = profession;
        this.healing = healing;
        this.barrier = barrier;
        this.downedHealing = downedHealing;
        this.total = healing + barrier;
    }

    public int compareTo(Healer c) {
       if (total==c.total)
            return 0;
        else if (total>c.total)
            return -1;
        else
            return 1;
    }

    public String toString() {
        return toHealerString();
    }

    public String toHealerString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 13 : 18;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(healing,healing < 1000000 ? 0 : healing >= 10000000 ? 1 : 2));
    }

    public String toBarrierString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 13 : 18;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(barrier,barrier < 1000000 ? 0 : barrier >= 10000000 ? 1 : 2));
    }

    public String toDownedHealerString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 13 : 18;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(downedHealing,downedHealing < 1000000 ? 0 : downedHealing >= 10000000 ? 1 : 2));
    }

    public static String withSuffix(long count, int decimals) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%."+decimals+"f%c",
                count / Math.pow(1000, exp),
                "kmbtqQ".charAt(exp-1));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public int getHealing() {
        return healing;
    }

    public void setHealing(int healing) {
        this.healing = healing;
        this.total = healing + barrier;
    }

    public int getBarrier() {
        return barrier;
    }

    public void setBarrier(int barrier) {
        this.barrier = barrier;
        this.total = healing + barrier;
    }

    public int getDownedHealing() {
        return downedHealing;
    }

    public void setDownedHealing(int downedHealing) {
        this.downedHealing = downedHealing;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
