package com.example.onpus.gameproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class GameActivity extends Activity {
    /** The initial size of the puzzle. */
    private static final int INITIAL_SIZE = 3;          //ping
    /** The game has not been started, no move yet. */
    private static final long NOT_STARTED = 0;

    /** The grid of items. */
    private GridView grid;
    /** The adapter of the grid. */
    private ArrayAdapter<PuzzleData.Card> adapter;
    /** The puzzle data. */
    private PuzzleData puzzleData = new PuzzleData();
    /** Number of moves made. */
    private int numMoves;
    /** NOT_STARTED, or the starting time. */
    private long startTime;
    /** Game state: 2 states: Game-on or Game-over */
    private boolean gameComplete = false;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        final TextView currentCard=(TextView) findViewById(R.id.currentCard);
        grid = (GridView) findViewById(R.id.grid);
        adapter = new ArrayAdapter<PuzzleData.Card>(this,
                R.layout.item_view, R.id.item_view_text,
                puzzleData.getDataList());
        grid.setAdapter(adapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Add code here
                // Task 3: Item click handler

                // Return if it is in GameComplete state
                // Set current time to "startTime" if it is the first move
                // Check if it is a valid move
                // If it is a valid move, update grid display, increase the numMoves by 1, and check if the puzzle solve or not
                // If puzzle is solved, call the "gameCompleted" method
                if (gameComplete) // Game completed, no more move
                    return;

                if (startTime == NOT_STARTED) // First move, keep time
                    startTime = System.currentTimeMillis();
                boolean matchCurrentCard = puzzleData.matchCardPattern((int) id);
                if (matchCurrentCard) {
                    adapter.notifyDataSetChanged(); // Update grid display
                    currentCard.setText(puzzleData.currentCard.toString());
                    numMoves++;
//                    if (puzzleData.validOrder())
//                        gameCompleted();
                }
            }
        });

        Button startButton = (Button) findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                resetGame();
            }
        });


        setPuzzleSize(INITIAL_SIZE);
        puzzleData.genCurrentCard();
        currentCard.setText(puzzleData.currentCard.toString());
    }


    /** Sets the size of the puzzle and updates display. */
    private void setPuzzleSize(int size) {
        puzzleData.genCards(size);  // Set the size of the puzzle
        adapter.notifyDataSetChanged();  // Update grid display
        grid.setNumColumns(size);  // Set the number of columns of the grid
    }

    /** Resets the game and updates display. */
    private void resetGame() {
        // Add code here
        // Task 1: Reset the game

        // Reset numMoves to 0
        // Set gameComplete to false
        // Set startTime to NOT_STARTED
        // Randomize the puzzle by invoking the "random" method with the puzzleData object
        // Update grid display by invoking the "notifyDataSetChanged" with the ArrayAdapter object
        numMoves = 0;
        startTime = NOT_STARTED;
        gameComplete = false;
        puzzleData.genCards(3);
        puzzleData.genCurrentCard();
        adapter.notifyDataSetChanged();
    }

    /** Acts on completion of game, showing statistics. */
    private void gameCompleted() {
        // Add code here
        // Task 2: Displaying statistics after completion of game

        // Obtain the current time and evaluating the total time used for solving the puzzle
        // Change the game state to GameComplete by setting "gameComplete" to true
        // Create and display an AlertDialog, the corresponding title and message can be find in the string resource file
        double time = (System.currentTimeMillis() - startTime) / 1000.0;
        gameComplete = true;
        new AlertDialog.Builder(this)
            .setTitle(R.string.congratulation)
            .setMessage(getString(R.string.congratulation_msg, numMoves, time))
            .setNeutralButton(android.R.string.ok, null)
            .show();
    }

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
}