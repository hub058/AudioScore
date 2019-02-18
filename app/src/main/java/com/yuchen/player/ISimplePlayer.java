package com.yuchen.player;

import java.io.File;

import android.media.MediaPlayer.OnCompletionListener;

public interface ISimplePlayer {

    public void play();

    public void pause();
    
    public void end();

    public void seekTo();

    public void setDataSource(String filePath);

    public void setDataSource(File file);
    
    public void setOnCompletionListener(OnCompletionListener listener);
}
