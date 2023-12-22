package org.vmy.util;

public class EnemyProfession implements Comparable<EnemyProfession> {
    private String profession;
    private int count;
    private int damage;
    private int condiDamage;

    public EnemyProfession(String profession, int damage, int condiDamage) {
        this.profession = profession;
        this.count = 1;
        this.damage = damage;
        this.condiDamage = condiDamage;
    }

    public void addEnemy(int damage, int condiDamage) {
        this.count++;
        this.damage += damage;
        this.condiDamage += condiDamage;
    }

    public int compareTo(EnemyProfession d) {
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
        return String.format("%3d", count) + "   "
                + String.format("%-15s", profession)
                + String.format("%6s",withSuffix(damage,damage < 1000000 ? 1 : 2)) + " "
                + String.format("%7s",withSuffix(condiDamage,condiDamage < 1000000 ? 1 : 2));
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

    public int getCondiDamage() {
        return condiDamage;
    }

    public void setCondiDamage(int condiDamage) {
        this.condiDamage = condiDamage;
    }
}
