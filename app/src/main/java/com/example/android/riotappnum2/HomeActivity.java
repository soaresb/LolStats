package com.example.android.riotappnum2;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBarActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private Button firebaseButton;
    private TextView textView;
    private DatabaseReference mDatabase;
    public String accountID;
    public String summonerID;
    public String summonerName;
    public String summonerIcon;
    private String apiKey;
    private static final String API_KEY = BuildConfig.API_KEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView) ;
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/gladdyy
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
                accountID = json.getString("accountId");
                summonerID = json.getString("id");
                summonerName=json.getString("name");
                summonerIcon=json.getString("profileIconId");
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("ACCOUNTID", accountID);
                intent.putExtra("ID",summonerID);
                intent.putExtra("SUMMONERNAME",summonerName);
                intent.putExtra("SUMMONERICON",summonerIcon);
                String FILENAME = "recentsummoners.txt";
                FileOutputStream fos =openFileOutput(FILENAME, Context.MODE_PRIVATE);
                byte [] buf = accountID.getBytes();
                fos.write(buf);
                fos.close();
                startActivity(intent);

            }
            catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Invaild Summoner Name", Toast.LENGTH_LONG).show();
                e.printStackTrace();

            }

        }
    }
    public void OnClick(View view){
        GetSummonerData getSummonerData = new GetSummonerData();
        getSummonerData.execute(new String[]{"https://lolstats-backend.herokuapp.com/summoner/"+editText.getText().toString()});
    }
    public void onGetFullStats(View view){
        Intent intent = new Intent(getBaseContext(), SeasonStats.class);
        startActivity(intent);
    }

}
