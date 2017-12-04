package com.example.onpus.gameproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.onpus.gameproject.MainActivity.bgmusic;
import static com.example.onpus.gameproject.MainActivity.player;


public class GameActivityWithImage extends Activity implements View.OnClickListener {
    private static final int INITIAL_SIZE = 3;          //number of box
    /** The game has not been started, no move yet. */
    private static final long NOT_STARTED = 0;


    private GridView grid;
    private TextView scoreTextView;
    private ImageAdapter adapter;
    private CardsData cardsData = new CardsData();
    private int numMoves;
    /** NOT_STARTED, or the starting time. */
    private long startTime;
    /** Game state: 2 states: Game-on or Game-over */
    private boolean gameComplete = false;
    //thread handler
    private Handler handler;
    private int gameLeftTime;
    private Timer timer = new Timer();
    //the score
    private int score=0;
    private CountDownTimer countDownTimer1;
    private CountDownTimer countDownTimer2;
    @BindView(R.id.pause)
    TextView pauseView;
    @BindView(R.id.scoreeffect)
    TextView scoreView;
    private SharedPreferences settings;
    private static final String data = "DATA";

    private ImageButton music;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        ButterKnife.bind(this);
        final Animation scoreeffect = AnimationUtils.loadAnimation(this,R.anim.scoreeffect);
        final ImageView currentCard=(ImageView) findViewById(R.id.currentCard);
        final TextView timeLeft=(TextView) findViewById(R.id.timeLeft);

        scoreTextView=(TextView) findViewById(R.id.score);

        music = (ImageButton) findViewById(R.id.musicBtn);
        if (bgmusic.isPlaying())
            music.setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
        else
            music.setBackgroundResource(R.drawable.ic_volume_mute_black_24dp);
        music.setOnClickListener(this);

        grid = (GridView) findViewById(R.id.grid);
        adapter=new ImageAdapter(this, cardsData.getDataList());
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final int theId=(int)id;

                if (gameComplete) // Game completed, no more move
                    return;
                if (bgmusic.isPlaying()) {
                    int resId = R.raw.cardsound;
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
                    player.play(GameActivityWithImage.this, uri, false, AudioManager.STREAM_MUSIC);
                }
//
                Log.i("id", id+"");
                Log.d("click time",gameLeftTime+"");

                boolean[] matchCurrentCard = cardsData.matchCardPattern((int) id, gameLeftTime);

