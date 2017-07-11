package com.example.android.riotappnum2;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatTextView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.R.drawable.arrow_down_float;
import static android.R.drawable.arrow_up_float;


public class ExpandableAdapter extends BaseAdapter {
    private static final String API_KEY = BuildConfig.API_KEY;
    List<Item> items;
    Context context;
    public HashMap<String,Match> fullMatch;
    ProgressDialog progress2;
    boolean ttt = false;
    TableLayout t;
    FrameLayout frameLayout;
    View conV;
    HashMap<String, String> champData;


    public class Row {
        AppCompatTextView mTvTitle;
        AppCompatTextView mTvDescription;
        FrameLayout mFlWrapper;
        ImageView mIvArrow;
        ImageView item1;
        ImageView item2;
        ImageView item3;
        ImageView item4;
        ImageView item5;
        SmartImageView item6;
        ImageView spell1;
        ImageView spell2;
        TextView decKDA;
        TextView creeps;
        TextView gold;
        TextView kdaview;
        TextView win;
        TextView time;
        ImageView arrow;
        SmartImageView myImage;
        TableLayout statsTable;


    }

    public ExpandableAdapter(Context context, List<Item> items) {
        this.items = items;
        this.context = context;
        fullMatch = new HashMap<String,Match>();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Row theRow;
        if (convertView == null) {
            theRow = new Row();
            convertView = LayoutInflater.from(context).inflate(R.layout.row_item, parent, false);

            theRow.mFlWrapper = (FrameLayout) convertView.findViewById(R.id.fl_wrapper);
            frameLayout=theRow.mFlWrapper;
            //theRow.mTvTitle = (AppCompatTextView) convertView.findViewById(R.id.tv_title);
            //theRow.mTvDescription = (AppCompatTextView) convertView.findViewById(R.id.tv_description);
            theRow.mIvArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            theRow.item1 = (ImageView) convertView.findViewById(R.id.item1);
            theRow.item2 = (ImageView) convertView.findViewById(R.id.item2);
            theRow.item3 = (ImageView) convertView.findViewById(R.id.item3);
            theRow.item4 = (ImageView) convertView.findViewById(R.id.item4);
            //theRow.item5 = (ImageView) convertView.findViewById(R.id.item5);
            theRow.item6 = (SmartImageView) convertView.findViewById(R.id.item6);
            theRow.kdaview = (TextView) convertView.findViewById(R.id.kDA);
            theRow.spell1 = (ImageView) convertView.findViewById(R.id.spell1);
            theRow.spell2 = (ImageView) convertView.findViewById(R.id.spell2);
            theRow.decKDA = (TextView) convertView.findViewById(R.id.decKDA);
            theRow.gold = (TextView) convertView.findViewById(R.id.gold);
            theRow.creeps = (TextView) convertView.findViewById(R.id.creeps);
            theRow.win = (TextView) convertView.findViewById(R.id.win);
            theRow.time = (TextView) convertView.findViewById(R.id.matchTime);
            theRow.arrow = (ImageView) convertView.findViewById(R.id.arrow);
            theRow.myImage = (SmartImageView) convertView.findViewById(R.id.my_image);
            theRow.statsTable = (TableLayout) convertView.findViewById(R.id.statsTable);
            theRow.statsTable.setVisibility(View.INVISIBLE);
            t=theRow.statsTable;
            convertView.setTag(theRow);
        } else {

            theRow = (Row) convertView.getTag();
        }

        // Update the View

        conV=convertView;
        Item item = items.get(position);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(64, 64, conf);
        bmp.eraseColor(Color.GRAY);
        theRow.myImage.setImageBitmap(bmp);
        Bitmap.Config conff = Bitmap.Config.ARGB_8888;
        Bitmap bmpp = Bitmap.createBitmap(64, 64, conf);
        bmpp.eraseColor(Color.GRAY);
        theRow.item6.setImageBitmap(bmpp);
        theRow.myImage.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item5 + ".png");
        theRow.item6.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item6 + ".png");
