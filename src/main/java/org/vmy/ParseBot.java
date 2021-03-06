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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParseBot {

    private static final String CRLF = "\n";

    protected static FightReport processWvwJsonLog(File jsonFile, File logFile) throws IOException {
        JSONObject jo = new JSONObject();
        FightReport report = new FightReport();

        //get upload URL from log file
        String uploadURL = null;
        if (logFile.exists()) {
            InputStream is = new FileInputStream(logFile);

            try {
                String logTxt = IOUtils.toString(is, "UTF-8");
                int index = logTxt.indexOf("https://");
                int end = logTxt.indexOf("\r", index);
                if (index > 0) {
                    uploadURL = logTxt.substring(index, end);
                    System.out.println("DPS Reports link=" + uploadURL);
                }
            } finally {
                is.close();
            }
        }

        InputStream is = new FileInputStream(jsonFile);
        try {

            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject jsonTop = new JSONObject(jsonTxt);

            //targets
            JSONArray targets = jsonTop.getJSONArray("targets");
            HashMap<String, Condier> condiers = new HashMap<String, Condier>();
            int countEnemyPlayers = 0;
            int countFriendlies = 0;
            for (int i = 1; i < targets.length(); i++) {
                JSONObject currTarget = targets.getJSONObject(i);
                String name = currTarget.getString("name");
                if (name.contains("-"))
                    countEnemyPlayers++;
                else
                    countFriendlies++;
                if (!currTarget.isNull("buffs")) {
                    JSONArray bArray = currTarget.getJSONArray("buffs");
                    populateCondierBuffs(condiers, bArray);
                }
            }

            //players
            JSONArray players = jsonTop.getJSONArray("players");
            List<DPSer> dpsers = new ArrayList<DPSer>();
            List<Cleanser> cleansers = new ArrayList<Cleanser>();
            List<Stripper> strippers = new ArrayList<Stripper>();
            List<DefensiveBooner> dbooners = new ArrayList<DefensiveBooner>();
            List<Spiker> top10Spiker = new ArrayList<Spiker>();
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
                cleansers.add(new Cleanser(name, profession,
                        currPlayerSupport.getBigInteger("condiCleanse").intValue()));
                strippers.add(new Stripper(name, profession,
                        currPlayerSupport.getBigInteger("boonStrips").intValue()));

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
                Spiker.computeTop10(name, profession, top10Spiker, netTargetDmgList);

                //active buffs
                DefensiveBooner dBooner = new DefensiveBooner(name, profession, group);
                if (!currPlayer.isNull("groupBuffsActive")) {
                    JSONArray bArray = currPlayer.getJSONArray("groupBuffsActive");
                    populateDefensiveBoons(dBooner, bArray);
                }
                if (!currPlayer.isNull("offGroupBuffsActive")) {
                    JSONArray bArray = currPlayer.getJSONArray("offGroupBuffsActive");
                    populateDefensiveBoons(dBooner, bArray);
                }
                dBooner.computeRating();
                dbooners.add(dBooner);
                addBoons(sumBoons, dBooner);
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
                report.setUrl(uploadURL);
            System.out.println("URL="+report.getUrl());
            if (jsonTop.has("timeEnd"))
                report.setEndTime(jsonTop.getString("timeEnd"));

            //mechanics
            int totalPlayersDead = 0;
            int totalPlayersDowned = 0;
            if (jsonTop.has("mechanics")) {
                JSONArray mechanics = jsonTop.getJSONArray("mechanics");
                List<Object> mdList = mechanics.getJSONObject(0).getJSONArray("mechanicsData").toList();
                for (Object mdo : mdList) {
                    HashMap<String, Object> mdMap = (HashMap<String, Object>) mdo;
                    String actor = (String) mdMap.get("actor");
                    Player p = playerMap.get(actor);
                    p.setDeaths(p.getDeaths()+1);
                }
                if (mechanics.length()>0)
                    totalPlayersDead = mechanics.getJSONObject(0).getJSONArray("mechanicsData").length();
                if (mechanics.length()>1)
                    totalPlayersDowned = mechanics.getJSONObject(1).getJSONArray("mechanicsData").length();
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

            //write to buffer
            StringBuffer buffer = new StringBuffer();
            buffer.append(" Players   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", players.length(),
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDmg / battleLength, 1),
                    totalPlayersDowned, totalPlayersDead));
            report.setSquadSummary(buffer.toString());

            if (countFriendlies == 1)
                report.setFriendliesSummary("plus " + countFriendlies + " friendly (total = " + (countFriendlies+players.length()) + " players)");
            else if (countFriendlies > 1)
                report.setFriendliesSummary("plus " + countFriendlies + " friendlies (total = " + (countFriendlies+players.length()) + " players)");

            //approximate enemyDps
            sumEnemyDps = (int) sumEnemyDmg / battleLength;

            buffer = new StringBuffer();
            buffer.append(" Enemies   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", countEnemyPlayers,
                    DPSer.withSuffix(sumEnemyDmg, sumEnemyDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumEnemyDps, 1),
                    countEnemyDowns, countEnemyDeaths));
            report.setEnemySummary(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                      Damage     DPS" + CRLF);
            buffer.append("--- -------------------------  --------  -------" + CRLF);
            dpsers.sort((d1, d2) -> d1.compareTo(d2));
            int index = 1;
            int count = dpsers.size() > 10 ? 10 : dpsers.size();
            for (DPSer x : dpsers.subList(0, count))
                if (x.getDamage()>0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setDamage(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                   2 sec  4 sec  Time" + CRLF);
            buffer.append("--- -----------------------  -----  -----  ----" + CRLF);
            top10Spiker.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = top10Spiker.size() > 10 ? 10 : top10Spiker.size();
            for (Spiker x : top10Spiker.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setSpikers(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                     Cleanses" + CRLF);
            buffer.append("--- -------------------------  --------" + CRLF);
            cleansers.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = cleansers.size() > 10 ? 10 : cleansers.size();
            for (Cleanser x : cleansers.subList(0, count))
                if (x.getCleanses()>0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setCleanses(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                      Strips" + CRLF);
            buffer.append("--- -------------------------  --------" + CRLF);
            strippers.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = strippers.size() > 10 ? 10 : strippers.size();
            for (Stripper x : strippers.subList(0, count))
                if (x.getStrips()>0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setStrips(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                     Rating  Group KDR" + CRLF);
            buffer.append("--- -------------------------  ------    -----" + CRLF);
            dbooners.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = dbooners.size() > 10 ? 10 : dbooners.size();
            for (DefensiveBooner x : dbooners.subList(0, count))
                if (x.getDefensiveRating()>0) {
                    buffer.append(String.format("%2s", (index++)) + "  " + x + "    "
                        + String.format("%5s",groups.get(x.getGroup()).getKills() + "/" + groups.get(x.getGroup()).getDeaths()) + CRLF);
                }
            report.setDbooners(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                          CCs" + CRLF);
            buffer.append("--- ------------------------  --------------" + CRLF);
            List<Condier> clist = new ArrayList<Condier>(condiers.values());
            clist.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = clist.size() > 10 ? 10 : clist.size();
            for (Condier x : clist.subList(0, count))
                if (x.getChilledCount()>0 || x.getCrippledCount()>0 || x.getImmobCount()>0 || x.getStunCount()>0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setCcs(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(String.format("[Report] Squad Players: %d (Deaths: %d) | Enemy Players: %d (Deaths: %d)",
                    players.length(), totalPlayersDead,
                    countEnemyPlayers, countEnemyDeaths));
            report.setOverview(buffer.toString());
            System.out.println(buffer.toString());

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
        File logFile = new File(args[2]);
        String homeDir = args[3];
        if (jsonFile.exists()) {
            FightReport report = processWvwJsonLog(jsonFile, logFile);

            FileOutputStream frf = null;
            ObjectOutputStream o = null;
            try {
                frf = new FileOutputStream(new File(homeDir + File.separator + "fightreport.bin"));
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
