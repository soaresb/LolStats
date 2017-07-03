package com.example.android.riotappnum2;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Brian's PC on 5/30/2017.
 */

public class Stats {
    public String summonerName;
    public String win;
    public String largestCriticalStrike;
    public String totalDamageDealt;
    public String largestMultiKill;
    public String largestKillingSpree;
    public String goldEarned;
    public String deaths;
    public String turretKills;
    public String kills;
    public String assists;
    public String totalMinionsKilled;
    public String totalDamageDealtToChampions;
    public String champKey;
    public String[] items;
    public int totalDamage;
    //after this go into timeline object and show those for extra data

    public Stats()
    {
        items = new String[7];
        //dmgMap = new HashMap<String, String>();
    }

}
