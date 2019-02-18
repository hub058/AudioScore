package com.yuchen.player;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer.OnCompletionListener;

public class MyAudioTrack implements ISimplePlayer {

    private static MyAudioTrack mMyAudioTrack = null;
    AudioTrack mAudioTrack;

    // private AudioTrack mAudioTrack = new AudioTrack(attributes, format, bufferSizeInBytes, mode, sessionId);
    private String filePath = "";

    private MyAudioTrack() {
    }

    public static ISimplePlayer getInstance() {
        if (mMyAudioTrack == null) {
            mMyAudioTrack = new MyAudioTrack();
        }
        mMyAudioTrack.initAudioTrack();
        return mMyAudioTrack;
    }

    private void initAudioTrack() {
        int sampleRateInHz = 44100;
        int channelConfig = AudioFormat.CHANNEL_OUT_DEFAULT;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, bufferSizeInBytes, AudioTrack.MODE_STREAM);
    }

    @Override
    public void play() {
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_STOPPED) {
            new PlayerThread().start();
        }else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
            new PlayerThread().start();
        }
    }

    @Override
    public void pause() {
        if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
            mAudioTrack.play();
        } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.pause();
        }
    }

    @Override
    public void end() {
        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_STOPPED) {
            mAudioTrack.stop();
        }
    }

    @Override
    public void seekTo() {
    }

    @Override
    public void setDataSource(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setDataSource(File file) {
        this.filePath = file.getAbsolutePath();
    }

    private class PlayerThread extends Thread {
        @Override
        public void run() {
            File file = new File(filePath);
            if (file.exists()) {
                //
                int musicLength = (int) (file.length() / 2);
                short[] music = new short[musicLength];
                try {
                    InputStream is = new FileInputStream(file);
                    BufferedInputStream bis = new BufferedInputStream(is);
                    DataInputStream dis = new DataInputStream(bis);
                    int i = 0;
                    while (dis.available() > 0) {
                        music[i] = dis.readShort();
                        i++;
                    }
                    dis.close();

                    mAudioTrack.play();
                    mAudioTrack.write(music, 0, musicLength);
                    mAudioTrack.stop();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("file not exists");
            }
        }
    }

    @Override
    public void setOnCompletionListener(OnCompletionListener listener) {
        
    }
}