//            if(theRow.item6==null){
//                Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//                Bitmap bmp = Bitmap.createBitmap(32, 32, conf);
//                bmp.eraseColor(Color.GRAY);
//                theRow.item6.setImageBitmap(bmp);
//            }
        new DownloadImageTask(theRow.item1)
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item1 + ".png");
        new DownloadImageTask(theRow.item2)
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item2 + ".png");
        new DownloadImageTask(theRow.item3)
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item3 + ".png");
        new DownloadImageTask(theRow.item4)
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/" + item.item4 + ".png");
        new DownloadImageTask(theRow.spell1)
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/spell/" + item.spell1 + ".png");
        new DownloadImageTask(theRow.spell2)
                .execute("http://ddragon.leagueoflegends.com/cdn/6.24.1/img/spell/" + item.spell2 + ".png");
        item.itemChanged=false;

        if (item.isExpanded) {

            champData=item.champData;
            theRow.mFlWrapper.setVisibility(View.VISIBLE);
            theRow.arrow.setImageResource(arrow_up_float);
            //theRow.statsTable.setVisibility(View.INVISIBLE);
            if(fullMatch.get(item.matchId)!=null){
                updateTable(theRow.statsTable,fullMatch.get(item.matchId));
                theRow.statsTable.setVisibility(View.VISIBLE);
            }
            else {
                progress2 = new ProgressDialog(context);
                progress2.setTitle("Loading");
                progress2.setMessage("Wait while loading...");
                progress2.setCancelable(false); // disable dismiss by tapping outside of the dialog
                progress2.show();
                GetFullMatchData getFullMatchData = new GetFullMatchData();
                try {
                    getFullMatchData.execute(new String[]{"https://na1.api.riotgames.com/lol/match/v3/matches/" + item.matchId + "?api_key=" + API_KEY});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //updateTable(theRow.statsTable);

        } else {
            theRow.mFlWrapper.setVisibility(View.GONE);
            theRow.arrow.setImageResource(arrow_down_float);
            //theRow.mIvArrow.setRotation(0f);
        }
        String icon = item.picS;
        int resID = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());
        theRow.mIvArrow.setImageResource(resID);
        theRow.kdaview.setText(item.kills + "/" + item.deaths + "/" + item.assists);
        theRow.decKDA.setText(String.format("%.2f", (Float.parseFloat(item.kills) + Float.parseFloat(item.assists)) / Float.parseFloat(item.deaths)) + " KDA");
        theRow.creeps.setText(item.cs + " CS");
        theRow.gold.setText(String.format("%.2f", Float.parseFloat(item.gold) / 1000) + " k");
        if (item.win.equals("true")) {
            theRow.win.setText("VICTORY");
            theRow.win.setTextColor(Color.GREEN);
        } else if (item.win.equals("false")) {
            theRow.win.setText("DEFEAT");
            theRow.win.setTextColor(Color.RED);
        }
        // return the view
        theRow.time.setText(DateUtils.formatElapsedTime(Long.parseLong(item.time)).toString());
        return convertView;


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
                    newStats.items[0] = temp.getString("item0");
                    newStats.items[1] = temp.getString("item1");
                    newStats.items[2] = temp.getString("item2");
                    newStats.items[3] = temp.getString("item3");
                    newStats.items[4] = temp.getString("item4");
                    newStats.items[5] = temp.getString("item5");
                }
                fullMatch.put(json.getString("gameId"),thisMatch);
                updateTable(t,thisMatch);
                //progress2.dismiss();

