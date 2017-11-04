package com.example.onpus.gameproject;

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
    
    /** Returns number of cards. */
    public int getSize() {
        return size;
    }

    //generate 9 cards
    public void genAllCards(int size) {
        this.size = size;
        resetDataList();
    }

    /** Returns the list of cards*/
    public ArrayList<Card> getDataList() {
        return dataList;
    }


    //match pattern
    public boolean matchCardPattern(int chosenCardId){
        Card chosenCard=dataList.get(chosenCardId);
        if (chosenCard.color.equals(currentCard.color)||chosenCard.insect.equals(currentCard.insect)) {
            currentCard = chosenCard;
            //gen new card
            dataList.set(chosenCardId, genNewCard());
            return true;
        }
        return false;
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

    public class Card{
        public String color;
        public String insect;

        public Card(String color, String insect){
            this.color=color;
            this.insect=insect;
        }
        public String toString(){
            return color+", "+insect;
        }

    }
}
