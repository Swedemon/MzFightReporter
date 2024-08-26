package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Burster implements Comparable<Burster> {
    private String name;
    private String profession;
    private int startTime;
    private int burst2s = 0;
    private int burst4s = 0;

    public Burster(String name, String profession, int startTime, int burst2s, int burst4s) {
        this.name = name;
        this.profession = profession;
        this.startTime = startTime;
        this.burst2s = burst2s;
        this.burst4s = burst4s;
    }

    public static void computeTop10(String name, String profession, List<Burster> top10, List<Object> dmgList) {
        List<Burster> sList = new ArrayList<>();
        HashMap<Integer, Burster> sMap = new HashMap<>();
        for (int q=4; q < dmgList.size(); q++) {
            int dmg4s = (int)dmgList.get(q) - (int)dmgList.get(q-4);
            int dmg2s = 0;
            for (int x=q-4; x<q-1; x++) {
                int dmg2sNext = (int)dmgList.get(x+2) - (int)dmgList.get(x);
                if (dmg2sNext > dmg2s)
                    dmg2s = dmg2sNext;
            }
            Burster burster = new Burster(name, profession, q-4, dmg2s, dmg4s);
            sList.add(burster);
            sMap.put(q-4, burster);
        }
        Collections.sort(sList);
        for (int x=0; x < sList.size(); x++) {
            if (sList.get(x).getBurst4s() > 0) {
                int q = sList.get(x).getStartTime();
                if (sMap.containsKey(q+1))
                    sMap.get(q+1).setBurst4s(0);
                if (sMap.containsKey(q+2))
                    sMap.get(q+2).setBurst4s(0);
                if (sMap.containsKey(q+3))
                    sMap.get(q+3).setBurst4s(0);
                if (sMap.containsKey(q-1))
                    sMap.get(q-1).setBurst4s(0);
                if (sMap.containsKey(q-2))
                    sMap.get(q-2).setBurst4s(0);
                if (sMap.containsKey(q-3))
                    sMap.get(q-3).setBurst4s(0);
            }
        }
        sList = sList.stream().filter(sp -> sp.getBurst4s()>0).collect(Collectors.toList());
        sList.addAll(top10);
        Collections.sort(sList);
        top10.clear();
        top10.addAll(sList.subList(0, Math.min(sList.size(), 10)));
    }

    public int compareTo(Burster d) {
        if (burst4s ==d.burst4s) {
            return -Integer.compare(burst2s, d.burst2s);
        }
        else
            return -Integer.compare(burst4s, d.burst4s);
    }

    public String toString() {
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 9 : 14;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%5s",withSuffix(burst4s, burst4s < 1000000 ? 0 : burst4s >= 10000000 ? 1 : 2))
                + String.format("%5s",withSuffix(burst2s, burst2s < 1000000 ? 0 : burst2s >= 10000000 ? 1 : 2))
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

    public int getBurst2s() { return burst2s; }

    public void setBurst2s(int burst2s) { this.burst2s = burst2s; }

    public int getBurst4s() {
        return burst4s;
    }

    public void setBurst4s(int burst4s) {
        this.burst4s = burst4s;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }
}