                if (matchCurrentCard[0]) {
                    adapter.currentposit = position;
                    adapter.clicked = true;
                    adapter.notifyDataSetChanged();                         // Update grid display
                    currentCard.setImageResource(adapter.context.getResources().getIdentifier(cardsData.currentCard.color + "_" + cardsData.currentCard.insect, "drawable", adapter.context.getPackageName()));
                    final Animation animationFadein = AnimationUtils.loadAnimation(adapter.context, R.anim.fade_in);
                    currentCard.startAnimation(animationFadein);
                    score=score+100;
                    scoreView.setVisibility(0);
                    scoreView.setBackgroundColor(Color.parseColor("#bf8040"));
                    scoreView.setTextColor(Color.parseColor("#99ffcc"));
                    scoreView.setText("+100");
                    scoreView.startAnimation(scoreeffect);
//
                    //if it is special time, start countdown
                    if(matchCurrentCard[1]) {
                        Log.d("special Item", "appears");
                        cardsData.getDataList().get((int) id).countDownTimer=
                                new CountDownTimer(10000, 1000) {                       // 10000 = 10 sec
                                    public void onTick(long millisUntilFinished) {
                                        cardsData.getDataList().get(theId).timeLeft=millisUntilFinished;
                                    }

                                    public void onFinish() {
                                        cardsData.replaceSpecialItem2((int) theId);
                                        adapter.notifyDataSetChanged();
                                    }
                                };
                         cardsData.getDataList().get((int) id).countDownTimer.start();
                    }

                    //if click special item, gain bonus
                    if(matchCurrentCard[2]) {
                        score = score + 100;    //add 200 score
                        //should add game time
                        scoreView.setVisibility(0);
                        scoreView.setText("+200");
                        scoreView.setBackgroundColor(Color.parseColor("#FACC2E"));
                        scoreView.setTextColor(Color.parseColor("#FFFFFF"));
                        scoreView.startAnimation(scoreeffect);
                        Log.d("clicked", " special");


                    }
                    scoreTextView.setText("score: "+score);
                    Log.d("score: ",score+"");
                }else{
                    score = score - 100;
                    scoreView.setVisibility(0);
                    scoreView.setText("-100");
                    scoreView.setBackgroundColor(Color.parseColor("#606060"));
                    scoreView.setTextColor(Color.parseColor("#FFFFFF"));
                    scoreView.startAnimation(scoreeffect);
                    adapter.currentposit = position;
                    adapter.isWrong = true;
                    adapter.notifyDataSetChanged();                         // Update grid display
                    scoreTextView.setText("score: "+score);
                }
            }
        });

        //handler: do something in each second
        handler = new Handler() {
            public void handleMessage(Message msg) {
                timeLeft.setText("time left： " + gameLeftTime);
                gameLeftTime--;
                // 时间小于0, 游戏失败
                if (gameLeftTime < 0) {
                    stopTimer();
                    if (bgmusic.isPlaying()) {
                        int resId = R.raw.win;
                        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
                        player.play(GameActivityWithImage.this, uri, false, AudioManager.STREAM_MUSIC);
                    }

                    settings = getSharedPreferences(data, MODE_PRIVATE);
                    int highScore = settings.getInt("high score", 0);
                    if (score > highScore)
                        settings.edit().putInt("high score", score).commit();

                        new AlertDialog.Builder(GameActivityWithImage.this)
                                .setTitle(R.string.congratulation)
                                .setMessage("Time's up. Your score is " + score)
                                //.setNeutralButton(android.R.string.ok, null)
                                .setPositiveButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(
                                                    DialogInterface dialog, int id) {
                                                finish();
                                            }
                                        })
                                .show();
                        return;
                    }
                }

        };

        //count from start
        new CountDownTimer(5000, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);
            public void onTick(long millisUntilFinished) {
                long temp = (millisUntilFinished / 1000)-1;
                if(temp>0)
                    countdown.setText((millisUntilFinished / 1000) -1 +"");
                else {
                    countdown.setTextSize(100);
                    countdown.setText("Start!!");
                }
            }

            @Override
            public void onFinish() {
                countdown.setVisibility(View.GONE);
                startTimer(100);                      //start count down

            }
        }.start();

        //count from start
        startimercount();

        //restart button
        Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (bgmusic.isPlaying()) {
                    int resId = R.raw.button;
                    Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + resId);
                    player.play(GameActivityWithImage.this, uri, false, AudioManager.STREAM_MUSIC);
                }
                resetGame();
                startTimer(100);
            }
        });

        //pause button
        Button pauseButton = (Button) findViewById(R.id.pausebutton);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                stopTimer();
                //pause special item countdown
                //show pause view
                pauseView.setVisibility(0);
                pauseCounDownTimer();
            }
        });




        //resume button
        Button resumeButton = (Button) findViewById(R.id.resumebutton);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                startTimer(gameLeftTime);
                //resume special item countdown
                //show pause text view
                resumeCounDownTimer();
            }
        });

        setNumberOfCards(INITIAL_SIZE);       //gen 9 cards
        cardsData.genCurrentCard();          //gen current card
