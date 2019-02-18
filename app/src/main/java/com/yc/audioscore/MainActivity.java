package com.yc.audioscore;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yc.audiodata.AudioDataOperate;
import com.yc.view.AudioWaveView;
import com.yuchen.player.ISimplePlayer;
import com.yuchen.player.SimplePlayerHelper;
import com.yuchen.recorder.ISimpleRecorder;
import com.yuchen.recorder.SimpleRecorderHelper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = "lzy";

    public static final String STANDARD = "standard";
    public static final String COMPARE = "compare";

    private Button standardRecordStart;
    private Button standardRecordStop;
    private Button compareRecordStart;
    private Button compareRecordStop;
    private AudioWaveView mStandardAudioWaveView;
    private AudioWaveView mCompareAudioWaveView;
    private ProgressBar mProgressBar;

    private ISimpleRecorder mRecorder;
    private ISimplePlayer mPlayer;

    private String standardAudioFilePath;
    private String compareAudioFilePath;

    String recordPath = "";

    //保存标准音频的数据
    private double[] standardAudioData;
    private double[] standardEnergyData;
    private double[] standardUsefulData;
    //保存对比音频的数据
    private double[] compareAudioData;
    private double[] compareEnergyData;
    private double[] compareUsefulData;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SimpleRecorderHelper.RECORD_STANDARD_SUCCESS:
                    //得到音频文件路径
                    standardAudioFilePath = (String) msg.obj;
                    Log.d(TAG, "handleMessage RECORD_STANDARD_SUCCESS: " + standardAudioFilePath);
                    break;
                case SimpleRecorderHelper.RECORD_COMPARE_SUCCESS:
                    //得到音频文件路径
                    compareAudioFilePath = (String) msg.obj;
                    Log.d(TAG, "handleMessage RECORD_STANDARD_SUCCESS: " + compareAudioFilePath);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("lzy", "*************************");
        standardRecordStart = (Button) findViewById(R.id.audio_score_standard_record_start);
        standardRecordStop = (Button) findViewById(R.id.audio_score_standard_record_stop);
        compareRecordStart = (Button) findViewById(R.id.audio_score_compare_record_start);
        compareRecordStop = (Button) findViewById(R.id.audio_score_compare_record_stop);
        mStandardAudioWaveView = (AudioWaveView) findViewById(R.id.standard_record_wave);
        mCompareAudioWaveView = (AudioWaveView) findViewById(R.id.compare_record_wave);

