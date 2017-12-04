package com.example.onpus.gameproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SharedPreferences settings;
    private static final String data = "DATA";
    public static MediaPlayer bgmusic;
    public static AsyncPlayer player;
    private ImageButton music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bgmusic = MediaPlayer.create(this, R.raw.bgmusic);
        bgmusic.setLooping(true);
        bgmusic.start();

        music = (ImageButton) findViewById(R.id.musicBtn);
        if (bgmusic.isPlaying())
            music.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
        else
            music.setBackgroundResource(R.drawable.ic_volume_mute_black_24dp);

        player = new AsyncPlayer(this.getPackageName());

        Button start = (Button) findViewById(R.id.start);
        Button about = (Button) findViewById(R.id.about);
        Button highScore = (Button) findViewById(R.id.highScore);
        Button exit = (Button) findViewById(R.id.exit);

        music.setOnClickListener(this);
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
        //sound.play(R.raw.button);
        if (bgmusic.isPlaying()) {
            int resId = R.raw.button;
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + resId);
            player.play(this, uri, false, AudioManager.STREAM_MUSIC);
        }

        switch(view.getId()) {
            case R.id.musicBtn:
                if (bgmusic.isPlaying()) {
                    bgmusic.pause();
                    findViewById(R.id.musicBtn).setBackgroundResource(R.drawable.ic_volume_mute_black_24dp);
                }else{
                    bgmusic.start();
                    findViewById(R.id.musicBtn).setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                }
                break;
            case R.id.start:
                Intent intent = new Intent(this, GameActivityWithImage.class);
                startActivity(intent);
                break;
            case R.id.about:
                new AlertDialog.Builder(this)
                        .setTitle("About Insect World")
                        .setMessage("How to play:\n" +
                                "Select a card which has the same color or insect with your card(at the bottom).\n" +
                                "You have 100 seconds to challenge yourself.\n" +
                                "Don't miss the two special cards(sweep net) to gain double scores!")
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
