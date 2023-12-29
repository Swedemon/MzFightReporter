# MzFightReporter (Guild Wars 2 WvW)
- **Description:** Monitor [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send descriptive WvW fight reports to a UI, Discord channel and optionally a Twitch channel.
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.  Major credit to the Elite Insights and ArcDps devs.
- **Author:** Swedemon.4670
## Setup Instructions (< 5 minutes)
1. Download and unzip to any directory the latest version of ```MzFightReporter_X.zip``` ([releases](https://github.com/Swedemon/MzFightReporter/releases)) ![downloads](https://img.shields.io/github/downloads/Swedemon/MzFightReporter/total).
   - Note: To unzip right click the downloaded file and select 'Extract All...'
1. Start the application by going to your unzip location and double clicking the 'MzFightReporter' batch file.
   - Note: If windows gives a warning message you can click on 'More Info' then 'Run Anyways'.
   <p align="center"><img height="140" src="https://i.imgur.com/JfOU4Vs.png"/></p>
1. In the UI click on the 'Settings' tab and paste in your 'Discord Webhook'.  Click Apply to save.
   - For help creating a discord webhook [click here](#create-a-discord-webhook).
   <p align="center"><img height="100" src="https://i.imgur.com/G91hC1P.png"/></p>
1. Enter Guild Wars 2 and open the ArcDps options (alt-shift-t).
   1. Under the 'LOGGING' tab check the wvw option: SAVE (AFTER SQUAD COMBAT)
   1. Beside the 'OPEN' button enter: ```C:\ARC```  
   1. Observe the other settings such as 'MINIMUM ENEMY PLAYERS': recommended is 10.
   <p align="center"><img height="160" src="https://i.imgur.com/y4sDiN1.png"/></p>
Setup is complete! Eligible fights will send reports to the UI and your discord channel.
- Note: To enable healing stats [click here](#enable-healing-stats).  To enable the Twitch bot [click here](#twitch-bot-instructions).
## Create a Discord Webhook
1. In your Discord create a channel Then right click it and choose 'Edit Channel'.
1. On the left choose 'Integrations' then 'Webhooks'.
1. Click on 'New Webhook' then click 'Copy Webhook URL' to copy the link.
## Enable Healing Stats
1. Install the ArcDps [heal addon](https://github.com/Krappa322/arcdps_healing_stats#readme).
1. In the ArcDps options (alt-shift-t) under 'Extensions' choose the 'healing_stats' tab then check 'log healing' and 'enable live sharing'.
   - Note: To view the in-game heal window check 'peers outgoing' option.
## How to Upgrade?
- Releases since version 2.1 will automatically update. For older versions go to [Releases](https://github.com/Swedemon/MzFightReporter/releases) for manual upgrade instructions.
## Twitch Bot Instructions
1. Create and login to a new account at https://www.twitch.tv to represent your bot.  Optionally, you can use your existing twitch account.
1. Go to https://twitchtokengenerator.com/
   1. Choose the option: Bot Chat Token
   1. Authorize.
      - Note: You can manage these connections at https://www.twitch.tv/settings/connections
1.  Copy the provided 'Access Token'.
1.  In the UI click on the 'Settings' tab and enter the below settings.  Click Apply to save.
      1. Set the value of 'Twitch Channel Name' to your main channel name.
      1. Set the value of 'Twitch Bot Token' to the copied access token.
- Note: To disable the twitch bot revert 'Twitch Channel Name' back to empty.
## Sneak Peek
<p align="center"><img height="450" src="https://i.imgur.com/OeQPypv.png"/></p>
<p align="center"><img height="400" src="https://i.imgur.com/yVJ7CST.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/4CZEPBm.png"/></p>
<p align="center"><img height="230" src="https://i.imgur.com/GBULKZa.png"/></p>
<p align="center"><img height="330" src="https://i.imgur.com/GeRrpQh.png"/></p>
<p align="center"><img height="300" src="https://i.imgur.com/aERE0sC.png"/></p>
<p align="center"><img height="60" src="https://i.imgur.com/LzLxS2C.png"/></p>

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
