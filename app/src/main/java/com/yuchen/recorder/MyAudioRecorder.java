package com.yuchen.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.yc.audioscore.MainActivity;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyAudioRecorder implements ISimpleRecorder {

    int sampleRateInHz;
    int bufferSizeInBytes;

    private static MyAudioRecorder mMyAudioRecorder = null;
    AudioRecord mAudioRecord;

    private String filePath = "";
    private boolean isRecording = false;

    private Handler handler;

    private MyAudioRecorder(Handler handler) {
        this.handler = handler;
    }

    public static ISimpleRecorder getInstance(Handler handler) {
        if (mMyAudioRecorder == null) {
            mMyAudioRecorder = new MyAudioRecorder(handler);
        }
        mMyAudioRecorder.initAudioRecorder();

        return mMyAudioRecorder;
    }

    private void initAudioRecorder() {
        sampleRateInHz = 44100;
        int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat,
                bufferSizeInBytes);
    }

    @Override
    public void start(String type) {
        new RecorderThread(type).start();
    }

    @Override
    public void stop() {
        isRecording = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        String temp = Environment.getExternalStorageDirectory().getAbsolutePath() + "/simpleRecord/" + "temp_int.wav";
//        Log.d("lzy", temp);
//        copyWaveFile(filePath, temp);
    }

    @Override
    public void setOutputFile(String filePath) {
        Log.d("lzy", "setoutputfile" + filePath);
        this.filePath = filePath;
    }

    @Override
    public void setOutputFile(File file) {
        this.filePath = file.getAbsolutePath();
    }

    public String getFilePath() {
        return filePath;
    }

    private class RecorderThread extends Thread {

        String type;

        public RecorderThread(String type) {
            this.type = type;
        }

        @Override
        public void run() {
            File file = new File(filePath);
            file.deleteOnExit();
            System.out.println("file deleteOnExit");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mMyAudioRecorder.initAudioRecorder();
            try {
                OutputStream os = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(os);
                DataOutputStream dos = new DataOutputStream(bos);

                sampleRateInHz = 44100;
                int channelConfig = AudioFormat.CHANNEL_IN_MONO;
                int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
                bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);

                short[] audioBuffer = new short[bufferSizeInBytes];
                mAudioRecord.startRecording();

                isRecording = true;
                while (isRecording) {
                    int bufferReadResult = mAudioRecord.read(audioBuffer, 0, bufferSizeInBytes);
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(audioBuffer[i]);
                    }
                }
                mAudioRecord.stop();
                mAudioRecord.release();
                dos.close();

                Message msg = Message.obtain();
                if(MainActivity.STANDARD.equals(type)){
                    msg.what = SimpleRecorderHelper.RECORD_STANDARD_SUCCESS;
                }else{
                    msg.what = SimpleRecorderHelper.RECORD_COMPARE_SUCCESS;
                }
                msg.obj = getFilePath();
                handler.sendMessage(msg);

                Log.d("lzy", "end send handler " + getFilePath());
            } catch (Exception e) {
                Log.d("lzy", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void copyWaveFile(String inFilename, String outFilename) {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = sampleRateInHz;
        int channels = 1;
        long byteRate = 16 * sampleRateInHz * channels / 8;
        byte[] data = new byte[bufferSizeInBytes];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            while (in.read(data) != -1) {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里提供一个头信息。插入这些信息就可以得到可以播放的文件。
     */
    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        System.out.println("WriteWaveFileHeader");
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 8; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
