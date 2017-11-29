package com.example.onpus.gameproject;

import android.os.CountDownTimer;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class CardsData {
    private static final String EMPTY = "";
    /** List of (size * size) data, each being a string. */
    private final ArrayList<Card> dataList = new ArrayList<Card>();
    private int size;
    //all color
    private final String[] color = {"red", "yellow", "blue","green"};
    //all insects
    private final String[] insect = {"bee", "ladybird", "butterfly","spider"};
    //current card
    public Card currentCard;
    //special time
    private int specialTime1;
    private int specialTime2;
    private Boolean specialTime1Done=false;
    private Boolean specialTime2Done=false;

    /** Returns number of cards. */
    public int getSize() {
        return size;
    }

    //generate 9 cards
    public void genAllCards(int size) {
        this.size = size;
        resetDataList();


    }

    //everytime restart, set special time
    public void resetSpecial(){
        setSpecialTime();
        specialTime1Done=false;
        specialTime2Done=false;
    }

    /** Returns the list of cards*/
    public ArrayList<Card> getDataList() {
        return dataList;
    }


    //match pattern
    public boolean[] matchCardPattern(int chosenCardId, int gameLeftTime) {
        boolean[] matchAndSpecial = {false, false, false}; //match, isSpecialTime, clickSpecialItem
        Card chosenCard = dataList.get(chosenCardId);
        Log.d("clickColor", chosenCard.color);

        if (chosenCard.color.equals(currentCard.color) || chosenCard.insect.equals(currentCard.insect)) {
            currentCard = chosenCard;
            matchAndSpecial[0] = true;

            if (chosenCard.countDownTimer != null) {
                chosenCard.countDownTimer.cancel();
                chosenCard.countDownTimer = null;
            }

            //if click special item
            if (chosenCard.insect.equals("special")) {
                matchAndSpecial[2] = true;
            }

            //if is special time(80, 30), special item comes out
            int currentLeftTime = gameLeftTime;

            if (!specialTime1Done && currentLeftTime <= 80) {
                dataList.set(chosenCardId, genSpecialCard());
                matchAndSpecial[1] = true;
                specialTime1Done = true;
            } else {
                if (!specialTime2Done && currentLeftTime <= 30) {
                    dataList.set(chosenCardId, genSpecialCard());
                    matchAndSpecial[1] = true;
                    specialTime2Done = true;
                } else {
                    //gen a new card
                    dataList.set(chosenCardId, genNewCard());
                }
            }

            //change to a new card if necessary
            Card newCard = genGoodCardIfNeed(chosenCardId);
            if (newCard != null) {
                dataList.set(chosenCardId, newCard);
            }
        }
        return matchAndSpecial;
    }

    //special item times up
    public void replaceSpecialItem(int rabbitCardId){
        dataList.set(rabbitCardId, genGoodNewCard(rabbitCardId));
    }


//    /** Randomizes the order of the items by moving. */
//    public void random() {
//        int n = size * size * 100;
//        Random random = new Random();
//        for (int i = 0; i < n; i++) {
//            //move(random.nextInt(dataList.size()));
//        }
//    }


    /** Resets the data list according to the size. */
    private void resetDataList() {
        String selectedColor;
        String selectedInsect;

        dataList.clear();
        Random random = new Random();

        //add 9 image to dataList
        for (int i = 0; i < size * size; i++) {
            //randomly gen color and insect
            selectedColor=color[random.nextInt(4)];
            selectedInsect=insect[random.nextInt(4)];
            dataList.add(new Card(selectedColor, selectedInsect));
        }
    }

    public void genCurrentCard(){
        Random random = new Random();
        String selectedColor=color[random.nextInt(4)];
        String selectedInsect=insect[random.nextInt(4)];
        currentCard=new Card(selectedColor, selectedInsect);
    }

    //after click a card, generate a new card
    public Card genNewCard(){
        Random random = new Random();
        String selectedColor=color[random.nextInt(4)];
        String selectedInsect=insect[random.nextInt(4)];
        return new Card(selectedColor, selectedInsect);
    }

    //make the game able to continue
    public Card genGoodNewCard(int chosenCardId) {
        Boolean alreadyHasMatchCard=false;

        //if has match item in grid
        Random random = new Random();
        String selectedColor = color[random.nextInt(4)];
        String selectedInsect = insect[random.nextInt(4)];

        dataList.set(chosenCardId, null);   //except the clicked one
        for (Card card : dataList) {
            if (card != null) {
                if (card.color.equals(currentCard.color) || card.insect.equals(currentCard.insect)) {
                    alreadyHasMatchCard=true;
                    Log.d("match", currentCard.color);
                    break;
                }

            }

        }

        if(!alreadyHasMatchCard){
            Log.d("not match", currentCard.color);
            Random random1 = new Random();
            if (random1.nextInt(2) == 0) {
                selectedColor = currentCard.color;
            } else {
                selectedInsect= currentCard.insect;
            }
        }

        return new Card(selectedColor, selectedInsect);
    }

    public Card genGoodCardIfNeed(int chosenCardId){
        Boolean alreadyHasMatchCard=false;
        String newColor=null;
        String newInsect=null;

        for (Card card : dataList) {
            if (card != null) {
                if (card.color.equals(currentCard.color) || card.insect.equals(currentCard.insect)) {
                    alreadyHasMatchCard=true;
                    Log.d("match", currentCard.color);
                    return null;
                }

            }
        }

        if(!alreadyHasMatchCard){
            Log.d("not match", currentCard.color);
            Random random1 = new Random();
            if (random1.nextInt(2) == 0) {
                newColor = currentCard.color;
            } else {
                newInsect= currentCard.insect;
            }
        }

        return new Card(newColor, newInsect);
    }

    public Card genSpecialCard(){
        Random random = new Random();
        String selectedColor=color[random.nextInt(4)];
        Log.d("special item",selectedColor+" special");
        return new Card(selectedColor, "special");
    }

    //gen 2 random time for special item
    public void setSpecialTime(){
        Random random = new Random();
        specialTime1=random.nextInt(50);
        specialTime2=random.nextInt(50)+51;
        Log.d("specialTime1 ", specialTime1+"");
        Log.d("specialTime2 ", specialTime2+"");
    }


    public class Card{
        public String color;
        public String insect;
        public CountDownTimer countDownTimer;
        public long timeLeft;

        public Card(String color, String insect){
            this.color=color;
            this.insect=insect;
        }
        public String toString(){
            return color+", "+insect;
        }

    }
}
