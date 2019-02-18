package com.yuchen.recorder;

import java.io.File;

public interface ISimpleRecorder {

    public void start(String type);

    public void stop();

    public void setOutputFile(String filePath);

    public void setOutputFile(File file);
}
