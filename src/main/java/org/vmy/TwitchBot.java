package org.vmy;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;

public class TwitchBot {

    private static TwitchBot singleton;

    public static TwitchBot getSingletonInstance() {
        if (singleton == null)
            singleton = new TwitchBot();
        return singleton;
    }

    private TwitchBot() {}

    TwitchClient twitchClient = null;

    public static void main(String[] args) throws Exception {
        TwitchBot bot = TwitchBot.getSingletonInstance();
        bot.sendMessage("Twitch bot sending a test message.");
    }

    protected void sendMessage(String msg) throws Exception {
        String channelName = Parameters.getInstance().twitchChannelName;
        String accessToken = Parameters.getInstance().twitchBotToken;

        if ("".equals(channelName) || "".equals(accessToken))
            return;

        try {
            if (twitchClient == null)
                twitchClient = TwitchClientBuilder.builder()
                    .withEnableHelix(false)
                    .withEnableChat(true)
                    .withChatAccount(new OAuth2Credential("twitch", accessToken))
                    .withDefaultAuthToken(new OAuth2Credential("twitch", accessToken))
                    .build();
            twitchClient.getChat().sendMessage(channelName, msg);
            System.out.println("Twitch msg sent to " + channelName + ".");
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void finalize() { if (twitchClient!=null) twitchClient.close(); }
}
