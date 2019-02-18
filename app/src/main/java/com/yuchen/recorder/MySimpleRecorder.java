package com.yuchen.recorder;

import java.io.File;
import java.io.IOException;

import android.media.MediaRecorder;

public class MySimpleRecorder implements ISimpleRecorder {

    private static MySimpleRecorder mMySimpleRecorder = null;

    private MediaRecorder mRecorder = new MediaRecorder();
    private String filePath = "";

    private MySimpleRecorder() {
    }

    public static ISimpleRecorder getInstance() {
        if (mMySimpleRecorder == null) {
            mMySimpleRecorder = new MySimpleRecorder();
        }
        return mMySimpleRecorder;
    }

    @Override
    public void start(String type) {
        if (mRecorder == null) {
            System.out.println("recorder is null");
            mRecorder = new MediaRecorder();
        }
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filePath);
        System.out.println(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        mRecorder.start();
    }

    @Override
    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        } else {
            System.out.println("on stop, mRecord is null");
        }
    }

    @Override
    public void setOutputFile(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void setOutputFile(File file) {
        this.filePath = file.getAbsolutePath();
    }

}
