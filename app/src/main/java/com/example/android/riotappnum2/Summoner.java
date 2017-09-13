package com.example.android.riotappnum2;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Brian's PC on 5/30/2017.
 */

public class Summoner {
    public String summonerId;
    public int accountId;
    public List<String> gameIds;
    public String name;

    public Summoner(String summId){
        summonerId = summId;
        gameIds = new ArrayList<String>();

    }
}
