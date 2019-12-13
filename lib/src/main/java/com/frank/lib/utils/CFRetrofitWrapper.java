package com.frank.lib.utils;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class CFRetrofitWrapper extends CFRetrofitUtil {
    public static final String DEFAULT_ERROR_CODE = "-1";

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json");

    private static CFRetrofitWrapper sInstance = new CFRetrofitWrapper();

    public static CFRetrofitWrapper getInstance() {
        return sInstance;
    }

    private static RequestBody createRequestBody(Object obj) {
        String jsonParams = new Gson().toJson(obj);
        return RequestBody.create(MEDIA_TYPE_JSON, jsonParams);
    }

    private static MultipartBody.Part createFileRequestBody(String filePath) {
        File file = new File(filePath);
        //构建body
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("multipart/form-data"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", file.getName(), requestFile);
        return body;
    }

    private static List<MultipartBody.Part> createFileRequestBody(List<String> filePaths) {

        List<MultipartBody.Part> multiParts = new ArrayList<>();
        if (filePaths == null || filePaths.size() == 0) {
            return multiParts;
        }
        for (int i = 0; i < filePaths.size(); i++) {
            File file = new File(filePaths.get(i));
            //构建body
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file);

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);
            multiParts.add(body);
        }

        return multiParts;
    }


    /**
     * 获取轮播图数据
     * @param id
     * @return
     */
    public Observable<ACheckUpdateInfo> checkUpdate(String host,String id) {
        return getService(CApiService.class,host).checkUpdate(id);
    }


}
