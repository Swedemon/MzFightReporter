***************************************************************************************
**MzFightReporter** 
- **Description:** Monitor [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send WvW fight reports to a Discord channel and optionally a Twitch channel.
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.
- **App Home:** https://github.com/Swedemon/MzFightReporter
***************************************************************************************
**Setup Instructions:** (~5-10 minutes)
1.  Download and unzip the latest version of ```MzFightReporter_X.zip``` to any directory.
	- Available at:  https://github.com/Swedemon/MzFightReporter/releases
2.  Install Java for Windows.
	- Available at:  https://www.java.com/en/download/
3.  Create a webhook in your Discord server:
	1. In your Discord create a channel.  Right click on it and choose 'Edit Channel'.
	2. On the left choose 'Integrations'.  Next click on 'Webhooks'.
	3. Click on 'New Webhook'.  Click on 'Copy Webhook URL'.
4.  Edit the file **config.properties** located at your unzip location:
	1. Set the value of 'discordWebhook' to your new webhook URL.
		- Example: ```discordWebhook=https://discord.com/api/webhooks/84073...```
	2. *Only required if you changed the default arcdps log folder.* Set the value of 'customLogFolder' to the path ArcDps saves logs with double slashes ```\\``` as shown.
		- Example: ```customLogFolder=C:\\Your\\Path\\With\\Double\\Slashes```
5.  Open Guild Wars 2 and enter the game.  Type Alt-Shift-T to open ArcDps options.
	1. Under 'Logging' choose: save after squad combat
	2. Recommended: Observe the other wvw squad settings such as 'min enemy participants' set to 10.
- Note: Be careful not to share your webhook URL with other people.  If necessary you can delete it in Discord and create a new one.
- Note: If you use the Healing addon in order to save resources it is recommended to uncheck the option 'log healing' under the ArcDps options healing section.
***************************************************************************************
**Run the app:**
- Go to the install location and double click the 'MzFightReporter' Windows Batch file.
***************************************************************************************
**(Optional) Twitch Bot Instructions:** (~5 minutes)
1.  Create and login to a new account on twitch.tv to represent your bot.  Optionally, you can use your existing account.
2.  Go to https://twitchtokengenerator.com/
	1. Choose the option: Bot Chat Token
	2. Authorize. 
		- Note: You can manage these connections at https://www.twitch.tv/settings/connections
3.  Copy the provided 'ACCESS TOKEN'.
4.  Edit the file **config.properties** located at your unzip location.
	1. Set the value of 'twitchChannelName' to your main channel name. 
		- Example: ```twitchChannelName=amouranth```
	2. Set the value of 'twitchBotToken' to the copied ACCESS TOKEN.
		- Example: ```twitchBotToken=li3j3l1ijlj13llj13lj1i3```
- Note: To disable the twitch bot revert ```twitchChannelName``` back to empty.
- Note: Be careful not to share your access token with other people.  If necessary you can disconnect it and create a new one.
***************************************************************************************
