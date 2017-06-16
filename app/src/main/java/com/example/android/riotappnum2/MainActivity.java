package com.example.android.riotappnum2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private ImageView imageView;
    public Summoner summoner;
    public List<Stats> statsList;
    public String currentPage;
    HashMap<String, String> champData;
    public int pageNum;
    public boolean dropDown = false;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        try {
            URL url = new URL("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/Aatrox.png");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        }
        catch (Exception e){e.printStackTrace();}

        champData = new HashMap<String, String>();
        String s = getIntent().getStringExtra("ACCOUNTID");
        String ss = getIntent().getStringExtra("ID");
        summoner = new Summoner(ss);
        statsList = new ArrayList<Stats>(20);
        for(int k=0;k<20;k++){
            Stats temp = new Stats();
            statsList.add(temp);
        }
        loadJSONFromAsset();
        DownloadWebPageTask task = new DownloadWebPageTask();
        task.execute(new String[] { "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"+s+"/recent?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25"});
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.games,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                pageNum=position;
                if (!dropDown) {
                    dropDown=true;
                }
                else{
                    GetMatchData getMatchData = new GetMatchData();
                    try{
                        getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(pageNum) + "?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25"});}
                    catch (Exception e){e.printStackTrace();}
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
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
                    champData.put(key,value.getString("name"));
                }
                catch(Exception e){e.printStackTrace();}
            }
            }


         catch (Exception e) {
            e.printStackTrace();
        }

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
                    String temp2 = match.getString("queue");

                    if(!temp2.equals("420"))
                    {}
                    else {summoner.gameIds.add(temp);}
                }
            int tempp = summoner.gameIds.size();
            GetMatchData getMatchData = new GetMatchData();
            getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(0) + "?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25"});

            }
            catch (Exception e) {e.printStackTrace();}
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
                        JSONObject temp = new JSONObject(match.getString("stats"));
                        Stats newStats = new Stats();
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
                        statsList.add(pageNum,newStats);
                        break;
                    }
                }


            } catch (Exception e) {
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
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                //Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void onClick(View view) {
        //progressBar.setVisibility(View.VISIBLE);
        try {
            Thread.sleep(100);
            //progressBar.setVisibility(View.GONE);
        }
        catch (Exception e){e.printStackTrace();}

        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/"+champData.get(statsList.get(pageNum).champKey)+".png");
        int temp = Integer.parseInt(statsList.get(0).champKey);
        try {
            textView2.setText(champData.get(statsList.get(pageNum).champKey));
        }
        catch (Exception e){e.printStackTrace();}
        textView3.setText(statsList.get(pageNum).kills+"/"+statsList.get(pageNum).deaths+"/"+statsList.get(pageNum).assists);
        if(statsList.get(pageNum).win.equals("false")){
            textView5.setText("LOSS\n");
        }
        else if(statsList.get(pageNum).win.equals("true")){
            textView5.setText("WIN\n");
        }
        else
            textView5.setText("DRAW\n");
        textView5.append("Total Damage Dealt to Champions : "+statsList.get(pageNum).totalDamageDealtToChampions+"\n");
        textView5.append("Total Gold Earned : "+statsList.get(pageNum).goldEarned+"\n");
        textView5.append("CS : "+statsList.get(pageNum).totalMinionsKilled+"\n");
        textView5.append("Largest Killing Spree : "+statsList.get(pageNum).largestKillingSpree+"\n");
        textView5.append("Largest Multi Kill : "+statsList.get(pageNum).largestMultiKill+"\n");

    }



}
