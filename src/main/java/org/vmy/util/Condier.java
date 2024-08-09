package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

import java.math.BigDecimal;

public class Condier implements Comparable<Condier> {
    private String name;
    private String profession;
    private int interruptCount=0;
    private int hardCcCount=0;
    private BigDecimal hardCcDur=new BigDecimal(0);
    private int immobCount=0;
    private BigDecimal immobDur=new BigDecimal(0);
    private int softCcCount=0;
    private BigDecimal softCcDur=new BigDecimal(0);
    private int dazeCount=0;
    private BigDecimal dazeDur=new BigDecimal(0);
    private int chillCount =0;
    private BigDecimal chillDur =new BigDecimal(0);
    private int cripplCount =0;
    private BigDecimal cripplDur =new BigDecimal(0);
    private int slowCount=0;
    private BigDecimal slowDur=new BigDecimal(0);
    private int blindCount=0;
    private BigDecimal blindDur=new BigDecimal(0);
    private int weaknessCount=0;
    private BigDecimal weaknessDur=new BigDecimal(0);

    public Condier(String name, String profession) {
        this.name = name;
        this.profession = profession;
    }

    public int compareTo(Condier c) {
        double netScore = hardCcCount+immobCount+.5*interruptCount+.1*softCcCount;
        double c_netScore = c.hardCcCount+c.immobCount+.5*c.interruptCount+.1*c.softCcCount;
        return Double.compare(c_netScore, netScore);
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 5 : 10;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%4s",hardCcCount)
                + String.format("%5s", softCcCount)
                + String.format("%6s",immobCount)
                + String.format("%5s",interruptCount);
    }

    private int sumSoftCcCount() {
        return dazeCount + chillCount + cripplCount + slowCount + blindCount + weaknessCount;
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

    public int getInterruptCount() {
        return interruptCount;
    }

    public void setInterruptCount(int interruptCount) {
        this.interruptCount = interruptCount;
    }

    public int getHardCcCount() {
        return hardCcCount;
    }

    public void setHardCcCount(int hardCcCount) {
        this.hardCcCount = hardCcCount;
    }

    public BigDecimal getHardCcDur() {
        return hardCcDur;
    }

    public void setHardCcDur(BigDecimal hardCcDur) {
        this.hardCcDur = hardCcDur;
    }

    public int getImmobCount() {
        return immobCount;
    }

    public void setImmobCount(int immobCount) {
        this.immobCount = immobCount;
    }

    public BigDecimal getImmobDur() {
        return immobDur;
    }

    public void setImmobDur(BigDecimal immobDur) {
        this.immobDur = immobDur;
    }

    public int getSoftCcCount() {
        return softCcCount;
    }

    public void setSoftCcCount(int softCcCount) {
        this.softCcCount = softCcCount;
    }

    public BigDecimal getSoftCcDur() {
        return softCcDur;
    }

    public void setSoftCcDur(BigDecimal softCcDur) {
        this.softCcDur = softCcDur;
    }

    public int getDazeCount() {
        return dazeCount;
    }

    public void setDazeCount(int dazeCount) {
        this.dazeCount = dazeCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getDazeDur() {
        return dazeDur;
    }

    public void setDazeDur(BigDecimal dazeDur) {
        this.dazeDur = dazeDur;
    }

    public int getChillCount() {
        return chillCount;
    }

    public void setChillCount(int chillCount) {
        this.chillCount = chillCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getChillDur() {
        return chillDur;
    }

    public void setChillDur(BigDecimal chillDur) {
        this.chillDur = chillDur;
    }

    public int getCripplCount() {
        return cripplCount;
    }

    public void setCripplCount(int cripplCount) {
        this.cripplCount = cripplCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getCripplDur() {
        return cripplDur;
    }

    public void setCripplDur(BigDecimal cripplDur) {
        this.cripplDur = cripplDur;
    }

    public int getSlowCount() {
        return slowCount;
    }

    public void setSlowCount(int slowCount) {
        this.slowCount = slowCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getSlowDur() {
        return slowDur;
    }

    public void setSlowDur(BigDecimal slowDur) {
        this.slowDur = slowDur;
    }

    public int getBlindCount() {
        return blindCount;
    }

    public void setBlindCount(int blindCount) {
        this.blindCount = blindCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getBlindDur() {
        return blindDur;
    }

    public void setBlindDur(BigDecimal blindDur) {
        this.blindDur = blindDur;
    }

    public int getWeaknessCount() {
        return weaknessCount;
    }

    public void setWeaknessCount(int weaknessCount) {
        this.weaknessCount = weaknessCount;
        this.softCcCount = sumSoftCcCount();
    }

    public BigDecimal getWeaknessDur() {
        return weaknessDur;
    }

    public void setWeaknessDur(BigDecimal weaknessDur) {
        this.weaknessDur = weaknessDur;
    }
}
