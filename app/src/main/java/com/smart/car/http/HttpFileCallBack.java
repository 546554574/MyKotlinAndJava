package com.smart.car.http;

import java.io.File;

/**
 * 下载文件返回
 */
public abstract class HttpFileCallBack {
    public void start() {
    }

    public abstract void progress(int size);

    public abstract void success(File file);

    public abstract void error(String err);

    public void end() {
    }
}
