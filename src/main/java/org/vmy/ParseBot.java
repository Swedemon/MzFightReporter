package org.vmy;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.vmy.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.util.*;

public class ParseBot {

    private static final String CRLF = "\n";

    protected static FightReport processWvwJsonLog(File jsonFile, String uploadUrl) throws IOException {
        JSONObject jo = new JSONObject();
        FightReport report = new FightReport();

        InputStream is = new FileInputStream(jsonFile);
        try {

            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject jsonTop = new JSONObject(jsonTxt);

            //targets
            JSONArray targets = jsonTop.getJSONArray("targets");
            HashMap<String, Condier> condiers = new HashMap<>();
            HashMap<String, EnemyProfession> enemies = new HashMap<>();
            int countEnemyPlayers = 0;
            for (int i = 1; i < targets.length(); i++) {
                JSONObject currTarget = targets.getJSONObject(i);
                String name = currTarget.getString("name");
                String profession = name != null && name.indexOf(" ") > 0 ? name.substring(0, name.indexOf(" ")) : null;
                countEnemyPlayers++;
                if (!currTarget.isNull("buffs")) {
                    JSONArray bArray = currTarget.getJSONArray("buffs");
                    populateCondierBuffs(condiers, bArray);
                }
                if (!currTarget.isNull("dpsAll")) {
                    JSONObject enemyDpsAll = currTarget.getJSONArray("dpsAll").getJSONObject(0);
                    populateEnemy(enemies, profession, enemyDpsAll);
                }
            }

            //players
            JSONArray players = jsonTop.getJSONArray("players");
            List<DPSer> dpsers = new ArrayList<DPSer>();
            List<Cleanser> cleansers = new ArrayList<Cleanser>();
            List<Stripper> strippers = new ArrayList<Stripper>();
            List<DefensiveBooner> dbooners = new ArrayList<DefensiveBooner>();
            List<Spiker> spikers = new ArrayList<Spiker>();
            List<Healer> healers = new ArrayList<Healer>();
            HashMap<String, Player> playerMap = new HashMap<String, Player>();
            HashMap<String, Group> groups = new HashMap<String, Group>();
            int sumPlayerDmg = 0;
            int battleLength = 0;
            int countEnemyDowns = 0;
            int countEnemyDeaths = 0;
            int sumEnemyDps = 0;
            int sumEnemyDmg = 0;
            DefensiveBooner sumBoons = new DefensiveBooner("Total", "Total", "0");
            String commander = null;
            for (int i = 0; i < players.length(); i++) {
                JSONObject currPlayer = players.getJSONObject(i);
                String name = currPlayer.getString("name");
                String profession = currPlayer.getString("profession");
                String group = ""+currPlayer.getInt("group");
                if (currPlayer.getBoolean("hasCommanderTag"))
                    commander = commander==null ? currPlayer.getString("name") : "n/a";
                if (condiers.containsKey(name))
                    condiers.get(currPlayer.getString("name")).setProfession(profession);

                //statsTargets
                List<Object> playerStatsTargets = currPlayer.getJSONArray("statsTargets").toList();
                for (Object a : playerStatsTargets) {
                    for (Object b : (List<Object>)a) {
                        HashMap<String, Integer> map = (HashMap<String, Integer>) b;
                        countEnemyDowns += map.get("downed");
                        countEnemyDeaths += map.get("killed");
                        if (playerMap.containsKey(name)) {
                            Player p = playerMap.get(name);
                            p.setKills(p.getKills()+map.get("killed"));
                        } else {
                            Player p = new Player(name, profession, group);
                            p.setKills(map.get("killed"));
                            playerMap.put(name, p);
                        }
                    }
                }

                //defenses
                JSONObject playerDefenses = currPlayer.getJSONArray("defenses").getJSONObject(0);
                sumEnemyDmg += playerDefenses.getBigInteger("damageTaken").intValue();

                //support
                JSONObject currPlayerSupport = currPlayer.getJSONArray("support").getJSONObject(0);
                int cleanses = currPlayerSupport.getBigInteger("condiCleanse").intValue();
                if (cleanses > 0)
                    cleansers.add(new Cleanser(name, profession, cleanses));
                int strips = currPlayerSupport.getBigInteger("boonStrips").intValue();
                if (strips > 0)
                    strippers.add(new Stripper(name, profession, strips));

                //targetDamage1S
                List<Object> targetDmgList = currPlayer.getJSONArray("targetDamage1S").toList();
                List<Object> netTargetDmgList = null;
                for (Object a : targetDmgList) {
                    List<Object> aobj = (List<Object>) a;
                    for (Object b : aobj) {
                        List<Object> bobj = (List<Object>) b;
                        if (netTargetDmgList==null) {
                            netTargetDmgList = bobj; //initialize using first instance
                        } else {
                            for (int q = 0; q < netTargetDmgList.size(); q++) {
                                Integer bdmg = (Integer) bobj.get(q);
                                Integer fdmg = (Integer) netTargetDmgList.get(q);
                                Integer current = bdmg + fdmg;
                                netTargetDmgList.set(q, current);
                            }
                        }
                    }
                }

                //set report dmg map
                report.getDmgMap().put(name,netTargetDmgList);

                //set player damage
                DPSer dpser = new DPSer(name, profession, netTargetDmgList);
                dpsers.add(dpser);
                sumPlayerDmg += dpser.getDamage();
                battleLength = netTargetDmgList.size();

                //update top 10 spikes
                Spiker.computeTop10(name, profession, spikers, netTargetDmgList);

                //active buffs
                DefensiveBooner dBooner = new DefensiveBooner(name, profession, group);
                if (!currPlayer.isNull("squadBuffs")) {
                    JSONArray bArray = currPlayer.getJSONArray("squadBuffs");
                    populateDefensiveBoons(dBooner, bArray);
                }
                dBooner.computeRating();
                dbooners.add(dBooner);
                addBoons(sumBoons, dBooner);

                //healing
                int healing = 0;
                if (!currPlayer.isNull("extHealingStats")) {
                    JSONObject ehObject = currPlayer.getJSONObject("extHealingStats");
                    if(!ehObject.isNull("outgoingHealing")) {
                        JSONArray ohArray = ehObject.getJSONArray("outgoingHealing");
                        JSONObject ohObj = (JSONObject) ohArray.get(0);
                        healing = ohObj.getInt("healing");
                    }
                }
                int barrier = 0;
                if (!currPlayer.isNull("extBarrierStats")) {
                    JSONObject ehObject = currPlayer.getJSONObject("extBarrierStats");
                    if(!ehObject.isNull("outgoingBarrier")) {
                        JSONArray ohArray = ehObject.getJSONArray("outgoingBarrier");
                        JSONObject ohObj = (JSONObject) ohArray.get(0);
                        barrier = ohObj.getInt("barrier");
                    }
                }
                Healer healer = new Healer(name, profession, healing, barrier);
                if (healer.getTotal() > 0)
                    healers.add(healer);
            }
            calculateWeightedBoons(sumBoons, dbooners);

            //basic info
            String zone = jsonTop.getString("fightName");
            zone = zone.indexOf(" - ") > 0 ? zone.substring(zone.indexOf(" - ") + 3) : zone;
            report.setZone(zone);
            report.setDuration(jsonTop.getString("duration"));
            report.setCommander("n/a".equals(commander)?null:commander); //EI bug in json output
            JSONArray uploadLinks = jsonTop.getJSONArray("uploadLinks");
            if (jsonTop.has("uploadLinks"))
                report.setUrl(jsonTop.getJSONArray("uploadLinks").getString(0));
            if (report.getUrl()==null || !report.getUrl().startsWith("http"))
                report.setUrl(uploadUrl);
            //System.out.println("URL="+report.getUrl());
            if (jsonTop.has("timeEnd"))
                report.setEndTime(jsonTop.getString("timeEnd"));

            //mechanics
            int totalPlayersDead = 0;
            int totalPlayersDowned = 0;
            if (jsonTop.has("mechanics")) {
                JSONArray mechanics = jsonTop.getJSONArray("mechanics");
                for (int i=0; i<mechanics.length(); i++) {
                    JSONObject mechObj = mechanics.getJSONObject(i);
                    String mechName = mechObj.getString("name");
                    if ("Dead".equals(mechName)) {
                        List<Object> mdList = mechObj.getJSONArray("mechanicsData").toList();
                        for (Object mdo : mdList) {
                            HashMap<String, Object> mdMap = (HashMap<String, Object>) mdo;
                            String actor = (String) mdMap.get("actor");
                            Player p = playerMap.get(actor);
                            p.setDeaths(p.getDeaths() + 1);
                        }
                        if (mechanics.length() > 0)
                            totalPlayersDead = mechObj.getJSONArray("mechanicsData").length();
                    } else if ("Downed".equals(mechName)) {
                        if (mechanics.length() > 1)
                            totalPlayersDowned = mechObj.getJSONArray("mechanicsData").length();
                    }
                }
            }

            //compile group data
            for (Player plyr :playerMap.values()) {
                String grp = plyr.getGroup();
                Group g = groups.get(grp);
                g = g==null ? new Group(grp, grp) : g;
                if ("Firebrand".equals(plyr.getProfession()))
                    g.setName(plyr.getName());
                groups.put(grp, g);
                g.setKills(g.getKills()+plyr.getKills());
                g.setDeaths(g.getDeaths()+plyr.getDeaths());
            }

            System.out.println("Zone: " + report.getZone());
            System.out.println("Commander: " + report.getCommander());
            System.out.println("Time: " + report.getEndTime());
            System.out.println("Duration: " + report.getDuration());
            System.out.println();

            //write to buffer
            StringBuffer buffer = new StringBuffer();
            buffer.append(" Players   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", players.length(),
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDmg / battleLength, 1),
                    totalPlayersDowned, totalPlayersDead));
            report.setSquadSummary(buffer.toString());
            System.out.println(buffer);
            System.out.println();

