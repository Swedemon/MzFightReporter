package org.vmy;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
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
        bot.sendWebhookMessage(new FightReport());
        bot.client.close();
    }

    private DiscordBot openSession() throws Exception
    {
        if (client == null) {
            client = WebhookClient.withUrl(Parameters.getInstance().discordWebhook);
        }
        return this;
    }

    protected void sendWebhookMessage(FightReport report) throws InterruptedException {
        Parameters p = Parameters.getInstance();
        File graphImage = new File(p.homeDir + File.separator + "fightreport.png");

        // Send and log (using embed)
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(Color.CYAN.getAlpha());
        embedBuilder.setThumbnailUrl(p.discordThumbnail);
        embedBuilder.setDescription("> "+report.getZone()+"\n\n" + (report.getCommander()!=null?"**Commander**: "+report.getCommander()+"\n":"") + "**Duration**: "+report.getDuration()+"\n");
        String squadSummary = "```"+report.getSquadSummary()+"```";
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Squad Summary",squadSummary));
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
        if (p.showCCs && report.getCcs()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Outgoing CC's  (stuns immobs chills cripples)","```"+report.getCcs()+"```"));
        if (p.showQuickReport && report.getOverview()!=null)
            embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Quick Report","```"+report.getOverview()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(true,"\u200b",report.getUrl()==null || report.getUrl().isEmpty()?"[DPSReports using EI: Upload process failed]":"[Full Report]("+report.getUrl()+")"));
        //embedBuilder.setImageUrl("attachment://fightreport.png");
        embedBuilder.setTimestamp(Instant.now());
        WebhookEmbed embed = embedBuilder.build();
        client.send(embed);
        if (graphImage.exists() && p.graphPlayerLimit > 0 && p.showDamageGraph)
            client.send(graphImage);
        System.out.println("Discord msg sent via webhook.");
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
