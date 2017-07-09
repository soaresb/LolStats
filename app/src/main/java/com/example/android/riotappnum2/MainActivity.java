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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;
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
    private static final String API_KEY = BuildConfig.API_KEY;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    ProgressDialog progress2;
    private TextView csScore;
    private ImageView minion;
    private ImageView imageView;
    private TextView kdText;
    private TableLayout statsTable;
    private Button matchDataButton;
    private ImageView item0;
    private ImageView item2;
    private ImageView item3;
    private ImageView item4;
    private ImageView item5;
    private ImageView item6;
    public ArrayList<Match> fullMatch;
    public Summoner summoner;
    public List<Stats> statsList;
    public String currentPage;
    HashMap<String, String> champData;
    public int pageNum;
    public EditText searchbar;
    public boolean dropDown = false;
    public boolean firstTime = true;
    ProgressDialog progress;
    ProgressDialog progress3;
    ArrayList<BarEntry> barEntries;
    ArrayList<String> xAxis;
    View v;
    private String apiKey;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
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
        searchbar = (EditText) findViewById(R.id.searchBar);
        searchbar.setText(getIntent().getStringExtra("SUMMONERNAME"));
        imageView = (ImageView) findViewById(R.id.imageView);
        kdText = (TextView) findViewById(R.id.kdText);
        matchDataButton = (Button) findViewById(R.id.matchDataButton);
        statsTable = (TableLayout) findViewById(R.id.statsTable);
        statsTable.setVisibility(View.GONE);
        progress3 = new ProgressDialog(this);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//        progressBar.setVisibility(View.GONE);

        champData = new HashMap<String, String>();
        fullMatch = new ArrayList<Match>(20);
        for(int k=0;k<20;k++){
            Match temp = new Match("1");
            fullMatch.add(temp);
        }
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
        task.execute(new String[] { "https://na1.api.riotgames.com/lol/match/v3/matchlists/by-account/"+s+"?queue=420&season=8&api_key="+API_KEY});
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
                    Button dataB = (Button) findViewById(R.id.matchDataButton);
                    dataB.setText("More Data");
                    statsTable.setVisibility(View.GONE);
                    progress3.setTitle("Loading");
                    progress3.setMessage("Wait while loading...");
                    progress3.setCancelable(false); // disable dismiss by tapping outside of the dialog
                    progress3.show();
                    GetMatchData getMatchData = new GetMatchData();
                    try{
                        getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(pageNum) + "?api_key="+API_KEY});}
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
                    champData.put(key,value.getString("key"));
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
                getMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(0) + "?api_key="+API_KEY});

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
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+champData.get(statsList.get(0).champKey)+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item0))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(0).items[5]+".png");
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
        textView4.setText(goldK+" k");
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
                Bitmap bmp= Bitmap.createBitmap(32, 32, conf);
                bmp.eraseColor(Color.GRAY);
                bmImage.setImageBitmap(bmp);
            }
            else{
                bmImage.setImageBitmap(result);
            }
        }
    }

    public void onClick() {
        //progress.show();
        v.setVisibility(View.INVISIBLE);
        try {
            Thread.sleep(100);
            //progressBar.setVisibility(View.GONE);
        }
        catch (Exception e){e.printStackTrace();}

        new DownloadImageTask((ImageView) findViewById(R.id.imageView))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+champData.get(statsList.get(pageNum).champKey)+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item0))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+statsList.get(pageNum).items[5]+".png");
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
        String goldK = String.format("%.1f",Float.parseFloat(statsList.get(pageNum).goldEarned) / 1000);
        textView4.setText(goldK+" k");
        csScore.setText("CS : "+statsList.get(pageNum).totalMinionsKilled);
        //textView5.append("Largest Killing Spree : "+statsList.get(pageNum).largestKillingSpree+"\n");
        //textView5.append("Largest Multi Kill : "+statsList.get(pageNum).largestMultiKill+"\n");
        addChartData(pieChart,statsList.get(pageNum));
        //progress.dismiss();
        v.setVisibility(View.VISIBLE);
        progress3.dismiss();
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

    private class GetFullMatchData extends AsyncTask<String, Void, String> {
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

        protected void onPostExecute(String result) {
            Match thisMatch;
            try {
                JSONObject json = new JSONObject(result);
                thisMatch = new Match(json.getString("gameId"));
                JSONArray jsonArray = json.getJSONArray("participantIdentities");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject matchDetails = jsonArray.getJSONObject(i);
                    JSONObject newsummoner = (JSONObject) matchDetails.get("player");
                    Stats newSummonerStats = new Stats();
                    newSummonerStats.summonerName = newsummoner.getString("summonerName");
                    thisMatch.fullMatchData.add(newSummonerStats);
                }
                JSONArray jsonArray2 = json.getJSONArray("participants");
                //Stats newStats = new Stats();
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject match = jsonArray2.getJSONObject(i);
                    JSONObject temp = new JSONObject(match.getString("stats"));
                    Stats newStats = thisMatch.fullMatchData.get(i);
                    newStats.deaths = temp.getString("deaths");
                    newStats.kills = temp.getString("kills");
                    newStats.assists = temp.getString("assists");
                    newStats.totalMinionsKilled = temp.getString("totalMinionsKilled");
                    newStats.totalDamageDealtToChampions = temp.getString("totalDamageDealtToChampions");
                    newStats.champKey = match.getString("championId");
                    newStats.goldEarned = temp.getString("goldEarned");
                    newStats.items[0]=temp.getString("item0");
                    newStats.items[1]=temp.getString("item1");
                    newStats.items[2]=temp.getString("item2");
                    newStats.items[3]=temp.getString("item3");
                    newStats.items[4]=temp.getString("item4");
                    newStats.items[5]=temp.getString("item5");
                }
                fullMatch.add(pageNum,thisMatch);
//                View table = statsTable;
//                TextView text = (TextView) table.findViewById(R.id.row1Champ);
//                text.setText(fullMatchData.get(0).fullMatchData.get(0).champKey);
            }
            catch (Exception e ){e.printStackTrace();}

            updateTable();



        }
    }

    public void OnClickMatchData(View view){
        if(statsTable.getVisibility()==View.VISIBLE){
            statsTable.setVisibility(View.GONE);
            Button dataB = (Button) findViewById(R.id.matchDataButton);
            dataB.setText("More Data");
        }
        else {
            Button dataB = (Button) findViewById(R.id.matchDataButton);
            dataB.setText("Less Data");
            progress2 = new ProgressDialog(this);
            progress2.setTitle("Loading");
            progress2.setMessage("Wait while loading...");
            progress2.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress2.show();
            GetFullMatchData getFullMatchData = new GetFullMatchData();
            try {
                getFullMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + summoner.gameIds.get(pageNum) + "?api_key="+API_KEY});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void updateTable(){
        View table = statsTable;
        TextView summoner1 = (TextView) table.findViewById(R.id.row1champ1);
        TextView summoner6 = (TextView) table.findViewById(R.id.row1champ6);
        TextView summoner1kda = (TextView) table.findViewById(R.id.row1champ1kda);
        TextView summoner6kda = (TextView) table.findViewById(R.id.row1champ6kda);
        summoner1kda.setText(fullMatch.get(pageNum).fullMatchData.get(0).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(0).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(0).assists+"\n");
        summoner6kda.setText(fullMatch.get(pageNum).fullMatchData.get(5).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(5).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(5).assists+"\n");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row1champ1pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(0).champKey)).replaceAll("\\s+","")+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row1champ6pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(5).champKey)).replaceAll("\\s+","")+".png");
        summoner1.setText(fullMatch.get(pageNum).fullMatchData.get(0).summonerName);
        summoner6.setText(fullMatch.get(pageNum).fullMatchData.get(5).summonerName);
        TextView sum1gold = (TextView) table.findViewById(R.id.champ1goldnum);
        TextView sum1cs = (TextView) table.findViewById(R.id.champ1csnum);
        sum1cs.setText(fullMatch.get(pageNum).fullMatchData.get(0).totalMinionsKilled);
        sum1gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(0).goldEarned)) / 1000)+" k");
        TextView sum6gold = (TextView) table.findViewById(R.id.champ6goldnum);
        TextView sum6cs = (TextView) table.findViewById(R.id.champ6csnum);
        sum6cs.setText(fullMatch.get(pageNum).fullMatchData.get(5).totalMinionsKilled);
        sum6gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(5).goldEarned)) / 1000)+" k");

        ////////////////////////////////////////////////////////////////////////
        TextView summoner2 = (TextView) table.findViewById(R.id.row2champ2);
        TextView summoner7 = (TextView) table.findViewById(R.id.row2champ7);
        TextView summoner2kda = (TextView) table.findViewById(R.id.row2champ2kda);
        TextView summoner7kda = (TextView) table.findViewById(R.id.row2champ7kda);
        summoner2kda.setText(fullMatch.get(pageNum).fullMatchData.get(1).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(1).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(1).assists+"\n");
        summoner7kda.setText(fullMatch.get(pageNum).fullMatchData.get(6).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(6).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(6).assists+"\n");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row2champ2pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(1).champKey)).replaceAll("\\s+","")+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row2champ7pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(6).champKey)).replaceAll("\\s+","")+".png");
        summoner2.setText(fullMatch.get(pageNum).fullMatchData.get(1).summonerName);
        summoner7.setText(fullMatch.get(pageNum).fullMatchData.get(6).summonerName);
        TextView sum2gold = (TextView) table.findViewById(R.id.champ2goldnum);
        TextView sum2cs = (TextView) table.findViewById(R.id.champ2csnum);
        sum2cs.setText(fullMatch.get(pageNum).fullMatchData.get(1).totalMinionsKilled);
        sum2gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(1).goldEarned)) / 1000)+" k");
        TextView sum7gold = (TextView) table.findViewById(R.id.champ7goldnum);
        TextView sum7cs = (TextView) table.findViewById(R.id.champ7csnum);
        sum7cs.setText(fullMatch.get(pageNum).fullMatchData.get(6).totalMinionsKilled);
        sum7gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(6).goldEarned)) / 1000)+" k");

        ////////////////////////////////////////////////////////////////////////
        TextView summoner3 = (TextView) table.findViewById(R.id.row3champ3);
        TextView summoner8 = (TextView) table.findViewById(R.id.row3champ8);
        TextView summoner3kda = (TextView) table.findViewById(R.id.row3champ3kda);
        TextView summoner8kda = (TextView) table.findViewById(R.id.row3champ8kda);
        summoner3kda.setText(fullMatch.get(pageNum).fullMatchData.get(2).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(2).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(2).assists+"\n");
        summoner8kda.setText(fullMatch.get(pageNum).fullMatchData.get(7).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(7).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(7).assists+"\n");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row3champ3pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(2).champKey)).replaceAll("\\s+","")+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row3champ8pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(7).champKey)).replaceAll("\\s+","")+".png");
        summoner3.setText(fullMatch.get(pageNum).fullMatchData.get(2).summonerName);
        summoner8.setText(fullMatch.get(pageNum).fullMatchData.get(7).summonerName);
        TextView sum3gold = (TextView) table.findViewById(R.id.champ3goldnum);
        TextView sum3cs = (TextView) table.findViewById(R.id.champ3csnum);
        sum3cs.setText(fullMatch.get(pageNum).fullMatchData.get(2).totalMinionsKilled);
        sum3gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(2).goldEarned)) / 1000)+" k");
        TextView sum8gold = (TextView) table.findViewById(R.id.champ8goldnum);
        TextView sum8cs = (TextView) table.findViewById(R.id.champ8csnum);
        sum8cs.setText(fullMatch.get(pageNum).fullMatchData.get(7).totalMinionsKilled);
        sum8gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(7).goldEarned)) / 1000)+" k");

        ////////////////////////////////////////////////////////////////////////
        TextView summoner4 = (TextView) table.findViewById(R.id.row4champ4);
        TextView summoner9 = (TextView) table.findViewById(R.id.row4champ9);
        TextView summoner4kda = (TextView) table.findViewById(R.id.row4champ4kda);
        TextView summoner9kda = (TextView) table.findViewById(R.id.row4champ9kda);
        summoner4kda.setText(fullMatch.get(pageNum).fullMatchData.get(3).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(3).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(3).assists+"\n");
        summoner9kda.setText(fullMatch.get(pageNum).fullMatchData.get(8).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(8).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(8).assists+"\n");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row4champ4pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(3).champKey)).replaceAll("\\s+","")+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row4champ9pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(8).champKey)).replaceAll("\\s+","")+".png");
        summoner4.setText(fullMatch.get(pageNum).fullMatchData.get(3).summonerName);
        summoner9.setText(fullMatch.get(pageNum).fullMatchData.get(8).summonerName);
        TextView sum4gold = (TextView) table.findViewById(R.id.champ4goldnum);
        TextView sum4cs = (TextView) table.findViewById(R.id.champ4csnum);
        sum4cs.setText(fullMatch.get(pageNum).fullMatchData.get(3).totalMinionsKilled);
        sum4gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(3).goldEarned)) / 1000)+" k");
        TextView sum9gold = (TextView) table.findViewById(R.id.champ9goldnum);
        TextView sum9cs = (TextView) table.findViewById(R.id.champ9csnum);
        sum9cs.setText(fullMatch.get(pageNum).fullMatchData.get(8).totalMinionsKilled);
        sum9gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(8).goldEarned)) / 1000)+" k");

        ////////////////////////////////////////////////////////////////////////
        TextView summoner5 = (TextView) table.findViewById(R.id.row5champ5);
        TextView summoner10 = (TextView) table.findViewById(R.id.row5champ10);
        TextView summoner5kda = (TextView) table.findViewById(R.id.row5champ5kda);
        TextView summoner10kda = (TextView) table.findViewById(R.id.row5champ10kda);
        summoner5kda.setText(fullMatch.get(pageNum).fullMatchData.get(4).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(4).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(4).assists+"\n");
        summoner10kda.setText(fullMatch.get(pageNum).fullMatchData.get(9).kills+"/"+fullMatch.get(pageNum).fullMatchData.get(9).deaths+"/"+fullMatch.get(pageNum).fullMatchData.get(9).assists+"\n");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row5champ5pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(4).champKey)).replaceAll("\\s+","")+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.row5champ10pic))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(fullMatch.get(pageNum).fullMatchData.get(9).champKey)).replaceAll("\\s+","")+".png");
        summoner5.setText(fullMatch.get(pageNum).fullMatchData.get(4).summonerName);
        summoner10.setText(fullMatch.get(pageNum).fullMatchData.get(9).summonerName);
        TextView sum5gold = (TextView) table.findViewById(R.id.champ5goldnum);
        TextView sum5cs = (TextView) table.findViewById(R.id.champ5csnum);
        sum5cs.setText(fullMatch.get(pageNum).fullMatchData.get(4).totalMinionsKilled);
        sum5gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(4).goldEarned)) / 1000)+" k");
        TextView sum10gold = (TextView) table.findViewById(R.id.champ10goldnum);
        TextView sum10cs = (TextView) table.findViewById(R.id.champ10csnum);
        sum10cs.setText(fullMatch.get(pageNum).fullMatchData.get(9).totalMinionsKilled);
        sum10gold.setText(String.format("%.1f",(Float.parseFloat(fullMatch.get(pageNum).fullMatchData.get(9).goldEarned)) / 1000)+" k");

        new DownloadImageTask((ImageView) findViewById(R.id.champ1item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ1item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ1item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ1item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ1item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ1item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(0).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ2item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ2item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ2item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ2item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ2item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ2item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(1).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ3item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ3item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ3item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ3item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ3item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ3item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(2).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ4item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ4item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ4item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ4item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ4item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ4item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(3).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ5item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ5item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ5item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ5item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ5item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ5item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(4).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ6item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ6item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ6item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ6item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ6item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ6item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(5).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ7item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ7item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ7item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ7item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ7item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ7item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(6).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ8item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ8item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ8item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ8item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ8item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ8item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(7).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ9item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ9item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ9item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ9item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ9item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ9item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(8).items[5]+".png");

        new DownloadImageTask((ImageView) findViewById(R.id.champ10item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[0]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ10item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[1]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ10item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[2]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ10item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[3]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ10item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[4]+".png");
        new DownloadImageTask((ImageView) findViewById(R.id.champ10item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+fullMatch.get(pageNum).fullMatchData.get(9).items[5]+".png");

        try{
            Thread.sleep(1050);
            progress2.dismiss();
            statsTable.setVisibility(View.VISIBLE);
        }
        catch (Exception e){e.printStackTrace();}


    }

    public class GetSummonerData extends AsyncTask<String, Void, String> {
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
                String accountID;
                String summonerID;
                String summonerName;
                accountID = json.getString("accountId");
                summonerID = json.getString("id");
                summonerName=json.getString("name");
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("ACCOUNTID", accountID);
                intent.putExtra("ID",summonerID);
                intent.putExtra("SUMMONERNAME",summonerName);
                startActivity(intent);

            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invaild Summoner Name", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

        }
    }
    public void getStats(View view){
        GetSummonerData getSummonerData = new GetSummonerData();
        getSummonerData.execute(new String[]{"https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"+searchbar.getText().toString()+"?api_key="+API_KEY});


    }

}