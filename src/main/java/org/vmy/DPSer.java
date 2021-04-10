package org.vmy;

public class DPSer implements Comparable<DPSer> {
    private String name;
    private String profession;
    private int damage;
    private int dps;

    public DPSer(String name, String profession, int damage, int dps) {
        this.name = name;
        this.profession = profession;
        this.damage = damage;
        this.dps = dps;
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
        return String.format("%-25s",
                String.format("%.18s", name).trim() + " (" + profession.substring(0,3) + ")")
                + String.format("%9s",withSuffix(damage,damage < 1000000 ? 1 : 2)) + " "
                + String.format("%8s",withSuffix(dps,1));
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

    public int getDamage() { return damage; }

    public void setDamage(int damage) { this.damage = damage; }

    public int getDps() {
        return dps;
    }

    public void setDps(int dps) {
        this.dps = dps;
    }
}
