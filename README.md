***************************************************************************************
**MzFightReporter** 
- **Description:** Monitor [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send WvW fight reports to a Discord channel and optionally a Twitch channel.
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.
- **App Home:** https://github.com/Swedemon/MzFightReporter
***************************************************************************************
**Instructions:**
1.  Download and unzip the latest version of ```MzFightReporter_X.zip``` to any directory.
	- Available at:  https://github.com/Swedemon/MzFightReporter/releases
2.  Install Java for Windows.
	- Available at:  https://www.java.com/en/download/
3.  Create a webhook in your Discord server and copy the provided URL.
	- How to: https://www.google.com/search?q=create+a+discord+webhook
4.  Edit the file 'config.properties' located at your unzip location:
	- Set the value of **discordWebhook** to your new webhook URL.
	  - Example: ```discordWebhook=https://discord.com/api/webhooks/84073...```
	- *Only required if you altered the default arcdps log folder:* Set the value of **customLogFolder** to the path ArcDps saves logs including double slashes ```\\``` as shown.
	  - Example: ```customLogFolder=C:\\Your\\Path\\With\\Double\\Slashes```
5.  Open Guild Wars 2 and enter the game.  Type Alt-Shift-T to open ArcDps options.
	- Under Logging choose: save after squad combat
	- Recommended: Observe the other wvw squad settings such as 'min enemy participants' which we recommend set to 10.
- Note: Be careful not to share your webhook URL with other people.  If necessary you can delete it in Discord and create a new one.
- Note: If you use the Healing addon in order to save resources it is recommended to uncheck the option 'log healing' under the ArcDps options healing section.
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
