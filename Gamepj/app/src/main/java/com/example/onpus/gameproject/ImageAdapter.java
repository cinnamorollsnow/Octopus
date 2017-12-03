package com.example.onpus.gameproject;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by pangleeping on 4/11/2017.
 */

public class ImageAdapter extends BaseAdapter  {
    Context context;
    private ArrayList<CardsData.Card> dataList = new ArrayList<CardsData.Card>();
    boolean[] alreadyLoadedIndexes = new boolean[50];
    int currentposit;
    boolean clicked,isWrong;

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

            Animation animationFadein = AnimationUtils.loadAnimation(context, R.anim.fade_out_card);
            Animation specialEffect = AnimationUtils.loadAnimation(context,R.anim.specialitem_effect);

            CardsData.Card card = dataList.get(position);
            Log.d("9", card.color);

            if (clicked&&position == currentposit) {
                imageView.startAnimation(animationFadein);
                clicked = false;
             }
             if(isWrong&&position == currentposit) {
                 animateImageView(imageView);
                 isWrong = false;
             }
        Log.d("9", card.insect.toString());


        if(card.insect.equals("special"))
            imageView.startAnimation(specialEffect);


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

    public void animateImageView(final ImageView v) {
        final int orange = context.getResources().getColor(R.color.Darkcolor);

        final ValueAnimator colorAnim = ObjectAnimator.ofFloat(0f, 1f);
        colorAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float mul = (Float) animation.getAnimatedValue();
                int alphaOrange = adjustAlpha(orange, mul);
                v.setColorFilter(alphaOrange, PorterDuff.Mode.SRC_ATOP);
                if (mul == 0.0) {
                    v.setColorFilter(null);
                }
            }
        });

        colorAnim.setDuration(500);
        colorAnim.setRepeatMode(ValueAnimator.REVERSE);
        colorAnim.setRepeatCount(3);
        colorAnim.start();
    }

    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }


}