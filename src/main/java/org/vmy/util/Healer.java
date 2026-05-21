package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

public class Healer implements Comparable<Healer> {
    private String name;
    private String profession;
    private int totalHealing;
    private int outgoingHealing;
    private int totalBarrier;
    private int outgoingBarrier;
    private int downedHealing;
    private int total;

    public Healer(String name, String profession, int totalHealing, int outgoingHealing, int totalBarrier, int outgoingBarrier, int downedHealing) {
        this.name = name;
        this.profession = profession;
        this.totalHealing = totalHealing;
        this.outgoingHealing = outgoingHealing;
        this.totalBarrier = totalBarrier;
        this.outgoingBarrier = outgoingBarrier;
        this.downedHealing = downedHealing;
        this.total = totalHealing + totalBarrier;
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
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 7 : 12;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(totalHealing,totalHealing < 1000000 ? 0 : totalHealing >= 10000000 ? 1 : 2))
                + String.format("%6s",withSuffix(outgoingHealing,outgoingHealing < 1000000 ? 0 : outgoingHealing >= 10000000 ? 1 : 2));
    }

    public String toBarrierString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 7 : 12;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(totalBarrier,totalBarrier < 1000000 ? 0 : totalBarrier >= 10000000 ? 1 : 2))
                + String.format("%6s",withSuffix(outgoingBarrier,outgoingBarrier < 1000000 ? 0 : outgoingBarrier >= 10000000 ? 1 : 2));
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

    public int getTotalHealing() {
        return totalHealing;
    }

    public void setTotalHealing(int healing) {
        this.totalHealing = healing;
        this.total = healing + totalBarrier;
    }

    public int getTotalBarrier() {
        return totalBarrier;
    }

    public void setTotalBarrier(int barrier) {
        this.totalBarrier = barrier;
        this.total = totalHealing + barrier;
    }

    public int getOutgoingHealing() {
        return outgoingHealing;
    }

    public void setOutgoingHealing(int healing) {
        this.outgoingHealing = healing;
    }

    public int getOutgoingBarrier() {
        return outgoingBarrier;
    }

    public void setOutgoingBarrier(int barrier) {
        this.outgoingBarrier = barrier;
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
