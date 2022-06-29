***************************************************************************************
**MzFightReporter** 
- **Description:** Monitor ArcDps logs, parse data using Elite Insights, and send WvW fight reports to a Discord channel and optionally a Twitch channel.
- **Credit:** Although a completely separate implementation this app was originally developed to mimic the "Indo-bot" (created by Tiff) but for broader use.
- **Home:** https://github.com/Swedemon/MzFightReporter
***************************************************************************************
**Instructions:**
1.  Download and unzip the latest version of ```MzFightReporter_X.zip``` to any directory.
2.  Install Java for Windows: https://www.java.com/en/download/
3.  Create a webhook in your Discord server and copy the provided URL.
	- Guide: http://help.dashe.io/en/articles/2521940-how-to-create-a-discord-webhook-url
4.  Edit the file 'config.properties' located at your unzip location:
	- Set the value of "discordWebhook" to your new webhook URL.
	  - Example: ```discordWebhook=https://discord.com/api/webhooks/84073...```
	- (If necessary) Set the value of "customLogFolder" to the path ArcDps saves logs including double slashes as shown.
	  - Example: ```customLogFolder=C:\\Your\\Path\\With\\Double\\Slashes```
5.  Open Guild Wars 2 and enter the game.  Type Alt-Shift-T to open ArcDps options.
	- Under Logging choose: save after squad combat
	  - Note: Observe the other wvw squad settings such as 'min enemy participants'.
- Note: Be careful not to share your webhook URL with other people.  If necessary you can remove it and create a new one.
***************************************************************************************
**Run the app:**
- Go to the install location and double click the 'MzFightReporter' Windows Batch file.
***************************************************************************************
**(Optional) Twitch Bot Instructions:**
1.  Create and login to a new account on twitch.tv to represent your bot.
2.  Go to https://twitchtokengenerator.com/
	- Choose the option: Bot Chat Token
	- Authorize. 
	  - Note: You can manage these connections at https://www.twitch.tv/settings/connections
3.  Copy the provided "ACCESS TOKEN".
4.  Edit the file **config.properties** located at your unzip location.
	- Set the value of "twitchChannelName" to your main channel name. 
	  - Example: ```twitchChannelName=amouranth```
	- Set the value of "twitchBotToken" to the copied ACCESS TOKEN.
	  - Example: ```twitchBotToken=li3j3l1ijlj13llj13lj1i3```
- Note: To disable the twitch bot revert ```twitchChannelName``` back to empty.
- Note: Be careful not to share your access token with other people.  If necessary you can disconnect it and create a new one.
***************************************************************************************