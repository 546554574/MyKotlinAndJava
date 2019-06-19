package com.smart.car.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.ResultPointCallback;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.smart.util.rxtool.RxDataTool;
import com.smart.util.rxtool.RxImageTool;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Vondear
 * @date 2017/10/11
 */

public class RxQrBarTool {

    /**
     * 解析图片中的 二维码 或者 条形码
     *
     * @param photo 待解析的图片
     * @return Result 解析结果，解析识别时返回NULL
     */
    public static Result decodeQrFromPhoto(Bitmap photo) {
        Result rawResult = null;
        if (photo != null) {
            Bitmap smallBitmap = RxImageTool.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生

            QRCodeReader multiFormatReader = new QRCodeReader();

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, true);
            hints.put(DecodeHintType.PURE_BARCODE, true);
            // 开始对图像资源解码
            try {
                rawResult = multiFormatReader.decode(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(smallBitmap))), hints);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rawResult;
    }

    /**
     * 解析图片中的 二维码 或者 条形码
     *
     * @param photo 待解析的图片
     * @return Result 解析结果，解析识别时返回NULL
     */
    public static DecoderResult decodeQrFromPhotoToByte(Bitmap photo) {
        Result rawResult = null;
        DecoderResult decoderResult = null;
        if (photo != null) {
            Bitmap smallBitmap = RxImageTool.zoomBitmap(photo, photo.getWidth() / 2, photo.getHeight() / 2);// 为防止原始图片过大导致内存溢出，这里先缩小原图显示，然后释放原始Bitmap占用的内存
            photo.recycle(); // 释放原始图片占用的内存，防止out of memory异常发生

            com.smart.car.utils.QRCodeReader multiFormatReader = new com.smart.car.utils.QRCodeReader();

            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, true);
            hints.put(DecodeHintType.PURE_BARCODE, true);
            // 开始对图像资源解码
            try {
                rawResult = multiFormatReader.decode(new BinaryBitmap(new HybridBinarizer(new BitmapLuminanceSource(smallBitmap))), hints);
                decoderResult = multiFormatReader.getDecoderResult();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return decoderResult;
    }


}
