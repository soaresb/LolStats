package com.example.android.riotappnum2;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by kyle.jablonski on 10/6/15.
 */
public class Item implements Parcelable{



    public String title;
    public String description;
    public boolean isExpanded;
    public boolean itemChanged;
    public String picS;
    public String item1;
    public String item2;
    public String item3;
    public String item4;
    public String item5;
    public String item6;
    public String kills;
    public String deaths;
    public String assists;
    public String spell1;
    public String spell2;
    public String gold;
    public String cs;
    public String win;
    public String time;
    public String matchId;
    HashMap<String, String> champData;



    public Item(){}

    public Item(Parcel in){
        title = in.readString();
        description = in.readString();
        isExpanded = in.readInt() == 1;
        itemChanged = in.readInt()==1;
        picS=in.readString();
        item1=in.readString();
        item2=in.readString();
        item3=in.readString();
        item4=in.readString();
        item5=in.readString();
        item6=in.readString();
        kills=in.readString();
        deaths=in.readString();
        assists=in.readString();
        spell1=in.readString();
        spell2=in.readString();
        gold=in.readString();
        cs=in.readString();
        win=in.readString();
        time=in.readString();
        matchId=in.readString();
        champData=in.readHashMap(ClassLoader.getSystemClassLoader());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeInt(isExpanded ? 1 : 0);
        dest.writeInt(itemChanged ? 1 : 0);
        dest.writeString(picS);
        dest.writeString(item1);
        dest.writeString(item2);
        dest.writeString(item3);
        dest.writeString(item4);
        dest.writeString(item5);
        dest.writeString(item6);
        dest.writeString(kills);
        dest.writeString(deaths);
        dest.writeString(assists);
        dest.writeString(spell1);
        dest.writeString(spell2);
        dest.writeString(cs);
        dest.writeString(gold);
        dest.writeString(win);
        dest.writeString(time);
        dest.writeString(matchId);
        dest.writeMap(champData);

    }

    public static final Creator<Item> CREATOR = new Creator<Item>(){
        @Override
        public Item createFromParcel(Parcel source) {
            return new Item(source);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };
}
