package org.vmy;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import org.apache.commons.lang.StringUtils;
import org.vmy.util.FightReport;

import java.awt.Color;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DiscordBot {

    private static DiscordBot singleton;
    private static String url = "";
    private static String threadId = "";

    public static DiscordBot getSingletonInstance() {
        if (singleton == null)
            singleton = new DiscordBot();
        return singleton;
    }

    private DiscordBot() {
        try {
            openSession();
        } catch (Exception e) {
            e.printStackTrace(System.out);
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
            buildSession();
        }
        return this;
    }

    public void buildSession() {
        url = Parameters.getInstance().discordWebhook;
        int indexOfThreadId = url.indexOf("?thread_id=");
        if (indexOfThreadId > 0) {
            String sThreadId = url.substring(indexOfThreadId + 11);
            url = url.substring(0, indexOfThreadId);
            client = WebhookClient.withUrl(url).onThread(Long.parseLong(sThreadId));
        } else {
            client = WebhookClient.withUrl(url);
        }
    }

    protected void sendMainMessage(FightReport report) {
        Parameters p = Parameters.getInstance();

        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(Color.CYAN.getAlpha());
        //embedBuilder.set(p.discordThumbnail);
        embedBuilder.setImageUrl("https://i.stack.imgur.com/Fzh0w.png");
        String iconUrl = report.getZone().startsWith("Eternal") ? "https://i.imgur.com/eFMK8D4.png"
                : report.getZone().startsWith("Green") ? "https://i.imgur.com/vyO4yKd.png"
                : report.getZone().startsWith("Blue") ? "https://i.imgur.com/xlg6JZp.png"
                : report.getZone().startsWith("Red") ? "https://i.imgur.com/hIq5RuB.png"
                : report.getZone().contains("Edge") ? "https://i.imgur.com/MFjFSZW.png"
                : "https://i.imgur.com/B0iKe5d.png"; //guild hall
        embedBuilder.setAuthor(new WebhookEmbed.EmbedAuthor(report.getZone(), iconUrl, "https://github.com/Swedemon/MzFightReporter"));
        if (!StringUtils.isEmpty(report.getUrl()))
            embedBuilder.setTitle(new WebhookEmbed.EmbedTitle("Full Report", report.getUrl()));
        embedBuilder.setDescription((report.getCommander()!=null?"**Commander**: "+report.getCommander()+"\n":"") + "**Time**: "+report.getEndTime()+"\n" + "**Duration**: "+report.getDuration()+"\n");

        //build embed field list
        List<WebhookEmbed.EmbedField> embedFields = new ArrayList<>();
        if (p.showSquadSummary && report.getSquadSummary()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Squad Summary","```"+report.getSquadSummary()+"```"));
        if (p.showEnemySummary && report.getEnemySummary()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Enemy Summary","```"+report.getEnemySummary()+"```"));
        if (p.showDamage && report.getDamage()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Damage & Down Contribution","```"+report.getDamage()+"```"));
        if (p.showSpikeDmg && report.getSpikers()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Spike Damage","```"+report.getSpikers()+"```"));
        if (p.showStrips && report.getStrips()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Strips","```"+report.getStrips()+"```"));
        if (p.showCleanses && report.getCleanses()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Cleanses","```"+report.getCleanses()+"```"));
        if (p.showHeals && report.getHealers()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Heals & Barrier (heal addon required)","```"+report.getHealers()+"```"));
        if (p.showCCs && report.getCcs()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Outgoing CC's (stuns immobs chills cripples) & Interrupts","```"+report.getCcs()+"```"));
        if (p.showDownsKills && report.getDownsKills()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Outgoing Downs & Kills","```"+report.getDownsKills()+"```"));
        if (p.showDefensiveBoons && report.getDbooners()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Defensive Boon Uptime by Party","```"+report.getDbooners()+"```"));
        if (p.showOffensiveBoons && report.getObooners()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Offensive Boon Uptime by Party","```"+report.getObooners()+"```"));
        if (p.showEnemyBreakdown && report.getEnemyBreakdown()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Enemy Breakdown","```"+ StringUtils.left(report.getEnemyBreakdown(), 1018)+"```"));
        if (p.showQuickReport && report.getOverview()!=null)
            embedFields.add(new WebhookEmbed.EmbedField(false,"Quick Report","```"+report.getOverview()+"```"));

        if (embedFields.size() > 8)
            for (int i=0; i < 8; i++)
                embedBuilder.addField(embedFields.get(i));
        else {
            embedFields.forEach(embedBuilder::addField);
        }

        WebhookEmbed embed = embedBuilder.build();
        if (client != null) {
            client.send(embed);
        } else {
            throw new RuntimeException("Unable to connect to the provided Discord webhook.");
        }

        if (embedFields.size() > 8) {

            embedBuilder = new WebhookEmbedBuilder();
            embedBuilder.setColor(Color.CYAN.getAlpha());
            embedBuilder.setImageUrl("https://i.stack.imgur.com/Fzh0w.png");

            for (int i=8; i < embedFields.size(); i++)
                embedBuilder.addField(embedFields.get(i));

            embed = embedBuilder.build();
            if (client != null) {
                client.send(embed);
            } else {
                throw new RuntimeException("Unable to connect to the provided Discord webhook.");
            }
        }

        System.out.println("Discord fight report msg sent.");
    }

    protected void sendReportUrlMessage(String url, String endTime) {
        WebhookEmbedBuilder embedBuilder = new WebhookEmbedBuilder();
        embedBuilder.setColor(Color.CYAN.getAlpha());

        embedBuilder.addField(new WebhookEmbed.EmbedField(true,"\u200b",StringUtils.isEmpty(url)?"[DPSReports using EI: Upload process failed]":"[Full Report]("+url+")"));

        WebhookEmbed embed = embedBuilder.build();
        if (client != null) {
            client.send(embed);
            System.out.println("Discord URL msg sent.");
        } else {
            throw new RuntimeException("Unable to connect to the provided Discord webhook.");
        }
    }

    protected void sendGraphMessage() {
        Parameters p = Parameters.getInstance();

        File graphImage = new File(p.homeDir + File.separator + "fightreport.png");
        if (graphImage.exists() && p.graphPlayerLimit > 0 && p.showDamageGraph) {
            if (client != null) {
                client.send(graphImage);
                System.out.println("Discord graph msg sent.");
            } else {
                throw new RuntimeException("Unable to connect to the provided Discord webhook.");
            }
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
