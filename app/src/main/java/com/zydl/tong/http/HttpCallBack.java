package com.zydl.tong.http;

public abstract class HttpCallBack<T> {
    public Class<T> clz;

    public HttpCallBack(Class aCla) {
        this.clz = aCla;
    }

    public void start() {
    }

    public abstract void success(T t);

    public abstract void error(String err);

    public void end() {
    }
}
