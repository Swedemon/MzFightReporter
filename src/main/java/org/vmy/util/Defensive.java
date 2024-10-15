package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

public class Defensive implements Comparable<Defensive> {
    private String name;
    private String profession;
    private String group;
    private int blocked=0;
    private int evaded=0;
    private int missed=0;
    private int invulned=0;
    private int dmgBarrier=0;
    private int dmgTaken=0;
    private int downed=0;
    private int dead=0;
    private int cced=0;
    private int defensiveScore=0;

    public Defensive() {
    }

    public Defensive(String name, String profession, String group) {
        this.name = name;
        this.profession = profession;
        this.group = group;
    }

    public Defensive(String name, String profession, String group, int blocked, int evaded, int missed, int invulned,
                     int dmgBarrier, int dmgTaken, int downed, int dead, int cced) {
        this.name = name;
        this.profession = profession;
        this.group = group;
        this.blocked = blocked;
        this.evaded = evaded;
        this.missed = missed;
        this.invulned = invulned;
        this.dmgBarrier = dmgBarrier;
        this.dmgTaken = dmgTaken;
        this.downed = downed;
        this.dead = dead;
        this.cced = cced;
        computeScore();
    }

    public void computeScore() {
        defensiveScore = blocked + evaded + invulned - 50*downed - 50*dead;
    }

    public int compareTo(Defensive c) {
        return Integer.compare(c.defensiveScore, this.defensiveScore);
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 8 : 13;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%5s", invulned)
                + String.format("%6s", evaded)
                + String.format("%6s", blocked);
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getBlocked() {
        return blocked;
    }

    public void setBlocked(int blocked) {
        this.blocked = blocked;
    }

    public int getEvaded() {
        return evaded;
    }

    public void setEvaded(int evaded) {
        this.evaded = evaded;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public int getInvulned() {
        return invulned;
    }

    public void setInvulned(int invulned) {
        this.invulned = invulned;
    }

    public int getDmgBarrier() {
        return dmgBarrier;
    }

    public void setDmgBarrier(int dmgBarrier) {
        this.dmgBarrier = dmgBarrier;
    }

    public int getDmgTaken() {
        return dmgTaken;
    }

    public void setDmgTaken(int dmgTaken) {
        this.dmgTaken = dmgTaken;
    }

    public int getDowned() {
        return downed;
    }

    public void setDowned(int downed) {
        this.downed = downed;
    }

    public int getDead() {
        return dead;
    }

    public void setDead(int dead) {
        this.dead = dead;
    }

    public int getCced() {
        return cced;
    }

    public void setCced(int cced) {
        this.cced = cced;
    }

    public int getDefensiveScore() {
        return defensiveScore;
    }

    public void setDefensiveScore(int defensiveScore) {
        this.defensiveScore = defensiveScore;
    }
}
