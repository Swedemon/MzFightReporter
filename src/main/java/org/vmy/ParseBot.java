package org.vmy;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ParseBot {

    private static final String LF = "\n";

    protected static FightReport processWvwJsonLog(File jsonFile, String uploadUrl) throws IOException {
        FightReport report = new FightReport();

        InputStream is = new FileInputStream(jsonFile);
        try {

            String jsonTxt = IOUtils.toString(is, "UTF-8");
            JSONObject jsonTop = new JSONObject(jsonTxt);

            Map<String, String> healUsers = new HashMap<>();
            try {
                JSONArray healExts = jsonTop.getJSONArray("usedExtensions").getJSONObject(0).getJSONArray("runningExtension");
                healExts.forEach(jo -> healUsers.put(String.valueOf(jo), ""));
            } catch (Exception ignored) {}

            //skillMap
            JSONObject skillMap = jsonTop.getJSONObject("skillMap");
            JSONObject buffMap = jsonTop.getJSONObject("buffMap");

            //targets
            JSONArray targets = jsonTop.getJSONArray("targets");
            HashMap<String, Condier> condiers = new HashMap<>();
            HashMap<String, Integer> enemyDmgBySkill = new HashMap<>();
            List<Enemy> enemies = new ArrayList<>();
            int sumEnemyDmg = 0;
            for (int i = 1; i < targets.length(); i++) {
                JSONObject currTarget = targets.getJSONObject(i);
                String name = currTarget.getString("name");
                String profession = name != null && name.indexOf(" ") > 0 ? name.substring(0, name.indexOf(" ")) : null;
                String team = currTarget.isNull("teamID") ? "" : mapTeamID(currTarget.getInt("teamID"));
                if (!currTarget.isNull("buffs")) {
                    JSONArray bArray = currTarget.getJSONArray("buffs");
                    populateCondierBuffs(condiers, bArray);
                }
                if (!currTarget.isNull("dpsAll")) {
                    int damage = currTarget.getJSONArray("dpsAll").getJSONObject(0).getInt("damage");
                    int dps = currTarget.getJSONArray("dpsAll").getJSONObject(0).getInt("dps");
                    int down = currTarget.getJSONArray("defenses").getJSONObject(0).getInt("downCount");
                    int dead = currTarget.getJSONArray("defenses").getJSONObject(0).getInt("deadCount");
                    enemies.add(new Enemy(name, profession, team, damage, dps, down, dead));
                    sumEnemyDmg += damage;
                }
                if (!currTarget.isNull("totalDamageDist")) {
                    JSONArray dmgDist = currTarget.getJSONArray("totalDamageDist").getJSONArray(0);
                    for (int j=0; j < dmgDist.length(); j++) {
                        JSONObject obj = dmgDist.getJSONObject(j);
                        Integer ddid = obj.getInt("id");
                        String sname = ""+ddid;
                        if (skillMap.has("s"+ddid)) {
                            JSONObject oname = skillMap.getJSONObject("s" + ddid);
                            sname = (String) oname.get("name");
                        } else if (buffMap.has("b"+ddid)) {
                            JSONObject oname = buffMap.getJSONObject("b" + ddid);
                            sname = (String) oname.get("name");
                        }
                        if (enemyDmgBySkill.containsKey(sname)) {
                            enemyDmgBySkill.put(sname, enemyDmgBySkill.get(sname) + obj.getInt("totalDamage"));
                        } else {
                            enemyDmgBySkill.put(sname, obj.getInt("totalDamage"));
                        }
                    }
                }
            }

            //players
            JSONArray players = jsonTop.getJSONArray("players");
            List<DPSer> dpsers = new ArrayList<>();
            List<Cleanser> cleansers = new ArrayList<>();
            List<Stripper> strippers = new ArrayList<>();
            List<DefensiveBooner> dbooners = new ArrayList<>();
            List<DefensiveBooner> aggDbooners = new ArrayList<>();
            List<OffensiveBooner> obooners = new ArrayList<>();
            List<OffensiveBooner> aggObooners = new ArrayList<>();
            List<Spiker> spikers = new ArrayList<>();
            List<Healer> healers = new ArrayList<>();
            HashMap<String, Player> playerMap = new HashMap<>();
            HashMap<String, Group> groups = new HashMap<>();
            int sumPlayerDmg = 0;
            int battleLength = 0;
            int countEnemyDowns = 0;
            int countEnemyDeaths = 0;
            int countNonSquadPlayers = 0;
            String team = null;
            String commander = null;
            for (int i = 0; i < players.length(); i++) {
                JSONObject currPlayer = players.getJSONObject(i);
                boolean notInSquad = currPlayer.getBoolean("notInSquad");
                if (currPlayer.has("notInSquad") && currPlayer.getBoolean("notInSquad")) {
                    countNonSquadPlayers++;
                    continue;
                }
                String name = currPlayer.getString("name");
                String profession = currPlayer.getString("profession");
                String group = ""+currPlayer.getInt("group");
                if (team == null)
                    team = currPlayer.isNull("teamID") ? "" : mapTeamID(currPlayer.getInt("teamID"));
                int downDmgOut = 0;
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
                            p.setDownsOut(p.getDownsOut()+map.get("downed"));
                            p.setKills(p.getKills()+map.get("killed"));
                        } else {
                            Player p = new Player(name, profession, group);
                            p.setDownsOut(map.get("downed"));
                            p.setKills(map.get("killed"));
                            playerMap.put(name, p);
                        }
                        Condier c = condiers.get(name);
                        if (c == null)
                            c = new Condier(name, profession);
                        c.setInterruptCount(map.get("interrupts"));
                        condiers.put(name, c);
                    }
                }

                //statsAll
                List<Object> playerStatsAll = currPlayer.getJSONArray("statsAll").toList();
                for (Object a : playerStatsAll) {
                    HashMap<String, Integer> map = (HashMap<String, Integer>) a;
                    Condier c = condiers.get(name);
                    c.setInterruptCount(map.get("interrupts"));
                    downDmgOut = map.get("downContribution");
                    condiers.put(name, c);
                }

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
                DPSer dpser = new DPSer(name, profession, group, netTargetDmgList);
                dpser.setOnDowns(downDmgOut);

                dpsers.add(dpser);
                sumPlayerDmg += dpser.getDamage();
                battleLength = netTargetDmgList.size();

                //update top 10 spikes
                Spiker.computeTop10(name, profession, spikers, netTargetDmgList);

                //active buffs
                DefensiveBooner dBooner = new DefensiveBooner(name, profession, group);
                OffensiveBooner oBooner = new OffensiveBooner(name, profession, group);
                if (!currPlayer.isNull("buffUptimes")) {
                    JSONArray bArray = currPlayer.getJSONArray("buffUptimes");
                    populateDefensiveBoons(dBooner, bArray);
                    populateOffensiveBoons(oBooner, bArray);
                }
                dBooner.computeRating();
                dbooners.add(dBooner);
                oBooner.computeRating();
                obooners.add(oBooner);

                //healing
                int healing = 0;
                if (!currPlayer.isNull("extHealingStats")) {
                    JSONObject ehObject = currPlayer.getJSONObject("extHealingStats");
                    if(!ehObject.isNull("outgoingHealingAllies")) {
                        JSONArray ohArray = ehObject.getJSONArray("outgoingHealingAllies");
                        for (int q=0; q<ohArray.length(); q++) {
                            JSONArray ihArray = ohArray.getJSONArray(q);
                            JSONObject healObj = (JSONObject) ihArray.get(0);
                            healing += healObj.getInt("healing");
                        }
                    }
                }
                int barrier = 0;
                if (!currPlayer.isNull("extBarrierStats")) {
                    JSONObject ehObject = currPlayer.getJSONObject("extBarrierStats");
                    if(!ehObject.isNull("outgoingBarrierAllies")) {
                        JSONArray ohArray = ehObject.getJSONArray("outgoingBarrierAllies");
                        for (int q=0; q<ohArray.length(); q++) {
                            JSONArray ihArray = ohArray.getJSONArray(q);
                            JSONObject healObj = (JSONObject) ihArray.get(0);
                            barrier += healObj.getInt("barrier");
                        }
                    }
                }
                if (healUsers.isEmpty() || healUsers.containsKey(name)) {
                    Healer healer = new Healer(name, profession, healing, barrier);
                    if (healer.getTotal() > 0)
                        healers.add(healer);
                }
            }

            //basic info
            String zone = jsonTop.getString("fightName");
            zone = zone.indexOf(" - ") > 0 ? zone.substring(zone.indexOf(" - ") + 3) : zone;
            report.setZone(zone);
            report.setDuration(jsonTop.getString("duration"));
            report.setDurationMS(jsonTop.getInt("durationMS"));
            report.setCommander("n/a".equals(commander)?null:commander); //EI bug in json output
            report.setArcVersion(jsonTop.getString("arcVersion"));
            report.setEiVersion(jsonTop.getString("eliteInsightsVersion"));
            report.setRecordedBy(jsonTop.getString("recordedAccountBy"));
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

            //compile dbooner data
            for (String grp : dbooners.stream().map(db -> db.getGroup()).distinct().collect(Collectors.toList())) {
                List<DefensiveBooner> grpBooners = dbooners.stream().filter(db -> db.getGroup().equals(grp)).collect(Collectors.toList());
                int grpSize = grpBooners.size();
                DefensiveBooner aggDbooner = new DefensiveBooner(grp);
                aggDbooner.setStability(grpBooners.stream().map(DefensiveBooner::getStability).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setAegis(grpBooners.stream().map(DefensiveBooner::getAegis).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setProtection(grpBooners.stream().map(DefensiveBooner::getProtection).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setResistance(grpBooners.stream().map(DefensiveBooner::getResistance).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setAlacrity(grpBooners.stream().map(DefensiveBooner::getAlacrity).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setQuickness(grpBooners.stream().map(DefensiveBooner::getQuickness).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.setResolution(grpBooners.stream().map(DefensiveBooner::getResolution).reduce(0, Integer::sum) / grpSize / 1000);
                aggDbooner.computeRating();
                aggDbooners.add(aggDbooner);
            }

            //compile obooner data
            for (String grp : obooners.stream().map(ob -> ob.getGroup()).distinct().collect(Collectors.toList())) {
                List<OffensiveBooner> grpBooners = obooners.stream().filter(db -> db.getGroup().equals(grp)).collect(Collectors.toList());
                int grpSize = grpBooners.size();
                OffensiveBooner aggObooner = new OffensiveBooner(grp);
                aggObooner.setMight(grpBooners.stream().map(OffensiveBooner::getMight).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setFury(grpBooners.stream().map(OffensiveBooner::getFury).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setAlacrity(grpBooners.stream().map(OffensiveBooner::getAlacrity).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setQuickness(grpBooners.stream().map(OffensiveBooner::getQuickness).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setVigor(grpBooners.stream().map(OffensiveBooner::getVigor).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setSwiftness(grpBooners.stream().map(OffensiveBooner::getSwiftness).reduce(0, Integer::sum) / grpSize / 1000);
                aggObooner.setDownCn(dpsers.stream().filter(d -> d.getGroup().equals(grp)).map(DPSer::getOnDowns).reduce(0, Integer::sum));
                aggObooner.setDowns(grpBooners.stream().map(ob -> playerMap.get(ob.getName()).getDownsOut()).reduce(0, Integer::sum));
                aggObooner.setKills(grpBooners.stream().map(ob -> playerMap.get(ob.getName()).getKills()).reduce(0, Integer::sum));
                aggObooner.computeRating();
                aggObooners.add(aggObooner);
            }

            buildReport(report, condiers, enemies, players, dpsers, cleansers, strippers, aggDbooners, aggObooners, spikers, healers, playerMap, groups, enemyDmgBySkill, sumPlayerDmg, battleLength, countEnemyDowns, countEnemyDeaths, sumEnemyDmg, team, totalPlayersDead, totalPlayersDowned, countNonSquadPlayers);

        } finally {
            is.close();
        }

        return report;
    }

    private static void buildReport(FightReport report, HashMap<String, Condier> condiers, List<Enemy> enemies, JSONArray players,
                                    List<DPSer> dpsers, List<Cleanser> cleansers, List<Stripper> strippers, List<DefensiveBooner> aggDbooners,
                                    List<OffensiveBooner> aggObooners, List<Spiker> spikers, List<Healer> healers,
                                    HashMap<String, Player> playerMap, HashMap<String, Group> groups,
                                    HashMap<String, Integer> enemyDmgBySkill, int sumPlayerDmg, int battleLength, int countEnemyDowns,
                                    int countEnemyDeaths, int sumEnemyDmg, String team, int totalPlayersDead, int totalPlayersDowned, int countNonSquadPlayers)
    {
        boolean existsEmptyTeams = enemies.stream().anyMatch(e -> e.getTeam().equals(""));
        int enemyDowns = enemies.stream().mapToInt(Enemy::getDowns).sum();
        int enemyDeaths = enemies.stream().mapToInt(Enemy::getDeaths).sum();

        BigDecimal sec = BigDecimal.valueOf(report.getDurationMS() / 1000);
        String playerPadding = Parameters.getInstance().enableDiscordMobileMode ? "" : "     ";
        String playerDashes = Parameters.getInstance().enableDiscordMobileMode ? "" : "-----";

        System.out.println("Zone: " + report.getZone());
        System.out.println("Commander: " + report.getCommander());
        System.out.println("Time: " + report.getEndTime());
        System.out.println("Duration: " + report.getDuration());
        System.out.println();

        //write to buffer
        StringBuffer buffer = new StringBuffer();
        if (Parameters.getInstance().enableDiscordMobileMode) {
            buffer.append("Players    Dmg   DPS  Downs Deaths" + LF);
            buffer.append("--------- ----- ----- -----  -----" + LF);
            String playerText = getPlayerTeamText(players.length() - countNonSquadPlayers, team);
            buffer.append(String.format("%9s%6s%6s%5d%7d", playerText,
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 0 : sumPlayerDmg >= 10000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDmg / battleLength, 1),
                    totalPlayersDowned, totalPlayersDead));
            if (countNonSquadPlayers > 0)
                buffer.append(LF +
                        StringUtils.leftPad(String.valueOf(countNonSquadPlayers),
                            Parameters.getInstance().enableDiscordMobileMode ? 2 : 3)
                        + (StringUtils.isEmpty(team) ? "" : " " + team) + " Friendlies");
        } else {
            buffer.append(" Players   Damage   DPS   Downs  Deaths" + LF);
            buffer.append("---------- ------  ----- ------- ------" + LF);
            String playerText = getPlayerTeamText(players.length() - countNonSquadPlayers, team);
            buffer.append(String.format("%9s%7s%7s%6d%7d", playerText,
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 0 : sumPlayerDmg >= 10000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDmg / battleLength, 1),
                    totalPlayersDowned, totalPlayersDead));
            if (countNonSquadPlayers > 0)
                buffer.append(LF +
                        StringUtils.leftPad(String.valueOf(countNonSquadPlayers),
                                Parameters.getInstance().enableDiscordMobileMode ? 2 : 3)
                        + (StringUtils.isEmpty(team) ? "" : " " + team) + " Friendlies");
        }
        report.setSquadSummary(buffer.toString());
        System.out.println("Squad Summary:" + LF + buffer);
        System.out.println();

        buffer = new StringBuffer();
        if (Parameters.getInstance().enableDiscordMobileMode) {
            buffer.append("Players    Dmg   DPS  Downs Deaths" + LF);
            buffer.append("--------- ----- ----- -----  -----" + LF);
        } else {
            buffer.append(" Players   Damage   DPS   Downs  Deaths" + LF);
            buffer.append("---------- ------  ----- ------- ------" + LF);
        }
        String teamCountSummary = buildTeamCountSummary(enemies);
        if (!existsEmptyTeams) {
            appendEnemySummary("Red", enemies, buffer);
            appendEnemySummary("Green", enemies, buffer);
            appendEnemySummary("Blue", enemies, buffer);
        } else {
            enemies.forEach(e -> e.setTeam(""));
            appendEnemySummary("", enemies, buffer);
        }
        String esum = buffer.toString();
        esum = esum.substring(0, esum.length()-1);
        report.setEnemySummary(esum);
        System.out.println("Enemy Summary:" + LF + esum);
        System.out.println();

        if (dpsers.stream().anyMatch(d -> d.getDamage()>0)) {
            buffer = new StringBuffer();
            buffer.append(" #  Player       " + playerPadding + "  Dmg   DPS DownC" + LF);
            buffer.append("--- -------------" + playerDashes  + " ----- ----- ----" + LF);
            dpsers.sort(Comparator.naturalOrder());
            int index = 1;
            int count = Math.min(dpsers.size(), 10);
            for (DPSer x : dpsers.subList(0, count))
                if (x.getDamage() > 0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + LF);
            report.setDamage(buffer.toString());
            System.out.println("Damage & Down Contribution:" + LF + buffer);
            System.out.println();
        }

        if (spikers.size()>0) {
            buffer = new StringBuffer();
            buffer.append(" #  Player        " + playerPadding + " 4sec 2sec  Time" + LF);
            buffer.append("--- --------------" + playerDashes  + " ---- ----  ----" + LF);
            spikers.sort(Comparator.naturalOrder());
            int index = 1;
            int count = Math.min(spikers.size(), 10);
            for (Spiker x : spikers.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + LF);
            report.setSpikers(buffer.toString());
            System.out.println("Spike Damage:" + LF + buffer);
            System.out.println();
        }

        if (strippers.size()>0) {
            buffer = new StringBuffer();
            buffer.append(" #  Player             " + playerPadding + "Total  SPS" + LF);
            buffer.append("--- -------------------" + playerDashes  + " ----  ----" + LF);
            strippers.sort(Comparator.naturalOrder());
            int index = 1;
            int count = Math.min(strippers.size(), 10);
            for (Stripper x : strippers.subList(0, count)) {
                BigDecimal rate = BigDecimal.valueOf(x.getStrips()).divide(sec, 2, RoundingMode.HALF_UP);
                buffer.append(String.format("%2s", (index++)) + "  " + x
                        + String.format("%6s", new DecimalFormat("#0.00").format(rate).replaceAll(",", "")) + LF);
            }
            report.setStrips(buffer.toString());
            System.out.println("Strips:" + LF + buffer);
            System.out.println();
        }

        if (cleansers.size()>0) {
            buffer = new StringBuffer();
            buffer.append(" #  Player             " + playerPadding + "Total  CPS" + LF);
            buffer.append("--- -------------------" + playerDashes  + " ----  ----" + LF);
            cleansers.sort(Comparator.naturalOrder());
            int index = 1;
            int count = Math.min(cleansers.size(), 10);
            for (Cleanser x : cleansers.subList(0, count)) {
                BigDecimal rate = BigDecimal.valueOf(x.getCleanses()).divide(sec, 2, RoundingMode.HALF_UP);
                buffer.append(String.format("%2s", (index++)) + "  " + x
                        + String.format("%6s", new DecimalFormat("#0.00").format(rate).replaceAll(",", "")) + LF);
            }
            report.setCleanses(buffer.toString());
            System.out.println("Cleanses:" + LF + buffer);
            System.out.println();
        }

        if (healers.stream().anyMatch(h -> h.getTotal()>0)) {
            buffer = new StringBuffer();
            buffer.append(" #  Player            " + playerPadding + " Heals  HPS" + LF);
            buffer.append("--- ------------------" + playerDashes  + " ----- -----" + LF);
            healers = healers.stream().sorted((o1, o2) -> Integer.compare(o2.getHealing(), o1.getHealing())).collect(Collectors.toList());
            int index = 1;
            int count = Math.min(healers.size(), 5);
            for (Healer x : healers.subList(0, count))
                if (x.getHealing() > 0) {
                    String hps = DPSer.withSuffix(x.getHealing() / battleLength, 1);
                    buffer.append(String.format("%2s", (index++)) + "  " + x.toHealerString()
                            + String.format("%6s", hps) + LF);
                }
            buffer.append(LF);
            buffer.append(" #  Player            " + playerPadding + "Barrier BPS" + LF);
            buffer.append("--- ------------------" + playerDashes  + " ----- -----" + LF);
            healers = healers.stream().sorted((o1, o2) -> Integer.compare(o2.getBarrier(), o1.getBarrier())).collect(Collectors.toList());
            index = 1;
            for (Healer x : healers.subList(0, count))
                if (x.getBarrier() > 0) {
                    String hps = DPSer.withSuffix(x.getBarrier() / battleLength, 1);
                    buffer.append(String.format("%2s", (index++)) + "  " + x.toBarrierString()
                            + String.format("%6s", hps) + LF);
                }
            report.setHealers(buffer.toString());
            System.out.println("Heals & Barrier (heal addon required):" + LF + buffer);
            System.out.println();
        }

        if (condiers.values().stream().anyMatch(x->x.getChilledCount() > 0 || x.getCrippledCount() > 0 || x.getInterruptCount() > 0 || x.getImmobCount() > 0 || x.getStunCount() > 0)) {
            buffer = new StringBuffer();
            buffer.append(" #  Player         " + playerPadding + "     CCs   Ints" + LF);
            buffer.append("--- ---------------" + playerDashes  + " ----------- --" + LF);
            List<Condier> clist = new ArrayList<>(condiers.values());
            clist.sort(Comparator.naturalOrder());
            int index = 1;
            int count = Math.min(clist.size(), 10);
            for (Condier x : clist.subList(0, count))
                if (x.getChilledCount() > 0 || x.getCrippledCount() > 0 || x.getInterruptCount() > 0 || x.getImmobCount() > 0 || x.getStunCount() > 0)
                    buffer.append(String.format("%2s", (index++)) + "  " + x + LF);
            report.setCcs(buffer.toString());
            System.out.println("Outgoing CCs (stuns immobs chills cripples) & Interrupts:" + LF + buffer);
            System.out.println();
        }

        List<Player> plist = playerMap.values().stream().filter(p -> p.getDownsOut() > 0 || p.getKills() > 0).sorted().collect(Collectors.toList());
        if (plist.size()>0) {
            buffer = new StringBuffer();
            buffer.append(" #  Player            " + playerPadding + " Downs Kills" + LF);
            buffer.append("--- ------------------" + playerDashes  + " ----- -----" + LF);
            int index = 1;
            int count = Math.min(plist.size(), 10);
            for (Player x : plist.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + LF);
            report.setDownsKills(buffer.toString());
            System.out.println("Outgoing Downs & Kills:" + LF + buffer);
            System.out.println();
        }

        if (aggDbooners.stream().anyMatch(d -> d.getDefensiveRating()>0)) {
            buffer = new StringBuffer();
            if (Parameters.getInstance().enableDiscordMobileMode) {
                buffer.append(" # StabAegiProtResiResoAlacQuik" + LF);
                buffer.append("--- --- --- --- --- --- --- ---" + LF);
                aggDbooners.sort(Comparator.naturalOrder());
                int count = Math.min(aggDbooners.size(), 15);
                for (DefensiveBooner x : aggDbooners.subList(0, count)) {
                    if (x.getDefensiveRating() > 0) {
                        buffer.append(String.format("%2s", x.getGroup())
                                //+ String.format("%5s", x.getDefensiveRating())
                                //+ String.format("%5s", groups.get(x.getGroup()).getKills() + "/" + groups.get(x.getGroup()).getDeaths())
                                + String.format("%5s", x.getStability())
                                + String.format("%4s", x.getAegis())
                                + String.format("%4s", x.getProtection())
                                + String.format("%4s", x.getResistance())
                                + String.format("%4s", x.getResolution())
                                + String.format("%4s", x.getAlacrity())
                                + String.format("%4s", x.getQuickness())
                                + LF);
                    }
                }
            } else {
                buffer.append(" # Stab Aegi Prot Resi Reso Alac Quik" + LF);
                buffer.append("--- ---  ---  ---  ---  ---  ---  ---" + LF);
                aggDbooners.sort(Comparator.naturalOrder());
                int count = Math.min(aggDbooners.size(), 15);
                for (DefensiveBooner x : aggDbooners.subList(0, count)) {
                    if (x.getDefensiveRating() > 0) {
                        buffer.append(String.format("%2s", x.getGroup())
                                //+ String.format("%5s", x.getDefensiveRating())
                                //+ String.format("%5s", groups.get(x.getGroup()).getKills() + "/" + groups.get(x.getGroup()).getDeaths())
                                + String.format("%5s", x.getStability())
                                + String.format("%5s", x.getAegis())
                                + String.format("%5s", x.getProtection())
                                + String.format("%5s", x.getResistance())
                                + String.format("%5s", x.getResolution())
                                + String.format("%5s", x.getAlacrity())
                                + String.format("%5s", x.getQuickness())
                                + LF);
                    }
                }
            }
            report.setDbooners(buffer.toString());
            System.out.println("Defensive Boon Uptime by Party:" + LF + buffer);
            System.out.println();
        }

        if (aggObooners.stream().anyMatch(d -> d.getOffensiveRating()>0)) {
            buffer = new StringBuffer();
            if (Parameters.getInstance().enableDiscordMobileMode) {
                buffer.append(" # MghtFuryVigrSwifAlacQuik" + LF);
                buffer.append("--- --- --- --- --- --- ---" + LF);
                aggObooners.sort(Comparator.naturalOrder());
                int count = Math.min(aggObooners.size(), 15);
                for (OffensiveBooner x : aggObooners.subList(0, count)) {
                    if (x.getOffensiveRating() > 0) {
                        buffer.append(String.format("%2s", x.getGroup())
                                //+ String.format("%5s", x.getOffensiveRating())
                                + String.format("%5s", x.getMight())
                                + String.format("%4s", x.getFury())
                                + String.format("%4s", x.getVigor())
                                + String.format("%4s", x.getSwiftness())
                                + String.format("%4s", x.getAlacrity())
                                + String.format("%4s", x.getQuickness())
                                //+ String.format("%8s", DPSer.withSuffix(x.getDownCn(), x.getDownCn() < 1000000 ? 0 : 2))
                                //+ String.format("%5s", x.getDowns())
                                //+ String.format("%6s", x.getKills())
                                + LF);
                    }
                }
            } else {
                buffer.append(" # Mght Fury Vigr Swif Alac Quik" + LF);
                buffer.append("--- ---  ---  ---  ---  ---  ---" + LF);
                aggObooners.sort(Comparator.naturalOrder());
                int count = Math.min(aggObooners.size(), 15);
                for (OffensiveBooner x : aggObooners.subList(0, count)) {
                    if (x.getOffensiveRating() > 0) {
                        buffer.append(String.format("%2s", x.getGroup())
                                //+ String.format("%5s", x.getOffensiveRating())
                                + String.format("%5s", x.getMight())
                                + String.format("%5s", x.getFury())
                                + String.format("%5s", x.getVigor())
                                + String.format("%5s", x.getSwiftness())
                                + String.format("%5s", x.getAlacrity())
                                + String.format("%5s", x.getQuickness())
                                //+ String.format("%8s", DPSer.withSuffix(x.getDownCn(), x.getDownCn() < 1000000 ? 0 : 2))
                                //+ String.format("%5s", x.getDowns())
                                //+ String.format("%6s", x.getKills())
                                + LF);
                    }
                }

            }
            report.setObooners(buffer.toString());
            System.out.println("Offensive Boon Uptime by Party:" + LF + buffer);
            System.out.println();
        }

        if (enemyDmgBySkill.size() > 0) {
            buffer = new StringBuffer();
            List<Map.Entry<String, Integer>> u = enemyDmgBySkill.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10).collect(Collectors.toList());
            buffer.append(" #  Skill                  Dmg" + LF);
            buffer.append("--- --------------------- ------" + LF);
            for (int z=0; z < u.size(); z++) {
                Map.Entry<String, Integer> x = u.get(z);
                if (x.getValue() > 0) {
                    buffer.append(String.format("%2s", z + 1) + "  "
                            + String.format("%-21s", StringUtils.left(x.getKey(), 21))
                            + String.format("%7s", DPSer.withSuffix(x.getValue(), x.getValue() < 1000000 ? 0 : x.getValue() >= 10000000 ? 1 : 2))
                            + LF);
                }
            }
            report.setEnemySkillDmg(buffer.toString());
            System.out.println("Enemy Top Damage Skills:" + LF + buffer);
            System.out.println();
        }

        if (enemies.size()>0) {
            Map<String, Map<String, EnemyBreakdown>> ebdMap = new TreeMap<>();
            enemies.forEach(e -> {
                if (ebdMap.containsKey(e.getTeam())) {
                    if (ebdMap.get(e.getTeam()).containsKey(DPSer.mapProf(e.getProfession()))) {
                        ebdMap.get(e.getTeam()).get(DPSer.mapProf(e.getProfession())).addEnemy(e);
                    } else {
                        ebdMap.get(e.getTeam()).put(DPSer.mapProf(e.getProfession()), new EnemyBreakdown(e));
                    }
                } else {
                    Map <String, EnemyBreakdown> map = new TreeMap<>();
                    map.put(DPSer.mapProf(e.getProfession()), new EnemyBreakdown(e));
                    ebdMap.put(e.getTeam(), map);
                }
            });
            List<String> keys = new ArrayList<>(ebdMap.keySet());
            if (!keys.isEmpty()) {
                String team1 = keys.get(0);
                int count1 = ebdMap.get(team1).values().stream().map(EnemyBreakdown::getCount).reduce(0, Integer::sum);
                List<EnemyBreakdown> e1 = ebdMap.get(team1).values().stream().sorted().collect(Collectors.toList());
                e1 = e1.stream().sorted().collect(Collectors.toList());
                int half1 = (int) Math.ceil((double)e1.size() / 2);
                String team2 = keys.size() > 1 ? keys.get(1) : null;
                int count2 = team2 != null ? ebdMap.get(team2).values().stream().map(EnemyBreakdown::getCount).reduce(0, Integer::sum) : 0;
                List<EnemyBreakdown> e2 = team2 != null ? ebdMap.get(team2).values().stream().sorted().collect(Collectors.toList()) : new ArrayList<>();
                e2 = e2.stream().sorted().collect(Collectors.toList());
                int half2 = (int) Math.ceil((double)e2.size() / 2);
                buffer = new StringBuffer();
                buffer.append(" #  Prof  Dmg     #  Prof  Dmg").append(LF);
                buffer.append("--- ---- -----   --- ---- -----").append(LF);
                buildEnemyBreakdown(buffer, team1, count1, e1, half1, team2, count2, e2, half2);
                if (existsEmptyTeams)
                    buffer.append((Parameters.getInstance().enableDiscordMobileMode ? "" : "--> ") + teamCountSummary.trim() + LF);
                report.setEnemyBreakdown(buffer.toString());
                System.out.println("Enemy Breakdown:" + LF + buffer);
                System.out.println();
            }
        }

        buffer = new StringBuffer();
        buffer.append(String.format("[Report] Squad Players: %d (Dmg: %s, Downs: %d, Deaths: %d) %s| Enemy Players: %d (Dmg: %s, Downs: %d, Deaths: %d)",
                players.length() - countNonSquadPlayers, DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 0 : sumPlayerDmg >= 10000000 ? 1 : 2), totalPlayersDowned, totalPlayersDead,
                countNonSquadPlayers > 0 ? "+"+countNonSquadPlayers+" Friendlies " : "", enemies.size(), DPSer.withSuffix(sumEnemyDmg, sumEnemyDmg < 1000000 ? 0 : sumEnemyDmg >= 10000000 ? 1 : 2), enemyDowns, enemyDeaths));
        report.setOverview(buffer.toString());
        System.out.println(buffer);
        System.out.println();
    }

    private static String buildTeamCountSummary(List<Enemy> enemies) {
        int redCount = (int) enemies.stream().filter(e -> e.getTeam().equals("Red")).count();
        int blueCount = (int) enemies.stream().filter(e -> e.getTeam().equals("Blue")).count();
        int greenCount = (int) enemies.stream().filter(e -> e.getTeam().equals("Green")).count();
        int unknownCount = (int) enemies.stream().filter(e -> e.getTeam().equals("")).count();
        return (redCount > 0 ? " Red: " + redCount : " ")
                + (blueCount > 0 ? " Blue: " + blueCount : "")
                + (greenCount > 0 ? " Green: " + greenCount : "")
                + (unknownCount > 0 ? " Unknown: " + unknownCount : "");
    }

    private static void buildEnemyBreakdown(StringBuffer buffer, String team1, int count1, List<EnemyBreakdown> e1, int half1, String team2, int count2, List<EnemyBreakdown> e2, int half2) {
        boolean isSingleTeam = team2 == null;
        for (int i = 0; i*2 < e1.size(); i++) {
            if (i == 0) {
                if (isSingleTeam && StringUtils.isEmpty(team1))
                    buffer.append(">>> Total: " + count1).append(LF);
                else if (StringUtils.isEmpty(team1))
                    buffer.append(">>> Team unknown: " + count1).append(LF);
                else
                    buffer.append(">>> " + team1 + ": " + count1).append(LF);
            }
            if (i+ half1 > e1.size())
                break;
            buffer.append(e1.get(i)).append("   ");
            if (i+ half1 < e1.size())
                buffer.append(e1.get(i+ half1));
            buffer.append(LF);
        }
        for (int i = 0; i*2 < e2.size(); i++) {
            if (i == 0) {
                if (StringUtils.isEmpty(team2))
                    buffer.append(">>> Team unknown: " + count2).append(LF);
                else
                    buffer.append(">>> " + team2 + ": " + count2).append(LF);
            }
            if (i+ half2 > e2.size())
                break;
            buffer.append(e2.get(i)).append("   ");
            if (i+ half2 < e2.size())
                buffer.append(e2.get(i+ half2));
            buffer.append(LF);
            if (i+ half2 >= e2.size())
                break;
        }
    }

    private static String getPlayerTeamText(int numPlayers, String team) {
        if (StringUtils.isEmpty(team)) {
            if (Parameters.getInstance().enableDiscordMobileMode)
                return StringUtils.leftPad(String.valueOf(numPlayers), 2) + " " + StringUtils.rightPad("Total", 6);
            else
                return StringUtils.leftPad(String.valueOf(numPlayers), 3) + " " + StringUtils.rightPad("Total", 6);
        } else {
            if (Parameters.getInstance().enableDiscordMobileMode)
                return StringUtils.leftPad(String.valueOf(numPlayers), 2) + " " + StringUtils.rightPad(team, 6);
            else
                return StringUtils.leftPad(String.valueOf(numPlayers), 3) + " " + StringUtils.rightPad(team, 6);
        }
    }

    private static List<Enemy> appendEnemySummary(String team, List<Enemy> enemies, StringBuffer buffer) {
        List<Enemy> thisTeam = enemies.stream().filter(e->team.equals(e.getTeam())).collect(Collectors.toList());
        if (thisTeam.size() > 0) {
            int sumDmg = thisTeam.stream().map(Enemy::getDamage).reduce(0, Integer::sum);
            int sumDps = thisTeam.stream().map(Enemy::getDps).reduce(0, Integer::sum);
            int sumDwn = thisTeam.stream().map(Enemy::getDowns).reduce(0, Integer::sum);
            int sumDed = thisTeam.stream().map(Enemy::getDeaths).reduce(0, Integer::sum);
            String playerText = getPlayerTeamText(thisTeam.size(), team);
            if (Parameters.getInstance().enableDiscordMobileMode) {
                buffer.append(String.format("%9s%6s%6s%5d%7d", playerText,
                        DPSer.withSuffix(sumDmg, sumDmg < 1000000 ? 0 : sumDmg >= 10000000 ? 1 : 2), DPSer.withSuffix(sumDps, 1),
                        sumDwn, sumDed));
            } else {
                buffer.append(String.format("%9s%7s%7s%6d%7d", playerText,
                        DPSer.withSuffix(sumDmg, sumDmg < 1000000 ? 0 : sumDmg >= 10000000 ? 1 : 2), DPSer.withSuffix(sumDps, 1),
                        sumDwn, sumDed));
            }
            buffer.append("\r\n");
        }
        return thisTeam;
    }

    private static String mapTeamID(int teamID) {
        //teamID = {705: 'Red', 882: 'Red', 2520: 'Red', 2739: 'Green', 2741: 'Green', 2752: 'Green', 432: 'Blue', 1277: 'Blue'}
        //guild hall teamID = {697: 'Red', 39: 'Green', 1989: 'Blue'}
        switch (teamID) {
            case 705:
            case 882:
            case 2520:
            case 697:
                return "Red";
            case 2739:
            case 2741:
            case 2752:
            case 39:
                return "Green";
            case 432:
            case 1277:
            case 1989:
                return "Blue";
            default:
                return "";
        }
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
            int quick = sumBoons.getQuickness();
            db.setQuickness(quick > 0 ? 100 * db.getQuickness() / quick : db.getQuickness());
            db.computeRating();
        }
    }

    private static void populateDefensiveBoons(DefensiveBooner dBooner, JSONArray bArray) {
        for (Object obj : bArray.toList()) {
            HashMap m = (HashMap)obj;
            int id = (int) (Integer) m.get("id");
            switch (id) {
                case 1122 : dBooner.setStability(dBooner.getStability() + getBuffGeneration(m)); break;
                case 743 : dBooner.setAegis(dBooner.getAegis() + getBuffGeneration(m)); break;
                case 717 : dBooner.setProtection(dBooner.getProtection() + getBuffGeneration(m)); break;
                case 26980 : dBooner.setResistance(dBooner.getResistance() + getBuffGeneration(m)); break;
                case 873 : dBooner.setResolution(dBooner.getResolution() + getBuffGeneration(m)); break;
                case 30328 : dBooner.setAlacrity(dBooner.getAlacrity() + getBuffGeneration(m)); break;
                case 1187 : dBooner.setQuickness(dBooner.getQuickness() + getBuffGeneration(m)); break;
            }
        }
    }

    private static void populateOffensiveBoons(OffensiveBooner oBooner, JSONArray bArray) {
        for (Object obj : bArray.toList()) {
            HashMap m = (HashMap)obj;
            int id = (int) (Integer) m.get("id");
            switch (id) {
                case 740 : oBooner.setMight(oBooner.getMight() + getBuffGeneration(m, true)); break;
                case 725 : oBooner.setFury(oBooner.getFury() + getBuffGeneration(m)); break;
                case 30328 : oBooner.setAlacrity(oBooner.getAlacrity() + getBuffGeneration(m)); break;
                case 1187 : oBooner.setQuickness(oBooner.getQuickness() + getBuffGeneration(m)); break;
                case 726 : oBooner.setVigor(oBooner.getVigor() + getBuffGeneration(m)); break;
                case 719 : oBooner.setSwiftness(oBooner.getSwiftness() + getBuffGeneration(m)); break;
            }
        }
    }

    private static int getBuffGeneration(HashMap m) {
        return getBuffGeneration(m, false);
    }

    private static int getBuffGeneration(HashMap m, boolean doUptime) {
        if (m.containsKey("buffData")) {
            List buffData = (List) m.get("buffData");
            if (buffData!=null && buffData.size()>0) {
                HashMap bdMap = (HashMap) buffData.get(0);
                if (!doUptime && bdMap.containsKey("presence")) {
                    BigDecimal presence = (BigDecimal) bdMap.get("presence");
                    if (presence.compareTo(BigDecimal.ZERO) > 0)
                        return presence.multiply(new BigDecimal(1000)).intValue();
                }
                if (bdMap.containsKey("uptime")) {
                    BigDecimal uptime = (BigDecimal) bdMap.get("uptime");
                    return uptime.multiply(new BigDecimal(1000)).intValue();
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
        sumBoons.setQuickness(sumBoons.getQuickness() + player.getQuickness());
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
