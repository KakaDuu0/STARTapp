package com.example.STARTapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;

public class Leaderboard {
    LinkedHashMap<String, Integer> user = new LinkedHashMap<>();

    public void init() {
        Connection connect = SQLConnection.getConnection();
        try {
            if (connect == null)
                return;
            user.clear();
            PreparedStatement statement = connect.prepareStatement("SELECT Username, Xp FROM " + SQLConnection.PROFILES_TABLE + " ORDER BY Xp DESC");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next())
                user.put(resultSet.getString(1), resultSet.getInt(2));
        } catch (Exception e) {
            Log.e("setData: ", e.toString());
        }
    }

    public void refresh() {
        init();
    }

    public LinkedHashMap<String, Integer> getMap() {
        return user;
    }
}
