package com.example.android.riotappnum2;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    public Summoner summoner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        summoner = new Summoner("35711275");
    }


    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
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
            }
            catch (Exception e){
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

                for (int i=0;i<jsonArray.length();i++)
                {
                    JSONObject match = jsonArray.getJSONObject(i);
                    String temp = match.getString("gameId");
                    summoner.gameIds.add(temp);
                }
                //String temp = json.getJSONObject("gladdyy").getString("id");
                textView2.setText("hi");
                textView4.setText(summoner.gameIds.toString());
                //String temp = json.getString("riotschmick");

            }
            catch (Exception e) {e.printStackTrace();}

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

        //"accountId": 50300517,
        //"id": 35711275,
        //use the matchv3 to get the 20 most recent matches
        //gather the match ids for those 20 matches in an array
        //use those 20 match ids on the api call using a matchid to get stats
        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("participantIdentities");
                String parId="";
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
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject match = jsonArray2.getJSONObject(i);
                    if (match.getString("participantId").equals(parId)){
                        String temp = match.getString("stats");
                        textView3.setText(temp);
                        break;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void onClick(View view) {
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/50300517/recent?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25"});
        GetMatchData getMatchData = new GetMatchData();
        getMatchData.execute(new String[] {"https://na1.api.riotgames.com/lol/match/v3/matches/2512249198?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25" });

    }


}