//                View table = statsTable;
//                TextView text = (TextView) table.findViewById(R.id.row1Champ);
//                text.setText(fullMatchData.get(0).fullMatchData.get(0).champKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //progress2.dismiss();



        }
    }
    public void loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = context.getResources().openRawResource(R.raw.champions);
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

    private void updateTable(TableLayout table,Match match) {

        TextView summoner1 = (TextView) table.findViewById(R.id.row1champ1);
        TextView summoner6 = (TextView) table.findViewById(R.id.row1champ6);
        TextView summoner1kda = (TextView) table.findViewById(R.id.row1champ1kda);
        TextView summoner6kda = (TextView) table.findViewById(R.id.row1champ6kda);
        summoner1kda.setText(match.fullMatchData.get(0).kills + "/" + match.fullMatchData.get(0).deaths + "/" + match.fullMatchData.get(0).assists + "\n");
        table.setVisibility(View.VISIBLE);
        summoner6kda.setText(match.fullMatchData.get(5).kills + "/" + match.fullMatchData.get(5).deaths + "/" + match.fullMatchData.get(5).assists + "\n");
        String sum1 = champData.get(match.fullMatchData.get(0).champKey).toLowerCase();
        int resID1 = context.getResources().getIdentifier(sum1, "drawable", context.getPackageName());
        ImageView sum1pic = (ImageView) table.findViewById(R.id.row1champ1pic);
        sum1pic.setImageResource(resID1);
        String sum6 = champData.get(match.fullMatchData.get(5).champKey).toLowerCase();
        int resID6 = context.getResources().getIdentifier(sum6, "drawable", context.getPackageName());
        ImageView sum6pic = (ImageView) table.findViewById(R.id.row1champ6pic);
        sum6pic.setImageResource(resID6);
        summoner1.setText(match.fullMatchData.get(0).summonerName);
        summoner6.setText(match.fullMatchData.get(5).summonerName);
        TextView sum1gold = (TextView) table.findViewById(R.id.champ1goldnum);
        TextView sum1cs = (TextView) table.findViewById(R.id.champ1csnum);
        sum1cs.setText(match.fullMatchData.get(0).totalMinionsKilled);
        sum1gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(0).goldEarned)) / 1000)+" k");
        TextView sum6gold = (TextView) table.findViewById(R.id.champ6goldnum);
        TextView sum6cs = (TextView) table.findViewById(R.id.champ6csnum);
        sum6cs.setText(match.fullMatchData.get(5).totalMinionsKilled);
        sum6gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(5).goldEarned)) / 1000)+" k");
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextView summoner2 = (TextView) table.findViewById(R.id.row2champ2);
        TextView summoner7 = (TextView) table.findViewById(R.id.row2champ7);
        TextView summoner2kda = (TextView) table.findViewById(R.id.row2champ2kda);
        TextView summoner7kda = (TextView) table.findViewById(R.id.row2champ7kda);
        summoner2kda.setText(match.fullMatchData.get(1).kills+"/"+match.fullMatchData.get(1).deaths+"/"+match.fullMatchData.get(1).assists+"\n");
        summoner7kda.setText(match.fullMatchData.get(6).kills+"/"+match.fullMatchData.get(6).deaths+"/"+match.fullMatchData.get(6).assists+"\n");
        String sum2 = champData.get(match.fullMatchData.get(1).champKey).toLowerCase();
        int resID2 = context.getResources().getIdentifier(sum2, "drawable", context.getPackageName());
        ImageView sum2pic = (ImageView) table.findViewById(R.id.row2champ2pic);
        sum2pic.setImageResource(resID2);
        String sum7 = champData.get(match.fullMatchData.get(6).champKey).toLowerCase();
        int resID7 = context.getResources().getIdentifier(sum7, "drawable", context.getPackageName());
        ImageView sum7pic = (ImageView) table.findViewById(R.id.row2champ7pic);
        sum7pic.setImageResource(resID7);
        summoner2.setText(match.fullMatchData.get(1).summonerName);
        summoner7.setText(match.fullMatchData.get(6).summonerName);
        TextView sum2gold = (TextView) table.findViewById(R.id.champ2goldnum);
        TextView sum2cs = (TextView) table.findViewById(R.id.champ2csnum);
        sum2cs.setText(match.fullMatchData.get(1).totalMinionsKilled);
        sum2gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(1).goldEarned)) / 1000)+" k");
        TextView sum7gold = (TextView) table.findViewById(R.id.champ7goldnum);
        TextView sum7cs = (TextView) table.findViewById(R.id.champ7csnum);
        sum7cs.setText(match.fullMatchData.get(6).totalMinionsKilled);
        sum7gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(6).goldEarned)) / 1000)+" k");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextView summoner3 = (TextView) table.findViewById(R.id.row3champ3);
        TextView summoner8 = (TextView) table.findViewById(R.id.row3champ8);
        TextView summoner3kda = (TextView) table.findViewById(R.id.row3champ3kda);
        TextView summoner8kda = (TextView) table.findViewById(R.id.row3champ8kda);
        summoner3kda.setText(match.fullMatchData.get(2).kills+"/"+match.fullMatchData.get(2).deaths+"/"+match.fullMatchData.get(2).assists+"\n");
        summoner8kda.setText(match.fullMatchData.get(7).kills+"/"+match.fullMatchData.get(7).deaths+"/"+match.fullMatchData.get(7).assists+"\n");
        String sum3 = champData.get(match.fullMatchData.get(2).champKey).toLowerCase();
        int resID3 = context.getResources().getIdentifier(sum3, "drawable", context.getPackageName());
        ImageView sum3pic = (ImageView) table.findViewById(R.id.row3champ3pic);
        sum3pic.setImageResource(resID3);
        String sum8 = champData.get(match.fullMatchData.get(7).champKey).toLowerCase();
        int resID8 = context.getResources().getIdentifier(sum8, "drawable", context.getPackageName());
        ImageView sum8pic = (ImageView) table.findViewById(R.id.row3champ8pic);
        sum8pic.setImageResource(resID8);
        summoner3.setText(match.fullMatchData.get(2).summonerName);
        summoner8.setText(match.fullMatchData.get(7).summonerName);
        TextView sum3gold = (TextView) table.findViewById(R.id.champ3goldnum);
        TextView sum3cs = (TextView) table.findViewById(R.id.champ3csnum);
        sum3cs.setText(match.fullMatchData.get(2).totalMinionsKilled);
        sum3gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(2).goldEarned)) / 1000)+" k");
        TextView sum8gold = (TextView) table.findViewById(R.id.champ8goldnum);
        TextView sum8cs = (TextView) table.findViewById(R.id.champ8csnum);
        sum8cs.setText(match.fullMatchData.get(7).totalMinionsKilled);
        sum8gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(7).goldEarned)) / 1000)+" k");
        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextView summoner4 = (TextView) table.findViewById(R.id.row4champ4);
        TextView summoner9 = (TextView) table.findViewById(R.id.row4champ9);
        TextView summoner4kda = (TextView) table.findViewById(R.id.row4champ4kda);
        TextView summoner9kda = (TextView) table.findViewById(R.id.row4champ9kda);
        summoner4kda.setText(match.fullMatchData.get(3).kills+"/"+match.fullMatchData.get(3).deaths+"/"+match.fullMatchData.get(3).assists+"\n");
        summoner9kda.setText(match.fullMatchData.get(8).kills+"/"+match.fullMatchData.get(8).deaths+"/"+match.fullMatchData.get(8).assists+"\n");
        String sum4 = champData.get(match.fullMatchData.get(3).champKey).toLowerCase();
        int resID4 = context.getResources().getIdentifier(sum4, "drawable", context.getPackageName());
        ImageView sum4pic = (ImageView) table.findViewById(R.id.row4champ4pic);
        sum4pic.setImageResource(resID4);
        String sum9 = champData.get(match.fullMatchData.get(8).champKey).toLowerCase();
        int resID9 = context.getResources().getIdentifier(sum9, "drawable", context.getPackageName());
        ImageView sum9pic = (ImageView) table.findViewById(R.id.row4champ9pic);
        sum9pic.setImageResource(resID9);
        summoner4.setText(match.fullMatchData.get(3).summonerName);
        summoner9.setText(match.fullMatchData.get(8).summonerName);
        TextView sum4gold = (TextView) table.findViewById(R.id.champ4goldnum);
        TextView sum4cs = (TextView) table.findViewById(R.id.champ4csnum);
        sum4cs.setText(match.fullMatchData.get(3).totalMinionsKilled);
        sum4gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(3).goldEarned)) / 1000)+" k");
        TextView sum9gold = (TextView) table.findViewById(R.id.champ9goldnum);
        TextView sum9cs = (TextView) table.findViewById(R.id.champ9csnum);
        sum9cs.setText(match.fullMatchData.get(8).totalMinionsKilled);
        sum9gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(8).goldEarned)) / 1000)+" k");
        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        TextView summoner5 = (TextView) table.findViewById(R.id.row5champ5);
        TextView summoner10 = (TextView) table.findViewById(R.id.row5champ10);
        TextView summoner5kda = (TextView) table.findViewById(R.id.row5champ5kda);
        TextView summoner10kda = (TextView) table.findViewById(R.id.row5champ10kda);
        summoner5kda.setText(match.fullMatchData.get(4).kills+"/"+match.fullMatchData.get(4).deaths+"/"+match.fullMatchData.get(4).assists+"\n");
        summoner10kda.setText(match.fullMatchData.get(9).kills+"/"+match.fullMatchData.get(9).deaths+"/"+match.fullMatchData.get(9).assists+"\n");
        String sum5 = champData.get(match.fullMatchData.get(4).champKey).toLowerCase();
        int resID5 = context.getResources().getIdentifier(sum5, "drawable", context.getPackageName());
        ImageView sum5pic = (ImageView) table.findViewById(R.id.row5champ5pic);
        sum5pic.setImageResource(resID5);
        String sum10 = champData.get(match.fullMatchData.get(9).champKey).toLowerCase();
        int resID10 = context.getResources().getIdentifier(sum10, "drawable", context.getPackageName());
        ImageView sum10pic = (ImageView) table.findViewById(R.id.row5champ10pic);
        sum10pic.setImageResource(resID10);
