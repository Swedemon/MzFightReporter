# MzFightReporter (Guild Wars 2 WvW Discord Bot)
![downloads](https://img.shields.io/github/downloads/Swedemon/MzFightReporter/total)
## How does it work?
Run this app in the background.  After each combat cycle arcdps generates a log file.  This program collates the data and sends it to your provided Discord (optionally Twitch).  [Example](#example)
## Table of Contents
- [Overview](#overview)
- [Setup Instructions (5 - 10 minutes)](#setup-instructions-5---10-minutes)
- [Create a Discord Webhook](#create-a-discord-webhook)
- [Enable Healing Stats](#enable-healing-stats)
- [Twitch Bot Instructions](#twitch-bot-instructions)
- [About the Data](#about-the-data)
- [Troubleshooting](#troubleshooting)
- [Example](#example)
## Overview
- **Description:** Monitor live [ArcDps](https://www.deltaconnected.com/arcdps/) logs, parse data using [Elite Insights](https://github.com/baaron4/GW2-Elite-Insights-Parser), and send descriptive WvW fight reports to a Discord channel and optionally a Twitch channel.  This program is open source. The provided data includes:
   - Squad Summary, Enemy Summary
   - Player Damage, Down Contribution, Burst Damage
   - Cleanses, Strips
   - Heals and Barrier (if setup)
   - Defensive and Offensive Boons
   - Outgoing Downs and Kills
   - Outgoing CCs and Interrupts
   - Enemy Top Skill Dmg, Enemy Breakdown
   - Link to [WvW Dps Report](https://dps.report/)
   - Squad Damage Graph
- **Credit:** This app was originally developed to mimic the "Indo-bot" (developed by Tiff).  This is a completely separate implementation made for broader use.  Major credit to the Elite Insights, ArcDps devs and Micca with the WvW Reports website.
- **Developer:** Mazz.5792
- **Support Discord:**
  
  [![](https://discordapp.com/api/guilds/1227596196823175198/widget.png?style=banner2)](https://discord.gg/5JfZ3qpW3Q)
## Setup Instructions (5 - 10 minutes)
**Pre-requisite:** [ArcDps](https://www.deltaconnected.com/arcdps/)
1. Download and unzip to any directory the latest version of ```MzFightReporter_X.zip``` at [releases](https://github.com/Swedemon/MzFightReporter/releases).
   - To unzip right click the downloaded file and select 'Extract All...'
1. Start the application by going into the extracted folder and double clicking the 'MzFightReporter' batch file.
   - Note: If windows gives a warning message you can click on 'More Info' then 'Run Anyways'.
   <p align="center"><img height="140" src="https://i.imgur.com/JfOU4Vs.png"/></p>
1. In the UI click on the 'Settings' tab and paste in your Discord webhook into 'Discord Webhook #1' as shown.  Click Apply to save.
   - For help creating a discord webhook [click here](#create-a-discord-webhook).
   <p align="center"><img height="60" src="https://i.imgur.com/AtqXIJV.png"/></p>
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
1. In the UI click on the 'Settings' tab and paste in your 'Discord Webhook'.  Click Apply to save.
## Enable Healing Stats
1. Install the ArcDps healing stats addon:  [click here for instructions](https://github.com/Krappa322/arcdps_healing_stats#readme)
1. Enter Guild Wars 2 and open the ArcDps options (alt-shift-t).  Under 'Extensions' tab choose the 'healing_stats' then check 'log healing' and 'enable live sharing'.
   - Note: To view the in-game heal window check the 'peers outgoing' option.
   - Note: Healing stats does contribute to larger arcdps logs therefore it is more resource intensive.
   <p align="center"><img height="260" src="https://i.imgur.com/IK1L1JM.png"/></p>
## Twitch Bot Instructions
1. Create and login to a new account at https://www.twitch.tv to represent your bot.  Optionally, you can use your existing twitch account.
1. Go to https://twitchtokengenerator.com/
   1. Choose the option: Bot Chat Token
   1. Authorize.
      - Note: You can manage these connections at https://www.twitch.tv/settings/connections
1.  Copy the provided 'Access Token'.
1.  In the UI click under the 'Settings' tab, enter the below settings.  Click Apply to save.
      1. Set the value of 'Twitch Channel Name' to your main channel name.
      1. Set the value of 'Twitch Bot Token' to the copied access token.
- Note: To disable the twitch bot deselect twitch in the settings and apply.
## About the Data
### Outgoing CC's
Elite Insights provides a data point called appliedCrowdControl which indicates a total count of hard CC's.  A hard CC is when an enemy endures a temporary full loss of control having only a stun break to resolve early.
- **Ranking Formula**: HardCC (x10) + SoftCC + Immob (x5) + Interrupts (x5)
- Hard CC includes: launch, knockback, knockdown, pull, sink, float, stun, fear, taunt
- Soft CC includes: daze, chill, cripple, slow, blind, weakness
- Immobs can be considered a Soft CC but due to it's raised impact this attribute is separated.
- Daze could be considered a Hard CC however ArcDps restricts Hard CC as a 'full loss of control' and I have moved into Soft CC.
### Limitations
Some data points are not provided due to a limitation in the game, arcdps or EI.
- **Healing**:  Only players using the Heal Addon on their side with Live Sharing enabled will be represented.
- **Long Fights**: Fights exceeding ~15 minutes run the risk of not reporting and/or not uploading.  Settings exist to manage the maximum upload size.
## Troubleshooting
#### > The program is not doing anything after fights complete.
  1. Ensure ArcDps is generating log files.  By default this will be in your user profile's Documents folder:
     - Typically under:  C:\Users\\<User Name\>\Documents\Guild Wars 2\addons\arcdps
  1. Double check the instructions above regarding ArcDps options.  Ensure the squad and enemy size is above the minimum.
  1. Ensure in the MzFightReporter Settings tab the 'ArcDps Log Folder #1' is correct.  If necessary edit the path accordingly and apply.
#### > It's not working, I'm stuck.
  1. Feel free to ask questions in the [Support Discord](https://discord.gg/5JfZ3qpW3Q).
## Example
<p align="center"><img src="https://i.imgur.com/X7AvY9k.png"/></p>
<p align="center"><img height="80" src="https://i.imgur.com/bzR4oC6.png"/></p>
