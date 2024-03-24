package org.vmy.util;

import org.apache.commons.lang.StringUtils;

public class EnemyBreakdown implements Comparable<EnemyBreakdown> {
    private String team;
    private String profession;
    private int count;
    private int damage;

    public EnemyBreakdown(Enemy e) {
        this.team = e.getTeam();
        this.profession = e.getProfession();
        this.count = 1;
        this.damage = e.getDamage();
    }

    public void addEnemy(Enemy e) {
        this.count++;
        this.damage += e.getDamage();
    }

    public int compareTo(EnemyBreakdown d) {
        if (count > d.count)
            return -1;
        if (count < d.count)
            return 1;
        if (damage > d.damage)
            return -1;
        if (damage < d.damage)
            return 1;
        return 0;
    }

    public String toString() {
        return String.format("%2d", count) + "  "
                + String.format("%-9s", StringUtils.left(profession, 8))
                + String.format("%6s",withSuffix(damage,damage < 1000000 ? 1 : 2));
    }

    public static String withSuffix(long count, int decimals) {
        if (count < 1000) return "" + count;
        int exp = (int) (Math.log(count) / Math.log(1000));
        return String.format("%."+decimals+"f%c",
                count / Math.pow(1000, exp),
                "kmbtqQ".charAt(exp-1));
    }

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
