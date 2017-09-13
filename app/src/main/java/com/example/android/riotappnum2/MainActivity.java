package com.example.android.riotappnum2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.android.riotappnum2.BuildConfig.API_KEY;

public class MainActivity extends AppCompatActivity {
    Summoner summoner;
    ListView lvItems;
    List<Stats> statsList;
    HashMap<String, String> champData;
    HashMap<String, String> summonerSpellData;
    public HashMap<String, Match> fullMatch;
    EditText editText2;
    Button button;
    ProgressDialog progress2;
    ImageView summIcon;
    TextView summName;
    TextView summRank;
    TextView summWL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String accountid = intent.getStringExtra("ACCOUNTID");
        String id = intent.getStringExtra("ID");
        String sum = intent.getStringExtra("SUMMONERNAME");
        String summonerIcon = intent.getStringExtra("SUMMONERICON");
        summoner = new Summoner(id);
        summoner.name=sum;
        summoner.accountId = Integer.parseInt(accountid);
        statsList = new ArrayList<Stats>();
        champData = new HashMap<String, String>();
        fullMatch = new HashMap<String, Match>();
        summonerSpellData = new HashMap<String, String>();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/" + String.valueOf(summoner.accountId) + "?queue=420&endIndex=20&beginIndex=0&api_key=" + API_KEY});
        GetSummonerLeague getSummonerLeague = new GetSummonerLeague();
        getSummonerLeague.execute(new String[]{"https://na1.api.riotgames.com/lol/league/v3/leagues/by-summoner/"+String.valueOf(summoner.summonerId)+"?api_key="+API_KEY});
        loadJSONFromAsset();
        editText2 = (EditText) findViewById(R.id.editText2);
        editText2.setText(sum);
        summIcon = (ImageView) findViewById(R.id.summIcon);
        summName = (TextView) findViewById(R.id.summName);
        summRank = (TextView) findViewById(R.id.summRank);
        summWL = (TextView) findViewById(R.id.summWL);
        button=(Button) findViewById(R.id.button2) ;
        new DownloadImageTask((ImageView) findViewById(R.id.summIcon))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.17.1/img/profileicon/"+summonerIcon+".png");
        loadSummonerSpells();
        lvItems = (ListView) findViewById(R.id.lv_items);
//        ExpandableAdapter adapter = getAdapter();
//        lvItems.setAdapter(adapter);
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ExpandableAdapter adapter = (ExpandableAdapter) parent.getAdapter();
                Log.d("asda", Integer.toString(adapter.getCount()));
                Item item = (Item) adapter.getItem(position);
                item.itemChanged = true;
                for (int i = 0; i < adapter.getCount(); i++) {
                    Item temp = (Item) adapter.getItem(i);
                    if (temp.equals(item)) {
                        if (item.isExpanded == true) {
                            item.isExpanded = false;

                        } else {
                            item.isExpanded = true;
                            //item.itemChanged=true;
                        }
                    } else {
                        temp.isExpanded = false;
                    }
                }
                adapter.notifyDataSetChanged();
            }

        });
    }

    public void loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.champions);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            //return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject ex = obj.getJSONObject("data");
            @SuppressWarnings("unchecked")
            Iterator<String> keys = ex.keys();
            while (keys.hasNext()) {
                try {
                    String key = keys.next();
                    JSONObject value = (JSONObject) ex.get(key);
                    champData.put(key, value.getString("key"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void loadSummonerSpells() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource(R.raw.summonerspells);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            //return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            JSONObject ex = obj.getJSONObject("data");
            @SuppressWarnings("unchecked")
            Iterator<String> keys = ex.keys();
            while (keys.hasNext()) {
                try {
                    String key = keys.next();
                    JSONObject value = (JSONObject) ex.get(key);
                    summonerSpellData.put(value.getString("key"), value.getString("id"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ExpandableAdapter getAdapter() {

        List<Item> items = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            Item item = new Item();
            item.title = statsList.get(i).kills;
            item.description = summoner.gameIds.get(i);
            item.isExpanded = false;
            item.picS = champData.get(statsList.get(i).champKey).toLowerCase();
            item.item1 = statsList.get(i).items[0];
            item.item2 = statsList.get(i).items[1];
            item.item3 = statsList.get(i).items[2];
            item.item4 = statsList.get(i).items[3];
            item.item5 = statsList.get(i).items[4];
            item.item6 = statsList.get(i).items[5];
            item.kills = statsList.get(i).kills;
            item.deaths = statsList.get(i).deaths;
            item.assists = statsList.get(i).assists;
            item.spell1 = statsList.get(i).spell1;
            item.spell2 = statsList.get(i).spell2;
            item.gold = statsList.get(i).goldEarned;
            item.cs = statsList.get(i).totalMinionsKilled;
            item.win = statsList.get(i).win;
            item.time = statsList.get(i).time;
            item.matchId = summoner.gameIds.get(i);
            item.champData = champData;
            item.itemChanged = true;
            item.matchList = fullMatch;
            items.add(item);
        }
        progress2.dismiss();
        return new ExpandableAdapter(this, items);
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
            progress2 = new ProgressDialog(MainActivity.this);
            progress2.setTitle("Loading");
            progress2.setMessage("Wait while loading...");
            progress2.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress2.show();
            try {
                JSONObject json = new JSONObject(result);
                JSONArray jsonArray = json.getJSONArray("matches");

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject match = jsonArray.getJSONObject(i);
                    String temp = match.getString("gameId");
                    String temp2 = match.getString("queue");

                    if (!temp2.equals("420")) {
                    } else {
                        summoner.gameIds.add(temp);
                    }
                }
                int tempp = summoner.gameIds.size();
//                GetMatchData getMatchData = new GetMatchData();
//                getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(0) + "?api_key="+API_KEY});
                for (int i = 0; i < 12; i++) {
                    GetMatchData getMatchData = new GetMatchData();
                    getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(i) + "?api_key=" + API_KEY});
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
//            Intent it = new Intent(MainActivity.this,SplashActivity.class);
//            startActivity(it);

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
            Match thisMatch;
            try {
                JSONObject json = new JSONObject(result);
                thisMatch = new Match(json.getString("gameId"));
                JSONArray jsonArray = json.getJSONArray("participantIdentities");
                String parId = "";
                boolean firstHalf;
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject match = jsonArray.getJSONObject(i);
                    JSONObject newsummoner = (JSONObject) match.get("player");
                    Stats newSummonerStats = new Stats();
                    newSummonerStats.summonerName = newsummoner.getString("summonerName");
                    thisMatch.fullMatchData.add(newSummonerStats);
                    if (newsummoner.getString("summonerId").equals(summoner.summonerId)) {
                        parId = match.getString("participantId");
                        //break;
                    }
                }
                if (Integer.valueOf(parId) <= 5) {
                    firstHalf = true;
                } else {
                    firstHalf = false;
                }
                JSONArray jsonArray2 = json.getJSONArray("participants");
                Stats newStats = new Stats();
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject match = jsonArray2.getJSONObject(i);
                    JSONObject temp = new JSONObject(match.getString("stats"));
                    if (match.getString("participantId").equals(parId)) {
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
                        newStats.items[0] = temp.getString("item0");
                        newStats.items[1] = temp.getString("item1");
                        newStats.items[2] = temp.getString("item2");
                        newStats.items[3] = temp.getString("item3");
                        newStats.items[4] = temp.getString("item4");
                        newStats.items[5] = temp.getString("item5");
                        //newStats.name=champData.get(statsList.get(i).champKey).toLowerCase();
                        newStats.time = json.getString("gameDuration");
                        newStats.spell1 = summonerSpellData.get(match.getString("spell1Id"));
                        newStats.spell2 = summonerSpellData.get(match.getString("spell2Id"));
                        newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
                        statsList.add(newStats);

                    }
                    Stats newStats2 = thisMatch.fullMatchData.get(i);
                    newStats2.deaths = temp.getString("deaths");
                    newStats2.kills = temp.getString("kills");
                    newStats2.assists = temp.getString("assists");
                    newStats2.totalMinionsKilled = temp.getString("totalMinionsKilled");
                    newStats2.totalDamageDealtToChampions = temp.getString("totalDamageDealtToChampions");
                    newStats2.champKey = match.getString("championId");
                    newStats2.goldEarned = temp.getString("goldEarned");
                    newStats2.items[0] = temp.getString("item0");
                    newStats2.items[1] = temp.getString("item1");
                    newStats2.items[2] = temp.getString("item2");
                    newStats2.items[3] = temp.getString("item3");
                    newStats2.items[4] = temp.getString("item4");
                    newStats2.items[5] = temp.getString("item5");
                    fullMatch.put(json.getString("gameId"), thisMatch);
//                    if(firstHalf){
//                        if(i<=4){
//                            newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
//                        }
//
//                    }
//                    else{
//                        if(i>=5){
//                            newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
//                        }
//                    }


                }
                if (statsList.size() >= 8) {
                    ExpandableAdapter adapter = getAdapter();
                    lvItems.setAdapter(adapter);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class GetSummonerData extends AsyncTask<String, Void, String> {
        private View v;

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
                String accountID;
                String summonerID;
                String summonerName;
                String summonerIcon;
                accountID = json.getString("accountId");
                summonerID = json.getString("id");
                summonerName = json.getString("name");
                summonerIcon=json.getString("profileIconId");
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("ACCOUNTID", accountID);
                intent.putExtra("ID", summonerID);
                intent.putExtra("SUMMONERNAME", summonerName);
                intent.putExtra("SUMMONERICON",summonerIcon);
                String FILENAME = "recentsummoners.txt";
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                byte[] buf = accountID.getBytes();
                fos.write(buf);
                fos.close();
                startActivity(intent);

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invaild Summoner Name", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

        }


    }
    public void onClickGo(View v) {
        //this.v = v;
        GetSummonerData getSummonerData = new GetSummonerData();
        getSummonerData.execute(new String[]{"https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/" + editText2.getText().toString() + "?api_key=" + API_KEY});
    }

    public class GetSummonerLeague extends AsyncTask<String, Void, String> {
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
                JSONArray json = new JSONArray(result);
                JSONObject jsonO =json.getJSONObject(0);
                String tier = jsonO.getString("tier");
                String queue = jsonO.getString("queue");
                String leagueName = jsonO.getString("name");
                JSONArray entries = jsonO.getJSONArray("entries");
                String addTier="";
                String wins="";
                String losses="";
                String lp="";
                for (int i=0;i<entries.length();i++){
                    JSONObject currentSummoner = entries.getJSONObject(i);
                    if (currentSummoner.getString("playerOrTeamName").equals(summoner.name)){
                        wins=currentSummoner.getString("wins");
                        losses=currentSummoner.getString("losses");
                        addTier = currentSummoner.getString("rank");
                        lp=currentSummoner.getString("leaguePoints");

                        break;
                    }
                }
                summName.setText(summoner.name);
                summRank.setText(tier+" "+addTier+" "+lp+" LP");
                summWL.setText(wins+" W ");
                summWL.append(losses+" L");

            }
            catch (Exception e) {

                e.printStackTrace();

            }

        }
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0].replaceAll("\\s+", "");
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                //e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
                Bitmap bmp = Bitmap.createBitmap(32, 32, conf);
                bmp.eraseColor(Color.GRAY);
                bmImage.setImageBitmap(bmp);
            } else {
                bmImage.setImageBitmap(result);
            }
        }
    }
}
