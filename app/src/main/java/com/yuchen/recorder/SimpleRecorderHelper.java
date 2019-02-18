package com.yuchen.recorder;

import android.os.Handler;
import android.util.Log;

public class SimpleRecorderHelper {

    public static final int TYPE_MEDIA_RECORDER = 0x0000;
    public static final int TYPE_AUDIO_RECORDER = 0x000f;

    public static final int RECORD_STANDARD_SUCCESS = 0x0001;
    public static final int RECORD_STANDARD_FAILURE = 0x0002;
    public static final int RECORD_COMPARE_SUCCESS = 0x0003;
    public static final int RECORD_COMPARE_FAILURE = 0x0004;


    public static ISimpleRecorder getRecord(int type, Handler handler) {
        switch (type) {
            case TYPE_MEDIA_RECORDER:
                return MySimpleRecorder.getInstance();
            case TYPE_AUDIO_RECORDER:
                return MyAudioRecorder.getInstance(handler);
        }
        return null;
    }

}
