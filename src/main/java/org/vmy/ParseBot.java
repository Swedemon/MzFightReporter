package org.vmy;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
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
            for (int i = 1; i < targets.length(); i++) {
                countEnemyPlayers++;
            }

            //players
            JSONArray players = jsonTop.getJSONArray("players");
            List<DPSer> dpsers = new ArrayList<DPSer>();
            List<Cleanser> cleansers = new ArrayList<Cleanser>();
            List<Stripper> strippers = new ArrayList<Stripper>();
            int sumPlayerDps = 0;
            int sumPlayerDmg = 0;
            int countEnemyDowns = 0;
            int countEnemyDeaths = 0;
            int sumEnemyDps = 0;
            int sumEnemyDmg = 0;
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
            }

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

            StringBuffer buffer = new StringBuffer();
            buffer.append(" Players   Damage    DPS    Downs    Deaths" + CRLF);
            buffer.append("--------- --------  -----  -------  --------" + CRLF);
            buffer.append(String.format("%6d %10s %7s %6d %8d", players.length(),
                    DPSer.withSuffix(sumPlayerDmg, sumPlayerDmg < 1000000 ? 1 : 2), DPSer.withSuffix(sumPlayerDps, 1),
                    totalPlayersDowned, totalPlayersDead));
            report.setSquadSummary(buffer.toString());

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
        } finally {
            is.close();
        }

        return report;
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