            //approximate enemyDps
            sumEnemyDps = (int) sumEnemyDmg / battleLength;

            buffer = new StringBuffer();
            buffer.append(" Enemies   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", countEnemyPlayers,
                    DPSer.withSuffix(sumEnemyDmg, sumEnemyDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumEnemyDps, 1),
                    countEnemyDowns, countEnemyDeaths));
            report.setEnemySummary(buffer.toString());
            System.out.println(buffer);
            System.out.println();

            if (dpsers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                      Damage     DPS" + CRLF);
                buffer.append("--- -------------------------  --------   -----" + CRLF);
                dpsers.sort(Comparator.naturalOrder());
                int index = 1;
                int count = dpsers.size() > 10 ? 10 : dpsers.size();
                for (DPSer x : dpsers.subList(0, count))
                    if (x.getDamage() > 0)
                        buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setDamage(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (spikers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                   2 sec  4 sec  Time" + CRLF);
                buffer.append("--- -----------------------  -----  -----  ----" + CRLF);
                spikers.sort(Comparator.naturalOrder());
                int index = 1;
                int count = spikers.size() > 10 ? 10 : spikers.size();
                for (Spiker x : spikers.subList(0, count))
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setSpikers(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (cleansers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                     Cleanses" + CRLF);
                buffer.append("--- -------------------------  --------" + CRLF);
                cleansers.sort(Comparator.naturalOrder());
                int index = 1;
                int count = cleansers.size() > 10 ? 10 : cleansers.size();
                for (Cleanser x : cleansers.subList(0, count))
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setCleanses(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (strippers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                      Strips" + CRLF);
                buffer.append("--- -------------------------  --------" + CRLF);
                strippers.sort(Comparator.naturalOrder());
                int index = 1;
                int count = strippers.size() > 10 ? 10 : strippers.size();
                for (Stripper x : strippers.subList(0, count))
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setStrips(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (dbooners.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                     Rating  GroupKDR" + CRLF);
                buffer.append("--- -------------------------  ------    -----" + CRLF);
                dbooners.sort(Comparator.naturalOrder());
                int index = 1;
                int count = dbooners.size() > 10 ? 10 : dbooners.size();
                for (DefensiveBooner x : dbooners.subList(0, count)) {
                    if (x.getDefensiveRating() > 0) {
                        buffer.append(String.format("%2s", (index++)) + "  " + x + "    "
                                + String.format("%5s", groups.get(x.getGroup()).getKills() + "/" + groups.get(x.getGroup()).getDeaths()) + CRLF);
                    }
                }
                report.setDbooners(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (healers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                 Total  Heals Barrier" + CRLF);
                buffer.append("--- ---------------------- ------ ------ ------" + CRLF);
                healers.sort(Comparator.naturalOrder());
                int index = 1;
                int count = healers.size() > 10 ? 10 : healers.size();
                for (Healer x : healers.subList(0, count))
                    if (x.getTotal() > 0)
                        buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setHealers(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (condiers.size()>0) {
                buffer = new StringBuffer();
                buffer.append(" #  Player                          CCs" + CRLF);
                buffer.append("--- ------------------------  --------------" + CRLF);
                List<Condier> clist = new ArrayList<Condier>(condiers.values());
                clist.sort(Comparator.naturalOrder());
                int index = 1;
                int count = clist.size() > 10 ? 10 : clist.size();
                for (Condier x : clist.subList(0, count))
                    if (x.getChilledCount() > 0 || x.getCrippledCount() > 0 || x.getImmobCount() > 0 || x.getStunCount() > 0)
                        buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
                report.setCcs(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            if (enemies.size()>0) {
                buffer = new StringBuffer();
                buffer.append("Count Profession     Damage   Condi" + CRLF);
                buffer.append("----- ------------- -------- -------" + CRLF);
                List<EnemyProfession> elist = new ArrayList<>(enemies.values());
                elist.sort(EnemyProfession::compareTo);
                int i = 0;
                for (EnemyProfession x : elist)
                    if (++i <= 15 || (i <= 50 && x.getCount() > 1))
                        buffer.append(x + CRLF);
                report.setEnemyBreakdown(buffer.toString());
                System.out.println(buffer);
                System.out.println();
            }

            buffer = new StringBuffer();
            buffer.append(String.format("[Report] Squad Players: %d (Dmg: %s, Deaths: %d) | Enemy Players: %d (Dmg: %s, Deaths: %d)",
                    players.length(), DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 1 : 2), totalPlayersDead,
                    countEnemyPlayers, DPSer.withSuffix(sumEnemyDmg, sumEnemyDmg < 1000000 ? 1 : 2), countEnemyDeaths));
            report.setOverview(buffer.toString());
            System.out.println(buffer);
            System.out.println();

        } finally {
            is.close();
        }

        return report;
    }

    private static void populateCondierBuffs(HashMap<String, Condier> condiers, JSONArray bArray) {
        for (Object obj : bArray.toList()) {
            HashMap m = (HashMap)obj;
            ArrayList buffData = (ArrayList) m.get("buffData");
            HashMap bm = (HashMap)buffData.get(0);
            HashMap generated = (HashMap) bm.get("generated");
            int id = (int) (Integer) m.get("id");
            for (Object e : generated.entrySet())
                addToCondiers(condiers, (Map.Entry<String, BigDecimal>) e, id);
        }
    }

    private static void addToCondiers(HashMap<String, Condier> condiers, Map.Entry<String, BigDecimal> me, int id) {
        String name = me.getKey();
        Condier c = condiers.get(name);
        if (c==null)
            c = new Condier(name, "    ");
        switch(id) { //stun=872 chilled=722 crippled=721 immob=727 slow=26766
            case 872:
                c.setStunCount(c.getStunCount() + 1);
                c.setStunDur(c.getStunDur().add(me.getValue()));
                break;
            case 722:
                c.setChilledCount(c.getChilledCount() + 1);
                c.setChilledDur(c.getChilledDur().add(me.getValue()));
                break;
            case 721:
                c.setCrippledCount(c.getCrippledCount() + 1);
                c.setCrippledDur(c.getCrippledDur().add(me.getValue()));
                break;
            case 727:
                c.setImmobCount(c.getImmobCount() + 1);
                c.setImmobDur(c.getImmobDur().add(me.getValue()));
                break;
            case 26766:
                c.setSlowCount(c.getSlowCount() + 1);
                c.setSlowDur(c.getSlowDur().add(me.getValue()));
                break;
        }
        condiers.put(name, c);
    }

    private static void populateEnemy(HashMap<String, EnemyProfession> enemies, String profession, JSONObject enemyDpsAll) {
        int dmg = (int) enemyDpsAll.get("damage");
        int condiDmg = (int) enemyDpsAll.get("condiDamage");
        if (enemies.containsKey(profession)) {
            enemies.get(profession).addEnemy(dmg, condiDmg);
        } else {
            enemies.put(profession, new EnemyProfession(profession, dmg, condiDmg));
        }
    }

    private static void calculateWeightedBoons(DefensiveBooner sumBoons, List<DefensiveBooner> dbooners) {
        for (DefensiveBooner db : dbooners) {
            int stab = sumBoons.getStability();
            db.setStability(stab > 0 ? 100 * db.getStability() / stab : db.getStability());
            int aegis = sumBoons.getAegis();
            db.setAegis(aegis > 0 ? 100 * db.getAegis() / aegis : db.getAegis());
            int prot = sumBoons.getProtection();
            db.setProtection(prot > 0 ? 100 * db.getProtection() / prot : db.getProtection());
            int resist = sumBoons.getResistance();
            db.setResistance(resist > 0 ? 100 * db.getResistance() / resist : db.getResistance());
            int resolu = sumBoons.getResolution();
            db.setResolution(resolu > 0 ? 100 * db.getResolution() / resolu : db.getResolution());
            int alac = sumBoons.getAlacrity();
            db.setAlacrity(alac > 0 ? 100 * db.getAlacrity() / alac : db.getAlacrity());
            db.computeRating();
        }
    }

    private static void populateDefensiveBoons(DefensiveBooner dBooner, JSONArray bArray) {
        for (Object obj : bArray.toList()) {
            HashMap m = (HashMap)obj;
            int id = (int) (Integer) m.get("id");
            switch (id) { //1122/743/717/26980/873/30328
                case 1122 : dBooner.setStability(dBooner.getStability() + getBuffGeneration(m)); break;
                case 743 : dBooner.setAegis(dBooner.getAegis() + getBuffGeneration(m)); break;
                case 717 : dBooner.setProtection(dBooner.getProtection() + getBuffGeneration(m)); break;
                case 26980 : dBooner.setResistance(dBooner.getResistance() + getBuffGeneration(m)); break;
                case 873 : dBooner.setResolution(dBooner.getResolution() + getBuffGeneration(m)); break;
                case 30328 : dBooner.setAlacrity(dBooner.getAlacrity() + getBuffGeneration(m)); break;
            }
        }
    }

    private static int getBuffGeneration(HashMap m) {
        if (m.containsKey("buffData")) {
            List buffData = (List) m.get("buffData");
            if (buffData!=null && buffData.size()>0) {
                HashMap bdMap = (HashMap) buffData.get(0);
                if (bdMap.containsKey("generation")) {
                    BigDecimal gen = (BigDecimal) bdMap.get("generation");
                    return (int) gen.multiply(new BigDecimal(1000)).intValue();
                } else if (bdMap.containsKey("generated")) {
                    HashMap genMap = (HashMap) bdMap.get("generated");
                    BigDecimal gen = new BigDecimal("0");
                    for (Object val : genMap.values()){
                        gen = gen.add((BigDecimal)val);
                    }
                    return (int) gen.multiply(new BigDecimal(1000)).intValue();
                }
            }
        }
        return 0;
    }

    private static void addBoons(DefensiveBooner sumBoons, DefensiveBooner player) {
        sumBoons.setStability(sumBoons.getStability() + player.getStability());
        sumBoons.setAegis(sumBoons.getAegis() + player.getAegis());
        sumBoons.setProtection(sumBoons.getProtection() + player.getProtection());
        sumBoons.setResistance(sumBoons.getResistance() + player.getResistance());
        sumBoons.setResolution(sumBoons.getResolution() + player.getResolution());
        sumBoons.setAlacrity(sumBoons.getAlacrity() + player.getAlacrity());
    }

    public static void main(String[] args) throws Exception {
        File jsonFile = new File(args[1]);
        String homeDir = args[2];
        String uploadUrl = args[3];
        if (jsonFile.exists()) {
            FightReport report = processWvwJsonLog(jsonFile, uploadUrl);

            FileOutputStream frf = null;
            ObjectOutputStream o = null;
            try {
                frf = new FileOutputStream(homeDir + File.separator + "fightreport.bin");
                o = new ObjectOutputStream(frf);
                // Write objects to file
                o.writeObject(report);
            } finally {
                if (o!=null)
                    o.close();
                if (frf!=null)
                    frf.close();
            }
        }
    }
}
