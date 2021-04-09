import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TheEngineNonDetailed {

    private static String jsonFolder = "C:\\Users\\Drew\\Documents\\Guild Wars 2\\addons\\arcdps\\arcdps.cbtlogs\\1\\";
    private static String jsonFile   = jsonFolder + "20210404-235720_wvw_kill.json";

    public static void main(String[] args) throws Exception {
        File f = new File(jsonFile);
        if (f.exists()) {
            processCsvLog(f);
        }
    }

    private static void processCsvLog(File f) throws IOException {
        JSONObject jo = new JSONObject();
        InputStream is = new FileInputStream(f);
        String jsonTxt = IOUtils.toString(is, "UTF-8");
        //System.out.println(jsonTxt);
        JSONObject jsonTop = new JSONObject(jsonTxt);

        //header
        System.out.println("zone="+jsonTop.getString("fightName"));
        System.out.println("duration="+jsonTop.getString("duration"));

        //targets
        JSONArray targets = jsonTop.getJSONArray("targets");
        JSONObject enemyDpsAll = targets.getJSONObject(0).getJSONArray("dpsAll").getJSONObject(0);
        System.out.println("totalEnemyDps="+enemyDpsAll.getBigInteger("dps"));
        System.out.println("totalEnemyDmg="+enemyDpsAll.getBigInteger("damage"));
        JSONObject enemyStatsAll = targets.getJSONObject(0).getJSONArray("statsAll").getJSONObject(0);
        System.out.println("totalEnemyKilled="+enemyStatsAll.getBigInteger("killed"));
        System.out.println("totalEnemyDowned="+enemyStatsAll.getBigInteger("downed"));
        JSONArray arrayEnemyTotDmgDist = targets.getJSONObject(0).getJSONArray("totalDamageDist").getJSONArray(0);
        System.out.println("totalEnemyDmgDistCount="+arrayEnemyTotDmgDist.length());
        JSONArray arrayEnemyTotDmgTaken = targets.getJSONObject(0).getJSONArray("totalDamageTaken").getJSONArray(0);
        System.out.println("totalEnemyDmgTakenCount="+arrayEnemyTotDmgTaken.length());
        JSONArray arrayEnemyRotation = targets.getJSONObject(0).getJSONArray("rotation");
        System.out.println("totalEnemyRotationCount="+arrayEnemyRotation.length());

        System.out.println(" Enemies   Damage     DPS     Downs    Deaths");
        System.out.println("---------  ---------  -----  -------  --------");
        System.out.println();

        //players
        JSONArray players = jsonTop.getJSONArray("players");
        System.out.println("totalPlayers="+players.length());
        HashSet playerTargetIdSet = new HashSet();
        List<DPSer> dpsers = new ArrayList<DPSer>();
        List<Cleanser> cleansers = new ArrayList<Cleanser>();
        List<Stripper> strippers = new ArrayList<Stripper>();

        for (int i=0; i<players.length(); i++) {
            //player
            JSONObject currPlayer = players.getJSONObject(i);
            //System.out.println("name="+currPlayer.getString("name"));
            //if (currPlayer.getBoolean("hasCommanderTag"))
                //System.out.println("isCommander=true");
            //System.out.println("profession="+currPlayer.getString("profession"));
            //player dpsTargets
            JSONObject currPlayerDpsTargets = currPlayer.getJSONArray("dpsTargets").getJSONArray(0).getJSONObject(0);
            dpsers.add(new DPSer(
                    currPlayer.getString("name"),
                    currPlayer.getString("profession"),
                    currPlayerDpsTargets.getBigInteger("damage").intValue(),
                    currPlayerDpsTargets.getBigInteger("dps").intValue()));
            //System.out.println("dps="+currPlayerDpsTargets.getBigInteger("dps"));
            //System.out.println("damage="+currPlayerDpsTargets.getBigInteger("damage"));
            //player targetDamageDist
            JSONArray currArrayTargetDamageDist = currPlayer.getJSONArray("targetDamageDist").getJSONArray(0).getJSONArray(0);
            //System.out.println("targetDamageDistCount="+currArrayTargetDamageDist.length());
            for(int j=0; j<currArrayTargetDamageDist.length(); j++) {
                playerTargetIdSet.add(currArrayTargetDamageDist.getJSONObject(j).getBigInteger("id"));
            }
            //player support
            JSONObject currPlayerSupport = currPlayer.getJSONArray("support").getJSONObject(0);
            cleansers.add(new Cleanser(
                    currPlayer.getString("name"),
                    currPlayer.getString("profession"),
                    currPlayerSupport.getBigInteger("condiCleanse").intValue()));
            strippers.add(new Stripper(
                    currPlayer.getString("name"),
                    currPlayer.getString("profession"),
                    currPlayerSupport.getBigInteger("boonStrips").intValue()));
            //System.out.println("condiCleanse="+currPlayerSupport.getBigInteger("condiCleanse"));
            //System.out.println("boonStrips="+currPlayerSupport.getBigInteger("boonStrips"));
        }
        System.out.println(" #  Player                      Damage     DPS");
        System.out.println("--- -------------------------  --------  -------");
                          // 1  Anastasiya Dolya (Her)      178.2k   101.1k
        dpsers.sort((d1,d2) -> d1.compareTo(d2));
        int index=1;
        for(DPSer x : dpsers.subList(0,10))
            System.out.println(String.format("%2s",(index++))+"  "+x);
        System.out.println(" #  Player                     Cleanses");
        System.out.println("--- -------------------------  --------");
                          // 1  Innovolt (Scr)               108
        cleansers.sort((d1,d2) -> d1.compareTo(d2));
        index=1;
        for(Cleanser x : cleansers.subList(0,10))
            System.out.println(String.format("%2s",(index++))+"  "+x);
        System.out.println(" #  Player                      Strips");
        System.out.println("--- -------------------------  --------");
        strippers.sort((d1,d2) -> d1.compareTo(d2));
        index=1;
        for(Stripper x : strippers.subList(0,10))
            System.out.println(String.format("%2s",(index++))+"  "+x);
        System.out.println("-----");

        System.out.println("playerTargetIdSet uniqueCount="+playerTargetIdSet.size());

        //mechanics
        JSONArray mechanics = jsonTop.getJSONArray("mechanics");
        System.out.println("totalPlayersDead="+mechanics.getJSONObject(0).getJSONArray("mechanicsData").length());
        System.out.println("totalPlayersDowned="+mechanics.getJSONObject(1).getJSONArray("mechanicsData").length());

        //uploadLinks
        JSONArray uploadLinks = jsonTop.getJSONArray("uploadLinks");
        System.out.println("uploadLink="+uploadLinks.getString(0));

        //timeEnd
        System.out.println("timeEnd="+jsonTop.getString("timeEnd"));
    }
}
