# MzFightReporter (Guild Wars 2 WvW)
- **Description:** Monitor [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send descriptive WvW fight reports to a Discord channel and optionally a Twitch channel.
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.  Major credit to the Elite Insights and ArcDps devs.
- **Author:** Swedemon.4670
## Setup Instructions (< 5 minutes)
1.  Download and unzip the latest version of ```MzFightReporter_X.zip``` to any directory.
	- https://github.com/Swedemon/MzFightReporter/releases [![downloads](https://img.shields.io/github/downloads/Swedemon/MzFightReporter/total)](https://github.com/Swedemon/MzFightReporter/releases/latest)
2.  Create a webhook in your Discord server and copy the link:
	1. In your Discord create a channel.  Right click on it and choose 'Edit Channel'.
	2. On the left choose 'Integrations' then 'Webhooks'.
	3. Click on 'New Webhook' then 'Copy Webhook URL' to copy the link.
3.  Using Notepad edit the file **config.properties** located at your unzip location:
	1. Set the value of 'discordWebhook' to your new webhook URL.  Save your file.
		- Example: ```discordWebhook=https://discord.com/api/webhooks/84073...```
4.  Open Guild Wars 2 and enter the game.  Open ArcDps options by pressing Alt-Shift-T.
	1. Under the 'Logging' tab check the wvw option: save (after squad combat)
	2. And beside the 'Open' button enter exactly: ```c:\arc```  
    3. Observe the other settings such as 'min enemy participants': recommended value is 10.
5.  Setup complete!  Before you begin remember to run the background program:
	1. Go to your unzip location and double click the 'MzFightReporter' Windows Batch file.
    - Note: You can load older arcdps logs by renaming the .zevtc files while the program runs.
- Note: To enable healing stats [click here](#enable-healing-stats).
## Enable Healing Stats
1. Install the ArcDps [heal addon](https://github.com/Krappa322/arcdps_healing_stats#readme).
2. In the ArcDps options (Alt-Shift-T) under 'Extensions' choose the 'healing_stats' tab. Then check 'log healing' and 'enable live sharing'.
	- Note: To view the in-game heal window check 'peers outgoing' option.
## How to Upgrade?
- Releases since version 2.1 will automatically update. For older versions go to [Releases](https://github.com/Swedemon/MzFightReporter/releases) for manual upgrade instructions.
## (Optional) Twitch Bot Instructions
1.  Create and login to a new account at https://www.twitch.tv to represent your bot.  Optionally, you can use your existing twitch account.
2.  Go to https://twitchtokengenerator.com/
	1. Choose the option: Bot Chat Token
	2. Authorize.
		- Note: You can manage these connections at https://www.twitch.tv/settings/connections
3.  Copy the provided 'ACCESS TOKEN'.
4.  Using Notepad edit the file **config.properties** located at your unzip location.  Save your file.
	1. Set the value of 'twitchChannelName' to your main channel name.
		- Example: ```twitchChannelName=drdisrespect```
	2. Set the value of 'twitchBotToken' to the copied ACCESS TOKEN.
		- Example: ```twitchBotToken=li3j3l1ijlj13llj13lj1i3```
- Note: To disable the twitch bot revert ```twitchChannelName``` back to empty.
- Note: Do not to share your access token.  If necessary you can disconnect it and create a new one.
## Sneak Peek
<p align="center"><img height="400" src="https://i.imgur.com/yVJ7CST.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/4CZEPBm.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/GBULKZa.png"/></p>
<p align="center"><img height="330" src="https://i.imgur.com/GeRrpQh.png"/></p>
<p align="center"><img height="360" src="https://i.imgur.com/aERE0sC.png"/></p>
<p align="center"><img height="80" src="https://i.imgur.com/LzLxS2C.png"/></p>
<p align="center"><img height="360" src="https://i.imgur.com/4pWH2xH.png"/></p>

## About the Data
### Limitations
Some data points are not provided due to a limitation in the game, arcdps or EI.
- **Friendly players**: Data on players contributing but not in squad at this time is not possible.  If a fight shows 20v50 then take this with a grain of salt.
- **Defensive Boons**:  Does not consider whether boons such as stab were wasted so a higher rating is not always better.  However, this rating does show someone was at least actively pressing the buttons that matter.
- **Healing**:  Only players using the Heal Addon with Live Sharing enabled will be accurately represented.
- **Outgoing CC's**: There is no data regarding knockdown, knockback, launch, pull, float, and sink.  For example, mesmer pulls, grav wells, DH pulls are not reported at this time. Also, outgoing CC durations are not considered.
- **Commander**: At times the data is ambiguous on who the commander is in which case it is skipped.
- **Long Fights**: Fights exceeding 15 minutes run the risk of not reporting due to system memory constraints.  Healing data will not be included when the arcdps logs exceed 10MB.
### Defensive Boons
- EI provides a data point called squadBuffs as the source of this data.  The value is a percentage on a given boon shared across the squad during the fight.
- Rating Formula = Stability(x3) + Aegis(x2) + Protection + Resistance + Resolution(x0.5) + Alacrity(x0.5)
- The maximum possible rating is 800 (%) which is normalized across the entire squad.
