package org.vmy;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.apache.commons.lang.StringUtils;
import org.vmy.util.FightReport;

import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.time.Instant;

public class DiscordBot {

    private static DiscordBot singleton;

    public static DiscordBot getSingletonInstance() {
        if (singleton == null)
            singleton = new DiscordBot();
        return singleton;
    }

    private DiscordBot() {
        try {
            openSession();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private WebhookClient client = null;

    public static void main(String[] args) throws Exception {
        DiscordBot bot = new DiscordBot().openSession();
        bot.sendMainMessage(new FightReport());
        bot.client.close();
    }

    private DiscordBot openSession()
    {
        if (client == null) {
            client = WebhookClient.withUrl(Parameters.getInstance().discordWebhook);
        }
        return this;
    }

    protected void sendMainMessage(FightReport report) {
        Parameters p = Parameters.getInstance();

        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(Color.CYAN.getAlpha());
        embedBuilder.setThumbnailUrl(p.discordThumbnail);
        embedBuilder.setDescription("> "+report.getZone()+"\n\n" + (report.getCommander()!=null?"**Commander**: "+report.getCommander()+"\n":"") + "**Time**: "+report.getEndTime()+"\n" + "**Duration**: "+report.getDuration()+"\n");
        if (p.showSquadSummary && report.getSquadSummary()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Squad Summary","```"+report.getSquadSummary()+"```"));
        if (p.showEnemySummary && report.getEnemySummary()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Enemy Summary","```"+report.getEnemySummary()+"```"));
        if (p.showDamage && report.getDamage()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Damage","```"+report.getDamage()+"```"));
        if (p.showSpikeDmg && report.getSpikers()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Spike Damage","```"+report.getSpikers()+"```"));
        if (p.showCleanses && report.getCleanses()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Cleanses","```"+report.getCleanses()+"```"));
        if (p.showStrips && report.getStrips()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Strips","```"+report.getStrips()+"```"));
        if (p.showDefensiveBoons && report.getDbooners()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Defensive Boons","```"+report.getDbooners()+"```"));
        if (p.showHeals && report.getHealers()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Heals  (only accurate for healers w/ arcdps heal addon)","```"+report.getHealers()+"```"));
        if (p.showDownsKills && report.getDownsKills()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Outgoing Downs & Kills","```"+report.getDownsKills()+"```"));
        if (p.showCCs && report.getCcs()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Outgoing CC's  (stuns immobs chills cripples)","```"+report.getCcs()+"```"));
        if (p.showEnemyBreakdown && report.getEnemyBreakdown()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Enemy Breakdown","```"+ StringUtils.left(report.getEnemyBreakdown(), 1024)+"```"));
        if (p.showQuickReport && report.getOverview()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Quick Report","```"+report.getOverview()+"```"));
        embedBuilder.setTimestamp(Instant.now());

        WebhookEmbed embed = embedBuilder.build();
        client.send(embed);
        System.out.println("Discord fight report msg sent.");
    }

    protected void sendReportUrlMessage(String url) {
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(Color.CYAN.getAlpha());

        embedBuilder.addField(new WebhookEmbed.EmbedField(true,"\u200b",StringUtils.isEmpty(url)?"[DPSReports using EI: Upload process failed]":"[Full Report]("+url+")"));

        WebhookEmbed embed = embedBuilder.build();
        client.send(embed);
        System.out.println("Discord URL msg sent.");
    }

    protected void sendGraphMessage() {
        Parameters p = Parameters.getInstance();

        File graphImage = new File(p.homeDir + File.separator + "fightreport.png");
        if (graphImage.exists() && p.graphPlayerLimit > 0 && p.showDamageGraph) {
            client.send(graphImage);
            System.out.println("Discord graph msg sent.");
        }
    }

    public void finalize() { if (client!=null) client.close(); }

    //hide needless errors via class block
    {
        PrintStream filterOut = new PrintStream(System.err) {
            public void println(String l) {
                if (!l.startsWith("SLF4J")) {
                    super.println(l);
                }
            }
        };
        System.setErr(filterOut);
    }
}