//        mProgressBar = createProgressBar(this, null);
//        mProgressBar.setVisibility(View.GONE);

        mPlayer = SimplePlayerHelper.getPlayer(SimplePlayerHelper.TYPE_AUDIO_TRACK);
        mRecorder = SimpleRecorderHelper.getRecord(SimpleRecorderHelper.TYPE_AUDIO_RECORDER, handler);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
        recordPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/audioScore/" + df.format(new Date()) + ".pcm";

        File file = new File(recordPath);
        try {
            if (!file.getParentFile().isDirectory()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        standardRecordStart.setEnabled(true);
        standardRecordStop.setEnabled(false);

        compareRecordStart.setEnabled(true);
        compareRecordStop.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.audio_score_standard_record_start:
                standardRecordStart.setEnabled(false);
                standardRecordStop.setEnabled(true);
                mRecorder.setOutputFile(recordPath);
                mRecorder.start(STANDARD);
                Log.d("lzy", "call audio_score_standard_record_start");
                break;
            case R.id.audio_score_standard_record_stop:
                standardRecordStart.setEnabled(true);
                standardRecordStop.setEnabled(false);
                mRecorder.stop();
                mRecorder = SimpleRecorderHelper.getRecord(SimpleRecorderHelper.TYPE_AUDIO_RECORDER, handler);
                Log.d("lzy", "call audio_score_standard_record_stop");
                //提示信息
                Toast.makeText(MainActivity.this,"标准录音保存成功",Toast.LENGTH_SHORT).show();
                break;

            case R.id.audio_score_compare_record_start:
                compareRecordStart.setEnabled(false);
                compareRecordStop.setEnabled(true);
                mRecorder.setOutputFile(recordPath);
                mRecorder.start(COMPARE);
                Log.d("lzy", "call audio_score_compare_record_start");
                break;
            case R.id.audio_score_compare_record_stop:
                compareRecordStart.setEnabled(true);
                compareRecordStop.setEnabled(false);
                mRecorder.stop();
                mRecorder = SimpleRecorderHelper.getRecord(SimpleRecorderHelper.TYPE_AUDIO_RECORDER, handler);

                Log.d("lzy", "call audio_score_compare_record_stop");
                //提示信息
                Toast.makeText(MainActivity.this,"对比录音保存成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.audio_score_standard_record_wave:
                showAudioWave(STANDARD);
                break;
            case R.id.audio_score_standard_record_filter:
                showFilterWave(STANDARD);
                break;
            case R.id.audio_score_standard_record_energy:
                showEnergyWave(STANDARD);
                break;
            case R.id.audio_score_standard_record_useful:
                showUsefulWave(STANDARD);
                break;

            case R.id.audio_score_compare_record_wave:
                showAudioWave(COMPARE);
                break;
            case R.id.audio_score_compare_record_filter:
                showFilterWave(COMPARE);
                break;
            case R.id.audio_score_compare_record_energy:
                showEnergyWave(COMPARE);
                break;
            case R.id.audio_score_compare_record_useful:
                showUsefulWave(COMPARE);
                break;
            case R.id.audio_score_calculate:
                calculateCompareResult();
        }
    }

    /**
     * 计算对比结果
     */
    private void calculateCompareResult() {
        if (null == standardUsefulData || null == compareUsefulData) {
            Toast.makeText(MainActivity.this, "standardUsefulData or compareUsefulData is null", Toast.LENGTH_SHORT).show();
            return;
        }
        final TextView tv = (TextView) findViewById(R.id.audio_score_compare_result);
        new Thread() {
            @Override
            public void run() {
                final double result = AudioDataOperate.cosineDistance(standardUsefulData, compareUsefulData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText((int) (result * 100) + "%");
                    }
                });
            }
        }.start();

    }


    /**
     * 绘制波形
     *
     * @param waveView
     * @param audioData
     */
    private void drawWave(AudioWaveView waveView, double[] audioData) {
        runOnUiThread(new WaveDrawRunnable(waveView, audioData));

    }

    /**
     * 获取音频数据
     *
     * @param filePath 音频数据文件路径
     * @return
     */
    public short[] getAudioData(String filePath) {
        File file = new File(filePath);
        System.out.println("File info   " + file.length());
        DataInputStream dis = null;
        short[] audioData = null;
        try {
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));

            // file.length() / 2 +1 : /2 : 两位byte数据保存为一位short数据; +1 : 保存文件结尾标志
            audioData = AudioDataOperate.getAudioData(dis, (int) file.length() / 2, AudioDataOperate.TYPE_16BIT);
            dis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioData;
    }

    /**
     * 显示原始波形（经过归一化处理）
     *
     * @param type
     */
    private void showAudioWave(final String type) {
        Log.d(TAG, "showAudioWave: ");
        new Thread() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + type);
                if (STANDARD.equals(type)) {
                    short[] data = getAudioData(standardAudioFilePath);

                    standardAudioData = AudioDataOperate.normalize(data);
                    //standardAudioData = AudioDataOperate.getUsefulData(standardAudioData);
                    drawWave(mStandardAudioWaveView, standardAudioData);
                } else if (COMPARE.equals(type)) {
                    short[] data = getAudioData(compareAudioFilePath);

                    compareAudioData = AudioDataOperate.normalize(data);
                    //compareAudioData = AudioDataOperate.getUsefulData(compareAudioData);
                    drawWave(mCompareAudioWaveView, compareAudioData);
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "显示原始波形", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    /**
     * 显示滤波后波形
     *
     * @param type
     */
    private void showFilterWave(final String type) {
        Log.d(TAG, "showFilterWave: ");
        new Thread() {
            @Override
            public void run() {
                if (STANDARD.equals(type)) {
                    if (null == standardAudioData) {
                        Log.d(TAG, "standardAudioData is null");
                        return;
                    }
                    // y(n) = 1*x(n)+(-0.9375)*x(n-1)  滤波
                    standardAudioData = AudioDataOperate.filter(standardAudioData, 1, -0.9375);
                    standardAudioData = AudioDataOperate.normalize(standardAudioData);
                    // standardAudioData = AudioDataOperate.getUsefulData(standardAudioData);
                    drawWave(mStandardAudioWaveView, standardAudioData);
                } else if (COMPARE.equals(type)) {
                    if (null == compareAudioData) {
                        Log.d(TAG, "compareAudioData is null");
                        return;
                    }
                    // y(n) = 1*x(n)+(-0.9375)*x(n-1)  滤波
                    compareAudioData = AudioDataOperate.filter(compareAudioData, 1, -0.9375);
                    compareAudioData = AudioDataOperate.normalize(compareAudioData);
//                    compareAudioData = AudioDataOperate.getUsefulData(compareAudioData);
                    drawWave(mCompareAudioWaveView, compareAudioData);
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "显示滤波后波形", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    /**
     * 显示短时能量波形
     *
     * @param type
     */
    private void showEnergyWave(final String type) {
        Log.d(TAG, "showEnergyWave: ");
        new Thread() {
            @Override
            public void run() {

                if (STANDARD.equals(type)) {
                    if (null == standardAudioData) {
                        Log.d(TAG, "standardAudioData is null: ");
                        return;
                    }
                    double[] dotProductData = AudioDataOperate.dotProduct(standardAudioData);
                    double[] wins = AudioDataOperate.generateHammingWindows(32, 16);
                    double[] convValue = AudioDataOperate.conv(dotProductData, wins);
                    standardEnergyData = AudioDataOperate.normalize(convValue);
                    drawWave(mStandardAudioWaveView, standardEnergyData);
                } else if (COMPARE.equals(type)) {
                    if (null == compareAudioData) {
                        Log.d(TAG, "compareAudioData is null");
                        return;
                    }
                    double[] dotProductData = AudioDataOperate.dotProduct(compareAudioData);
                    double[] wins = AudioDataOperate.generateHammingWindows(32, 16);
                    double[] convValue = AudioDataOperate.conv(dotProductData, wins);
                    compareEnergyData = AudioDataOperate.normalize(convValue);
                    drawWave(mCompareAudioWaveView, compareEnergyData);
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "显示短时能量波形", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }

    /**
     * 显示截取有效短时能量数据波形
     *
     * @param type
     */
    private void showUsefulWave(final String type) {
        Log.d(TAG, "showUsefulWave: ");
        new Thread() {
            @Override
            public void run() {
                if (STANDARD.equals(type)) {
                    if (null == standardEnergyData) {
                        Log.d(TAG, "showUsefulWave: standardEnergyData is null");
                        return;
                    }
                    Log.d(TAG, "showUsefulWave calculate");
                    standardUsefulData = AudioDataOperate.getUsefulData(standardEnergyData);
                    drawWave(mStandardAudioWaveView, standardUsefulData);
                } else if (COMPARE.equals(type)) {
                    if (null == compareEnergyData) {
                        Log.d(TAG, "showUsefulWave: compareEnergyData is null");
                        return;
                    }

                    compareUsefulData = AudioDataOperate.dealCompareData(compareEnergyData, standardUsefulData.length);
                    drawWave(mCompareAudioWaveView, compareUsefulData);
                }

                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "显示截取有效短时能量数据波形", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }.start();
    }
}
