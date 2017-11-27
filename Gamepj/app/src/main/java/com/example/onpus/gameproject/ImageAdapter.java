package com.example.onpus.gameproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by pangleeping on 4/11/2017.
 */

public class ImageAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CardsData.Card> dataList = new ArrayList<CardsData.Card>();
    boolean[] alreadyLoadedIndexes = new boolean[50];

    public ImageAdapter(Context context, ArrayList<CardsData.Card> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_view_image, null);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.item_image);

            //anim fade in
            AlphaAnimation alphaAnim = new AlphaAnimation(0.0f, 1.0f);
            alphaAnim.setDuration(500);

        if (!alreadyLoadedIndexes[position])  {

            imageView.startAnimation(alphaAnim);
            alreadyLoadedIndexes[position] = true;
        }
            CardsData.Card card = dataList.get(position);
            Log.d("9", card.color);

            imageView.setImageResource(context.getResources().getIdentifier(card.color+"_"+card.insect,"drawable",context.getPackageName()));

        return gridView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}