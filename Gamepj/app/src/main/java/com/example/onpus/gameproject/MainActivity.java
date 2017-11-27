package com.example.onpus.gameproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences settings;
    private static final String data = "DATA";
    private MediaPlayer bgmusic;
    private SoundManager sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgmusic = MediaPlayer.create(this, R.raw.bgmusic);
        bgmusic.setLooping(true);
        bgmusic.start();

        sound = new SoundManager(this, true);
        Button start = (Button) findViewById(R.id.start);
        Button about = (Button) findViewById(R.id.about);
        Button highScore = (Button) findViewById(R.id.highScore);
        Button exit = (Button) findViewById(R.id.exit);

        start.setOnClickListener(this);
        about.setOnClickListener(this);
        highScore.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        sound.play(R.raw.button);
        switch(view.getId()) {
            case R.id.start:
                Intent intent = new Intent(this, GameActivityWithImage.class);
                startActivity(intent);
                break;
            case R.id.about:
                new AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage("About content")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            case R.id.highScore:
                settings = getSharedPreferences(data,MODE_PRIVATE);
                int highScore = settings.getInt("high score", 0);
                if (highScore!=0)
                    new AlertDialog.Builder(this)
                            .setTitle("High Score")
                            .setMessage("Your High Score is "+ highScore +".")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                else
                    new AlertDialog.Builder(this)
                            .setTitle("High Score")
                            .setMessage("No record found.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                break;
            case R.id.exit:
                new AlertDialog.Builder(this)
                        .setTitle("Exit")
                        .setMessage("Sure Exit?")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                bgmusic.release();
                                finish();
                            }
                        })
                        .setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            default:
        }

    }
}
