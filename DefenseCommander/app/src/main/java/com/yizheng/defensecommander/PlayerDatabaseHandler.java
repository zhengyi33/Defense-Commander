package com.yizheng.defensecommander;

import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class PlayerDatabaseHandler extends AsyncTask<String, Void, String> {

    private static final String TAG = "PlayerDatabaseHandler";
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private ScoreActivity scoreActivity;
    private static String dbURL = "jdbc:mysql://christopherhield.com:3306/chri5558_missile_defense";
    private Connection conn;
    private static final String TABLE = "AppScores";
    private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm", Locale.getDefault());

    private boolean needsInitials = false;

    public PlayerDatabaseHandler(ScoreActivity scoreActivity) {
        this.scoreActivity = scoreActivity;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        Log.d(TAG, "onPostExecute: " + s);
        if (!needsInitials){
            scoreActivity.setResults(s);
        } else {
            scoreActivity.askForInitials();
        }

    }

    @Override
    protected String doInBackground(String... strings) {

        if (strings.length == 2){
            String score = strings[0].trim();
            String level = strings[1].trim();
            try{
                Class.forName(JDBC_DRIVER);

                conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

                StringBuilder sb = new StringBuilder();

                int lastScore = getLastScore();

                if (lastScore>0 && Integer.valueOf(score)<=lastScore) {
                    sb.append("     Top Players     \n");
                    sb.append(" #  Init  Level    Score     Date/time\n");
                    sb.append(getAll());
                    return sb.toString();
                } else {
                    needsInitials = true;
                    return null;
                }
            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        } else {

            String initials = strings[0];
            String score = strings[1].trim();
            String level = strings[2].trim();

            try {
                Class.forName(JDBC_DRIVER);

                conn = DriverManager.getConnection(dbURL, "chri5558_student", "ABC.123");

                StringBuilder sb = new StringBuilder();

                addPlayer(initials, score, level);
                sb.append("     Top Players     \n");
                sb.append(" #  Init  Level    Score     Date/time\n");
                sb.append(getAll());
                needsInitials = false;
                return sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private void addPlayer(String initials, String score, String level) throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "insert into " + TABLE + " values (" +
                System.currentTimeMillis() + ", '" + initials + "', " + score + ", " +
                level +
                ")";

        int result = stmt.executeUpdate(sql);

        stmt.close();

    }

    private String getAll() throws SQLException {
        Statement stmt = conn.createStatement();
        String sql = "select * from " + TABLE + " ORDER BY Score DESC LIMIT 10";
        StringBuilder sb = new StringBuilder();
        ResultSet rs = stmt.executeQuery(sql);

        int i = 0;
        while (rs.next()) {
            long millis = rs.getLong(1);
            String name = rs.getString(2);
            int s = rs.getInt(3);
            int l = rs.getInt(4);
            sb.append(String.format(Locale.getDefault(), "%2d  %4s  %4d     %3d     %12s%n", ++i, name, l, s, sdf.format(new Date(millis))));
        }
        rs.close();
        stmt.close();

        return sb.toString();
    }

    private Integer getLastScore() throws SQLException {
        Statement stmt = conn.createStatement();

        String sql = "select * from " + TABLE + " ORDER BY Score DESC LIMIT 10";

        StringBuilder sb = new StringBuilder();

        ResultSet rs = stmt.executeQuery(sql);
        if (rs.last()){
            int score = rs.getInt(3);
            return score;
        }
        return -1;
    }
}
