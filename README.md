# MzFightReporter (Guild Wars 2 WvW)
- **Description:** Monitor [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send WvW fight reports to a Discord channel and optionally a Twitch channel.
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.  Major credit to the Elite Insights and ArcDps devs.
## Setup Instructions
1.  Download and unzip the latest version of ```MzFightReporter_X.zip``` to any directory. [![downloads](https://img.shields.io/github/downloads/Swedemon/MzFightReporter/total)](https://github.com/Swedemon/MzFightReporter/releases/latest)
	- Available under 'Assets':  https://github.com/Swedemon/MzFightReporter/releases
2.  Install Java for Windows.  (this ensures Java is in your system path)
	- Available at:  https://www.java.com/en/download/
3.  Create a webhook in your Discord server:
	1. In your Discord create a channel.  Right click on it and choose 'Edit Channel'.
	2. On the left choose 'Integrations'.  Next click on 'Webhooks'.
	3. Click on 'New Webhook'.  Click on 'Copy Webhook URL'.
4.  Edit the file **config.properties** located at your unzip location (you can use Notepad):
	1. Set the value of 'discordWebhook' to your new webhook URL.
		- Example: ```discordWebhook=https://discord.com/api/webhooks/84073...```
5.  Open Guild Wars 2 and enter the game.  Type Alt-Shift-T to open ArcDps options.
	1. Under 'Logging' choose: save after squad combat
	2. Under 'Logging' beside the 'Open' button enter: ```c:\arc```
		- Observe the other WvW squad settings such as 'min enemy participants'.  Set this to 10 if you want.
6.  Setup complete.  Run the background program and start gaming:
	- Go to your unzip location and double click the 'MzFightReporter' Windows Batch file.
- Note: To enable healing stats [click here](#enable-healing-stats).
## Upgrade Instructions
- Go to [Releases](https://github.com/Swedemon/MzFightReporter/releases) for instructions.
## (Optional) Twitch Bot Instructions
1.  Create and login to a new account at https://www.twitch.tv to represent your bot.  Optionally, you can use your existing account.
2.  Go to https://twitchtokengenerator.com/
	1. Choose the option: Bot Chat Token
	2. Authorize.
		- Note: You can manage these connections at https://www.twitch.tv/settings/connections
3.  Copy the provided 'ACCESS TOKEN'.
4.  Edit the file **config.properties** located at your unzip location (you can use Notepad).
	1. Set the value of 'twitchChannelName' to your main channel name.
		- Example: ```twitchChannelName=drew5```
	2. Set the value of 'twitchBotToken' to the copied ACCESS TOKEN.
		- Example: ```twitchBotToken=li3j3l1ijlj13llj13lj1i3```
- Note: To disable the twitch bot revert ```twitchChannelName``` back to empty.
- Note: Do not to share your access token.  If necessary you can disconnect it and create a new one.
## Enable Healing Stats
1. Install the ArcDps [heal addon](https://github.com/Krappa322/arcdps_healing_stats#readme) -OR- install the [GW2 Addon Manager](https://github.com/gw2-addon-loader/GW2-Addon-Manager) and enable the heal addon (easy way to keep everything up-to-date).
2. In the ArcDps options (Alt-Shift-T) under 'Extensions' choose the 'healing_stats' tab. Then check the 'log healing' setting.
- Note: To view healing in-game check options 'Peers Outgoing' and 'Enable live sharing'.
## Sneak Peek
<p align="center"><img height="400" src="https://i.imgur.com/yVJ7CST.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/4CZEPBm.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/GBULKZa.png"/></p>
<p align="center"><img height="360" src="https://i.imgur.com/aERE0sC.png"/></p>
<p align="center"><img src="https://i.imgur.com/LzLxS2C.png"/></p>
<p align="center"><img height="360" src="https://i.imgur.com/4pWH2xH.png"/></p>
