package com.yc.audiodata;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * Created by 20252365 on 2015/11/18.
 */
public class AudioDataOperate {

    public static final int TYPE_16BIT = 1;
    public static final double DATA_START_VALUE = 0.025;
    public static final double DATA_END_VALUE = 0.025;

    /**
     * 按编码类型提取音频数据
     *
     * @param dis
     * @param size
     * @param type
     * @return
     */
    public static short[] getAudioData(DataInputStream dis, int size, int type) {
        short[] audioData = new short[size];
        try {
            byte[] tempData = new byte[2];
            long audioDataSize = 0;
            while (dis.read(tempData) != -1) {
                // 每16位读取一个音频数据
                audioData[(int) audioDataSize] = (short) (((tempData[0] & 0xff) << 8) | (tempData[1] & 0xff));
                audioDataSize++;
                if (audioDataSize == size) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audioData;
    }

    /**
     * 归一化
     */
    public static double[] normalize(short[] data) {
        short max = findMax(data);
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = ((double) data[i] / max);
        }
        return result;
    }

    /**
     * 归一化
     */
    public static double[] normalize(double[] data) {
        double max = findMax(data);
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] / max;
        }
        return result;
    }

    /**
     * 点乘
     *
     * @return
     */
    public static double[] dotProduct(double[] data) {
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i] * data[i];
        }
        return result;
    }

    /**
     * 查找最大值
     *
     * @param data
     * @return
     */
    private static short findMax(short[] data) {
        short max = data[0];
        for (int i = 0; i < data.length; i++) {
            if (max < Math.abs(data[i])) {
                max = (short) Math.abs(data[i]);
            }
        }
        System.out.println("max :  " + max);
        return max;
    }

    /**
     * 查找最大值
     *
     * @param data
     * @return
     */
    private static double findMax(double[] data) {
        double max = data[0];
        for (int i = 0; i < data.length; i++) {
            if (max < Math.abs(data[i])) {
                max = Math.abs(data[i]);
            }
        }
        System.out.println("max :  " + max);
        return max;
    }

    /**
     * 生成窗函数
     */
    public static double[] generateWindows(int N, int i) {
        // 使用最简单的矩形窗
        double[] wins = new double[i * N];
        for (int j = 0; j < i * N; j++) {
            wins[j] = 1;
        }
        return wins;
    }

    /**
     * 生成窗函数   hamming窗
     */
    public static double[] generateHammingWindows(int N, int i) {
        // 使用最简单的矩形窗
        double[] wins = new double[i * N];
        for (int j = 0; j < i * N; j++) {
            wins[j] = 0.54 - 0.46 * (Math.cos(2 * Math.PI * j / (i * N)));
        }
        return wins;
        // hamming窗
    }


    /**
     * 短时能量
     */
    public static void shortTimeEnergy() {
    }

    /**
     * 计算卷积
     *
     * @param self  数据段
     * @param other 窗函数 （默认窗函数的长度远小于数据长度）
     * @return
     */
    public static double[] conv(double[] self, double[] other) {
        double[] result = new double[self.length + other.length - 1];
        double current = 0;
        for (int i = 0; i < self.length + other.length - 1; i++) {
            current = 0;
            for (int j = 0; j <= i; j++) {
                if (j >= self.length || i - j >= other.length) {
                    continue;
                }
                //TODO 去除一些数据的运算提高效率

                current += self[j] * other[i - j];
            }
            result[i] = current;
        }
        return result;
    }

    /**
     * 计算余弦距离 dot(En_compare, En_standard)/(norm(En_compare)*norm(En_standard))
     *
     * @param standard
     * @param comapre
     * @return
     */
    public static double cosineDistance(double[] standard, double[] comapre) {
        double dot = 0;
        double normStandard = 0;
        double normCompare = 0;
        for (int i = 0; i < standard.length; i++) {
            dot += standard[i] * comapre[i];
            normStandard += standard[i] * standard[i];
            normCompare += comapre[i] * comapre[i];
        }
        double distance = dot / (Math.sqrt(normStandard) * Math.sqrt(normCompare));
        return distance;
    }

    /**
     * 通过阈值得到音频有效数据开始的下标
     */
    public static int findDataStartIndex(double[] audioData) {
        for (int i = 0; i < audioData.length; i++) {
            if (audioData[i] > DATA_START_VALUE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 通过阈值得到音频有效数据结束的下标
     */
    public static int findDataEndIndex(double[] audioData) {
        for (int i = audioData.length - 1; i >= 0; i--) {
            if (audioData[i] > DATA_END_VALUE) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 截取音频有效数据（通过阈值获得的数据前后开始结束的下标来截取数据）
     *
     * @param audioData
     * @return
     */
    public static double[] getUsefulData(double[] audioData, int start, int end) {
        double[] usefulData = new double[end - start];
        for (int i = start; i < end; i++) {
            usefulData[i - start] = audioData[i];
        }
        return usefulData;
    }

    /**
     * 截取音频有效数据（通过阈值获得的数据前后开始结束的下标来截取数据）
     *
     * @param audioData
     * @return
     */
    public static double[] getUsefulData(double[] audioData) {
        int start = AudioDataOperate.findDataStartIndex(audioData);
        int end = AudioDataOperate.findDataEndIndex(audioData);
        System.out.println("dataLength   " + audioData.length + "     getUsefulData    " + "start: " + start + "   end : " + end);
        return getUsefulData(audioData, start, end);
    }

    /**
     * 处理对比音频使其与标准音频长度相同（通过阈值获得的数据开始下标截取与标准音频相同长度的音频数据）
     *
     * @param audioData
     * @return
     */
    public static double[] dealCompareData(double[] audioData, int start, int length) {
        double[] usefulData = new double[length];
        for (int i = start; i < start + length; i++) {
            //从有效音频开始点截取标准音频的长度的音频可能超过对比音频长度边界
            if (i >= audioData.length) {
                usefulData[i - start] = 0;
            } else {
                usefulData[i - start] = audioData[i];
            }
        }
        return usefulData;
    }

    /**
     * 处理对比音频使其与标准音频长度相同（通过阈值获得的数据开始下标截取与标准音频相同长度的音频数据）
     *
     * @param audioData
     * @return
     */
    public static double[] dealCompareData(double[] audioData, int length) {
        int start = AudioDataOperate.findDataStartIndex(audioData);
        System.out.println("dealCompareData: " + audioData.length + "   " + start + "   " + length);
        return dealCompareData(audioData, start, length);
    }

    /**
     * 滤波（差分方程）
     *
     * @param audioData
     */
    public static double[] filter(double[] audioData, double b0, double b1) {
        double[] result = new double[audioData.length];
        result[0] = audioData[0];
        for (int i = 1; i < audioData.length; i++) {
            result[i] = b0 * audioData[i] + b1 * audioData[i - 1];
        }
        return result;
    }
}
