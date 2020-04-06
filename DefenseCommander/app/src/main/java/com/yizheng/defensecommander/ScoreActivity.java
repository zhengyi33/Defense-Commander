package com.yizheng.defensecommander;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private final static int MAX_LEN = 3;

    private String initials = "";
    private Integer scoreValue;
    private Integer levelValue;
    private TextView scoreboard;
    private ImageView gameover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Utilities.setupFullScreen(this);

        Intent i = getIntent();
        scoreValue = i.getIntExtra("score", 0);
        levelValue = i.getIntExtra("level", 0);

        scoreboard = findViewById(R.id.textView2);
        scoreboard.setMovementMethod(new ScrollingMovementMethod());

        gameover = findViewById(R.id.imageView);

        checkIfTop();
    }

    public void exitClicked(View v){
        finish();
    }


    void checkIfTop(){
        new PlayerDatabaseHandler(this).execute(scoreValue.toString(), levelValue.toString());
    }

    void askForInitials() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        EditText et = new EditText(this);
        et.setGravity(Gravity.CENTER_HORIZONTAL);

        et.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(MAX_LEN), new InputFilter.AllCaps()
        });
        builder.setView(et);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                initials = et.getText().toString();
                if (!initials.equals("")){
                    new PlayerDatabaseHandler(ScoreActivity.this).execute(initials, scoreValue.toString(), levelValue.toString());
                }
            }
        });
        builder.setNegativeButton("CANCEL", (dialog, id)->{});
        builder.setTitle("You are a Top-Player!");
        builder.setMessage("Please enter your initials (up to 3 characters):");
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    public void setResults(String s) {
        //gameover.setVisibility(View.INVISIBLE);
        scoreboard.setText(s);
    }
}
