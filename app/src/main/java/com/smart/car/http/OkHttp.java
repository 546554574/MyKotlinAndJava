package com.smart.car.http;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.green.util.rxtool.GsonBinder;
import com.green.util.rxtool.RxFileTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttp {
    private static int METHOD = 1000;
    private static final int POST = 1001;
    private static final int GET = 1002;
    private static OkHttp okHttp;
    private File updateFile;

    public static void instance() {
        okHttp = new OkHttp();
    }

    private static OkHttpClient okHttpClient;
    private static RequestBody requestBody;
    private static Request request;
    private static String requestUrl;

    public static OkHttp post(String url) {
        instance();
        okHttpClient = new OkHttpClient();
        METHOD = POST;
        requestUrl = url;
        return okHttp;
    }

    public static OkHttp get(String url) {
        instance();
        okHttpClient = new OkHttpClient();
        METHOD = GET;
        requestUrl = url;
        return okHttp;
    }

    private Map<String, Object> params = new HashMap();
    private Map<String, File> files = new HashMap<>();

    public OkHttp add(String key, Object value) {
        params.put(key, String.valueOf(value));
        return this;
    }

    public OkHttp add(Map<String, String> map) {
        params.putAll(map);
        return this;
    }

    public OkHttp add(String key, File file) {
        files.put(key, file);
        return this;
    }

    private int BUILD_TYPE = 300;
    private final int JSON_TYPE = 301;
    private final int FILE_TYPE = 302;
    private MediaType JSONType = MediaType.parse("application/json; charset=utf-8");

    public <T> void buildByJson(final HttpCallBack<T> httpCallBack) {
        BUILD_TYPE = JSON_TYPE;
        build(httpCallBack);
    }

    /**
     * 基于http的文件上传（传入文件数组和key）混合参数和文件请求
     * 通过addFormDataPart可以添加多个上传的文件
     */
    private <T> void buidByFile(final HttpCallBack<T> myDataCallBack) {
        if (params == null) {
            params = new HashMap<>();
        }
        FormBody.Builder builder = new FormBody.Builder();
        //form表单提交
        for (String key : params.keySet()) {
            builder.add(key, (String) params.get(key));
        }
        RequestBody body = builder.build();
        MultipartBody.Builder multipartBody = new MultipartBody.Builder();
        multipartBody.setType(MultipartBody.ALTERNATIVE)
                .addPart(body);
        if (files != null) {
            RequestBody fileBody = null;
            for (String key : files.keySet()) {
                File file = files.get(key);
                String fileName = file.getName();
                fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
                //TODO 根据文件名设置contentType
                multipartBody.addPart(Headers.of("Content-Disposition",
                        "form-data; name=\"" + key + "\"; filename=\"" + fileName + "\""),
                        fileBody);
            }
        }
        requestBody = multipartBody.build();
        BUILD_TYPE = FILE_TYPE;
    }

    private String guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(fileName);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    public <T> void build(final HttpCallBack<T> httpCallBack) {
        this.httpCallBack = httpCallBack;
        handler.sendEmptyMessage(START);
        switch (BUILD_TYPE) {
            case JSON_TYPE: //json提交
                requestBody = RequestBody.create(JSONType, GsonBinder.toJsonStr(params));
                break;
            case FILE_TYPE:
                break;
            default:
                //form表单提交
                FormBody.Builder builder = new FormBody.Builder();
                for (String key : params.keySet()) {
                    builder.add(key, (String) params.get(key));
                }
                requestBody = builder.build();
                break;
        }
        if (METHOD == GET) {
            request = new Request.Builder()
                    .url(requestUrl)
                    .get()//默认就是GET请求，可以不写
                    .build();
        } else if (METHOD == POST) {
            request = new Request.Builder()
                    .url(requestUrl)
                    .post(requestBody)
                    .build();
        }
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e.getMessage();
                message.what = ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonStr = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonStr);
                    String msg = "";
                    if (jsonObject.has("info")) {
                        msg = jsonObject.getString("info");
                    }
                    if (jsonObject.has("status")) {
                        String status = jsonObject.getString("status");
                        if ("1".equals(status)) {
                            //成功
                            if (jsonObject.has("data")) {
                                T data = GsonBinder.toObj(jsonObject.getString("data"), httpCallBack.clz);
                                response.close();
                                Message message = new Message();
                                message.obj = data;
                                message.what = SUCCESS;
                                handler.sendMessage(message);
                            }
                        } else if ("0".equals(status)) {
                            //错误
                            Message message = new Message();
                            message.obj = msg;
                            message.what = ERROR;
                            handler.sendMessage(message);
                        } else {
                            Message message = new Message();
                            message.obj = msg;
                            message.what = ERROR;
                            handler.sendMessage(message);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(END);
                }
                handler.sendEmptyMessage(END);
            }
        });
    }

    private HttpCallBack httpCallBack;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case START:
                    httpCallBack.start();
                    break;
                case SUCCESS:
                    httpCallBack.success(msg.obj);
                    break;
                case ERROR:
                    if (msg.obj != null) {
                        httpCallBack.error(msg.obj.toString());
                    }
                    httpCallBack.end();
                    break;
                case END:
                    httpCallBack.end();
                    break;
            }
        }
    };
    private final int START = 10001;
    private final int SUCCESS = 10002;
    private final int ERROR = 10003;
    private final int END = 10004;


    /**
     * 下载文件******************************************************************************************************************************************
     */
    /**
     * 文件下载
     *
     * @param url path路径
     * @param destFileDir 本地存储的文件夹路径
     * @param myDataCallBack 自定义回调接口
     */
    private static String downUrl;
    private static String filrDir;
    private long totalSize = 0L;    //APK总大小
    private long downloadSize = 0L;  // 下载的大小
    private float count = 0L;       //下载百分比

    public static OkHttp downFile(String realURL, String destFileDir) {
        instance();
        downUrl = realURL;
        filrDir = destFileDir;
        return okHttp;
    }

    public void down(HttpFileCallBack httpFileCallBack) {
        this.httpFileCallBack = httpFileCallBack;
        Request request = new Request.Builder()
                .url(downUrl)
                .build();
        okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message message = new Message();
                message.obj = e.getMessage();
                message.what = ERROR;
                fileHandler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.code() == 200) {
                    InputStream is = null;
                    byte[] buf = new byte[4096];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        totalSize = response.body().contentLength();
                        downloadSize = 0L;
                        if (memoryAvailable(totalSize)) {
                            is = response.body().byteStream();
                            fos = new FileOutputStream(updateFile, true);
                            while ((len = is.read(buf)) != -1) {
                                downloadSize += len;
                                fos.write(buf, 0, len);
                                if ((count == 0) || (int) (downloadSize * 100 / totalSize) >= count) {
                                    count += 5;
                                    //文本进度（百分比）
                                    Message message = new Message();
                                    message.obj = count;
                                    message.what = FILE_PROGRESS;
                                }
                            }
                            fos.flush();
                            if (totalSize >= downloadSize) {
                                fileHandler.sendEmptyMessage(SUCCESS);
                            } else {
                                Message message = new Message();
                                message.obj = "下载失败";
                                message.what = ERROR;
                                fileHandler.sendMessage(message);
                            }
                        } else {
                            Message message = new Message();
                            message.obj = "内存不足";
                            message.what = ERROR;
                            fileHandler.sendMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Message message = new Message();
                        message.obj = "下载失败";
                        message.what = ERROR;
                        fileHandler.sendMessage(message);
                    } finally {
                        try {
                            if (is != null) {
                                is.close();
                            }
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    Message message = new Message();
                    message.obj = "下载失败";
                    message.what = ERROR;
                    fileHandler.sendMessage(message);
                }
            }
        });
    }

    private String getFileName(String url) {
        int separatorIndex = url.lastIndexOf("/");
        return (separatorIndex < 0) ? url : url.substring(separatorIndex + 1, url.length());

    }

    /**
     * 可用内存大小
     *
     * @param fileSize
     * @return
     */
    private boolean memoryAvailable(long fileSize) {
        fileSize += (1024 << 10);
        if (MemoryStatus.externalMemoryAvailable()) {
            if ((MemoryStatus.getAvailableExternalMemorySize() <= fileSize)) {
                if ((MemoryStatus.getAvailableInternalMemorySize() > fileSize)) {
                    createFile(false);
                    return true;
                } else {
                    return false;
                }
            } else {
                createFile(true);
                return true;
            }
        } else {
            if (MemoryStatus.getAvailableInternalMemorySize() <= fileSize) {
                return false;
            } else {
                createFile(false);
                return true;
            }
        }
    }

    private void createFile(boolean b) {
        RxFileTool.createFileByDeleteOldFile(filrDir + getFileName(downUrl));
        updateFile = RxFileTool.getFileByPath(filrDir + getFileName(downUrl));
    }

    private HttpFileCallBack httpFileCallBack;
    Handler fileHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILE_START:
                    httpFileCallBack.start();
                    break;
                case FILE_PROGRESS:
                    httpFileCallBack.progress((int) msg.obj);
                    break;
                case FILE_SUCCESS:
                    httpFileCallBack.success(updateFile);
                    break;
                case FILE_ERROR:
                    if (msg.obj != null) {
                        httpFileCallBack.error(msg.obj.toString());
                    }
                    httpFileCallBack.end();
                    break;
                case FILE_END:
                    httpFileCallBack.end();
                    break;
            }
        }
    };
    private final int FILE_START = 100001;
    private final int FILE_SUCCESS = 100002;
    private final int FILE_ERROR = 100003;
    private final int FILE_END = 100004;
    private static final int FILE_PROGRESS = 100005;

    /***************************************************************下载完文件*******************************************************************/
}