//        new DownloadImageTask((ImageView) table.findViewById(R.id.row5champ5pic))
//                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(match.fullMatchData.get(4).champKey)).replaceAll("\\s+","")+".png");
//        new DownloadImageTask((ImageView) table.findViewById(R.id.row5champ10pic))
//                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/champion/"+(champData.get(match.fullMatchData.get(9).champKey)).replaceAll("\\s+","")+".png");
        summoner5.setText(match.fullMatchData.get(4).summonerName);
        summoner10.setText(match.fullMatchData.get(9).summonerName);
        TextView sum5gold = (TextView) table.findViewById(R.id.champ5goldnum);
        TextView sum5cs = (TextView) table.findViewById(R.id.champ5csnum);
        sum5cs.setText(match.fullMatchData.get(4).totalMinionsKilled);
        sum5gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(4).goldEarned)) / 1000)+" k");
        TextView sum10gold = (TextView) table.findViewById(R.id.champ10goldnum);
        TextView sum10cs = (TextView) table.findViewById(R.id.champ10csnum);
        sum10cs.setText(match.fullMatchData.get(9).totalMinionsKilled);
        sum10gold.setText(String.format("%.1f",(Float.parseFloat(match.fullMatchData.get(9).goldEarned)) / 1000)+" k");
        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        SmartImageView c1i1 = (SmartImageView) table.findViewById(R.id.champ1item1);
        c1i1.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[0]+".png");
        SmartImageView c1i2 = (SmartImageView) table.findViewById(R.id.champ1item2);
        c1i2.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[1]+".png");
        SmartImageView c1i3 = (SmartImageView) table.findViewById(R.id.champ1item3);
        c1i3.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[2]+".png");
        SmartImageView c1i4 = (SmartImageView) table.findViewById(R.id.champ1item4);
        c1i4.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[3]+".png");
        SmartImageView c1i5 = (SmartImageView) table.findViewById(R.id.champ1item5);
        c1i5.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[4]+".png");
        SmartImageView c1i6 = (SmartImageView) table.findViewById(R.id.champ1item6);
        c1i6.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[5]+".png");

        SmartImageView c2i1 = (SmartImageView) table.findViewById(R.id.champ2item1);
        c2i1.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[0]+".png");
        SmartImageView c2i2 = (SmartImageView) table.findViewById(R.id.champ2item2);
        c2i2.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[1]+".png");
        SmartImageView c2i3 = (SmartImageView) table.findViewById(R.id.champ2item3);
        c2i3.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[2]+".png");
        SmartImageView c2i4 = (SmartImageView) table.findViewById(R.id.champ2item4);
        c2i4.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[3]+".png");
        SmartImageView c2i5 = (SmartImageView) table.findViewById(R.id.champ2item5);
        c2i5.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[4]+".png");
        SmartImageView c2i6 = (SmartImageView) table.findViewById(R.id.champ2item6);
        c2i6.setImageUrl("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(0).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ2item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(1).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ3item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(2).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ4item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(3).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ5item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(4).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ6item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(5).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ7item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(6).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ8item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(7).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ9item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(8).items[5]+".png");

        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item1))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[0]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item2))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[1]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item3))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[2]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item4))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[3]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item5))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[4]+".png");
        new DownloadImageTask((ImageView) table.findViewById(R.id.champ10item6))
                .execute("http://ddragon.leagueoflegends.com/cdn/7.10.1/img/item/"+match.fullMatchData.get(9).items[5]+".png");
        notifyDataSetChanged();
        try{Thread.sleep(1000);}
        catch (Exception e){e.printStackTrace();}
        progress2.dismiss();


    }
}

