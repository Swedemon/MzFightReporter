package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Spiker implements Comparable<Spiker> {
    private String name;
    private String profession;
    private int startTime;
    private int spike2s = 0;
    private int spike4s = 0;

    public Spiker(String name, String profession, int startTime, int spike2s, int spike4s) {
        this.name = name;
        this.profession = profession;
        this.startTime = startTime;
        this.spike2s = spike2s;
        this.spike4s = spike4s;
    }

    public static void computeTop10(String name, String profession, List<Spiker> top10, List<Object> dmgList) {
        List<Spiker> sList = new ArrayList<>();
        HashMap<Integer, Spiker> sMap = new HashMap<>();
        for (int q=4; q < dmgList.size(); q++) {
            int dmg4s = (int)dmgList.get(q) - (int)dmgList.get(q-4);
            int dmg2s = 0;
            for (int x=q-4; x<q-1; x++) {
                int dmg2sNext = (int)dmgList.get(x+2) - (int)dmgList.get(x);
                if (dmg2sNext > dmg2s)
                    dmg2s = dmg2sNext;
            }
            Spiker spiker = new Spiker(name, profession, q-4, dmg2s, dmg4s);
            sList.add(spiker);
            sMap.put(q-4, spiker);
        }
        Collections.sort(sList);
        for (int x=0; x < sList.size(); x++) {
            if (sList.get(x).getSpike4s() > 0) {
                int q = sList.get(x).getStartTime();
                if (sMap.containsKey(q+1))
                    sMap.get(q+1).setSpike4s(0);
                if (sMap.containsKey(q+2))
                    sMap.get(q+2).setSpike4s(0);
                if (sMap.containsKey(q+3))
                    sMap.get(q+3).setSpike4s(0);
                if (sMap.containsKey(q-1))
                    sMap.get(q-1).setSpike4s(0);
                if (sMap.containsKey(q-2))
                    sMap.get(q-2).setSpike4s(0);
                if (sMap.containsKey(q-3))
                    sMap.get(q-3).setSpike4s(0);
            }
        }
        sList = sList.stream().filter(sp -> sp.getSpike4s()>0).collect(Collectors.toList());
        sList.addAll(top10);
        Collections.sort(sList);
        top10.clear();
        top10.addAll(sList.subList(0, Math.min(sList.size(), 10)));
    }

    public int compareTo(Spiker d) {
        if (spike4s==d.spike4s) {
            return -Integer.compare(spike2s, d.spike2s);
        }
        else
            return -Integer.compare(spike4s, d.spike4s);
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 9 : 14;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%5s",withSuffix(spike4s,spike4s < 1000000 ? 0 : spike4s >= 10000000 ? 1 : 2))
                + String.format("%5s",withSuffix(spike2s,spike2s < 1000000 ? 0 : spike2s >= 10000000 ? 1 : 2))
                + String.format("%6s",String.format("%s",startTime/60)+":"+String.format("%02d",startTime%60));
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

    public void setSpike2s(int spike2s) { this.spike2s = spike2s; }

    public int getSpike4s() {
        return spike4s;
    }

    public void setSpike4s(int spike4s) {
        this.spike4s = spike4s;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
