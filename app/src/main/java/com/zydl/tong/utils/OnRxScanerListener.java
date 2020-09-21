package com.zydl.tong.utils;


import com.google.zxing.common.DecoderResult;

/**
 * @author Vondear
 * @date 2017/9/22
 */

public interface OnRxScanerListener {
    void onSuccess(String type, DecoderResult result);

    void onFail(String type, String message);
}
