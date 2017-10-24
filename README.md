# Lol Stats
Android App for League of Legends stats using the Riot API


## Overview
LolStats is an android application to look up and recieve ranked statistics for a summoner in League of Legends. With a simple search, users can view their rank, win loss ratio, LP, and statistics of their previous 10 games along with item builds.


## Note

This project is still under development and is still using a development API key which is reset every 24 hours.  Hopefully soon I will be able to use a production API key for 100% uptime on the application.  However, I will try to update my key every 24 hours in order to ensure 24 hour uptime.

## Sources used
<https://developer.riotgames.com/>

## Current State
- search any summoner name and receive ranked solo statistics
- 10 most recent games along with item builds for all 10 players

## Future Plans
- allow for other regions not just NA
- database implementation for faster lookup and more in-depth stats
- expand to other queue types not just ranked solo
- more data analysis including champion specific win rates for each summoner

![ScreenShot](https://raw.github.com/soaresb/LolStats/master/demoPicture.png)


# To Run
### Download and open in Andriod Studio
### Please place your api key as a string in your gradle.properties file and name it API_KEY. 



