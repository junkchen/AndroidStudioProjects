package com.junkchen.playsound;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;

class MediaPlayerUtils {

    private Context mContext;
    private MediaPlayer mMediaPlayer;

    volatile private static MediaPlayerUtils ourInstance = null;

    static MediaPlayerUtils getInstance(Context context) {
        if (null == ourInstance) {
            synchronized (MediaPlayerUtils.class) {
                if (null == ourInstance) {
                    ourInstance = new MediaPlayerUtils(context);
                }
            }
        }
        return ourInstance;
    }

    private MediaPlayerUtils(Context context) {
        this.mContext = context;
        mMediaPlayer = new MediaPlayer();
    }

    public void play(String fileName) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();
        AssetFileDescriptor fd = null;
        try {
            fd = mContext.getAssets().openFd(fileName);
            mMediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(
                    mp -> mp.start()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (mMediaPlayer.isPlaying()) mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        ourInstance = null;
    }
}
