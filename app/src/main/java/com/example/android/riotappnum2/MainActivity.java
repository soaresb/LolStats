package com.example.android.riotappnum2;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
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
    private static String TAG = "MainActivity";
    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam", "Robert", "Ashley"};
    PieChart pieChart;
    BarChart barChart;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private TextView csScore;
    private ImageView minion;
    private ImageView imageView;
    private TextView kdText;
    private ImageView item0;
    private ImageView item2;
    private ImageView item3;
    private ImageView item4;
    private ImageView item5;
    private ImageView item6;
    public Summoner summoner;
    public List<Stats> statsList;
    public String currentPage;
    HashMap<String, String> champData;
    public int pageNum;
    public boolean dropDown = false;
    public boolean firstTime = true;
    ProgressDialog progress;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> xAxis;
    View v;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        v = this.getWindow().getDecorView().findViewById(android.R.id.content);
        v.setVisibility(View.INVISIBLE);
        pieChart = (PieChart) findViewById(R.id.PieChart);
        pieChart.setTouchEnabled(false);
        barEntries = new ArrayList<>();
        BarDataSet barDataSet = new BarDataSet(barEntries,"");
        xAxis = new ArrayList<>();

//        BarData theData = new BarData(xAxis,barDataSet);
//        barChart.setData(theData);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);
        textView5 = (TextView) findViewById(R.id.textView5);
        csScore = (TextView) findViewById(R.id.csScore);
        minion = (ImageView) findViewById(R.id.minion);
        item0 = (ImageView) findViewById(R.id.item0);
        item2 = (ImageView) findViewById(R.id.item2);
        item3 = (ImageView) findViewById(R.id.item3);
        item4 = (ImageView) findViewById(R.id.item4);
        item5 = (ImageView) findViewById(R.id.item5);
        item6 = (ImageView) findViewById(R.id.item6);
        imageView = (ImageView) findViewById(R.id.imageView);
        kdText = (TextView) findViewById(R.id.kdText);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.GONE);
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
    public void onStart() {
        super.onStart();
        progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();


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
                if(Integer.valueOf(parId) <= 5){
                    firstHalf=true;
                }
                else {firstHalf = false;}
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

                    }
                    if(firstHalf){
                        if(i<=4){
                            newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
                        }

                    }
                    else{
                        if(i>=5){
                            newStats.totalDamage += Integer.valueOf(temp.getString("totalDamageDealtToChampions"));
                        }
                    }


                }
                statsList.add(pageNum,newStats);


            } catch (Exception e) {
                e.printStackTrace();
            }
            if(firstTime) {
                tryShow();
                firstTime=false;
            }
            else {onClick();}
        }
    }
    public void tryShow(){
        try {
            Thread.sleep(100);
            //progressBar.setVisibility(View.GONE);
        }
        catch (Exception e){e.printStackTrace();}

        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/"+champData.get(statsList.get(0).champKey)+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item0))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(0).items[5]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.goldPic))
                .execute("http://ddragon.leagueoflegends.com/cdn/5.5.1/img/ui/gold.png");
        new DownloadImageTask((ImageView) findViewById(R.id.minion))
                .execute("http://ddragon.leagueoflegends.com/cdn/5.5.1/img/ui/minion.png");
//                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/1001.png");
        //int temp = Integer.parseInt(statsList.get(0).champKey);
        try {
            textView2.setText(champData.get(statsList.get(0).champKey));
        }
        catch (Exception e){e.printStackTrace();}

        if(statsList.get(0).win.equals("false")){
            textView3.setText("DEFEAT");
            textView3.setTextColor(Color.parseColor("#f4426b"));
        }
        else if(statsList.get(0).win.equals("true")){
            textView3.setText("VICTORY");
            textView3.setTextColor(Color.parseColor("#2dff65"));
        }
        else
            textView3.setText("DRAW");
        kdText.setText(statsList.get(0).kills+"/"+statsList.get(0).deaths+"/"+statsList.get(0).assists+"\n");
        kdText.append(String.format("%.2f",(Float.parseFloat(statsList.get(0).kills)+Float.parseFloat(statsList.get(0).assists))/Float.parseFloat(statsList.get(0).deaths))+" KDA");
        //textView5.setText("Total Damage Dealt to Champions : "+statsList.get(0).totalDamageDealtToChampions+"\n");
        String goldK = String.format("%.1f",Float.parseFloat(statsList.get(0).goldEarned) / 1000);
        //textView5.append("Total Gold Earned : "+statsList.get(0).goldEarned+"\n");
        textView4.setText("Total Gold Earned : "+goldK+" k");
        csScore.setText("CS : "+statsList.get(0).totalMinionsKilled);
        //textView5.append("Largest Killing Spree : "+statsList.get(0).largestKillingSpree+"\n");
        //textView5.append("Largest Multi Kill : "+statsList.get(0).largestMultiKill+"\n");
        int j =0;
        addChartData(pieChart,statsList.get(0));
        progress.dismiss();
        v.setVisibility(View.VISIBLE);
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

    public void onClick() {
        //progressBar.setVisibility(View.VISIBLE);
        try {
            Thread.sleep(100);
            //progressBar.setVisibility(View.GONE);
        }
        catch (Exception e){e.printStackTrace();}

        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/champion/"+champData.get(statsList.get(pageNum).champKey)+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item0))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/item/"+statsList.get(pageNum).items[5]+".png");
        int temp = Integer.parseInt(statsList.get(0).champKey);
        try {
            textView2.setText(champData.get(statsList.get(pageNum).champKey));
        }
        catch (Exception e){e.printStackTrace();}

        if(statsList.get(pageNum).win.equals("false")){
            textView3.setText("DEFEAT");
            textView3.setTextColor(Color.parseColor("#f4426b"));
        }
        else if(statsList.get(pageNum).win.equals("true")){
            textView3.setText("VICTORY");
            textView3.setTextColor(Color.parseColor("#2dff65"));
        }
        else
            textView3.setText("DRAW");
        kdText.setText(statsList.get(pageNum).kills+"/"+statsList.get(pageNum).deaths+"/"+statsList.get(pageNum).assists+"\n");
        kdText.append(String.format("%.2f",(Float.parseFloat(statsList.get(pageNum).kills)+Float.parseFloat(statsList.get(pageNum).assists))/Float.parseFloat(statsList.get(pageNum).deaths))+" KDA");
        //textView5.setText("Total Damage Dealt to Champions : "+statsList.get(pageNum).totalDamageDealtToChampions+"\n");
        //textView5.append("Total Gold Earned : "+statsList.get(pageNum).goldEarned+"\n");
        String goldK = String.format("%.1f",Float.parseFloat(statsList.get(pageNum).goldEarned) / 1000);
        textView4.setText("Total Gold Earned : "+goldK+" k");
        csScore.setText("CS : "+statsList.get(pageNum).totalMinionsKilled);
        //textView5.append("Largest Killing Spree : "+statsList.get(pageNum).largestKillingSpree+"\n");
        //textView5.append("Largest Multi Kill : "+statsList.get(pageNum).largestMultiKill+"\n");
        progress.dismiss();
    }

    private void addChartData(PieChart chart, Stats stats){
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        ArrayList<String> xEntries = new ArrayList<>();
        Float tempNum = (Float.valueOf(stats.totalDamageDealtToChampions)/Float.valueOf(stats.totalDamage))*100;
        yEntries.add(new PieEntry(100-tempNum));
        xEntries.add("team");
        yEntries.add(new PieEntry(Float.valueOf(tempNum)));
        xEntries.add("me");
        PieDataSet pieDataSet = new PieDataSet(yEntries,"testGraph");
        pieDataSet.setValueTextSize(16f);
        pieDataSet.setDrawValues(false);
        pieDataSet.setColors(new int[] {Color.GRAY, Color.RED});
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.setDrawCenterText(true);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(11);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setCenterText(String.format("%.1f", (Float.valueOf(stats.totalDamageDealtToChampions))/1000)+"k");
        pieChart.invalidate();
    }

}