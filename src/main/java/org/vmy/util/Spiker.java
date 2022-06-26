package org.vmy.util;

import java.util.List;

public class Spiker implements Comparable<Spiker> {
    private String name;
    private String profession;
    private int spike2s = 0;
    private int spike4s = 0;

    public Spiker(String name, String profession, int spike2s, int spike4s) {
        this.name = name;
        this.profession = profession;
        this.spike2s = spike2s;
        this.spike4s = spike4s;
    }

    public Spiker(String name, String profession, List<Object> dmgList) {
        this.name = name;
        this.profession = profession;

        for (int q=0; q < dmgList.size(); q++) {
            int before2s = q>1?(int)dmgList.get(q-2):q>0?(int)dmgList.get(q-1):0;
            int dmg2s = (int)dmgList.get(q) - before2s;
            spike2s = dmg2s > spike2s ? dmg2s : spike2s;
            int before4s = q>3?(int)dmgList.get(q-4):q>2?(int)dmgList.get(q-3):q>1?(int)dmgList.get(q-2):q>0?(int)dmgList.get(q-1):0;
            int dmg4sBack = (int)dmgList.get(q) - before4s;
            spike4s = dmg4sBack > spike4s ? dmg4sBack : spike4s;
            int debug12341=0;
        }
    }

    public int compareTo(Spiker d) {
        if (spike2s==d.spike2s)
            return 0;
        else if (spike2s>d.spike2s)
            return -1;
        else
            return 1;
    }

    public String toString() {
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,4) + ")")
                + String.format("%9s",withSuffix(spike2s,1)) + " "
                + String.format("%8s",withSuffix(spike4s,1));
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

    public int getSpike2s() { return spike2s; }

    public void setSpike2s(int damage) { this.spike2s = spike2s; }

    public int getSpike4s() {
        return spike4s;
    }

    public void setSpike4s(int dps) {
        this.spike4s = spike4s;
    }
}