//      cardsData.setSpecialTime(specialTime1, specialTime2);   //set special time
        currentCard.setImageResource(adapter.context.getResources().getIdentifier(cardsData.currentCard.color+"_"+cardsData.currentCard.insect,"drawable",adapter.context.getPackageName()));
        final Animation animationFadein = AnimationUtils.loadAnimation(adapter.context, R.anim.fade_in);
        currentCard.startAnimation(animationFadein);
        startTimer(100);                      //start count down

    }
    //resume the game
    @OnClick(R.id.pause) void clickresume(){
        startTimer(gameLeftTime);
        //resume special item countdown
        //show pause text view
        pauseView.setAlpha(255);
        pauseView.setVisibility(View.GONE);
        resumeCounDownTimer();
    }

    // start counter
    public void startimercount() {
        new CountDownTimer(5000, 1000) {
            TextView countdown = (TextView) findViewById(R.id.countdown);

            public void onTick(final long millisUntilFinished) {
                countdown.setVisibility(0);
                countdown.setTextSize(300);
                runOnUiThread(new Runnable() {
                    public void run() {
                        long temp = (millisUntilFinished / 1000) - 1;
                        if (temp > 0)
                            countdown.setText((millisUntilFinished / 1000) - 1 + "");
                        else {
                            countdown.setTextSize(100);
                            countdown.setText("Start!!");
                        }
                    }
                });

            }


            @Override
            public void onFinish() {
                countdown.setVisibility(View.GONE);
                startTimer(100);                      //start count down

            }
        }.start();
    }
    /** Sets the number of box and updates display. */
    private void setNumberOfCards(int size) {
        cardsData.genAllCards(size);          // Set the size of the puzzle
        adapter.notifyDataSetChanged();     // Update grid display
        grid.setNumColumns(size);           // Set the number of columns of the grid
    }

    /** Restart the game and updates display. */
    private void resetGame() {
        ImageView currentCard=(ImageView) findViewById(R.id.currentCard);
        startTime = NOT_STARTED;
        gameComplete = false;
        cardsData.genAllCards(INITIAL_SIZE);
        cardsData.genCurrentCard();

        cardsData.resetSpecial();
        currentCard.setImageResource(adapter.context.getResources().getIdentifier(cardsData.currentCard.color+"_"+cardsData.currentCard.insect,"drawable",adapter.context.getPackageName()));
        final Animation animationFadein = AnimationUtils.loadAnimation(adapter.context, R.anim.fade_in);
        currentCard.startAnimation(animationFadein);

        Arrays.fill(adapter.alreadyLoadedIndexes, Boolean.FALSE);
        adapter.notifyDataSetChanged();
        resetScore();

        //count from start
        startimercount();

    }


    //reset score
    private void resetScore() {
        scoreTextView.setText("score: ");
        score=0;
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (bgmusic.isPlaying()) {
            int resId = R.raw.button;
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + resId);
            player.play(this, uri, false, AudioManager.STREAM_MUSIC);
        }
        switch (item.getItemId()) {
            case R.id.menuAbout:
                new android.support.v7.app.AlertDialog.Builder(this)
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

            case R.id.endGame:
                android.support.v7.app.AlertDialog.Builder scoreAlert = new android.support.v7.app.AlertDialog.Builder(this);
                scoreAlert.setTitle("Score")
                        .setMessage("Your Score is "+ score)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(
                                            DialogInterface dialog, int id) {
                                        finish();
                                    }
                                });
                // create alert dialog
                final android.support.v7.app.AlertDialog scoreAlertDialog = scoreAlert.create();

                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("End the Game")
                        .setMessage("Are you sure to end the game?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (score!=0 && gameLeftTime>0)
                                    scoreAlertDialog.show();
                                else
                                    finish();
                            }
                        }).show();
                break;
        }
        return true;
    }

    //stopTimer
    private void stopTimer(){
        this.timer.cancel();
        this.timer = null;
    }

    //start timer
    private void startTimer(int gameTime) {
        // 如果之前的timer还未取消，取消timer
        if (this.timer != null) {
            stopTimer();
        }

        //initial game time
        this.gameLeftTime=gameTime;
        //start countdown
        this.timer = new Timer();
        this.timer.schedule(new TimerTask() {
            public void run() {
                //send time to UI
                handler.sendEmptyMessage(0x123);
            }
        }, 0, 1000);
    }

    //resume countDown timer
    private void resumeCounDownTimer() {
        for (int i = 0; i < cardsData.getDataList().size(); i++) {
            final int index = i;
            final CardsData.Card theCard = cardsData.getDataList().get(i);

            if (theCard.timeLeft != 0) {
                theCard.countDownTimer = new CountDownTimer(theCard.timeLeft, 1000) {
                    public void onTick(long millisUntilFinished) {
                        theCard.timeLeft = millisUntilFinished;
                    }

                    public void onFinish() {
                        cardsData.replaceSpecialItem2(index);
                        adapter.notifyDataSetChanged();
                    }
                };
                theCard.countDownTimer.start();
            }
        }
    }

    //pause countDown timer
    private void pauseCounDownTimer() {
        for (CardsData.Card card : cardsData.getDataList()) {
            if (card.countDownTimer != null) {
                card.countDownTimer.cancel();
                card.countDownTimer = null;
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.musicBtn:
                if (bgmusic.isPlaying()) {
                    bgmusic.pause();
                    findViewById(R.id.musicBtn).setBackgroundResource(R.drawable.ic_volume_mute_black_24dp);
                } else {
                    bgmusic.start();
                    findViewById(R.id.musicBtn).setBackgroundResource(R.drawable.ic_volume_up_black_24dp);
                }
                break;
        }
    }
}