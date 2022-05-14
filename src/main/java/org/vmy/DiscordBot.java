package org.vmy;

import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.io.File;
import java.io.PrintStream;
import java.time.Instant;
import java.util.List;

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
        if (report.getFriendliesSummary() != null)
            squadSummary += " _* " + report.getFriendliesSummary() + "_";
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Squad Summary",squadSummary));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Enemy Summary","```"+report.getEnemySummary()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Damage","```"+report.getDamage()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Cleanses","```"+report.getCleanses()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Strips","```"+report.getStrips()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Defensive Boons","```"+report.getDbooners()+"```"));
        embedBuilder.addField(new WebhookEmbed.EmbedField(false,"Outgoing CC's  (stuns immobs chills cripples)","```"+report.getCcs()+"```\n"+(report.getUrl()==null?"":"[Full Report]("+report.getUrl()+")")));
        //embedBuilder.setImageUrl("attachment://fightreport.png");
        embedBuilder.setTimestamp(Instant.now());
        WebhookEmbed embed = embedBuilder.build();
        client.send(embed);

        if (graphImage.exists() && p.graphPlayerLimit > 0)
            client.send(graphImage);
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
