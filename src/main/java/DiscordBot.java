import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
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

    private JDA jdaSession = null;

    public static void main(String[] args) throws Exception {
        DiscordBot bot = new DiscordBot().openSession();
        bot.sendMessage(Parameters.discordChannel,new FightReport());
    }

    private DiscordBot openSession() throws Exception
    {
        if (jdaSession == null) {
            JDABuilder builder = JDABuilder.createDefault(Parameters.secretBotToken);
            jdaSession = builder.build();
            jdaSession.awaitReady();
        }
        return this;
    }

    protected void sendMessage(String channelName, FightReport report) throws InterruptedException
    {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setThumbnail(Parameters.thumbnail);
        embedBuilder.setDescription("> "+report.getZone()+"\n\n" + (report.getCommander()!=null?"**Commander**: "+report.getCommander()+"\n":"") + "**Duration**: "+report.getDuration()+"\n");
        embedBuilder.addField("Squad Summary","```"+report.getSquadSummary()+"```",false);
        embedBuilder.addField("Enemy Summary","```"+report.getEnemySummary()+"```",false);
        embedBuilder.addField("Damage","```"+report.getDamage()+"```",false);
        embedBuilder.addField("Cleanses","```"+report.getCleanses()+"```",false);
        embedBuilder.addField("Strips","```"+report.getStrips()+"```\n"+(report.getUrl()==null?"":"[Full Report]("+report.getUrl()+")"),false);
        embedBuilder.setTimestamp(Instant.now());

        //TODO filter further by specific discord server
        List<TextChannel> channelList = jdaSession.getTextChannelsByName(channelName, true);
        for (TextChannel c : channelList)
        {
            c.sendMessage(embedBuilder.build()).queue();
        }
    }

    public void finalize() {
        if (jdaSession != null)
            jdaSession.shutdownNow();
    }

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
