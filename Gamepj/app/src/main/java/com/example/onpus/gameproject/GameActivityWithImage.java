package com.example.onpus.gameproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class GameActivityWithImage extends Activity {
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final TextView currentCard=(TextView) findViewById(R.id.currentCard);
        final TextView timeLeft=(TextView) findViewById(R.id.timeLeft);
        scoreTextView=(TextView) findViewById(R.id.score);

        grid = (GridView) findViewById(R.id.grid);
        adapter=new ImageAdapter(this, cardsData.getDataList());
        grid.setAdapter(adapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final int theId=(int)id;

                if (gameComplete) // Game completed, no more move
                    return;
//                if (startTime == NOT_STARTED) // First move, keep time
//                    startTime = System.currentTimeMillis();
                Log.i("id", id+"");
                Log.d("click time",gameLeftTime+"");
                boolean[] matchCurrentCard = cardsData.matchCardPattern((int) id, gameLeftTime);

                if (matchCurrentCard[0]) {
                    adapter.notifyDataSetChanged();                         // Update grid display
                    currentCard.setText(cardsData.currentCard.toString());
                    score=score+100;
//                  if (cardsData.validOrder())
//                        gameCompleted();

                    //is special time, start countdown
                    if(matchCurrentCard[1]) {
                        Log.d("special Item", "appears");
                        cardsData.getDataList().get((int) id).countDownTimer=
                                new CountDownTimer(10000, 1000) { // 5000 = 5 sec
                                    public void onTick(long millisUntilFinished) {
                                        cardsData.getDataList().get(theId).timeLeft=millisUntilFinished;
                                    }

                                    public void onFinish() {
                                        cardsData.replaceSpecialItem((int) theId);
                                        adapter.notifyDataSetChanged();
                                    }
                                };
                         cardsData.getDataList().get((int) id).countDownTimer.start();
                    }

                    //if click special item, add game left time 20s
                    if(matchCurrentCard[2]) {
                        score = score + 100;    //add 200 score
                        //should add game time
                        Log.d("clicked", " special");


                    }
                    scoreTextView.setText("score: "+score);
                    Log.d("score: ",score+"");
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
                    // 更改游戏的状态
                    //isPlaying = false;
                    // 失败后弹出对话框
                    //lostDialog.show();
                    String scoreMessage="Time's up. Your score is "+score;
                    new AlertDialog.Builder(GameActivityWithImage.this)
                            .setTitle(R.string.congratulation)
                            .setMessage(scoreMessage)
                            .setNeutralButton(android.R.string.ok, null)
                            .show();
//                    resetGame();
//                    startTimer(100);
                    return;
                }
            }
        };

        //restart button
        Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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
                resumeCounDownTimer();
            }
        });

        setNumberOfCards(INITIAL_SIZE);       //gen 9 cards
        cardsData.genCurrentCard();          //gen current card
//        cardsData.setSpecialTime(specialTime1, specialTime2);   //set special time
        currentCard.setText(cardsData.currentCard.toString());
        startTimer(100);                      //start count down
    }

    /** Sets the number of box and updates display. */
    private void setNumberOfCards(int size) {
        cardsData.genAllCards(size);          // Set the size of the puzzle
        adapter.notifyDataSetChanged();     // Update grid display
        grid.setNumColumns(size);           // Set the number of columns of the grid
    }

    /** Restart the game and updates display. */
    private void resetGame() {
        TextView currentCard=(TextView) findViewById(R.id.currentCard);
        startTime = NOT_STARTED;
        gameComplete = false;
        cardsData.genAllCards(INITIAL_SIZE);
        cardsData.genCurrentCard();
        cardsData.resetSpecial();
        currentCard.setText(cardsData.currentCard.toString());
        adapter.notifyDataSetChanged();
        resetScore();
    }

    //reset score
    private void resetScore() {
        scoreTextView.setText("score: ");
        score=0;
    }

    /** Acts on completion of game, showing statistics. */
//    private void gameCompleted() {
//        // Add code here
//        // Task 2: Displaying statistics after completion of game
//
//        // Obtain the current time and evaluating the total time used for solving the puzzle
//        // Change the game state to GameComplete by setting "gameComplete" to true
//        // Create and display an AlertDialog, the corresponding title and message can be find in the string resource file
//        double time = (System.currentTimeMillis() - startTime) / 1000.0;
//        gameComplete = true;
//        new AlertDialog.Builder(this)
//            .setTitle(R.string.congratulation)
//            .setMessage(getString(R.string.congratulation_msg, numMoves, time))
//            .setNeutralButton(android.R.string.ok, null)
//            .show();
//    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAbout:
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("About")
                        .setMessage("About content")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
                break;
            case R.id.restart:
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("Restart")
                        .setMessage("Are you sure to restart the game?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //code for restart the game
                            }
                        }).show();
                break;
            case R.id.endGame:
                android.support.v7.app.AlertDialog.Builder scoreAlert = new android.support.v7.app.AlertDialog.Builder(this);
                scoreAlert.setTitle("Score")
                        .setMessage("Your Score is"+ " Score")
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
                                scoreAlertDialog.show();
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
                        cardsData.replaceSpecialItem(index);
                        adapter.notifyDataSetChanged();
                    }
                };
                theCard.countDownTimer.start();
            }
        }
    }

    //pause countDown timer
    private void pauseCounDownTimer() {
        for(CardsData.Card card: cardsData.getDataList()) {
            if (card.countDownTimer!=null){
                card.countDownTimer.cancel();
                card.countDownTimer=null;
            }
        }
    }
}