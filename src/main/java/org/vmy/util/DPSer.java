package org.vmy.util;

import org.apache.commons.lang.StringUtils;

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
        return  StringUtils.rightPad( StringUtils.left(name, 7), 7) + " " + mapProf(profession.substring(0,4))
                + String.format("%6s",withSuffix(damage,damage < 1000000 ? 0 : damage >= 10000000 ? 1 : 2)) + " "
                + String.format("%5s",withSuffix(dps,1)) + " "
                + String.format("%5s",withSuffix(onDowns,0));
    }

    public static String mapProf(String p) {
        if (p == null)
            return "    ";
        String prof = StringUtils.left(p, 4);
        if (prof.equals("Drui"))
            return "DRUI";
        if (prof.equals("Scra"))
            return "SCRA";
        if (prof.equals("Chro"))
            return "CHRO";
        if (prof.equals("Drag"))
            return "DRAG";
        if (prof.equals("Holo"))
            return "HOLO";
        if (prof.equals("Vind"))
            return "VIND";
        if (prof.equals("Reap"))
            return "REAP";
        if (prof.equals("Bers"))
            return "ZERK";
        if (prof.equals("Spel"))
            return "SPEL";
        if (prof.equals("Soul"))
            return "SOUL";
        if (prof.equals("Mira"))
            return "MIRA";
        if (prof.equals("Rene"))
            return "RENE";
        if (prof.equals("Will"))
            return "WILL";
        if (prof.equals("Spec"))
            return "SPEC";
        if (prof.equals("Mesm"))
            return "MESM";
        if (prof.equals("Guar"))
            return "GUAR";
        if (prof.equals("Weav"))
            return "WEAV";
        if (prof.equals("Cata"))
            return "CATA";
        if (prof.equals("Virt"))
            return "VIRT";
        if (prof.equals("Fire"))
            return "FIRE";
        if (prof.equals("Thie"))
            return "THIE";
        if (prof.equals("Rang"))
            return "RANG";
        if (prof.equals("Dead"))
            return "DEAD";
        if (prof.equals("Harb"))
            return "HARB";
        if (prof.equals("Unta"))
            return "UNTA";
        if (prof.equals("Mech"))
            return "MECH";
        if (prof.equals("Engi"))
            return "ENGI";
        if (prof.equals("Elem"))
            return "ELEM";
        if (prof.equals("Scou"))
            return "SCOU";
        if (prof.equals("Dare"))
            return "DARE";
        if (prof.equals("Temp"))
            return "TEMP";
        if (prof.equals("Hera"))
            return "HERA";
        if (prof.equals("Warr"))
            return "WARR";
        if (prof.equals("Reve"))
            return "REVE";
        if (prof.equals("Necr"))
            return "NECR";
        return prof;
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
