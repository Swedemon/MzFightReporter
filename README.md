# MzFightReporter (Guild Wars 2 WvW)
- **Description:** Monitor live [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send descriptive WvW fight reports to a Discord channel and optionally a Twitch channel.  This program is open source. The provided data includes:
   - Squad Summary, Enemy Summary
   - Player Damage, Down Contribution, Spike Damage
   - Cleanses, Strips
   - Heals and Barrier (if setup)
   - Defensive and Offensive Boons
   - Outgoing Downs and Kills
   - Outgoing CCs and Interrupts
   - Enemy Top Skill Dmg, Enemy Breakdown
   - Link to [WvW Dps Report](https://dps.report/)
   - Squad Damage Graph
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.  Major credit to the Elite Insights, ArcDps devs and Micca with the WvW Reports website.  Also shout-outs to Jahar from PvD, Jay from Apex and Perish_VS on Twitch!
- **Developer:** Swedemon.4670 aka Mazz
- **Support Discord:** https://discord.gg/5JfZ3qpW3Q
- **Pre-requisites:** [ArcDps addon](https://www.deltaconnected.com/arcdps/)
## Setup Instructions (5 - 10 minutes)
1. Download and unzip to any directory the latest version of ```MzFightReporter_X.zip``` ([releases](https://github.com/Swedemon/MzFightReporter/releases)) ![downloads](https://img.shields.io/github/downloads/Swedemon/MzFightReporter/total).
   - To unzip right click the downloaded file and select 'Extract All...'
1. Start the application by going into the extracted folder and double clicking the 'MzFightReporter' batch file.
   - Note: If windows gives a warning message you can click on 'More Info' then 'Run Anyways'.
   <p align="center"><img height="140" src="https://i.imgur.com/JfOU4Vs.png"/></p>
1. In the UI click on the 'Settings' tab and paste in your 'Discord Webhook'.  Click Apply to save.
   - For help creating a discord webhook [click here](#create-a-discord-webhook).
   <p align="center"><img height="100" src="https://i.imgur.com/G91hC1P.png"/></p>
1. Enter Guild Wars 2 and open the ArcDps options (alt-shift-t).
   1. Under the 'LOGGING' tab check the wvw option: SAVE (AFTER SQUAD COMBAT)
   1. Observe the other settings such as 'MINIMUM ENEMY PLAYERS': recommended is 10 // less for havoc.
   <p align="center"><img height="160" src="https://i.imgur.com/y4sDiN1.png"/></p>
Setup is complete! Eligible fights will send reports to your discord channel while this program is running.
- Note: To enable healing stats [click here](#enable-healing-stats).  To enable the Twitch bot [click here](#twitch-bot-instructions).
## Create a Discord Webhook
1. In your Discord create a channel then right click it and choose 'Edit Channel'.
1. On the left choose 'Integrations' then 'Webhooks'.
1. Click on 'New Webhook' then click 'Copy Webhook URL' to copy the link.
   <p align="center"><img height="260" src="https://i.imgur.com/1WKwOuz.png"/>
## Enable Healing Stats
1. Install the ArcDps healing stats addon:  [click here for instructions](https://github.com/Krappa322/arcdps_healing_stats#readme)
1. In the ArcDps options (alt-shift-t) under 'Extensions' choose the 'healing_stats' tab then check 'log healing' and 'enable live sharing'.
   - Note: To view the in-game heal window check the 'peers outgoing' option.
   - Note: Healing stats does contribute to larger arcdps logs and therefore it is more resource intensive.
   <p align="center"><img height="260" src="https://i.imgur.com/IK1L1JM.png"/></p>
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
- Note: To disable the twitch bot deselect twitch in the settings and apply.
## Sneak Peek
<p align="center"><img src="https://i.imgur.com/7QBOhat.png"/></p>
<p align="center"><img height="100" src="https://i.imgur.com/F9LDo1h.png"/></p>

## About the Data
### Limitations
Some data points are not provided due to a limitation in the game, arcdps or EI.
- **Friendly players**: Data on players contributing but not in squad at this time is not possible.  If a fight shows 20v50 then take this with a bulk shipment of salt.
- **Healing**:  Only players using the Heal Addon on their side with Live Sharing enabled will be represented.
- **Outgoing CC's**: There is no data regarding knockdown, knockback, launch, pull, float, and sink.  For example, mesmer pulls, grav wells, DH pulls are not reported at this time. Also, outgoing CC durations are not considered.  However, interrupts are included which gives some indication of all effective CC's.
- **Long Fights**: Fights exceeding ~15 minutes run the risk of not reporting and/or not uploading.  Settings exist to manage the maximum upload size.
