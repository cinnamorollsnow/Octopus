package com.example.onpus.gameproject;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.net.Uri;

/** Sound manager for managing the on/off status, drawables, and playbacks. */
public class SoundManager {
    /** The context of this sound manager. */
    private Context context;
    /** The drawable when sound is on. */
    private Drawable onDrawable;
    /** The drawable when sound is off. */
    private Drawable offDrawable;
    /** Whether sound is on. */
    private boolean on;
    /** Sound player for playback. */
    private AsyncPlayer player;

    /** Constructs a sound manager object. */
    public SoundManager(Context context, boolean on) {
        this.context = context;
        this.on = on;
        onDrawable = context.getResources().getDrawable(R.drawable.ic_volume_up_black_24dp);
        offDrawable = context.getResources().getDrawable(R.drawable.ic_volume_mute_black_24dp);
        player = new AsyncPlayer(context.getPackageName());
    }

    /** Returns whether sound is on. */
    public boolean getOn() {
        return on;
    }

    /** Sets whether sound is on. */
    public void setOn(boolean on) {
        this.on = on;
    }

    /** Returns the current drawable according to the on/off status. */
    public Drawable getDrawable() {
        return on ? onDrawable : offDrawable;
    }

    /** Returns the bounds of the current drawable. */
    public Rect getBounds() {
        return onDrawable.getBounds();
    }

    /** Sets the bounds of the drawable. */
    public void setBounds (Rect bounds) {
        onDrawable.setBounds(bounds);
        offDrawable.setBounds(bounds);
    }

    /** Plays a sound resource file with the parameter ID. */
    public void play(int resId) {
        if (on) {
            Uri uri = Uri.parse("android.resource://" + context.getPackageName() + "/" + resId);
            player.play(context, uri, false, AudioManager.STREAM_MUSIC);
        }
    }
}

