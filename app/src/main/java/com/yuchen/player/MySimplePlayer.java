package com.yuchen.player;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class MySimplePlayer implements ISimplePlayer {

    private static MySimplePlayer mMySimplePlayer = null;

    private MediaPlayer mPlayer = new MediaPlayer();
    private String filePath = "";

    private MySimplePlayer() {
    }

    public static ISimplePlayer getInstance() {
        if (mMySimplePlayer == null) {
            mMySimplePlayer = new MySimplePlayer();
            
        }
        return mMySimplePlayer;
    }

    @Override
    public void play() {
        if (mPlayer == null) {
            System.out.println("player is null, recreate it.");
            mPlayer = new MediaPlayer();
        }
        try {
            mPlayer.setDataSource(filePath);
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mPlayer.start();
    }

    @Override
    public void pause() {
        if (mPlayer != null) {
            System.out.println("player is not null");
            mPlayer.pause();
        }
    }

    @Override
    public void end() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public void seekTo() {
        
    }
    
    public void setOnCompletionListener(OnCompletionListener listener){
        mPlayer.setOnCompletionListener(listener);
    }

    @Override
    public void setDataSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setDataSource(File file) {
        this.filePath = file.getAbsolutePath();
    }

}
