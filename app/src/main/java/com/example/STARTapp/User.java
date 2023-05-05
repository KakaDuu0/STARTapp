package com.example.STARTapp;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;

import static com.example.STARTapp.SQLConnection.PROFILES_TABLE;

/**
 * Clasa pentru stocarea temporara a datelor
 */
public class User {
    private String username;
    private String email;
    private int xp;
    private int cursuri;
    private int streak;
    private byte[] barray;
    private float progress;
    Connection connect;

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
        updateProfile("Xp", xp);
    }

    public int getCursuri() {
        return cursuri;
    }

    public void setCursuri(int cursuri) {
        this.cursuri = cursuri;
        updateProfile("Cursuri", cursuri);
    }

    public int getStreak() {
        return streak;
    }


    public byte[] getBarray() {
        return barray;
    }

    public void setBarray(byte[] barray) {
        this.barray = barray;
    }

    public void getData() {
        getDataFromDB();
    }

    /**
     * Functie care aduce datele utilizatorului din baza de date
     */
    private void getDataFromDB() {
        try {
            connect = SQLConnection.getConnection();
            if (connect == null)
                return;
            PreparedStatement stmt = connect.prepareStatement("SELECT * FROM " + PROFILES_TABLE + " WHERE Username = ?");
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            RegisterPage create = new RegisterPage();
            rs.next();
            Calendar c = Calendar.getInstance();

            xp = rs.getInt(4);
            if (rs.wasNull()) create.createUserProfile(username);
            cursuri = rs.getInt(2);
            if (rs.wasNull()) create.createUserProfile(username);
            streak = rs.getInt(6);
            if (rs.wasNull()) create.createUserProfile(username);
            barray = rs.getBytes(3);
            if (rs.wasNull()) create.createUserProfile(username);
            int lastDay = rs.getInt(5);
            int thisDay = c.get(Calendar.DAY_OF_YEAR);
            if (rs.wasNull()) create.createUserProfile(username);
            updateProfile("Lastlogin", thisDay);
            if (lastDay == thisDay - 1 || (lastDay == 365 || lastDay == 366) && thisDay == 1) {
                streak++;
                updateProfile("Streakcounter", streak);
                xp += 10 * streak;
            } else if (thisDay != lastDay)
                updateProfile("Streakcounter", 0);
            updateProfile("Xp", xp);
        } catch (Exception e) {
            Log.e("getData() ", e.toString());
        }
    }

    /**
     * Functie care actualizeaza profilul utilizatorului
     *
     * @param column numele coloanei din tabela
     * @param val    valoare actualizata
     */
    private void updateProfile(String column, int val) {
        try {
            connect = SQLConnection.getConnection();
            if (connect == null)
                return;
            PreparedStatement stmt = connect.prepareStatement("UPDATE " + PROFILES_TABLE + " SET " + column + " = ? WHERE Username = ?");
            stmt.setInt(1, val);
            stmt.setString(2, username);
            stmt.executeUpdate();
        } catch (Exception e) {
            Log.e("", "Check connection " + e);
        }
    }

}
