package com.yc.audioscore;

import android.util.Log;

import com.yc.view.AudioWaveView;

/**
 * Created by 20252365 on 2015/11/18.
 */
public class WaveDrawRunnable implements Runnable {

    private AudioWaveView audioWaveView;
    private double[] audioData;

    public WaveDrawRunnable(AudioWaveView audioWaveView, double[] audioData) {
        this.audioWaveView = audioWaveView;
        this.audioData = audioData;
    }

    @Override
    public void run() {
        Log.d("lzy", "WaveDrawRunnable  run");
        audioWaveView.setAudioData(audioData);
        audioWaveView.invalidate();
    }
}
