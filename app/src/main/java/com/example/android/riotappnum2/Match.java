package com.example.android.riotappnum2;

import java.util.ArrayList;

/**
 * Created by Brian's PC on 7/2/2017.
 */

public class Match {
    public String  gameId;
    public ArrayList<Stats> fullMatchData;

    public Match(String gameId){
        this.gameId=gameId;
        fullMatchData = new ArrayList<Stats>();
    }
}
