package org.vmy.util;

import java.util.List;

public class DPSer implements Comparable<DPSer> {
    private String name;
    private String profession;
    private String group;
    private int damage;
    private int dps;
    private int onDowns;

    public DPSer(String name, String profession, String group, List<Object> dmgList) {
        this.name = name;
        this.profession = profession;
        this.group = group;
        int size = dmgList.size();
        damage = (int) dmgList.get(size - 1);
        int firstDmg = 0;
        int lastDmg = 0;
        int prevDmg = 0;
        for (int i=0; i < size; i++) {
            int currDmg = (int) dmgList.get(i);
            if (currDmg > 0) {
                if (currDmg > prevDmg)
                    lastDmg = i;
                if (firstDmg == 0)
                    firstDmg = i;
            }
            prevDmg = currDmg;
        }
        int dmgLength = lastDmg - firstDmg + 1;
        dmgLength = dmgLength==0 ? 1 : dmgLength;
        dps = (int) damage / dmgLength;
    }

    public int compareTo(DPSer d) {
        if (damage==d.damage)
            return 0;
        else if (damage>d.damage)
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-23s",
                String.format("%.16s", name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%7s",withSuffix(damage,damage < 1000000 ? 0 : 2)) + " "
                + String.format("%5s",withSuffix(dps,1)) + " "
                + String.format("%6s",withSuffix(onDowns,0));
    }

    public static String withSuffix(long count, int decimals) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%."+decimals+"f%c",
                count / Math.pow(1000, exp),
                "kmbtqQ".charAt(exp-1));
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getProfession() { return profession; }

    public void setProfession(String profession) { this.profession = profession; }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getDamage() { return damage; }

    public void setDamage(int damage) { this.damage = damage; }

    public int getDps() {
        return dps;
    }

    public void setDps(int dps) {
        this.dps = dps;
    }

    public int getOnDowns() {
        return onDowns;
    }

    public void setOnDowns(int onDowns) {
        this.onDowns = onDowns;
    }
}
