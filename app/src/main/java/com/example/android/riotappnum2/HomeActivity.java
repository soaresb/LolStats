package com.example.android.riotappnum2;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends AppCompatActivity {
    private EditText editText;
    private Button button;
    private TextView textView;
    public String accountID;
    public String summonerID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        editText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button2);
        textView = (TextView) findViewById(R.id.textView) ;
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
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.putExtra("ACCOUNTID", accountID);
                intent.putExtra("ID",summonerID);
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
        getSummonerData.execute(new String[]{"https://na1.api.riotgames.com/lol/summoner/v3/summoners/by-name/"+editText.getText().toString()+"?api_key=RGAPI-22d59933-21c5-4a66-8896-702a6bcdda25"});


    }
}
