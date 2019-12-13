package com.frank.lib.utils;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.ObservableTransformer;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 *
 */
public class FRetrofitUtil {

    /**
     * 服务器地址
     */
    private static String API_HOST = "";

    private static Object service;
    private static Retrofit retrofit;
    public static MediaType MediaType_Json = MediaType.parse("application/json");
    public static MediaType MediaType_image = MediaType.parse("image/*");

    private CObservableTransformer base64ObservableTransformer = new CObservableTransformer();

    public static <A> A getService(Class<A> clz, String host) {
        if (!TextUtils.isEmpty(host)) {
            if (!host.equals(API_HOST)) {
                retrofit = null;
                service = null;
                API_HOST = host;
            }
        }
        if (service == null) {
            service = getRetrofit().create(clz);
        }
        return (A) service;
    }

    private static Retrofit getRetrofit() {
        if (retrofit == null) {

            HttpLogInterceptor logInterceptor = new HttpLogInterceptor(new HttpLogInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.i("rxJava",message);
                }
            });

            OkHttpClient client = new OkHttpClient.Builder()
                    .readTimeout(20000*10, TimeUnit.MILLISECONDS)
                    .connectTimeout(20000, TimeUnit.MILLISECONDS)
                    .writeTimeout(20000*10, TimeUnit.MILLISECONDS)
                    .addInterceptor(logInterceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl(API_HOST)
                    .addConverterFactory(Base64ConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }

    /**
     * 自定义异常，当接口返回的{@link Response#code}不为{@link}时，需要跑出此异常
     * eg：登陆时验证码错误；参数为传递等
     */
    public static class APIException extends Exception {
        public String code;
        public String message;

        public APIException(String code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    protected ObservableTransformer applyBase64Schedulers() {
        return base64ObservableTransformer;
    }

    /**
     * 当APIService中接口的注解为{@link retrofit2.http.Multipart}时，参数为{@link RequestBody}
     * 生成对应的RequestBody
     *
     * @param param
     * @return
     */
    protected RequestBody createRequestBody(int param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(long param) {
        return RequestBody.create(MediaType.parse("text/plain"), String.valueOf(param));
    }

    protected RequestBody createRequestBody(String param) {
        return RequestBody.create(MediaType.parse("text/plain"), param);
    }

    protected RequestBody createRequestBody(File param) {
        return RequestBody.create(MediaType.parse("image/*"), param);
    }


}
