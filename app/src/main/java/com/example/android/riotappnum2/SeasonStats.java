package com.example.android.riotappnum2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SeasonStats extends AppCompatActivity {
    private static final String API_KEY = BuildConfig.API_KEY;
    private ArrayList<String> gameList;
    private Summoner summoner;
    private HashMap<String,ArrayList<Stats>> matchStatsHistory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_season_stats);
        getSupportActionBar().hide();
        matchStatsHistory = new HashMap<String,ArrayList<Stats>>();
        summoner = new Summoner("50300517");
        summoner.summonerId="35711275";
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/50300517?queue=420&season=8&api_key="+API_KEY});
    }



    public class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            // we use the OkHttp library from https://github.com/square/okhttp
            OkHttpClient client = new OkHttpClient();
            Request request =
                    new Request.Builder()
                            .url(urls[0])
                            .build();
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }

        //"accountId": 50300517,
        //"id": 35711275,
        //use the matchv3 to get the 20 most recent matches
        //gather the match ids for those 20 matches in an array
        //use those 20 match ids on the api call using a matchid to get stats
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("matches");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject match = jsonArray.getJSONObject(i);
                    String gameId = match.getString("gameId");
                    summoner.gameIds.add(gameId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            Intent it = new Intent(MainActivity.this,SplashActivity.class);
//            startActivity(it);
            for (int i=0;i<10;i++){
                GetMatchData getMatchData = new GetMatchData();
                try{
                    getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(i) + "?api_key="+API_KEY});}
                catch (Exception e){e.printStackTrace();}
            }
        }

    }
        private class GetMatchData extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... urls) {
                // we use the OkHttp library from https://github.com/square/okhttp
                OkHttpClient client = new OkHttpClient();
                Request request =
                        new Request.Builder()
                                .url(urls[0])
                                .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        return response.body().string();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }
            @Override
            protected void onPostExecute(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    JSONArray jsonArray = json.getJSONArray("participantIdentities");
                    String parId="";
                    boolean firstHalf;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject match = jsonArray.getJSONObject(i);
                        JSONObject newsummoner = (JSONObject) match.get("player");
                        if (newsummoner.getString("summonerId").equals(summoner.summonerId))
                        {
                            parId = match.getString("participantId");
                            break;
                        }
                    }
                    JSONArray jsonArray2 = json.getJSONArray("participants");
                    Stats newStats = new Stats();
                    for (int i = 0; i < jsonArray2.length(); i++) {
                        JSONObject match = jsonArray2.getJSONObject(i);
                        JSONObject temp = new JSONObject(match.getString("stats"));
                        if (match.getString("participantId").equals(parId)){
                            newStats.win = temp.getString("win");
                            newStats.largestCriticalStrike = temp.getString("largestCriticalStrike");
                            newStats.totalDamageDealt = temp.getString("totalDamageDealt");
                            newStats.largestMultiKill = temp.getString("largestMultiKill");
                            newStats.largestKillingSpree = temp.getString("largestKillingSpree");
                            newStats.goldEarned = temp.getString("goldEarned");
                            newStats.deaths = temp.getString("deaths");
                            newStats.turretKills = temp.getString("turretKills");
                            newStats.kills = temp.getString("kills");
                            newStats.assists = temp.getString("assists");
                            newStats.totalMinionsKilled = temp.getString("totalMinionsKilled");
                            newStats.totalDamageDealtToChampions = temp.getString("totalDamageDealtToChampions");
                            newStats.champKey = match.getString("championId");
                            newStats.items[0]=temp.getString("item0");
                            newStats.items[1]=temp.getString("item1");
                            newStats.items[2]=temp.getString("item2");
                            newStats.items[3]=temp.getString("item3");
                            newStats.items[4]=temp.getString("item4");
                            newStats.items[5]=temp.getString("item5");
                            newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
                            if(matchStatsHistory.get(newStats.champKey)==null) {
                                ArrayList<Stats> firstChampInstance= new ArrayList<Stats>();
                                firstChampInstance.add(newStats);
                                matchStatsHistory.put(newStats.champKey, firstChampInstance);
                            }
                            else{
                                matchStatsHistory.get(newStats.champKey).add(newStats);
                            }
                            break;
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    public void onClick(View view){
//        for (int i=0;i<summoner.gameIds.size();i++){
//            GetMatchData getMatchData = new GetMatchData();
//            try{
//                getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(i) + "?api_key="+API_KEY});}
//            catch (Exception e){e.printStackTrace();}
//        }
        Log.e("Asd",matchStatsHistory.toString());

    }
}
