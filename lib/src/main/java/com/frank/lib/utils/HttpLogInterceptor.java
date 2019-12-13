package com.frank.lib.utils;

/**
 * @author Frank
 * @Date 2019/4/29 0029 下午 4:30
 */
import androidx.annotation.IntDef;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;

import static okhttp3.internal.platform.Platform.INFO;

/**
 * Created by Frank on 2016/7/15.
 */
public class HttpLogInterceptor implements Interceptor {
    private static final Charset UTF8 = Charset.forName("UTF-8");


    private final Logger logger;

    private @Level int level = BODY;

    public HttpLogInterceptor() {
        this(Logger.DEFAULT);
    }

    public HttpLogInterceptor(Logger logger) {
        this.logger = logger;
    }

    /**
     * Change the level at which this interceptor logs.
     * @param level {@link Level}
     * @return
     */
    public HttpLogInterceptor setLevel(@Level int level) {
        this.level = level;
        return this;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request request = chain.request();

        if (level == NONE) {
            return chain.proceed(request);
        }

        printReqLog(request);

        Response response = chain.proceed(request);

        printRespLog(response);

        return response;
    }

    /**
     * 打印响应相关日志
     * @param response
     * @throws IOException
     */
    private void printRespLog(Response response) throws IOException {
        ResponseBody body = response.body();

        BufferedSource source = body.source();
        source.request(Long.MAX_VALUE); // Buffer the entire body.

        Buffer buffer = source.buffer();
        Charset charset = Charset.defaultCharset();
        MediaType contentType = body.contentType();
        if (contentType != null) {
            charset = contentType.charset(charset);
        }
        logger.log("respones:"+buffer.clone().readString(charset));
    }

    /**
     * 打印请求参数相关日志
     * @param request
     * @throws IOException
     */
    private void printReqLog(Request request) throws IOException {
        boolean logBody = level == BODY;
        boolean logHeaders = logBody || level == HEADERS;

        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;

        String requestStartMessage = request.method() + ' ' + request.url();

        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody.contentLength() + "-byte body)";
        }

        logger.log(requestStartMessage);

        if (!logHeaders) {
            return;
        }

        if (!logBody || !hasRequestBody) {
            logger.log("http method :" + request.method());
        } else if (bodyEncoded(request.headers())) {
            logger.log("http method :" + request.method() + " (encoded body omitted)");
        } else if (request.body() instanceof MultipartBody) {
            //如果是MultipartBody，会log出一大推乱码的东东
        } else {
            Buffer rbBuffer = new Buffer();
            requestBody.writeTo(rbBuffer);
            Charset charset = Charset.defaultCharset();
            MediaType contentType = requestBody.contentType();
            if (contentType != null) {
                contentType.charset(charset);
            }
            logger.log("request:"+rbBuffer.readString(charset));
            rbBuffer.close();
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }


    /**
     * No logs.
     */
    public static final int NONE = 0;

    /**
     * Logs request and response lines.
     */
    public static final int BASIC = 1;

    /**
     * Logs request and response lines and their respective headers.
     */
    public static final int HEADERS = 2;

    /**
     * Logs request and response lines and their respective headers and bodies (if present).
     */
    public static final int BODY = 3;

    /**
     * 定义日志输出类型
     */
    @IntDef({NONE, BASIC,HEADERS,BODY})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Level {}

    /**
     * 日志接口
     */
    public interface Logger {
        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = new Logger() {
            @Override
            public void log(String message) {
                Platform.get().log(INFO, message, null);
            }
        };
    }
}