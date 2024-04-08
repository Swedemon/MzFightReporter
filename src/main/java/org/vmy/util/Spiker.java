package org.vmy.util;

import org.apache.commons.lang.StringUtils;
import org.vmy.Parameters;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Spiker> sList = new ArrayList<Spiker>();
        for (int q=0; q < dmgList.size(); q++) {
            int before2s = q>1?(int)dmgList.get(q-2):q>0?(int)dmgList.get(q-1):0;
            int after2s = (int)dmgList.get(q);
            int dmg2s = after2s - before2s;
            //int before4s = q>2?(int)dmgList.get(q-3):q>1?(int)dmgList.get(q-2):q>0?(int)dmgList.get(q-1):0;
            int after4s = q+2<dmgList.size()?(int)dmgList.get(q+2):q+1<dmgList.size()?(int)dmgList.get(q+1):(int)dmgList.get(q);
            int dmg4s = after4s - before2s;
            if (q > 0) {
                Spiker prevSpike = sList.get(q-1);
                if (prevSpike.getSpike2s() < dmg2s)
                    prevSpike.setSpike2s(0); //omit by setting to zero
                else
                    dmg2s = 0; //omit current
            }
            sList.add(new Spiker(name, profession, q, dmg2s, dmg4s));
        }
        sList = sList.stream().filter(sp -> sp.getSpike2s()>0).collect(Collectors.toList());
        sList.addAll(top10);
        Collections.sort(sList);
        top10.clear();
        top10.addAll(sList.subList(0, sList.size() < 10 ? sList.size() : 10));
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
        int playerLength = Parameters.getInstance().enableDiscordMobileMode ? 9 : 14;
        return StringUtils.rightPad( StringUtils.left(name, playerLength), playerLength) + " " + DPSer.mapProf(profession.substring(0,4))
                + String.format("%5s",withSuffix(spike2s,spike2s < 1000000 ? 0 : spike2s >= 10000000 ? 1 : 2))
                + String.format("%5s",withSuffix(spike4s,spike4s < 1000000 ? 0 : spike2s >= 10000000 ? 1 : 2))
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
