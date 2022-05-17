package org.vmy.util;

import java.math.BigDecimal;

public class Condier {
    private String name;
    private String profession;
    private int stunCount=0;
    private BigDecimal stunDur=new BigDecimal(0);
    private int knockdownCount=0;
    private BigDecimal knockdownDur=new BigDecimal(0);
    private int pullCount=0;
    private BigDecimal pullDur=new BigDecimal(0);
    private int floatCount=0;
    private BigDecimal floatDur=new BigDecimal(0);
    private int sinkCount=0;
    private BigDecimal sinkDur=new BigDecimal(0);
    private int tauntCount=0;
    private BigDecimal tauntDur=new BigDecimal(0);
    private int chilledCount=0;
    private BigDecimal chilledDur=new BigDecimal(0);
    private int crippledCount=0;
    private BigDecimal crippledDur=new BigDecimal(0);
    private int immobCount=0;
    private BigDecimal immobDur=new BigDecimal(0);
    private int slowCount=0;
    private BigDecimal slowDur=new BigDecimal(0);
    private int totalCount=0;
    private BigDecimal totalDur=new BigDecimal(0);

    public Condier(String name, String profession) {
        this.name = name;
        this.profession = profession;
    }

    public void computeTotals() {
        totalCount = stunCount+knockdownCount+pullCount+floatCount+sinkCount+tauntCount+chilledCount+crippledCount+immobCount+slowCount;
        totalDur = stunDur.add(knockdownDur).add(pullDur).add(floatDur).add(sinkDur).add(tauntDur).add(chilledDur).add(crippledDur).add(immobDur).add(slowDur);
    }

    public int compareTo(Condier c) {

        if (stunCount+immobCount+.1*(chilledCount+crippledCount) == c.stunCount+c.immobCount+.1*(c.chilledCount+c.crippledCount))
            return 0;
        else if (stunCount+immobCount+.1*(chilledCount+crippledCount) > c.stunCount+c.immobCount+.1*(c.chilledCount+c.crippledCount))
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%3s",stunCount) + " "
                + String.format("%3s",immobCount) + " "
                + String.format("%3s",chilledCount) + " "
                + String.format("%3s",crippledCount);
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

    public int getStunCount() {
        return stunCount;
    }

    public void setStunCount(int stunCount) {
        this.stunCount = stunCount;
    }

    public BigDecimal getStunDur() {
        return stunDur;
    }

    public void setStunDur(BigDecimal stunDur) {
        this.stunDur = stunDur;
    }

    public int getKnockdownCount() {
        return knockdownCount;
    }

    public void setKnockdownCount(int knockdownCount) {
        this.knockdownCount = knockdownCount;
    }

    public int getPullCount() {
        return pullCount;
    }

    public void setPullCount(int pullCount) {
        this.pullCount = pullCount;
    }

    public BigDecimal getPullDur() {
        return pullDur;
    }

    public void setPullDur(BigDecimal pullDur) {
        this.pullDur = pullDur;
    }

    public int getFloatCount() {
        return floatCount;
    }

    public void setFloatCount(int floatCount) {
        this.floatCount = floatCount;
    }

    public BigDecimal getFloatDur() {
        return floatDur;
    }

    public void setFloatDur(BigDecimal floatDur) {
        this.floatDur = floatDur;
    }

    public int getSinkCount() {
        return sinkCount;
    }

    public void setSinkCount(int sinkCount) {
        this.sinkCount = sinkCount;
    }

    public BigDecimal getSinkDur() {
        return sinkDur;
    }

    public void setSinkDur(BigDecimal sinkDur) {
        this.sinkDur = sinkDur;
    }

    public int getTauntCount() {
        return tauntCount;
    }

    public void setTauntCount(int tauntCount) {
        this.tauntCount = tauntCount;
    }

    public BigDecimal getTauntDur() {
        return tauntDur;
    }

    public void setTauntDur(BigDecimal tauntDur) {
        this.tauntDur = tauntDur;
    }

    public int getChilledCount() {
        return chilledCount;
    }

    public void setChilledCount(int chilledCount) {
        this.chilledCount = chilledCount;
    }

    public BigDecimal getChilledDur() {
        return chilledDur;
    }

    public void setChilledDur(BigDecimal chilledDur) {
        this.chilledDur = chilledDur;
    }

    public int getCrippledCount() {
        return crippledCount;
    }

    public void setCrippledCount(int crippledCount) {
        this.crippledCount = crippledCount;
    }

    public BigDecimal getCrippledDur() {
        return crippledDur;
    }

    public void setCrippledDur(BigDecimal crippledDur) {
        this.crippledDur = crippledDur;
    }

    public int getSlowCount() {
        return slowCount;
    }

    public void setSlowCount(int slowCount) {
        this.slowCount = slowCount;
    }

    public BigDecimal getSlowDur() {
        return slowDur;
    }

    public void setSlowDur(BigDecimal slowDur) {
        this.slowDur = slowDur;
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

}
