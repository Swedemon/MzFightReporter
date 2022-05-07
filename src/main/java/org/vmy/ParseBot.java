package org.vmy;

import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
            int countEnemyPlayers = 0;
            int countFriendlies = 0;
            for (int i = 1; i < targets.length(); i++) {
                String name = targets.getJSONObject(i).getString("name");
                if (name.contains("-"))
                    countEnemyPlayers++;
                else
                    countFriendlies++;
            }

            //players
            JSONArray players = jsonTop.getJSONArray("players");
            List<DPSer> dpsers = new ArrayList<DPSer>();
            List<Cleanser> cleansers = new ArrayList<Cleanser>();
            List<Stripper> strippers = new ArrayList<Stripper>();
            List<DefensiveBooner> dbooners = new ArrayList<DefensiveBooner>();
            int sumPlayerDps = 0;
            int sumPlayerDmg = 0;
            int countEnemyDowns = 0;
            int countEnemyDeaths = 0;
            int sumEnemyDps = 0;
            int sumEnemyDmg = 0;
            DefensiveBooner sumBoons = new DefensiveBooner("Total", "Total");
            String commander = null;
            for (int i = 0; i < players.length(); i++) {
                JSONObject currPlayer = players.getJSONObject(i);
                if (currPlayer.getBoolean("hasCommanderTag"))
                    commander = commander==null ? currPlayer.getString("name") : "n/a";
                JSONObject playerDpsAll = currPlayer.getJSONArray("dpsAll").getJSONObject(0);
                sumPlayerDps += playerDpsAll.getBigInteger("dps").intValue();
                sumPlayerDmg += playerDpsAll.getBigInteger("damage").intValue();
                //JSONObject playerStatsAll = currPlayer.getJSONArray("statsAll").getJSONObject(0);
                //countEnemyDowns += playerStatsAll.getBigInteger("downed").intValue();
                //countEnemyDeaths += playerStatsAll.getBigInteger("killed").intValue();
                List<Object> playerStatsTargets = currPlayer.getJSONArray("statsTargets").toList();
                for (Object a : playerStatsTargets) {
                    for (Object b : (List<Object>)a) {
                        HashMap<String, Integer> map = (HashMap<String, Integer>) b;
                        countEnemyDowns += map.get("downed");
                        countEnemyDeaths += map.get("killed");
                    }
                }
                JSONObject playerDefenses = currPlayer.getJSONArray("defenses").getJSONObject(0);
                sumEnemyDmg += playerDefenses.getBigInteger("damageTaken").intValue();
                JSONObject currPlayerDpsTargets = currPlayer.getJSONArray("dpsTargets").getJSONArray(0).getJSONObject(0);
                dpsers.add(new DPSer(
                        currPlayer.getString("name"),
                        currPlayer.getString("profession"),
                        playerDpsAll.getBigInteger("damage").intValue(),
                        playerDpsAll.getBigInteger("dps").intValue()));
                JSONObject currPlayerSupport = currPlayer.getJSONArray("support").getJSONObject(0);
                cleansers.add(new Cleanser(
                        currPlayer.getString("name"),
                        currPlayer.getString("profession"),
                        currPlayerSupport.getBigInteger("condiCleanse").intValue()));
                strippers.add(new Stripper(
                        currPlayer.getString("name"),
                        currPlayer.getString("profession"),
                        currPlayerSupport.getBigInteger("boonStrips").intValue()));

                JSONArray dArray = currPlayer.getJSONArray("damage1S").getJSONArray(0);
                List<Object> oList = dArray.toList();
                report.getDmgMap().put(currPlayer.getString("name"),oList);

                DefensiveBooner dBooner = new DefensiveBooner(currPlayer.getString("name"),currPlayer.getString("profession"));
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

            //base info
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
                if (mechanics.length()>0)
                    totalPlayersDead = mechanics.getJSONObject(0).getJSONArray("mechanicsData").length();
                if (mechanics.length()>1)
                    totalPlayersDowned = mechanics.getJSONObject(1).getJSONArray("mechanicsData").length();
            }

            //write to buffer
            StringBuffer buffer = new StringBuffer();
            buffer.append(" Players   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", players.length(),
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDps, 1),
                    totalPlayersDowned, totalPlayersDead));
            report.setSquadSummary(buffer.toString());

            if (countFriendlies == 1)
                report.setFriendliesSummary("plus " + countFriendlies + " friendly (total = " + (countFriendlies+players.length()) + " players)");
            else if (++countFriendlies > 1)
                report.setFriendliesSummary("plus " + countFriendlies + " friendlies (total = " + (countFriendlies+players.length()) + " players)");

            //approximate enemyDps
            sumEnemyDps = (int) sumEnemyDmg / (sumPlayerDmg / sumPlayerDps);

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
                buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setDamage(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                     Cleanses" + CRLF);
            buffer.append("--- -------------------------  --------" + CRLF);
            cleansers.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = cleansers.size() > 10 ? 10 : cleansers.size();
            for (Cleanser x : cleansers.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setCleanses(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                      Strips" + CRLF);
            buffer.append("--- -------------------------  --------" + CRLF);
            strippers.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = strippers.size() > 10 ? 10 : strippers.size();
            for (Stripper x : strippers.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            report.setStrips(buffer.toString());

            buffer = new StringBuffer();
            buffer.append(" #  Player                      Rating" + CRLF);
            buffer.append("--- -------------------------  --------" + CRLF);
            dbooners.sort((d1, d2) -> d1.compareTo(d2));
            index = 1;
            count = dbooners.size() > 10 ? 10 : cleansers.size();
            for (DefensiveBooner x : dbooners.subList(0, count))
                buffer.append(String.format("%2s", (index++)) + "  " + x + CRLF);
            //buffer.append("=> stab*3+aegis*2+prot+resist*.5+resolv+alac*.5" + CRLF);
            report.setDbooners(buffer.toString());
        } finally {
            is.close();
        }

        return report;
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
        //currPlayerSupport.getBigInteger("condiCleanse").intValue()
        for (Object obj : bArray.toList()) {
            int sfesef = 0;
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
        int debug = 0;
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
